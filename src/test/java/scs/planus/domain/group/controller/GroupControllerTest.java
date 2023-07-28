package scs.planus.domain.group.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import scs.planus.domain.group.dto.GroupCreateRequestDto;
import scs.planus.domain.group.dto.GroupDetailUpdateRequestDto;
import scs.planus.domain.group.dto.GroupGetDetailResponseDto;
import scs.planus.domain.group.dto.GroupGetMemberResponseDto;
import scs.planus.domain.group.dto.GroupMemberResponseDto;
import scs.planus.domain.group.dto.GroupNoticeUpdateRequestDto;
import scs.planus.domain.group.dto.GroupResponseDto;
import scs.planus.domain.group.dto.GroupsGetResponseDto;
import scs.planus.domain.group.service.GroupService;
import scs.planus.support.ControllerTest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupController.class)
class GroupControllerTest extends ControllerTest {

    @MockBean
    private GroupService groupService;

    @DisplayName("Group을 생성한다.")
    @Test
    void createGroup() throws Exception {
        //given
        String path = "/app/groups";

        MockMultipartFile image = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());

        GroupCreateRequestDto requestDto = GroupCreateRequestDto.builder()
                .name("group")
                .limitCount(30)
                .build();
        String dtoToJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile groupCreateRequestDto = new MockMultipartFile("groupCreateRequestDto", "", "application/json", dtoToJson.getBytes(StandardCharsets.UTF_8));

        given(groupService.createGroup(anyLong(), any(GroupCreateRequestDto.class), any(MockMultipartFile.class)))
                .willReturn(GroupResponseDto.builder().groupId(1L).build());

