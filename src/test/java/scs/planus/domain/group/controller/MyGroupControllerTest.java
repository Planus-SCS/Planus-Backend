package scs.planus.domain.group.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import scs.planus.domain.group.dto.mygroup.MyGroupDetailResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupGetMemberResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupOnlineStatusResponseDto;
import scs.planus.domain.group.dto.mygroup.MyGroupResponseDto;
import scs.planus.domain.group.service.MyGroupService;
import scs.planus.support.ControllerTest;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MyGroupController.class)
class MyGroupControllerTest extends ControllerTest {

    @MockBean
    private MyGroupService myGroupService;

    @DisplayName("멤버가 속한 모든 Group들을 조회한다.")
    @Test
    void getMyGroups() throws Exception {
        //given
        String path = "/app/my-groups";

        List<MyGroupResponseDto> responseDtos = List.of(MyGroupResponseDto.builder()
                .groupId(1L)
                .groupImageUrl("groupImageUrl")
                .groupName("group1")
                .leaderName("groupLeader")
                .build());

        given(myGroupService.getMyAllGroups(anyLong()))
                .willReturn(responseDtos);

        //when & then
        mockMvc
                .perform(get(path)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("멤버가 속한 단일 Group을 조회한다.")
    @Test
    void getMyEachGroup() throws Exception {
        //given
        String path = "/app/my-groups/{groupId}";
        Long groupId = 1L;

        MyGroupDetailResponseDto responseDto = MyGroupDetailResponseDto.builder()
                .groupId(groupId)
                .groupImageUrl("groupImageUrl")
                .groupName("group1")
                .leaderName("groupLeader")
                .build();

        given(myGroupService.getMyEachGroupDetail(anyLong(), anyLong()))
                .willReturn(responseDto);

        //when & then
        mockMvc
                .perform(get(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("자신이 속한 Group의 GroupMember들을 조회한다.")
    @Test
    void getGroupMembersForMember() throws Exception {
        //given
        String path = "/app/my-groups/{groupId}/members";
        Long groupId = 1L;

        List<MyGroupGetMemberResponseDto> responseDtos = List.of(MyGroupGetMemberResponseDto.builder()
                .memberId(1L)
                .nickname("groupMember1")
                .isLeader(true)
                .build());

        given(myGroupService.getGroupMembers(anyLong(), anyLong()))
                .willReturn(responseDtos);

        //when & then
        mockMvc
                .perform(get(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Group의 온라인 상태를 변경한다.")
    @Test
    void updateOnlineStatus() throws Exception {
        //given
        String path = "/app/my-groups/{groupId}/online-status";
        Long groupId = 1L;

        given(myGroupService.changeOnlineStatus(anyLong(), anyLong()))
                .willReturn(MyGroupOnlineStatusResponseDto.builder().groupMemberId(1L).build());

        //when & then
        mockMvc
                .perform(patch(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}