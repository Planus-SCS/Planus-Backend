package scs.planus.domain.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import scs.planus.domain.group.dto.GroupCreateRequestDto;
import scs.planus.domain.group.dto.GroupGetResponseDto;
import scs.planus.domain.group.dto.GroupResponseDto;
import scs.planus.domain.group.dto.GroupTagResponseDto;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.entity.GroupTag;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.group.repository.GroupTagRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
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
    private final TagService tagService;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupTagRepository groupTagRepository;

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

    public GroupGetResponseDto getGroup( Long groupId ) {
        Group group = groupRepository.findWithGroupMemberById( groupId )
                .orElseThrow(() ->  new PlanusException(NOT_EXIST_GROUP));

        // 그룹 리더 이름 조회
        String leaderName = group.getLeaderName();

        // 그룹 테그 조회 후 List<dto>로 변경
        List<GroupTag> groupTags = groupTagRepository.findAllByGroup( group );
        List<GroupTagResponseDto> groupTagResponseDtos =
                groupTags.stream()
                        .map( GroupTagResponseDto::of )
                        .collect( Collectors.toList() );

        return GroupGetResponseDto.of( group, leaderName, groupTagResponseDtos );
    }

    private String createGroupImage( MultipartFile multipartFile ) {
        if ( multipartFile != null ) {
            return s3Uploader.upload( multipartFile, "groups" );
        }
        throw new PlanusException(INVALID_FILE);
    }

    @Transactional
    public GroupResponseDto joinGroup(Long groupId, Long memberId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> {
                    throw new PlanusException(NOT_EXIST_GROUP);
                });

        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> {
                    throw new PlanusException(NONE_USER);
                });

        GroupMember groupMember = GroupMember.creatGroupMember(member, group);
        groupMemberRepository.save(groupMember);

        return GroupResponseDto.of( group );
    }
}