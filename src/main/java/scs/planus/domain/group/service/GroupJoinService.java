package scs.planus.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.List;
import java.util.stream.Collectors;

import static scs.planus.global.exception.CustomExceptionStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupJoinService {
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final GroupJoinRepository groupJoinRepository;

    @Transactional
    public GroupJoinResponseDto joinGroup(Long memberId, Long groupId ) {
        Group group = groupRepository.findByIdAndStatus(groupId)
                .orElseThrow(() -> { throw new PlanusException( NOT_EXIST_GROUP ); });

        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> { throw new PlanusException( NONE_USER ); });

        groupJoinRepository.findByMemberIdAndGroupId(memberId, groupId)
                .ifPresent(groupJoin -> {
                    throw new PlanusException(ALREADY_APPLY_JOINED_GROUP);});

        List<GroupMember> allGroupMembers = groupMemberRepository.findAllWithMemberByGroupAndStatus( group );

        // 제한 인원 초과 검증
        validateExceedLimit( group, allGroupMembers );

        // 가입 여부 검증
        Boolean isJoined = groupMemberQueryRepository.existByMemberIdAndGroupId( member.getId(), groupId );
        if (isJoined) {throw new PlanusException( ALREADY_JOINED_GROUP );}

        GroupJoin groupJoin = GroupJoin.createGroupJoin( member, group );
        GroupJoin saveGroupJoin = groupJoinRepository.save( groupJoin );

        return GroupJoinResponseDto.of( saveGroupJoin );
    }

    public List<GroupJoinGetResponseDto> getAllGroupJoin(Long memberId ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> { throw new PlanusException( NONE_USER ); });

        // 내가 리더인 그룹들 조회
        List<GroupMember> groupMembers = groupMemberRepository.findWithGroupByLeaderMember(member);
        List<Group> groups = groupMembers.stream()
                .map(GroupMember::getGroup)
                .collect(Collectors.toList());

        // 내가 리더인 그룹에 들어온 가입 신청 조회
        List<GroupJoin> allGroupJoinsOfGroups = groupJoinRepository.findAllByGroupIn(groups);

        return allGroupJoinsOfGroups.stream()
                .map(GroupJoinGetResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupMemberResponseDto acceptGroupJoin( Long leaderId, Long groupJoinId ) {
        Member leader = memberRepository.findById( leaderId )
                .orElseThrow(() -> { throw new PlanusException( NONE_USER ); });

        GroupJoin groupJoin = groupJoinRepository.findWithGroupById( groupJoinId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP_JOIN ));

        validateLeaderPermission( leader, groupJoin.getGroup() );

        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupIdAndInactive( groupJoin.getMember().getId(),
                                                                                             groupJoin.getGroup().getId() )
                .map(existedGroupMember -> {
                    existedGroupMember.changeStatusToActive();
                    return existedGroupMember;
                })
                .orElseGet(() -> {
                    GroupMember createGroupMember = GroupMember.creatGroupMember( groupJoin.getMember(), groupJoin.getGroup() );
                    return groupMemberRepository.save(createGroupMember);
                });

        groupJoinRepository.delete( groupJoin );

        return GroupMemberResponseDto.of( groupMember );
    }

    @Transactional
    public GroupJoinResponseDto rejectGroupJoin( Long memberId, Long groupJoinId ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> { throw new PlanusException( NONE_USER ); });

        GroupJoin groupJoin = groupJoinRepository.findWithGroupById( groupJoinId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP_JOIN ));

        validateLeaderPermission( member, groupJoin.getGroup() );

        groupJoinRepository.delete( groupJoin );

        return GroupJoinResponseDto.of( groupJoin );
    }

    private void validateExceedLimit( Group group, List<GroupMember> allGroupMembers ) {
        // 제한 인원을 초과하지 않았는지
        if ( allGroupMembers.size() >= group.getLimitCount() ) {
            throw new PlanusException( EXCEED_GROUP_LIMIT_COUNT );
        }
    }

    // TODO GroupJoinService 내 동일 메서드 존재 -> GroupValidate Class 로 빼는것 고려
    private void validateLeaderPermission( Member member, Group group ) {
        GroupMember groupLeader = groupMemberRepository.findWithGroupAndLeaderByGroup( group )
                .orElseThrow( () -> { throw new PlanusException( NOT_EXIST_LEADER ); });

        if ( !member.equals( groupLeader.getMember() ) )
            throw new PlanusException( NOT_GROUP_LEADER_PERMISSION );
    }
}
