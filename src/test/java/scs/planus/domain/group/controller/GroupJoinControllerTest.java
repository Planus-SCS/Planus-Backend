package scs.planus.domain.group.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import scs.planus.domain.group.dto.GroupMemberResponseDto;
import scs.planus.domain.group.dto.groupJoin.GroupJoinGetResponseDto;
import scs.planus.domain.group.dto.groupJoin.GroupJoinResponseDto;
import scs.planus.domain.group.service.GroupJoinService;
import scs.planus.support.ControllerTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupJoinController.class)
class GroupJoinControllerTest extends ControllerTest {

    @MockBean
    private GroupJoinService groupJoinService;

    @DisplayName("Group에 가입신청을 한다.")
    @Test
    void joinGroup() throws Exception {
        //given
        String path = "/app/group-joins/groups/{groupId}";
        Long groupId = 1L;

        given(groupJoinService.joinGroup(anyLong(), anyLong()))
                .willReturn(GroupJoinResponseDto.builder().groupJoinId(1L).build());

        //when & then
        mockMvc
                .perform(post(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Group에 요청된 가입신청들을 조회한다.")
    @Test
    void getAllGroupJoin() throws Exception {
        //given
        String path = "/app/group-joins";

        List<GroupJoinGetResponseDto> responseDtos = List.of(GroupJoinGetResponseDto.builder()
                .groupJoinId(1L)
                .groupId(1L)
                .groupName("group1")
                .memberId(1L)
                .memberName("groupMember")
                .build());

        given(groupJoinService.getAllGroupJoin(anyLong()))
                .willReturn(responseDtos);

        //when & then
        mockMvc
                .perform(get(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("요청된 가입신청을 수락한다.")
    @Test
    void acceptGroupJoin() throws Exception {
        //given
        String path = "/app/group-joins/{groupJoinId}/accept";
        Long groupJoinId = 1L;

        given(groupJoinService.acceptGroupJoin(anyLong(), anyLong()))
                .willReturn(GroupMemberResponseDto.builder().groupMemberId(1L).build());

        //when & then
        mockMvc
                .perform(post(path, groupJoinId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("요청된 가입신청을 거절한다.")
    @Test
    void rejectGroupJoin() throws Exception {
        //given
        String path = "/app/group-joins/{groupJoinId}/reject";
        Long groupJoinId = 1L;

        given(groupJoinService.rejectGroupJoin(anyLong(), anyLong()))
                .willReturn(GroupJoinResponseDto.builder().groupJoinId(groupJoinId).build());

        //when & then
        mockMvc
                .perform(post(path, groupJoinId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}