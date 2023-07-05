package scs.planus.domain.category.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import scs.planus.domain.Status;
import scs.planus.domain.category.dto.TodoCategoryGetResponseDto;
import scs.planus.domain.category.dto.TodoCategoryRequestDto;
import scs.planus.domain.category.dto.TodoCategoryResponseDto;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.global.exception.PlanusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class MemberTodoCategoryServiceTest {
    private static final long TEST_ID = 1L;
    private static final String INVALID_COLOR = "invalid color";
    @InjectMocks
    private MemberTodoCategoryService memberTodoCategoryService;
    @Mock
    private TodoCategoryRepository todoCategoryRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private Member member;
    @Mock
    private TodoCategory mockTodoCategory;
    @Mock
    private TodoCategoryRequestDto mockTodoCategoryRequestDto;


    @DisplayName("회원 소유의 모든 카테고리를 List<Dto> 로 변환하여 반환해야 한다.")
    @Test
    void findAll_Success_Exist() {
        //given
        MemberTodoCategory memberTodoCategory1 = MemberTodoCategory.builder()
                .member(member)
                .name("회원 카테고리1")
                .color(Color.BLUE)
                .build();

        MemberTodoCategory memberTodoCategory2 = MemberTodoCategory.builder()
                .member(member)
                .name("회원 카테고리2")
                .color(Color.RED)
                .build();

        List<MemberTodoCategory> memberTodoCategoryList = new ArrayList<>(
                List.of(memberTodoCategory1, memberTodoCategory2)
        );

        when(memberRepository.findById(TEST_ID)).thenReturn(Optional.of(member));
        when(todoCategoryRepository.findMemberTodoCategoryAllByMember(member)).thenReturn(memberTodoCategoryList);

        //when
        List<TodoCategoryGetResponseDto> responseDtoList = memberTodoCategoryService.findAll(TEST_ID);

        //then
        verify(todoCategoryRepository).findMemberTodoCategoryAllByMember(member);
        assertThat(responseDtoList.size()).isEqualTo(memberTodoCategoryList.size());
        assertThat(responseDtoList.get(0).getName()).isEqualTo(memberTodoCategory1.getName());
        assertThat(responseDtoList.get(0).getStatus()).isEqualTo(Status.ACTIVE);
    }

    @DisplayName("회원 소유의 카테고리가 없을 경우, 빈 리스트를 반환해야 한다.")
    @Test
    void findAll_Success_Empty() {
        //given
        when(memberRepository.findById(TEST_ID)).thenReturn(Optional.of(member));

        List<MemberTodoCategory> emptyList = List.of();
        when(todoCategoryRepository.findMemberTodoCategoryAllByMember(member)).thenReturn(emptyList);

        //when
        List<TodoCategoryGetResponseDto> responseDtoList = memberTodoCategoryService.findAll(TEST_ID);

        //then
        verify(memberRepository).findById(TEST_ID);
        verify(todoCategoryRepository).findMemberTodoCategoryAllByMember(member);
        assertThat(responseDtoList.size()).isEqualTo(0);
    }

    @DisplayName("회원정보가 없을 시, NON_USER Exception 을 발생시켜야 한다.")
    @Test
    void findAll_Fail_Non_User() {
        //given
        //when
        when(memberRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> memberTodoCategoryService.findAll(TEST_ID))
                .isInstanceOf(PlanusException.class);
    }

    @DisplayName("회원이 속한 모든 그룹의 groupTodoCategory 를 반환해야 한다.")
    @Test
    void findAllGroupTodoCategories_Success() {
        //given
        Group mockGroup1 = mock(Group.class);
        Group mockGroup2 = mock(Group.class);

        GroupTodoCategory group1TodoCategory1 = GroupTodoCategory.builder()
                .group(mockGroup1).build();

        GroupTodoCategory group1TodoCategory2 = GroupTodoCategory.builder()
                .group(mockGroup1).build();

        GroupTodoCategory group2TodoCategory1 = GroupTodoCategory.builder()
                .group(mockGroup2).build();

        GroupTodoCategory group2TodoCategory2 = GroupTodoCategory.builder()
                .group(mockGroup2).build();

        GroupMember group1Member = GroupMember.builder()
                .member(member)
                .group(mockGroup1).build();

        GroupMember group2Member = GroupMember.builder()
                .member(member)
                .group(mockGroup2).build();

        List<GroupMember> groupMemberList = new ArrayList<>(
                List.of(group1Member, group2Member));

        List<GroupTodoCategory> groupTodoCategoryList = new ArrayList<>(
                List.of(group1TodoCategory1, group1TodoCategory2, group2TodoCategory1, group2TodoCategory2));

        when(groupMemberRepository.findAllByActiveGroupAndMemberId(TEST_ID)).thenReturn(groupMemberList);
        when(todoCategoryRepository.findAllGroupTodoCategoriesInGroups(any())).thenReturn(groupTodoCategoryList);

        //when
        List<TodoCategoryGetResponseDto> responseDtoList = memberTodoCategoryService.findAllGroupTodoCategories(TEST_ID);

        //then
        verify(groupMemberRepository).findAllByActiveGroupAndMemberId(TEST_ID);
        verify(todoCategoryRepository).findAllGroupTodoCategoriesInGroups(any());
        assertThat(responseDtoList.size()).isEqualTo(4);
    }

    @DisplayName("회원의 카테고리를 생성할 수 있다.")
    @Test
    void createCategory_Success() {
        //given
        when(memberRepository.findById(TEST_ID)).thenReturn(Optional.of(member));
        when(todoCategoryRepository.save(any())).thenReturn(mockTodoCategory);
        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("카테고리1")
                .color("BLUE")
                .build();

        //when
        TodoCategoryResponseDto responseDto =
                memberTodoCategoryService.createCategory(TEST_ID, todoCategoryRequestDto);

        //then
        verify(todoCategoryRepository).save(any());
        assertThat(responseDto.getId()).isEqualTo(mockTodoCategory.getId());
    }

    @Test
    @DisplayName("존재하지 않는 Color name 일 경우, Exception 을 발생시켜야 한다.")
    void createCategory_Fail_Invalid_Color() {
        //given
        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("그룹 카테고리1")
                .color(INVALID_COLOR)
                .build();

        //when
        //then
        assertThatThrownBy(() -> memberTodoCategoryService.createCategory(member.getId(), todoCategoryRequestDto))
                .isInstanceOf(PlanusException.class);
    }

    @DisplayName("회원정보가 없을 경우, Exception 을 발생시켜야 한다.")
    @Test
    void createCategory_Fail_Non_User() {
        //given
        //when
        when(memberRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> memberTodoCategoryService.createCategory(TEST_ID, mockTodoCategoryRequestDto))
                .isInstanceOf(PlanusException.class);
    }

    @DisplayName("카테고리의 name 과 color 를 변경하고 categoryId 를 반환해야 한다.")
    @Test
    void changeMemberTodoCategory_Success() {
        //given
        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .color("RED")
                .build();

        when(todoCategoryRepository.findById(TEST_ID)).thenReturn(Optional.of(mockTodoCategory));

        //when
        TodoCategoryResponseDto responseDto =
                memberTodoCategoryService.changeCategory(TEST_ID, todoCategoryRequestDto);

        //then
        verify(todoCategoryRepository).findById(TEST_ID);
        assertThat(responseDto.getId()).isEqualTo(mockTodoCategory.getId());
    }

    @DisplayName("존재하지 않는 Category 의 경우, Exception 을 발생시켜야 한다.")
    @Test
    void changeCategory_Fail_Invalid_CategoryId() {
        //given
        when(todoCategoryRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> memberTodoCategoryService.changeCategory(TEST_ID, mockTodoCategoryRequestDto))
                .isInstanceOf(PlanusException.class);
    }

    @DisplayName("존재하지 않는 Color 인 경우, Exception 을 발생시켜야 한다.")
    @Test
    void changeCategory_Fail_Invalid_Color() {
        //given
        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .color(INVALID_COLOR)
                .build();

        when(todoCategoryRepository.findById(TEST_ID)).thenReturn(Optional.of(mockTodoCategory));

        //when
        //then
        assertThatThrownBy(() -> memberTodoCategoryService.changeCategory(TEST_ID, todoCategoryRequestDto))
                .isInstanceOf(PlanusException.class);
    }

    @DisplayName("카테고리의 status 를 Inactive 로 변경하고 categoryId를 반환해야 한다.")
    @Test
    void deleteCategory_Success() {
        //given
        when(todoCategoryRepository.findById(TEST_ID)).thenReturn(Optional.of(mockTodoCategory));
        when(mockTodoCategory.getId()).thenReturn(TEST_ID);

        //when
        TodoCategoryResponseDto responseDto = memberTodoCategoryService.deleteCategory(TEST_ID);

        //then
        verify(todoCategoryRepository).findById(TEST_ID);
        verify(mockTodoCategory).changeStatusToInactive();
        assertThat(responseDto.getId()).isEqualTo(TEST_ID);
    }

    @DisplayName("존재하지 않는 Category 의 경우, Exception 을 발생시켜야 한다.")
    @Test
    void deleteCategory_Fail_Invalid_CategoryId() {
        //given
        when(todoCategoryRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> memberTodoCategoryService.changeCategory(TEST_ID, mockTodoCategoryRequestDto))
                .isInstanceOf(PlanusException.class);
    }

}