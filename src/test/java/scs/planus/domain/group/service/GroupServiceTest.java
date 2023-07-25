package scs.planus.domain.group.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import scs.planus.domain.Status;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.dto.GroupCreateRequestDto;
import scs.planus.domain.group.dto.GroupDetailUpdateRequestDto;
import scs.planus.domain.group.dto.GroupGetDetailResponseDto;
import scs.planus.domain.group.dto.GroupGetMemberResponseDto;
import scs.planus.domain.group.dto.GroupMemberResponseDto;
import scs.planus.domain.group.dto.GroupNoticeUpdateRequestDto;
import scs.planus.domain.group.dto.GroupResponseDto;
import scs.planus.domain.group.dto.GroupsGetResponseDto;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.entity.GroupTag;
import scs.planus.domain.group.repository.GroupMemberQueryRepository;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.group.repository.GroupTagRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.tag.dto.TagCreateRequestDto;
import scs.planus.domain.tag.entity.Tag;
import scs.planus.domain.tag.repository.TagRepository;
import scs.planus.domain.tag.service.TagService;
import scs.planus.global.exception.PlanusException;
import scs.planus.infra.s3.AmazonS3Uploader;
import scs.planus.support.ServiceTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scs.planus.global.exception.CustomExceptionStatus.*;

@ServiceTest
class GroupServiceTest {

    private final static Long NOT_EXISTED_ID = 0L;
    private final static int COUNT = 7;

    @MockBean
    private final AmazonS3Uploader s3Uploader;

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupTagRepository groupTagRepository;
    private final TagRepository tagRepository;

    private final GroupMemberQueryRepository groupMemberQueryRepository;
    private final GroupTagService groupTagService;
    private final TagService tagService;
    private final GroupService groupService;

    private Member leader;
    private Member member;
    private Group group;

    @Autowired
    public GroupServiceTest(AmazonS3Uploader s3Uploader, MemberRepository memberRepository,
                            GroupRepository groupRepository, GroupMemberRepository groupMemberRepository,
                            GroupTagRepository groupTagRepository, TagRepository tagRepository,
                            JPAQueryFactory queryFactory) {
        this.s3Uploader = s3Uploader;
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupTagRepository = groupTagRepository;
        this.tagRepository = tagRepository;

        this.groupMemberQueryRepository = new GroupMemberQueryRepository(queryFactory);
        this.groupTagService = new GroupTagService(groupTagRepository);
        this.tagService = new TagService(tagRepository);
        this.groupService = new GroupService(
                s3Uploader,
                memberRepository,
                groupRepository,
                groupMemberRepository,
                groupMemberQueryRepository,
                groupTagRepository,
                groupTagService,
                tagService);
    }

    @BeforeEach
    void init() {
        leader = memberRepository.findById(1L).orElseThrow();
        member = memberRepository.findById(2L).orElseThrow();

        group = Group.creatGroup("group1", "groupNotice", 10, "hello");
        GroupMember groupLeader = GroupMember.createGroupLeader(leader, group);
        groupMemberRepository.save(groupLeader);
        groupRepository.save(group);
    }

    @DisplayName("Group이 제대로 생성되어야 한다.")
    @Test
    void createGroup() {
        //given
        TagCreateRequestDto tag = TagCreateRequestDto.builder().name("tag1").build();
        MockMultipartFile file = new MockMultipartFile("testImg", "test.png", "image/png", "test".getBytes());
        GroupCreateRequestDto requestDto = GroupCreateRequestDto.builder()
                .name("testGroup")
                .notice("test Notice")
                .limitCount(5)
                .tagList(new ArrayList<>(List.of(tag)))
                .build();

        //when
        GroupResponseDto group
                = groupService.createGroup(leader.getId(), requestDto, file);

        //then
        assertThat(group.getGroupId()).isNotNull();
    }

