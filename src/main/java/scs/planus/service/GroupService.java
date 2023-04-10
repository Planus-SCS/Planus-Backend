package scs.planus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import scs.planus.common.exception.PlanusException;
import scs.planus.common.response.CustomResponseStatus;
import scs.planus.domain.*;
import scs.planus.dto.group.GroupCreateRequestDto;
import scs.planus.dto.group.GroupResponseDto;
import scs.planus.infra.AmazonS3Uploader;
import scs.planus.repository.GroupRepository;
import scs.planus.repository.MemberRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    private final AmazonS3Uploader s3Uploader;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final TagService tagService;

    @Transactional
    public GroupResponseDto createGroup(Long memberId, GroupCreateRequestDto requestDto, MultipartFile multipartFile ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> {
                    throw new PlanusException( CustomResponseStatus.NONE_USER );
                });

        String groupImageUrl = createGroupImage( multipartFile );
        Group group = Group.creatGroup( requestDto.getName(), requestDto.getNotice(), requestDto.getLimitCount(), groupImageUrl );

        List<Tag> tagList = tagService.transformToTag( requestDto.getTagList() );
        GroupTag.create( group, tagList );

        GroupMember.creatGroupMemberLeader( member, group );

        Group saveGroup = groupRepository.save( group );

        return GroupResponseDto.of( saveGroup );
    }

    private String createGroupImage(MultipartFile multipartFile) {
        if (multipartFile != null) {
            return s3Uploader.upload(multipartFile, "groups");
        }
        throw new PlanusException(CustomResponseStatus.INVALID_FILE);
    }
}
