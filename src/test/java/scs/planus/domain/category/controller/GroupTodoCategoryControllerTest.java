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
import scs.planus.domain.category.service.GroupTodoCategoryService;
import scs.planus.support.ControllerTest;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupTodoCategoryController.class)
class GroupTodoCategoryControllerTest extends ControllerTest {
    private static final String INVALID_COLOR = "invalid color";

    @MockBean
    private GroupTodoCategoryService groupTodoCategoryService;

    private final Long groupId = 1L;
    private final Long categoryId = 1L;

    @DisplayName("전체 그룹 투두 카테고리를 를 조회할 수 있다.")
    @Test
    void getAllGroupTodoCategories() throws Exception {
        // given
        String path = "/app/my-groups/{groupId}/categories";

        List<TodoCategoryGetResponseDto> todoCategoryGetResponseDtos = List.of(
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

        given(groupTodoCategoryService.findAll(anyLong(), anyLong()))
                .willReturn(todoCategoryGetResponseDtos);

        // when
        ResultActions response = mockMvc.perform(get(path, groupId));

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("그룹 투두 카테고리를 를 생성할 수 있다.")
    @Test
    void createGroupTodoCategory() throws Exception {
        // given
        String path = "/app/my-groups/{groupId}/categories";

        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("카테고리 생성")
                .color("BLUE")
                .build();

        TodoCategoryResponseDto todoCategoryResponseDto = TodoCategoryResponseDto.builder()
                .id(categoryId)
                .build();

        given(groupTodoCategoryService.createCategory(anyLong(), anyLong(), any(TodoCategoryRequestDto.class)))
                .willReturn(todoCategoryResponseDto);

        // when
        ResultActions response = mockMvc.perform(
                post(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoCategoryRequestDto))
                        .with(csrf())
        );

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("그룹 투두 카테고리 생성시, 요청값이 검증조건을 만족하지 못하면 예외를 발생시킨다." +
            "- name: 10글자 초과" +
            "- color: 존재하지 않는 색")
    @Test
    void createGroupTodoCategory_Fail_Not_Validated_Request() throws Exception {
        // given
        String path = "/app/my-groups/{groupId}/categories";

        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("A".repeat(11))
                .color(INVALID_COLOR)
                .build();

        // when
        ResultActions response = mockMvc.perform(
                post(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoCategoryRequestDto))
                        .with(csrf())
        );

        // then
        response.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("그룹 투두 카테고리를 수정할 수 있다.")
    @Test
    void modifyGroupTodoCategory() throws Exception {
        // given
        String path = "/app/my-groups/{groupId}/categories/{categoryId}";

        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("카테고리 수정")
                .color("RED")
                .build();

        TodoCategoryResponseDto todoCategoryResponseDto = TodoCategoryResponseDto.builder()
                .id(categoryId)
                .build();

        given(groupTodoCategoryService.changeCategory(anyLong(), anyLong(), anyLong(), any(TodoCategoryRequestDto.class)))
                .willReturn(todoCategoryResponseDto);

        // when
        ResultActions response = mockMvc.perform(
                patch(path, groupId, categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoCategoryRequestDto))
                        .with(csrf())
        );

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("그룹 투두 카테고리 수정시, 요청값이 검증조건을 만족하지 못하면 예외를 발생시킨다." +
            "- name: 10글자 초과" +
            "- color: 존재하지 않는 색")
    @Test
    void modifyGroupTodoCategory_Fail_Not_Validated_Request() throws Exception {
        // given
        String path = "/app/my-groups/{groupId}/categories/{categoryId}";

        TodoCategoryRequestDto todoCategoryRequestDto = TodoCategoryRequestDto.builder()
                .name("A".repeat(11))
                .color(INVALID_COLOR)
                .build();

        // when
        ResultActions response = mockMvc.perform(
                patch(path, groupId, categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoCategoryRequestDto))
                        .with(csrf())
        );

        // then
        response.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("그룹 투두 카테고리를 삭제(soft)할 수 있다.")
    @Test
    void deleteGroupTodoCategory() throws Exception {
        // given
        String path = "/app/my-groups/{groupId}/categories/{categoryId}";

        TodoCategoryResponseDto todoCategoryResponseDto = TodoCategoryResponseDto.builder()
                .id(categoryId)
                .build();

        given(groupTodoCategoryService.deleteCategory(anyLong(), anyLong(), anyLong()))
                .willReturn(todoCategoryResponseDto);

        // when
        ResultActions response = mockMvc.perform(
                delete(path, groupId, categoryId)
                        .with(csrf()));

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }
}