    @DisplayName("Group 생성시, 중복된 Tag로 요청한다면 예외를 던진다.")
    @Test
    void createGroup_Throw_Exception_If_Duplicated_Tag() {
        //given
        TagCreateRequestDto tag1 = TagCreateRequestDto.builder().name("tag1").build();
        TagCreateRequestDto tag2 = TagCreateRequestDto.builder().name("tag1").build();
        List<TagCreateRequestDto> tags = Arrays.asList(tag1, tag2);

        MockMultipartFile file = new MockMultipartFile("testImg", "test.png", "image/png", "test".getBytes());
        GroupCreateRequestDto requestDto = GroupCreateRequestDto.builder()
                .name("testGroup")
                .notice("test Notice")
                .limitCount(5)
                .tagList(tags)
                .build();

        //then
        assertThatThrownBy(() ->
                groupService.createGroup(
                        leader.getId(),
                        requestDto,
                        file))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(EXIST_DUPLICATE_TAGS);
    }

    @DisplayName("Group 상세정보를 조회한다. 만약 가입한 경우, isJoined가 true여야 한다.")
    @Test
    void getGroupDetail_IsJoined_Is_True_If_Joined_Group() {
        //when
        GroupGetDetailResponseDto groupDetail
                = groupService.getGroupDetail(leader.getId(), group.getId());

        //then
        assertThat(groupDetail.getName()).isEqualTo(group.getName());
        assertThat(groupDetail.getNotice()).isEqualTo(group.getNotice());
        assertThat(groupDetail.getLimitCount()).isEqualTo(group.getLimitCount());
        assertThat(groupDetail.getGroupImageUrl()).isEqualTo(group.getGroupImageUrl());
        assertThat(groupDetail.getIsJoined()).isTrue();
    }

    @DisplayName("Group 상세정보를 조회한다. 만약 가입하지 않은 경우, isJoined가 false여야 한다.")
    @Test
    void getGroupDetail_IsJoined_Is_False_If_Not_Joined_Group() {
        //when
        GroupGetDetailResponseDto groupDetail
                = groupService.getGroupDetail(member.getId(), group.getId());

        //then
        assertThat(groupDetail.getName()).isEqualTo(group.getName());
        assertThat(groupDetail.getNotice()).isEqualTo(group.getNotice());
        assertThat(groupDetail.getLimitCount()).isEqualTo(group.getLimitCount());
        assertThat(groupDetail.getGroupImageUrl()).isEqualTo(group.getGroupImageUrl());
        assertThat(groupDetail.getIsJoined()).isFalse();
    }

