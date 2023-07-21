package scs.planus.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import scs.planus.domain.group.dto.*;
import scs.planus.domain.group.entity.Group;
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
import static scs.planus.global.util.validator.Validator.validateDuplicateTagName;

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
    private final GroupTagService groupTagService;
    private final TagService tagService;

    public List<GroupsGetResponseDto> getGroupsSearchHome(Pageable pageable) {

        List<Group> groups = groupRepository.findAllByActiveOrderByNumOfMembersAndId(pageable);

        List<GroupTag> allGroupTags = groupTagRepository.findAllTagInGroups(groups);

        List<GroupsGetResponseDto> groupsGetResponseDtos = groups.stream()
                .map(group -> {
                    List<GroupTagResponseDto> eachGroupTags = getEachGroupTags(group, allGroupTags);

                    return GroupsGetResponseDto.of(group, eachGroupTags);
                })
                .collect(Collectors.toList());

        return groupsGetResponseDtos;
    }

    public List<GroupsGetResponseDto> getGroupsSearchByKeyword(String keyword, Pageable pageable) {

        List<Group> groups = groupRepository.findAllByKeywordAndActiveOrderByNumOfMembersAndId(keyword, pageable);

        List<GroupTag> allGroupTags = groupTagRepository.findAllTagInGroups(groups);

        List<GroupsGetResponseDto> groupsGetResponseDtos = groups.stream()
                .map(group -> {
                    List<GroupTagResponseDto> eachGroupTags = getEachGroupTags(group, allGroupTags);

                    return GroupsGetResponseDto.of(group, eachGroupTags);
                }).collect(Collectors.toList());

        return groupsGetResponseDtos;
    }

    @Transactional
    public GroupResponseDto createGroup(Long memberId, GroupCreateRequestDto requestDto, MultipartFile multipartFile ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException(NONE_USER));

        String groupImageUrl = s3Uploader.upload(multipartFile, "groups");
        Group group = Group.creatGroup( requestDto.getName(), requestDto.getNotice(), requestDto.getLimitCount(), groupImageUrl );

        validateDuplicateTagName( requestDto.getTagList() );
        List<Tag> tagList = tagService.transformToTag( requestDto.getTagList() );
        GroupTag.create( group, tagList );

        GroupMember.createGroupLeader( member, group );

        Group saveGroup = groupRepository.save( group );

        return GroupResponseDto.of( saveGroup );
    }

    public GroupGetDetailResponseDto getGroupDetail(Long memberId, Long groupId ) {
        Group group = groupRepository.findWithGroupMemberById( groupId )
                .orElseThrow(() ->  new PlanusException( NOT_EXIST_GROUP ));

        // 가입한 그룹인지 검증
        Boolean isJoined = groupMemberQueryRepository.existByMemberIdAndGroupId( memberId, groupId );

        // 그룹 테그 조회 후 List<dto>로 변경
        List<GroupTag> groupTags = groupTagRepository.findAllByGroup( group );
        List<GroupTagResponseDto> groupTagResponseDtos =
                groupTags.stream()
                        .map( GroupTagResponseDto::of )
                        .collect( Collectors.toList() );

        return GroupGetDetailResponseDto.of( group, groupTagResponseDtos, isJoined );
    }

    public List<GroupGetMemberResponseDto> getGroupMember(Long groupId) {
        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow( () -> new PlanusException(NOT_EXIST_GROUP));

        List<GroupMember> allGroupMembers = groupMemberRepository.findAllWithMemberByGroupAndStatus(group);

        return allGroupMembers.stream()
                .map( gm -> GroupGetMemberResponseDto.of( gm.getMember(), gm.isLeader() ) )
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupResponseDto updateGroupDetail( Long memberId, Long groupId, GroupDetailUpdateRequestDto requestDto, MultipartFile multipartFile ) {
        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow( () -> new PlanusException(NOT_EXIST_GROUP));

        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException(NONE_USER));

        // 리더가 아니면 수정 불가능
        validateLeaderPermission( member, group );

        // 그룹 이미지 변경
        String oldGroupImageUrl = group.getGroupImageUrl();
        String newGroupImageUrl = s3Uploader.updateImage(multipartFile, oldGroupImageUrl, "groups");

        // 그룹 테그 수정.
        validateDuplicateTagName( requestDto.getTagList() );
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
                .orElseThrow( () -> new PlanusException(NOT_EXIST_GROUP));

        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException(NONE_USER));

        // 리더가 아니면 수정 불가능
        validateLeaderPermission( member, group );

        group.updateNotice( requestDto.getNotice() );

        return GroupResponseDto.of( group );
    }

    @Transactional
    public GroupResponseDto softDeleteGroup( Long memberId, Long groupId ) {
        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow( () -> new PlanusException(NOT_EXIST_GROUP));

        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException(NONE_USER));

        // 리더가 아니면 수정 불가능
        validateLeaderPermission( member, group );

        group.changeStatusToInactive();

        return GroupResponseDto.of( group );
    }

    @Transactional
    public GroupMemberResponseDto withdrawGroupMember(Long leaderId, Long memberId, Long groupId) {
        Member leader = memberRepository.findById( leaderId )
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Member withdrawMember = memberRepository.findById( memberId )
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Group group = groupRepository.findByIdAndStatus( groupId )
                .orElseThrow( () -> new PlanusException(NOT_EXIST_GROUP));

        GroupMember withdrawGroupMember = groupMemberRepository.findByMemberIdAndGroupId( withdrawMember.getId(), group.getId() )
                .orElseThrow(() -> new PlanusException(NOT_JOINED_GROUP));

        validateLeaderPermission( leader, group );

        withdrawGroupMember.changeStatusToInactive();

        return GroupMemberResponseDto.of( withdrawGroupMember );
    }

    @Transactional
    public GroupMemberResponseDto softWithdraw( Long memberId, Long groupId ) {
        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId( memberId, groupId )
                .orElseThrow(() -> {
                    groupRepository.findByIdAndStatus( groupId )
                            .orElseThrow(() -> new PlanusException( NOT_EXIST_GROUP ));
                    return new PlanusException( NOT_JOINED_GROUP );
                });

        if ( groupMember.isLeader() ) {throw new PlanusException( CANNOT_WITHDRAW );}

        groupMember.changeStatusToInactive();

        return GroupMemberResponseDto.of( groupMember );
    }

    // TODO MyGroupService 내 동일 메서드 존재 -> 추후 통합 리펙토링 고려
    private List<GroupTagResponseDto> getEachGroupTags(Group group, List<GroupTag> allGroupTags) {
        return allGroupTags.stream()
                .filter(groupTag -> groupTag.getGroup().getId().equals(group.getId()))
                .map(GroupTagResponseDto::of)
                .collect(Collectors.toList());
    }

    private void validateLeaderPermission( Member member, Group group ) {
        GroupMember groupLeader = groupMemberRepository.findWithGroupAndLeaderByGroup( group )
                .orElseThrow( () -> new PlanusException(NOT_EXIST_LEADER));

        if ( !member.equals( groupLeader.getMember() ) )
            throw new PlanusException( NOT_GROUP_LEADER_PERMISSION );
    }
}