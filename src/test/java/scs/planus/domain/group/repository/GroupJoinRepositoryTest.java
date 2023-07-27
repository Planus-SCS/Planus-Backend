package scs.planus.domain.group.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupJoin;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.support.RepositoryTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GroupJoinRepositoryTest extends RepositoryTest {
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupJoinRepository groupJoinRepository;

    private Member member;
    private Group group;
    private GroupJoin groupJoin;

    @Autowired
    public GroupJoinRepositoryTest(MemberRepository memberRepository,
                                   GroupRepository groupRepository,
                                   GroupJoinRepository groupJoinRepository) {
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
        this.groupJoinRepository = groupJoinRepository;
    }

    @BeforeEach
    void init() {
        member = Member.builder()
                .build();
        memberRepository.save(member);

        group = Group.builder()
                .build();
        groupRepository.save(group);

        groupJoin = GroupJoin.createGroupJoin(member, group);
        groupJoinRepository.save(groupJoin);
    }

    @DisplayName("List<Group> 으로 각 그룹의 모든 GroupJoin 을 조회할 수 있다.")
    @Test
    void findAllByGroupIn() {
        // given
        Member member2 = Member.builder()
                .build();
        memberRepository.save(member2);

        Group group2 = Group.builder()
                .build();
        groupRepository.save(group2);

        GroupJoin groupJoin2 = GroupJoin.createGroupJoin(member2, group2);
        groupJoinRepository.save(groupJoin2);

        // when
        List<Group> groups = List.of(group, group2);
        List<GroupJoin> findGroupJoins = groupJoinRepository.findAllByGroupIn(groups);

        // then
        assertThat(findGroupJoins).hasSize(2);
    }

    @DisplayName("groupJoinId 로 GroupJoin 을 조회할 수 있다.")
    @Test
    void findWithGroupById() {
        // when
        GroupJoin findGroupJoin = groupJoinRepository.findWithGroupById(groupJoin.getId())
                .orElseThrow();

        // then
        assertThat(findGroupJoin.getId()).isEqualTo(groupJoin.getId());
        assertThat(findGroupJoin.getMember()).isEqualTo(groupJoin.getMember());
        assertThat(findGroupJoin.getGroup()).isEqualTo(groupJoin.getGroup());
    }

    @DisplayName("memberId, groupId 로 GroupJoin 을 조회할 수 있다.")
    @Test
    void findByMemberIdAndGroupId() {
        // when
        GroupJoin findGroupJoin = groupJoinRepository.findByMemberIdAndGroupId(member.getId(), group.getId())
                .orElseThrow();

        // then
        assertThat(findGroupJoin.getId()).isEqualTo(groupJoin.getId());
        assertThat(findGroupJoin.getMember()).isEqualTo(groupJoin.getMember());
        assertThat(findGroupJoin.getGroup()).isEqualTo(groupJoin.getGroup());
    }
}