    @DisplayName("존재하지 않는 Group인 경우 예외를 던진다.")
    @Test
    void getGroupDetail_Throw_Exception_If_Not_Existed_Group() {
        //then
        assertThatThrownBy(() ->
                groupService.getGroupDetail(
                        member.getId(),
                        NOT_EXISTED_ID))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_GROUP);
    }

    @DisplayName("Group에 속한 GroupMember들이 조회되어야 한다.")
    @Test
    void getGroupMember() {
        //given
        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMemberRepository.save(groupMember);

        //when
        List<GroupGetMemberResponseDto> groupMembers
                = groupService.getGroupMember(group.getId());

        //then
        assertThat(groupMembers).hasSize(2);
    }
    
    @DisplayName("Group 상세 정보가 변경되어야 한다.")
    @Test
    void updateGroupDetail() {
        //given
        Tag tag = new Tag("tag");
        tagRepository.save(tag);
        GroupTag groupTag = GroupTag.builder()
                .group(group)
                .tag(tag)
                .build();
        groupTagRepository.save(groupTag);

        MockMultipartFile file = new MockMultipartFile("testImg", "test.png", "image/png", "test".getBytes());
        TagCreateRequestDto tagRequest = TagCreateRequestDto.builder().name("newTag").build();
        GroupDetailUpdateRequestDto requestDto = GroupDetailUpdateRequestDto.builder()
                .limitCount(5)
                .tagList(new ArrayList<>(List.of(tagRequest)))
                .build();

        //when
        GroupResponseDto responseDto
                = groupService.updateGroupDetail(leader.getId(), group.getId(), requestDto, file);

        //then
        assertThat(responseDto.getGroupId()).isEqualTo(group.getId());
        assertThat(group.getLimitCount()).isEqualTo(requestDto.getLimitCount());
        assertThat(group.getGroupTags()).hasSize(requestDto.getTagList().size())
                .extracting(GroupTag::getTag)
                .extracting(Tag::getName)
                .containsExactly(tagRequest.getName());
    }

    @DisplayName("Group 상세정보 수정 중, GroupTag가 이미 존재하는 경우, 이를 유지해야 한다.")
    @Test
    void updateGroupDetail_Retain_GroupTag_If_Duplicated_Tag() {
        //given
        Tag tag = new Tag("tag");
        tagRepository.save(tag);
        GroupTag groupTag = GroupTag.builder()
                .group(group)
                .tag(tag)
                .build();
        groupTagRepository.save(groupTag);

        MockMultipartFile file = new MockMultipartFile("testImg", "test.png", "image/png", "test".getBytes());
        TagCreateRequestDto tagRequest1 = TagCreateRequestDto.builder().name("tag").build();
        TagCreateRequestDto tagRequest2 = TagCreateRequestDto.builder().name("newTag").build();

        GroupDetailUpdateRequestDto requestDto = GroupDetailUpdateRequestDto.builder()
                .limitCount(5)
                .tagList(new ArrayList<>(List.of(tagRequest1, tagRequest2)))
                .build();

        //when
        GroupResponseDto responseDto
                = groupService.updateGroupDetail(leader.getId(), group.getId(), requestDto, file);

        //then
        assertThat(responseDto.getGroupId()).isEqualTo(group.getId());
        assertThat(group.getLimitCount()).isEqualTo(requestDto.getLimitCount());
        assertThat(group.getGroupTags()).hasSize(requestDto.getTagList().size())
                .extracting(GroupTag::getTag)
                .extracting(Tag::getName)
                .containsExactly(tagRequest1.getName(), tagRequest2.getName());
    }

    @DisplayName("Group 공지사항이 수정되어야 한다.")
    @Test
    void updateGroupNotice() {
        //given
        GroupNoticeUpdateRequestDto requestDto = GroupNoticeUpdateRequestDto.builder()
                .notice("newNotice")
                .build();

        //when
        groupService.updateGroupNotice(leader.getId(), group.getId(), requestDto);

        //then
        assertThat(group.getNotice()).isEqualTo(requestDto.getNotice());
    }

    @DisplayName("Leader가 아닌 GroupMember가 Group 공지사항 수정 시 예외를 던진다.")
    @Test
    void updateGroupNotice_Throw_Exception_If_Not_Leader() {
        //given
        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMemberRepository.save(groupMember);

        GroupNoticeUpdateRequestDto requestDto = GroupNoticeUpdateRequestDto.builder()
                .notice("newNotice")
                .build();

        //then
        assertThatThrownBy(() ->
                groupService.updateGroupNotice(
                        member.getId(),
                        group.getId(),
                        requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_GROUP_LEADER_PERMISSION);
    }

    @DisplayName("Group을 삭제할 때, status가 Inactive로 변경되어야 한다.")
    @Test
    void softDeleteGroup() {
        //when
        GroupResponseDto responseDto
                = groupService.softDeleteGroup(leader.getId(), group.getId());

        //then
        assertThat(responseDto.getGroupId()).isEqualTo(group.getId());
        assertThat(group.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @DisplayName("GroupMember를 강퇴하는 경우, 해당 GroupMember의 status가 Inactive로 변경되어야 한다.")
    @Test
    void withdrawGroupMember() {
        //given
        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMemberRepository.save(groupMember);

        //when
        GroupMemberResponseDto groupMemberResponseDto
                = groupService.withdrawGroupMember(leader.getId(), member.getId(), group.getId());

        //then
        assertThat(groupMemberResponseDto.getGroupMemberId()).isEqualTo(groupMember.getId());
        assertThat(groupMember.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @DisplayName("GroupMember가 아닌데 강퇴하는 경우, 예외를 던진다.")
    @Test
    void withdrawGroupMember_Throw_Exception_If_Not_Joined_GroupMember() {
        //then
        assertThatThrownBy(() ->
                groupService.withdrawGroupMember(
                        leader.getId(),
                        member.getId(),
                        group.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_JOINED_GROUP);
    }

    @DisplayName("GroupLeader가 아닌데 GroupMember를 강퇴하고자 하는 경우, 예외를 던진다.")
    @Test
    void withdrawGroupMember_Throw_Exception_If_Not_Leader() {
        //given
        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMemberRepository.save(groupMember);

        //then
        assertThatThrownBy(() ->
                groupService.withdrawGroupMember(
                        member.getId(),
                        leader.getId(),
                        group.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_GROUP_LEADER_PERMISSION);
    }

    @DisplayName("Group을 탈퇴한다면, status가 Inactive가 되어야 한다.")
    @Test
    void softWithdraw() {
        //given
        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMemberRepository.save(groupMember);

        //when
        GroupMemberResponseDto groupMemberResponseDto
                = groupService.softWithdraw(member.getId(), group.getId());

        //then
        assertThat(groupMemberResponseDto.getGroupMemberId()).isEqualTo(groupMember.getId());
        assertThat(groupMember.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @DisplayName("GroupLeader가 Group을 탈퇴하고자 한다면 예외를 던진다.")
    @Test
    void softWithdraw_Throw_Exception_If_Leader() {
        //then
        assertThatThrownBy(() ->
                groupService.softWithdraw(
                        leader.getId(),
                        group.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(CANNOT_WITHDRAW);
    }

    @Nested
    @DisplayName("GroupSearch 테스트")
    class GroupSearchTest {

        private final static int PAGE = 0;
        private final static int PAGE_SIZE = 5;

        @BeforeEach
        void init(@Autowired TodoCategoryRepository todoCategoryRepository) {
            todoCategoryRepository.deleteAll();
            groupRepository.deleteAll();

            for (int i = 0; i < COUNT; i++) {
                Group group = Group.builder()
                        .name("group" + i)
                        .status(Status.ACTIVE)
                        .build();
                GroupMember groupLeader = GroupMember.createGroupLeader(leader, group);
                groupMemberRepository.save(groupLeader);
                groupRepository.save(group);
            }
        }

        @DisplayName("Group 화면 조회시 결과가 제대로 페이징되어 조회되어야 한다.")
        @Test
        void getGroupsSearchHome_Paging() {
            //given
            Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

            //when
            List<GroupsGetResponseDto> groupsSearchHome
                    = groupService.getGroupsSearchHome(pageable);

            //then
            assertThat(groupsSearchHome).hasSize(PAGE_SIZE);
        }

        @DisplayName("Group 화면 조회시 GroupMember 수 내림차순, id 내림차순으로 정렬되어 조회되어야 한다.")
        @Test
        void getGroupsSearchHome_Sort() {
            //given
            Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

            Group group = Group.builder().status(Status.ACTIVE).build();
            GroupMember groupLeader = GroupMember.createGroupLeader(leader, group);
            GroupMember groupMember = GroupMember.createGroupMember(leader, group);
            groupMemberRepository.saveAll(List.of(groupLeader, groupMember));
            groupRepository.save(group);

            //when
            List<GroupsGetResponseDto> groupsSearchHome
                    = groupService.getGroupsSearchHome(pageable);

            //then
            assertThat(groupsSearchHome).hasSize(PAGE_SIZE)
                    .isSortedAccordingTo(
                            Comparator.comparingInt(GroupsGetResponseDto::getMemberCount).reversed()
                                    .thenComparing(Comparator.comparingLong(GroupsGetResponseDto::getGroupId).reversed()));
        }

        @DisplayName("Group의 이름에 포함된 keyword를 통해 Group 조회가 이루어져야 한다.")
        @Test
        void getGroupsSearchByKeyword() {
            //given
            String keyword = "1";
            Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

            //when
            List<GroupsGetResponseDto> groupsSearchByKeyword
                    = groupService.getGroupsSearchByKeyword(keyword, pageable);

            //then
            assertThat(groupsSearchByKeyword).hasSize(1)
                    .isSortedAccordingTo(
                            Comparator.comparingInt(GroupsGetResponseDto::getMemberCount).reversed()
                                    .thenComparing(Comparator.comparingLong(GroupsGetResponseDto::getGroupId).reversed()))
                    .extracting(GroupsGetResponseDto::getName)
                    .allMatch(name -> name.contains(keyword));
        }
    }
}