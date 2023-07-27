package scs.planus.domain.category.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import scs.planus.domain.category.dto.TodoCategoryGetResponseDto;
import scs.planus.domain.category.dto.TodoCategoryRequestDto;
import scs.planus.domain.category.dto.TodoCategoryResponseDto;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.service.MemberTodoCategoryService;
import scs.planus.support.ControllerTest;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberTodoCategoryController.class)
class MemberTodoCategoryControllerTest extends ControllerTest {

    @MockBean
    private MemberTodoCategoryService memberTodoCategoryService;

    private final Long categoryId = 1L;

    @DisplayName("자신의 모든 MemberTodoCategory 를 조회할 수 있다.")
    @Test
    void getAllMemberTodoCategory() throws Exception {
        // given
        String path = "/app/categories";

        List<TodoCategoryGetResponseDto> responseDto = List.of(
                TodoCategoryGetResponseDto.builder()
                        .id(1L)
                        .name("카테고리1")
                        .color(Color.BLUE)
                        .build(),
                TodoCategoryGetResponseDto.builder()
                        .id(2L)
                        .name("카테고리2")
                        .color(Color.RED)
                        .build()
        );

        given(memberTodoCategoryService.findAll(anyLong()))
                .willReturn(responseDto);

        // when
        ResultActions response = mockMvc.perform(get(path));

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("자신이 속한 모든 그룹의 모든 GroupTodoCategory 를 조회한다.")
    @Test
    void getAllGroupTodoCategory() throws Exception {
        // given
        String path = "/app/categories/groups";

        List<TodoCategoryGetResponseDto> responseDto = List.of(
                TodoCategoryGetResponseDto.builder()
                        .id(1L)
                        .name("카테고리1")
                        .color(Color.BLUE)
                        .build(),
                TodoCategoryGetResponseDto.builder()
                        .id(2L)
                        .name("카테고리2")
                        .color(Color.RED)
                        .build()
        );

        given(memberTodoCategoryService.findAllGroupTodoCategories(anyLong()))
                .willReturn(responseDto);

        // when
        ResultActions response = mockMvc.perform(get(path));

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("MemberTodoCategory 를 생성할 수 있다.")
    @Test
    void createMemberTodoCategory() throws Exception  {
        // given
        String path = "/app/categories";

        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("카테고리")
                .color("BLUE")
                .build();

        TodoCategoryResponseDto todoCategoryResponseDto = TodoCategoryResponseDto.builder()
                .id(1L)
                .build();

        given(memberTodoCategoryService.createCategory(anyLong(), any(TodoCategoryRequestDto.class)))
                .willReturn(todoCategoryResponseDto);

        // when
        ResultActions response = mockMvc.perform(
                post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoCategoryRequestDto))
                        .with(csrf())
        );

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("MemberTodoCategory 를 수정할 수 있다.")
    @Test
    void modifyMemberTodoCategory() throws Exception  {
        // given
        String path = "/app/categories/{categoryId}";

        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("수정된 카테고리")
                .color("BLUE")
                .build();

        TodoCategoryResponseDto todoCategoryResponseDto = TodoCategoryResponseDto.builder()
                .id(categoryId)
                .build();

        given(memberTodoCategoryService.changeCategory(anyLong(), any(TodoCategoryRequestDto.class)))
                .willReturn(todoCategoryResponseDto);

        // when
        ResultActions response = mockMvc.perform(
                patch(path, categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoCategoryRequestDto))
                        .with(csrf())
        );

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("MemberTodoCategory 를 삭제할 수 있다.")
    @Test
    void deleteMemberTodoCategory() throws Exception  {
        // given
        String path = "/app/categories/{categoryId}";

        TodoCategoryResponseDto todoCategoryResponseDto = TodoCategoryResponseDto.builder()
                .id(categoryId)
                .build();

        given(memberTodoCategoryService.deleteCategory(anyLong()))
                .willReturn(todoCategoryResponseDto);

        // when
        ResultActions response = mockMvc.perform(
                delete(path, categoryId)
                        .with(csrf())
        );

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }
}