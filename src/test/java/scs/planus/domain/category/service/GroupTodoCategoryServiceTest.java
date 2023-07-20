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
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
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
class GroupTodoCategoryServiceTest {
    private static final long NOT_EXIST_ID = 0L;
    private static final String INVALID_COLOR = "invalid color";

    private final TodoCategoryRepository todoCategoryRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

    private final GroupTodoCategoryService groupTodoCategoryService;

    private Member leader;
    private Group group;

    @Autowired
    public GroupTodoCategoryServiceTest(TodoCategoryRepository todoCategoryRepository,
                                        GroupRepository groupRepository,
                                        GroupMemberRepository groupMemberRepository,
                                        MemberRepository memberRepository) {
        this.todoCategoryRepository = todoCategoryRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;

        groupTodoCategoryService
                = new GroupTodoCategoryService(
                todoCategoryRepository,
                groupRepository,
                groupMemberRepository
        );
    }

    @BeforeEach
    void init() {
        leader = memberRepository.findById(1L).orElseThrow();
        group = groupRepository.findById(1L).orElseThrow();
    }

    @DisplayName("리더는 그룹의 투두 카테고리를 모두 조회할 수 있다.")
    @Test
    void findAll_Success() {
        // given
        // when
        List<TodoCategoryGetResponseDto> responseDtos
                = groupTodoCategoryService.findAll(leader.getId(), group.getId());

        // then
        assertThat(responseDtos).hasSize(1);
    }

    @DisplayName("리더권한이 없으면 DO_NOT_HAVE_TODO_AUTHORITY 예외가 발생해야 한다.")
    @Test
    void findAll_Fail_Todo_Authority() {
        // given
        Member member = memberRepository.findById(2L).orElseThrow();

        // when
        // then
        assertThatThrownBy(() -> groupTodoCategoryService.findAll(member.getId(), group.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(DO_NOT_HAVE_TODO_AUTHORITY);
    }

    @DisplayName("리더는 그룹 투두 카테고리를 생성할 수 있다.")
    @Test
    void createCategory_Success() {
        // given
        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .color("BLUE")
                .build();

        // when
        TodoCategoryResponseDto todoCategoryResponseDto
                = groupTodoCategoryService.createCategory(leader.getId(), group.getId(), todoCategoryRequestDto);

        // then
        assertThat(todoCategoryResponseDto.getId()).isNotNull();
    }

    @DisplayName("존재하지 않는 Color 일 경우 INVALID_CATEGORY_COLOR 예외가 발생해야 한다.")
    @Test
    void createCategory_Fail_Color() {
        // given
        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .color(INVALID_COLOR)
                .build();

        // when
        // then
        assertThatThrownBy(()->groupTodoCategoryService.createCategory(leader.getId(), group.getId(),
                                                                        todoCategoryRequestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_CATEGORY_COLOR);
    }

    @DisplayName("리더는 그룹 투두 카테고리를 수정할 수 있다.")
    @Test
    void changeCategory_Success() {
        // given
        GroupTodoCategory groupTodoCategory = GroupTodoCategory.builder()
                .group(group)
                .name("그룹 카테고리")
                .color(Color.BLUE)
                .build();

        todoCategoryRepository.save(groupTodoCategory);

        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("수정 괸 카테고리")
                .color("RED")
                .build();

        // when
        TodoCategoryResponseDto todoCategoryResponseDto
                = groupTodoCategoryService.changeCategory(leader.getId(), group.getId(),
                                                            groupTodoCategory.getId(), todoCategoryRequestDto);

        TodoCategory findCategory = todoCategoryRepository.findById(todoCategoryResponseDto.getId()).orElseThrow();

        // then
        assertThat(todoCategoryResponseDto).isNotNull();
        assertThat(findCategory.getName()).isEqualTo(todoCategoryRequestDto.getName());
        assertThat(findCategory.getColor().name()).isEqualTo(todoCategoryRequestDto.getColor());
    }

    @DisplayName("존재하지 않는 category 일 경우 NOT_EXIST_CATEGORY 예외가 발생해야 한다.")
    @Test
    void changeCategory_Fail_Non_Exist_Category() {
        // given
        GroupTodoCategory groupTodoCategory = GroupTodoCategory.builder()
                .group(group)
                .build();

        todoCategoryRepository.save(groupTodoCategory);

        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .color("BLUE")
                .build();

        // when
        // then
        assertThatThrownBy(
                () -> groupTodoCategoryService.changeCategory(leader.getId(), group.getId(),
                                                                NOT_EXIST_ID, todoCategoryRequestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_CATEGORY);
    }

    @DisplayName("리더는 그룹 투두 카테고리를 삭제할 수 있다.")
    @Test
    void deleteCategory_Success() {
        // given
        GroupTodoCategory groupTodoCategory = GroupTodoCategory.builder()
                .group(group)
                .build();

        todoCategoryRepository.save(groupTodoCategory);

        // when
        TodoCategoryResponseDto todoCategoryResponseDto
                = groupTodoCategoryService.deleteCategory(leader.getId(), group.getId(), groupTodoCategory.getId());

        TodoCategory findCategory = todoCategoryRepository.findById(todoCategoryResponseDto.getId()).orElseThrow();

        // then
        assertThat(todoCategoryResponseDto).isNotNull();
        assertThat(findCategory.getStatus()).isEqualTo(Status.INACTIVE);
    }
}