        //when & then
        mockMvc
                .perform(multipart(HttpMethod.POST, path)
                        .file(image)
                        .file(groupCreateRequestDto)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Group을 생성 시, 검증조건을 만족하지 못하면 예외를 던진다.")
    @Test
    void createGroup_Throw_Exception_If_Not_Validated_Request() throws Exception {
        //given
        String path = "/app/groups";

        MockMultipartFile image = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());

        GroupCreateRequestDto requestDto = GroupCreateRequestDto.builder()
                .name("A".repeat(21))
                .limitCount(51)
                .build();
        String dtoToJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile groupCreateRequestDto = new MockMultipartFile("groupCreateRequestDto", "", "application/json", dtoToJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc
                .perform(multipart(HttpMethod.POST, path)
                        .file(image)
                        .file(groupCreateRequestDto)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Group의 상세정보를 조회한다.")
    @Test
    void getGroupDetail() throws Exception {
        //given
        String path = "/app/groups/{groupId}";
        Long groupId = 1L;

        GroupGetDetailResponseDto responseDto = GroupGetDetailResponseDto.builder()
                .id(groupId)
                .name("group1")
                .memberCount(5)
                .limitCount(10)
                .leaderName("groupLeader")
                .build();

        given(groupService.getGroupDetail(anyLong(), anyLong()))
                .willReturn(responseDto);

        //when & then
        mockMvc
                .perform(get(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Group에 속한 GroupMember를 조회한다.")
    @Test
    void getGroupMember() throws Exception {
        //given
        String path = "/app/groups/{groupId}/members";
        Long groupId = 1L;

        List<GroupGetMemberResponseDto> responseDto = List.of(GroupGetMemberResponseDto.builder()
                .memberId(1L)
                .nickname("groupMember1")
                .isLeader(true)
                .build());

        given(groupService.getGroupMember(anyLong()))
                .willReturn(responseDto);

        //when & then
        mockMvc
                .perform(get(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Group 상세정보를 수정한다.")
    @Test
    void updateGroupDetail() throws Exception {
        //given
        String path = "/app/groups/{groupid}";
        Long groupId = 1L;

        MockMultipartFile image = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());

        GroupDetailUpdateRequestDto requestDto = GroupDetailUpdateRequestDto.builder()
                .limitCount(50)
                .build();
        String dtoToJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile groupUpdateRequestDto = new MockMultipartFile("groupUpdateRequestDto", "", "application/json", dtoToJson.getBytes(StandardCharsets.UTF_8));

        given(groupService.updateGroupDetail(anyLong(), anyLong(), any(GroupDetailUpdateRequestDto.class), any(MockMultipartFile.class)))
                .willReturn(GroupResponseDto.builder().groupId(groupId).build());

        //when & then
        mockMvc
                .perform(multipart(HttpMethod.PATCH, path, groupId)
                        .file(image)
                        .file(groupUpdateRequestDto)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Group 상세정보를 수정시, 검증조건을 만족하지 못하면 예외를 던진다.")
    @Test
    void updateGroupDetail_Throw_Exception_If_Not_Validated_Request() throws Exception {
        //given
        String path = "/app/groups/{groupid}";
        Long groupId = 1L;

        MockMultipartFile image = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());

        GroupDetailUpdateRequestDto requestDto = GroupDetailUpdateRequestDto.builder()
                .limitCount(51)
                .build();
        String dtoToJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile groupUpdateRequestDto = new MockMultipartFile("groupUpdateRequestDto", "", "application/json", dtoToJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc
                .perform(multipart(HttpMethod.PATCH, path, groupId)
                        .file(image)
                        .file(groupUpdateRequestDto)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Group의 공지사항을 변경한다.")
    @Test
    void updateGroupNotice() throws Exception {
        //given
        String path = "/app/groups/{groupId}/notice";
        Long groupId = 1L;

        GroupNoticeUpdateRequestDto requestDto = GroupNoticeUpdateRequestDto.builder()
                .notice("new Notice")
                .build();

        given(groupService.updateGroupNotice(anyLong(), anyLong(), any(GroupNoticeUpdateRequestDto.class)))
                .willReturn(GroupResponseDto.builder().groupId(groupId).build());

        //when & then
        mockMvc
                .perform(patch(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Group을 삭제한다.")
    @Test
    void softDeleteGroup() throws Exception {
        //given
        String path = "/app/groups/{groupId}";
        Long groupId = 1L;

        given(groupService.softDeleteGroup(anyLong(), anyLong()))
                .willReturn(GroupResponseDto.builder().groupId(groupId).build());

        //when & then
        mockMvc
                .perform(delete(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("GroupMember를 Group에서 강퇴시킨다")
    @Test
    void withdrawGroupMember() throws Exception {
        //given
        String path = "/app/groups/{groupId}/members/{memberId}";
        Long groupId = 1L;
        Long memberId = 1L;

        given(groupService.withdrawGroupMember(anyLong(), anyLong(), anyLong()))
                .willReturn(GroupMemberResponseDto.builder().groupMemberId(memberId).build());

        //when & then
        mockMvc
                .perform(delete(path, groupId, memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("GroupMember가 Group을 자발적으로 탈퇴한다.")
    @Test
    void withdraw() throws Exception {
        //given
        String path = "/app/groups/{groupId}/withdraw";
        Long groupId = 1L;

        given(groupService.softWithdraw(anyLong(), anyLong()))
                .willReturn(GroupMemberResponseDto.builder().groupMemberId(1L).build());

        //when & then
        mockMvc
                .perform(delete(path, groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Nested
    @DisplayName("Group Search 테스트")
    class GroupSearch {

        private List<GroupsGetResponseDto> responseDtos;

        @BeforeEach
        void init() {
            responseDtos = List.of(GroupsGetResponseDto.builder()
                    .groupId(1L)
                    .name("group1")
                    .groupImageUrl("groupImageUrl")
                    .memberCount(5)
                    .limitCount(10)
                    .leaderId(1L)
                    .leaderName("groupLeader")
                    .build());
        }

        @DisplayName("모든 Group들을 조회한다.")
        @Test
        void getGroupsSearchHome() throws Exception {
            //given
            String path = "/app/groups";

            given(groupService.getGroupsSearchHome(any(Pageable.class)))
                    .willReturn(responseDtos);

            //when & then
            mockMvc
                    .perform(get(path)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("Group 이름에 속하는 단어로 Group을 검색하여 조회한다.")
        @Test
        void getGroupsSearch() throws Exception {
            //given
            String path = "/app/groups/search";
            String keyword = "group";

            given(groupService.getGroupsSearchByKeyword(anyString(), any(Pageable.class)))
                    .willReturn(responseDtos);

            //when & then
            mockMvc
                    .perform(get(path)
                            .param("keyword", keyword)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}