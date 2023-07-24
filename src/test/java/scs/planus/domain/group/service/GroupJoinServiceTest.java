package scs.planus.domain.group.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.Status;
import scs.planus.domain.group.dto.GroupMemberResponseDto;
import scs.planus.domain.group.dto.groupJoin.GroupJoinGetResponseDto;
import scs.planus.domain.group.dto.groupJoin.GroupJoinResponseDto;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupJoin;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.repository.GroupJoinRepository;
import scs.planus.domain.group.repository.GroupMemberQueryRepository;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.global.exception.PlanusException;
import scs.planus.support.ServiceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scs.planus.global.exception.CustomExceptionStatus.*;

@ServiceTest
class GroupJoinServiceTest {
    private static final long NOT_EXIST_ID = 0L;

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupJoinRepository groupJoinRepository;

    private final GroupJoinService groupJoinService;

    private Member leader;
    private Member member;
    private Group group;

    @Autowired
    public GroupJoinServiceTest(MemberRepository memberRepository,
                                GroupRepository groupRepository,
                                GroupMemberRepository groupMemberRepository,
                                GroupJoinRepository groupJoinRepository,
                                JPAQueryFactory queryFactory) {
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupJoinRepository = groupJoinRepository;

        GroupMemberQueryRepository groupMemberQueryRepository = new GroupMemberQueryRepository(queryFactory);
        groupJoinService = new GroupJoinService(memberRepository,
                groupRepository,
                groupMemberRepository,
                groupMemberQueryRepository,
                groupJoinRepository
        );
    }

    @BeforeEach
    void init() {
        leader = memberRepository.findById(1L).orElseThrow();
        group = groupRepository.findById(1L).orElseThrow();

        member = Member.builder()
                .nickname("미가입 회원")
                .description("미가입 그룹에 대한 테스트를 위한 회원입니다.")
                .profileImageUrl("image_url")
                .build();
        memberRepository.save(member);
    }

