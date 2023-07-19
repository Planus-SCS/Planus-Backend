package scs.planus.domain.category.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.Status;
import scs.planus.domain.category.dto.TodoCategoryGetResponseDto;
import scs.planus.domain.category.dto.TodoCategoryRequestDto;
import scs.planus.domain.category.dto.TodoCategoryResponseDto;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
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

@Slf4j
@ServiceTest
class MemberTodoCategoryServiceTest {
    private static final long NOT_EXIST_ID = 0L;
    private static final String INVALID_COLOR = "invalid color";

    private final TodoCategoryRepository todoCategoryRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;

    private final MemberTodoCategoryService memberTodoCategoryService;

    private Member member;

    @Autowired
    public MemberTodoCategoryServiceTest(TodoCategoryRepository todoCategoryRepository,
                                         MemberRepository memberRepository,
                                         GroupMemberRepository groupMemberRepository,
                                         GroupRepository groupRepository) {
        this.todoCategoryRepository = todoCategoryRepository;
        this.memberRepository = memberRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupRepository = groupRepository;

        memberTodoCategoryService
                = new MemberTodoCategoryService(
                todoCategoryRepository,
                memberRepository,
                groupMemberRepository
        );
    }

    @BeforeEach
    void init() {
        member = memberRepository.findById(1L).orElseThrow();
    }

    @DisplayName("회원 소유의 모든 카테고리를 List<Dto> 로 변환하여 반환해야 한다.")
    @Test
    void findAll_Success_Exist() {
        //given
        MemberTodoCategory memberTodoCategory2 = MemberTodoCategory.builder()
                .member(member)
                .name("회원 카테고리2")
                .color(Color.RED)
                .build();

        todoCategoryRepository.save(memberTodoCategory2);

        //when
        List<TodoCategoryGetResponseDto> responseDtos = memberTodoCategoryService.findAll(member.getId());

        //then
        assertThat(responseDtos.size()).isEqualTo(2);
        assertThat(responseDtos.get(1).getName()).isEqualTo(memberTodoCategory2.getName());
        assertThat(responseDtos.get(1).getStatus()).isEqualTo(Status.ACTIVE);
    }

    @DisplayName("회원정보가 없을 시, NON_USER Exception 을 발생시켜야 한다.")
    @Test
    void findAll_Fail_Non_User() {
        //given
        //when
        //then
        assertThatThrownBy(() -> memberTodoCategoryService.findAll(NOT_EXIST_ID))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NONE_USER);
    }

    @DisplayName("회원이 속한 모든 그룹의 groupTodoCategory 를 반환해야 한다.")
    @Test
    void findAllGroupTodoCategories_Success() {
        //given
        Group group1 = groupRepository.findById(1L).orElseThrow();
        Group group2 = Group.builder()
                .status(Status.ACTIVE)
                .build();

        groupRepository.save(group2);

        GroupMember group1Member = GroupMember.builder()
                .member(member)
                .group(group1)
                .build();

        GroupMember group2Member = GroupMember.builder()
                .member(member)
                .group(group2)
                .build();

        groupMemberRepository.saveAll(List.of(group1Member, group2Member));

        GroupTodoCategory group1TodoCategory2 = GroupTodoCategory.builder()
                .group(group1)
                .build();

        GroupTodoCategory group2TodoCategory1 = GroupTodoCategory.builder()
                .group(group2)
                .build();

        GroupTodoCategory group2TodoCategory2 = GroupTodoCategory.builder()
                .group(group2)
                .build();

        todoCategoryRepository.saveAll(List.of(group1TodoCategory2, group2TodoCategory1, group2TodoCategory2));

        //when
        List<TodoCategoryGetResponseDto> responseDtos =
                memberTodoCategoryService.findAllGroupTodoCategories(member.getId());

        //then
        assertThat(responseDtos).hasSize(4);
    }

    @DisplayName("회원의 카테고리를 생성할 수 있다.")
    @Test
    void createCategory_Success() {
        //given
        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("카테고리")
                .color("BLUE")
                .build();

        //when
        TodoCategoryResponseDto responseDto =
                memberTodoCategoryService.createCategory(member.getId(), todoCategoryRequestDto);

        //then
        assertThat(responseDto.getId()).isNotNull();
    }

    @DisplayName("존재하지 않는 Color name 일 경우, Exception 을 발생시켜야 한다.")
    @Test
    void createCategory_Fail_Invalid_Color() {
        //given
        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("그룹 카테고리")
                .color(INVALID_COLOR)
                .build();

        //when
        //then
        assertThatThrownBy(() -> memberTodoCategoryService.createCategory(member.getId(), todoCategoryRequestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_CATEGORY_COLOR);
    }

    @DisplayName("회원정보가 없을 경우, Exception 을 발생시켜야 한다.")
    @Test
    void createCategory_Fail_Non_User() {
        //given
        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("그룹 카테고리")
                .color(INVALID_COLOR)
                .build();

        //when
        //then
        assertThatThrownBy(() -> memberTodoCategoryService.createCategory(NOT_EXIST_ID, todoCategoryRequestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NONE_USER);
    }

    @DisplayName("카테고리의 name 과 color 를 변경하고 categoryId 를 반환해야 한다.")
    @Test
    void changeMemberTodoCategory_Success() {
        //given
        MemberTodoCategory category = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElseThrow();

        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("수정된 카테고리")
                .color("RED")
                .build();

        //when
        TodoCategoryResponseDto responseDto =
                memberTodoCategoryService.changeCategory(category.getId(), todoCategoryRequestDto);

        //then
        assertThat(responseDto.getId()).isEqualTo(category.getId());
        assertThat(category.getColor()).isEqualTo(Color.RED);
        assertThat(category.getName()).isEqualTo("수정된 카테고리");
    }

    @DisplayName("존재하지 않는 Category 의 경우, Exception 을 발생시켜야 한다.")
    @Test
    void changeCategory_Fail_Invalid_CategoryId() {
        //given
        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("수정된 카테고리")
                .color("RED")
                .build();

        //when
        //then
        assertThatThrownBy(() -> memberTodoCategoryService.changeCategory(NOT_EXIST_ID, todoCategoryRequestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_CATEGORY);
    }

    @DisplayName("존재하지 않는 Color 인 경우, Exception 을 발생시켜야 한다.")
    @Test
    void changeCategory_Fail_Invalid_Color() {
        //given
        MemberTodoCategory category = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElseThrow();

        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .color(INVALID_COLOR)
                .build();

        //when
        //then
        assertThatThrownBy(() -> memberTodoCategoryService.changeCategory(category.getId(), todoCategoryRequestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_CATEGORY_COLOR);
    }

    @DisplayName("카테고리의 status 를 Inactive 로 변경하고 categoryId를 반환해야 한다.")
    @Test
    void deleteCategory_Success() {
        //given
        MemberTodoCategory category = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElseThrow();

        //when
        TodoCategoryResponseDto responseDto = memberTodoCategoryService.deleteCategory(category.getId());

        //then
        assertThat(responseDto.getId()).isEqualTo(category.getId());
        assertThat(category.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @DisplayName("존재하지 않는 Category 의 경우, Exception 을 발생시켜야 한다.")
    @Test
    void deleteCategory_Fail_Invalid_CategoryId() {
        //given
        //when
        //then
        assertThatThrownBy(() -> memberTodoCategoryService.deleteCategory(NOT_EXIST_ID))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_CATEGORY);
    }

}