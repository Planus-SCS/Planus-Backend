package scs.planus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.netty.internal.shaded.reactor.pool.PoolAcquirePendingLimitException;
import scs.planus.common.exception.PlanusException;
import scs.planus.common.response.CustomResponseStatus;
import scs.planus.domain.Group;
import scs.planus.domain.GroupMember;
import scs.planus.domain.Member;
import scs.planus.dto.group.GroupCreateRequestDto;
import scs.planus.dto.group.GroupResponseDto;
import scs.planus.infra.AmazonS3Uploader;
import scs.planus.repository.GroupRepository;
import scs.planus.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    private final AmazonS3Uploader s3Uploader;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public GroupResponseDto create( Long memberId, GroupCreateRequestDto requestDto, MultipartFile multipartFile ) {
        Member member = memberRepository.findById( memberId )
                .orElseThrow(() -> {
                    throw new PlanusException( CustomResponseStatus.NONE_USER );
                });

        String groupImageUrl = createGroupImage( multipartFile );
        Group group = Group.creatGroup( requestDto.getName(), requestDto.getNotice(), requestDto.getLimitCount(), groupImageUrl );
        GroupMember.creatGroupMemberLeader(member, group);
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
