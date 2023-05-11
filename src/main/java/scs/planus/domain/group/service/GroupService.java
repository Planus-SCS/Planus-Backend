package scs.planus.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import scs.planus.domain.group.dto.*;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupJoin;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.entity.GroupTag;
import scs.planus.domain.group.repository.*;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.tag.dto.TagCreateRequestDto;
import scs.planus.domain.tag.entity.Tag;
import scs.planus.domain.tag.service.TagService;
import scs.planus.global.exception.PlanusException;
import scs.planus.infra.s3.AmazonS3Uploader;

import java.util.List;
import java.util.stream.Collectors;

import static scs.planus.global.exception.CustomExceptionStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    private final AmazonS3Uploader s3Uploader;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final GroupTagRepository groupTagRepository;
    private final GroupJoinRepository groupJoinRepository;
    private final GroupTagService groupTagService;
    private final TagService tagService;

    @Transactional
    public GroupResponseDto createGroup(Long memberId, GroupCreateRequestDto requestDto, MultipartFile multipartFile ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> {
                    throw new PlanusException( NONE_USER );
                });

        String groupImageUrl = createGroupImage( multipartFile );
        Group group = Group.creatGroup( requestDto.getName(), requestDto.getNotice(), requestDto.getLimitCount(), groupImageUrl );

        List<Tag> tagList = tagService.transformToTag( requestDto.getTagList() );
        GroupTag.create( group, tagList );

        GroupMember.creatGroupLeader( member, group );

        Group saveGroup = groupRepository.save( group );

        return GroupResponseDto.of( saveGroup );
    }

    public GroupGetResponseDto getGroupDetailForNonMember( Long memberId, Long groupId ) {
        Group group = groupRepository.findWithGroupMemberById( groupId )
                .orElseThrow(() ->  new PlanusException( NOT_EXIST_GROUP ));

        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException( NONE_USER ));

        // 가입한 그룹인지 검증
        Boolean isJoined = groupMemberQueryRepository.existByMemberIdAndGroupId( member.getId(), groupId );

        // 그룹 테그 조회 후 List<dto>로 변경
        List<GroupTag> groupTags = groupTagRepository.findAllByGroup( group );
        List<GroupTagResponseDto> groupTagResponseDtos =
                groupTags.stream()
                        .map( GroupTagResponseDto::of )
                        .collect( Collectors.toList() );

        return GroupGetResponseDto.of( group, groupTagResponseDtos, isJoined );
    }

    public List<GroupGetMemberResponseDto> getGroupMemberForNonMember(Long groupId) {
        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow( () -> { throw new PlanusException( NOT_EXIST_GROUP ); });

        List<GroupMember> allGroupMembers = groupMemberRepository.findAllWithMemberByGroupAndStatus(group);

        return allGroupMembers.stream()
                .map( gm -> GroupGetMemberResponseDto.of( gm.getMember(), gm.isLeader() ) )
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupResponseDto updateGroupDetail( Long memberId, Long groupId, GroupDetailUpdateRequestDto requestDto, MultipartFile multipartFile ) {
        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow( () -> { throw new PlanusException( NOT_EXIST_GROUP ); });

        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> { throw new PlanusException( NONE_USER ); });

        // 리더가 아니면 수정 불가능
        validateLeaderPermission( member, group );

        // 그룹 이미지 변경
        String oldGroupImageUrl = group.getGroupImageUrl();
        String newGroupImageUrl = updateGroupImage(multipartFile, oldGroupImageUrl);

        // 그룹 테그 수정.
        List<TagCreateRequestDto> addTagDtos = groupTagService.update( group, requestDto.getTagList() );
        List<Tag> addTags = tagService.transformToTag( addTagDtos );
        GroupTag.create( group, addTags );

        // 그 외 세부사항 수정
        group.updateDetail( requestDto.getLimitCount(), newGroupImageUrl );

        return GroupResponseDto.of( group );
    }

    @Transactional
    public GroupResponseDto updateGroupNotice( Long memberId, Long groupId, GroupNoticeUpdateRequestDto requestDto ) {
        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow( () -> { throw new PlanusException( NOT_EXIST_GROUP ); });

        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> { throw new PlanusException( NONE_USER ); });

        // 리더가 아니면 수정 불가능
        validateLeaderPermission( member, group );

        group.updateNotice( requestDto.getNotice() );

        return GroupResponseDto.of( group );
    }

    @Transactional
    public GroupResponseDto softDeleteGroup( Long memberId, Long groupId ) {
        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow( () -> { throw new PlanusException( NOT_EXIST_GROUP ); });

        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> { throw new PlanusException( NONE_USER ); });

        // 리더가 아니면 수정 불가능
        validateLeaderPermission( member, group );

        group.changeStatusToInactive();

        return GroupResponseDto.of( group );
    }

    private String createGroupImage( MultipartFile multipartFile ) {
        if ( multipartFile != null ) {
            return s3Uploader.upload( multipartFile, "groups" );
        }
        throw new PlanusException( INVALID_FILE );
    }

    private String updateGroupImage( MultipartFile multipartFile, String groupImageUrl ) {
        if (multipartFile != null) {
            s3Uploader.deleteImage( groupImageUrl );
            groupImageUrl = s3Uploader.upload( multipartFile, "groups" );
        }
        return groupImageUrl;
    }

    @Transactional
    public GroupJoinResponseDto joinGroup( Long memberId, Long groupId ) {
        Group group = groupRepository.findByIdAndStatus(groupId)
                .orElseThrow(() -> { throw new PlanusException( NOT_EXIST_GROUP ); });

        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> { throw new PlanusException( NONE_USER ); });

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
    public GroupMemberResponseDto acceptGroupJoin( Long memberId, Long groupJoinId ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> { throw new PlanusException( NONE_USER ); });

        GroupJoin groupJoin = groupJoinRepository.findWithGroupById( groupJoinId )
                .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP_JOIN ));

        validateLeaderPermission( member, groupJoin.getGroup() );

        GroupMember groupMember = GroupMember.creatGroupMember( groupJoin.getMember(), groupJoin.getGroup() );
        GroupMember saveGroupMember = groupMemberRepository.save( groupMember );

        groupJoinRepository.delete( groupJoin );

        return GroupMemberResponseDto.of( saveGroupMember );
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

    @Transactional
    public GroupMemberResponseDto withdrawGroupMember(Long leaderId, Long memberId, Long groupId) {
        Member leader = memberRepository.findById( leaderId )
                .orElseThrow(() -> { throw new PlanusException( NONE_USER ); });

        Member withdrawMember = memberRepository.findById( memberId )
                .orElseThrow(() -> { throw new PlanusException( NONE_USER ); });

        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow( () -> { throw new PlanusException( NOT_EXIST_GROUP ); });

        GroupMember withdrawGroupMember = groupMemberRepository.findByMemberIdAndGroupId( withdrawMember.getId(), group.getId() )
                .orElseThrow(() -> new PlanusException(NOT_JOINED_GROUP));

        validateLeaderPermission( leader, group );

        withdrawGroupMember.changeStatusToInactive();

        return GroupMemberResponseDto.of( withdrawGroupMember );
    }

    private void validateLeaderPermission( Member member, Group group ) {
        GroupMember groupLeader = groupMemberRepository.findWithGroupAndLeaderByGroup( group )
                .orElseThrow( () -> { throw new PlanusException( NOT_EXIST_LEADER ); });

        if ( !member.equals( groupLeader.getMember() ) )
            throw new PlanusException( NOT_GROUP_LEADER_PERMISSION );
    }

    private void validateExceedLimit( Group group, List<GroupMember> allGroupMembers ) {
        // 제한 인원을 초과하지 않았는지
        if ( allGroupMembers.size() >= group.getLimitCount() ) {
            throw new PlanusException( EXCEED_GROUP_LIMIT_COUNT );
        }
    }
}