package scs.planus.domain.group.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.Status;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.support.RepositoryTest;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class GroupMemberQueryRepositoryTest {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

    private final GroupMemberQueryRepository groupMemberQueryRepository;

    @Autowired
    public GroupMemberQueryRepositoryTest(MemberRepository memberRepository, GroupRepository groupRepository,
                                          JPAQueryFactory queryFactory) {
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;

        groupMemberQueryRepository = new GroupMemberQueryRepository(queryFactory);
    }

    @DisplayName("GroupMember가 존재하면 true를 반환한다.")
    @Test
    void existByMemberIdAndGroupId_Return_True_If_GroupMember(){
        //given
        Member member = Member.builder().status(Status.ACTIVE).build();
        Group group = Group.builder().status(Status.ACTIVE).build();

        memberRepository.save(member);
        groupRepository.save(group);

        GroupMember.createGroupMember(member, group);

        //when
        Boolean isExisted
                = groupMemberQueryRepository.existByMemberIdAndGroupId(member.getId(), group.getId());

        //then
        assertThat(isExisted).isTrue();
    }

    @DisplayName("GroupMember가 존재하지 않다면 false를 반환한다.")
    @Test
    void existByMemberIdAndGroupId_Return_False_If_Not_GroupMember(){
        //given
        Member member = Member.builder().status(Status.ACTIVE).build();
        Group group = Group.builder().status(Status.ACTIVE).build();

        memberRepository.save(member);
        groupRepository.save(group);

        //when
        Boolean isExisted
                = groupMemberQueryRepository.existByMemberIdAndGroupId(member.getId(), group.getId());

        //then
        assertThat(isExisted).isFalse();
    }

    @DisplayName("GroupMember의 status가 inactive라면 false를 반환한다.")
    @Test
    void existByMemberIdAndGroupId_Return_False_If_GroupMember_Status_Inactive(){
        //given
        Member member = Member.builder().status(Status.ACTIVE).build();
        Group group = Group.builder().status(Status.ACTIVE).build();

        memberRepository.save(member);
        groupRepository.save(group);

        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMember.changeStatusToInactive();

        //when
        Boolean isExisted
                = groupMemberQueryRepository.existByMemberIdAndGroupId(member.getId(), group.getId());

        //then
        assertThat(isExisted).isFalse();
    }
}