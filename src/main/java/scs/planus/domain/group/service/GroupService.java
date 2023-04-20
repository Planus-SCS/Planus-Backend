package scs.planus.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.entity.GroupTag;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.tag.entity.Tag;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.exception.CustomExceptionStatus;
import scs.planus.domain.group.dto.GroupCreateRequestDto;
import scs.planus.domain.group.dto.GroupGetResponseDto;
import scs.planus.domain.group.dto.GroupResponseDto;
import scs.planus.domain.group.dto.GroupTagResponseDto;
import scs.planus.infra.s3.AmazonS3Uploader;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.group.repository.GroupTagRepository;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.tag.service.TagService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    private final AmazonS3Uploader s3Uploader;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final TagService tagService;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupTagRepository groupTagRepository;

    @Transactional
    public GroupResponseDto createGroup(Long memberId, GroupCreateRequestDto requestDto, MultipartFile multipartFile ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> {
                    throw new PlanusException( CustomExceptionStatus.NONE_USER );
                });

        String groupImageUrl = createGroupImage( multipartFile );
        Group group = Group.creatGroup( requestDto.getName(), requestDto.getNotice(), requestDto.getLimitCount(), groupImageUrl );

        List<Tag> tagList = tagService.transformToTag( requestDto.getTagList() );
        GroupTag.create( group, tagList );

        GroupMember.creatGroupLeader( member, group );

        Group saveGroup = groupRepository.save( group );

        return GroupResponseDto.of( saveGroup );
    }

    public GroupGetResponseDto getGroup(Long groupId ) {
        Group group = groupRepository.findById( groupId )
                .orElseThrow( () -> {
                    throw new PlanusException( CustomExceptionStatus.NOT_EXIST_GROUP );
                });

        GroupMember groupLeader = groupMemberRepository.findLeaderByGroup(group)
                .orElseThrow(() -> {
                    throw new PlanusException(CustomExceptionStatus.NOT_EXIST_LEADER);
                });

        List<GroupTag> groupTags = groupTagRepository.findAllByGroupId(group);
        List<GroupTagResponseDto> GroupTagResponseDtos = groupTags.stream()
                                                            .map(GroupTagResponseDto::of)
                                                            .collect(Collectors.toList());

        return GroupGetResponseDto.of( group, groupLeader.getMember().getNickname(), GroupTagResponseDtos );
    }

    private String createGroupImage( MultipartFile multipartFile ) {
        if ( multipartFile != null ) {
            return s3Uploader.upload( multipartFile, "groups" );
        }
        throw new PlanusException( CustomExceptionStatus.INVALID_FILE );
    }

    @Transactional
    public GroupResponseDto joinGroup(Long groupId, Long memberId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> {
                    throw new PlanusException(CustomExceptionStatus.NOT_EXIST_GROUP);
                });

        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> {
                    throw new PlanusException( CustomExceptionStatus.NONE_USER );
                });

        GroupMember groupMember = GroupMember.creatGroupMember(member, group);
        groupMemberRepository.save(groupMember);

        return GroupResponseDto.of( group );
    }
}