    @DisplayName("new Member 는 미가입 Group 에 그룹신청을 할 수 있다." +
            "생성된 GroupJoin 의 Status 는 INACTIVE 여야 한다.")
    @Test
    void joinGroup_Success() {
        // when
        GroupJoinResponseDto groupJoinResponseDto
                = groupJoinService.joinGroup(member.getId(), group.getId());

        GroupJoin groupJoin
                = groupJoinRepository.findById(groupJoinResponseDto.getGroupJoinId()).orElseThrow();

        // then
        assertThat(groupJoinResponseDto.getGroupJoinId()).isNotNull();
        assertThat(groupJoin.getId()).isEqualTo(groupJoinResponseDto.getGroupJoinId());
        assertThat(groupJoin.getMember()).isEqualTo(member);
        assertThat(groupJoin.getGroup()).isEqualTo(group);
        assertThat(groupJoin.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @DisplayName("존재하지 Group 일 경우 NOT_EXIST_GROUP 예외가 발생해야 한다.")
    @Test
    void joinGroup_Fail_Not_Exist_Group() {
        // when & then
        assertThatThrownBy(() -> groupJoinService.joinGroup(member.getId(), NOT_EXIST_ID))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_GROUP);
    }

    @DisplayName("존재하지 Member 일 경우 NONE_USER 예외가 발생해야 한다.")
    @Test
    void joinGroup_Fail_Not_Exist_Member() {
        // when & then
        assertThatThrownBy(() -> groupJoinService.joinGroup(NOT_EXIST_ID, group.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NONE_USER);
    }

    @DisplayName("이미 신청서를 보낸 경우 ALREADY_APPLY_JOINED_GROUP 예외가 발생해야 한다.")
    @Test
    void joinGroup_Fail_Already_Apply_Joined_Group() {
        // given
        groupJoinService.joinGroup(member.getId(), group.getId());

        // when & then
        assertThatThrownBy(() -> groupJoinService.joinGroup(member.getId(), group.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(ALREADY_APPLY_JOINED_GROUP);
    }

    @DisplayName("그룹 최대인원을 초과한 경우 EXCEED_GROUP_LIMIT_COUNT 예외가 발생해야 한다.")
    @Test
    void joinGroup_Fail_Exceed_Group_Limit_Count() {
        // given
        Group limitExceededGroup = Group.builder()
                .limitCount(0)
                .status(Status.ACTIVE)
                .build();
        groupRepository.save(limitExceededGroup);

        // when & then
        assertThatThrownBy(() -> groupJoinService.joinGroup(member.getId(), limitExceededGroup.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(EXCEED_GROUP_LIMIT_COUNT);
    }

    @DisplayName("이미 가입 되어 있는 경우 ALREADY_JOINED_GROUP 예외가 발생해야 한다.")
    @Test
    void joinGroup_Fail_Already_Joined_Group() {
        // given
        GroupMember groupMember = GroupMember.builder()
                .member(member)
                .group(group)
                .build();
        groupMemberRepository.save(groupMember);

        // when & then
        assertThatThrownBy(() -> groupJoinService.joinGroup(member.getId(), group.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(ALREADY_JOINED_GROUP);
    }

    @DisplayName("로그인 유저가 리더인 그룹의 모든 가입 신청서를 조회할 수 있다.")
    @Test
    void getAllGroupJoin_Success() {
        // given
        Group group2 = Group.builder()
                .status(Status.ACTIVE)
                .build();
        groupRepository.save(group2);

        GroupMember groupMember = GroupMember.builder()
                .group(group2)
                .member(leader)
                .leader(true)
                .build();
        groupMemberRepository.save(groupMember);

        GroupJoin groupJoin1 = GroupJoin.builder()
                .group(group)
                .member(member)
                .build();

        GroupJoin groupJoin2 = GroupJoin.builder()
                .group(group2)
                .member(member)
                .build();

        List<GroupJoin> groupJoins = List.of(groupJoin1, groupJoin2);
        groupJoinRepository.saveAll(groupJoins);

        // when
        List<GroupJoinGetResponseDto> groupJoinGetResponseDtos = groupJoinService.getAllGroupJoin(leader.getId());

        // then
        assertThat(groupJoinGetResponseDtos).hasSize(2);
        assertThat(groupJoinGetResponseDtos)
                .filteredOn(dto -> dto.getGroupJoinId().equals(groupJoin1.getId()))
                .first()
                .satisfies(groupJoinDto1 -> {
                    assertThat(groupJoinDto1.getGroupId()).isEqualTo(group.getId());
                    assertThat(groupJoinDto1.getGroupName()).isEqualTo(group.getName());
                    assertThat(groupJoinDto1.getMemberId()).isEqualTo(member.getId());
                    assertThat(groupJoinDto1.getMemberName()).isEqualTo(member.getNickname());
                    assertThat(groupJoinDto1.getMemberDescription()).isEqualTo(member.getDescription());
                    assertThat(groupJoinDto1.getMemberProfileImageUrl()).isEqualTo(member.getProfileImageUrl());
                });
    }

    @DisplayName("그룹의 리더는 그룹 가입 신청을 수락할 수 있다." +
            "이후 해당 GroupJoin 은 삭제되어야 한다.")
    @Nested
    class acceptGroupJoin_Success{

        @DisplayName("탈퇴 이력이 있는 회원의 경우 상태만 변경한다." +
                "기존 GroupMember 의 Id 를 반환한다.")
        @Test
        void acceptGroupJoin_Success_Withdraw_Member() {
            // given
            GroupMember groupMember = GroupMember.builder()
                    .member(member)
                    .group(group)
                    .build();
            groupMember.changeStatusToInactive();

            GroupJoin groupJoin = GroupJoin.builder()
                    .member(member)
                    .group(group)
                    .build();
            groupJoinRepository.save(groupJoin);

            // when
            GroupMemberResponseDto groupMemberResponseDto
                    = groupJoinService.acceptGroupJoin(leader.getId(), groupJoin.getId());

            GroupJoin findGroupJoin = groupJoinRepository.findById(groupJoin.getId())
                    .orElse(null);

            // then
            assertThat(groupMemberResponseDto.getGroupMemberId()).isEqualTo(groupMember.getId());
            assertThat(groupMember.getStatus()).isEqualTo(Status.ACTIVE);
            assertThat(findGroupJoin).isNull();
        }

        @DisplayName("이력이 없는 회원의 경우 GroupMember 를 새로 생성한다." +
                "생성된 GroupMember 의 Id 를 반환한다.")
        @Test
        void acceptGroupJoin_Success_New_Member() {
            // given
            GroupJoin groupJoin = GroupJoin.builder()
                    .member(member)
                    .group(group)
                    .build();
            groupJoinRepository.save(groupJoin);

            // when
            GroupMemberResponseDto groupMemberResponseDto
                    = groupJoinService.acceptGroupJoin(leader.getId(), groupJoin.getId());

            GroupJoin findGroupJoin = groupJoinRepository.findById(groupJoin.getId())
                    .orElse(null);

            GroupMember findGroupMember
                    = groupMemberRepository.findByMemberIdAndGroupId(member.getId(), group.getId())
                    .orElse(null);

            // then
            assertThat(findGroupMember).isNotNull();
            assertThat(groupMemberResponseDto.getGroupMemberId()).isEqualTo(findGroupMember.getId());
            assertThat(findGroupJoin).isNull();
        }

    }

    @DisplayName("존재하지 않는 GroupJoin 일 경우 NOT_EXIST_GROUP_JOIN 예외가 발생해야 한다.")
    @Test
    void acceptGroupJoin_Fail_Not_Exist_Group_Join() {
        // when & then
        assertThatThrownBy(() -> groupJoinService.acceptGroupJoin(leader.getId(), NOT_EXIST_ID))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_GROUP_JOIN);
    }

    @DisplayName("그룹의 리더가 없는 경우 NOT_EXIST_LEADER 예외가 발생해야 한다.")
    @Test
    void acceptGroupJoin_Fail_Not_Exist_Leader() {
        // given
        Group nonLeaderGroup = Group.builder()
                .build();
        groupRepository.save(nonLeaderGroup);

        Member notLeader = Member.builder()
                .build();
        memberRepository.save(notLeader);

        GroupJoin groupJoin = GroupJoin.builder()
                .group(nonLeaderGroup)
                .member(member)
                .build();
        groupJoinRepository.save(groupJoin);

        // when & then
        assertThatThrownBy(() -> groupJoinService.acceptGroupJoin(notLeader.getId(), group.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_LEADER);
    }

    @DisplayName("로그인 유저가 그룹의 리더가 아닌 경우 NOT_GROUP_LEADER_PERMISSION 예외가 발생해야 한다.")
    @Test
    void acceptGroupJoin_Fail_Not_Group_Leader_Permission() {
        // given
        Member notLeader = Member.builder()
                .build();
        memberRepository.save(notLeader);

        GroupJoin groupJoin = GroupJoin.builder()
                .group(group)
                .member(member)
                .build();
        groupJoinRepository.save(groupJoin);

        // when & then
        assertThatThrownBy(() -> groupJoinService.acceptGroupJoin(notLeader.getId(), groupJoin.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_GROUP_LEADER_PERMISSION);
    }

    @DisplayName("그룹의 리더는 그룹 가입 신청을 거절할 수 있다." +
            "이후 해당 GroupJoin 은 삭제되어야 한다.")
    @Test
    void rejectGroupJoin_Success() {
        // given
        GroupJoin groupJoin = GroupJoin.builder()
                .group(group)
                .member(member)
                .build();
        groupJoinRepository.save(groupJoin);

        // when
        GroupJoinResponseDto groupJoinResponseDto
                = groupJoinService.rejectGroupJoin(leader.getId(), group.getId());

        GroupJoin findGroupJoin = groupJoinRepository.findById(groupJoin.getId()).orElse(null);

        // then
        assertThat(groupJoinResponseDto.getGroupJoinId()).isEqualTo(groupJoin.getId());
        assertThat(findGroupJoin).isNull();
    }
}