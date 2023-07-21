package scs.planus.domain.group.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import scs.planus.domain.Status;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.support.RepositoryTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class GroupRepositoryTest {

    private final static int PAGE = 0;
    private final static int PAGE_SIZE = 5;
    private final static int COUNT = 7;

    @Autowired
    private GroupRepository groupRepository;

    @DisplayName("Group 조회시, 페이징 처리가 제대로 이루어져야 한다.")
    @Test
    void findAllByActiveOrderByNumOfMembersAndId() {
        //given
        Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

        for (int i = 0; i < COUNT; i++) {
            Group group = Group.builder().status(Status.ACTIVE).build();
            groupRepository.save(group);
        }

        //when
        List<Group> groups =
                groupRepository.findAllByActiveOrderByNumOfMembersAndId(pageable);

        //then
        assertThat(groups).hasSize(PAGE_SIZE);
    }

    @DisplayName("Group의 이름에 포함된 keyword를 통해 Group 조회가 이루어져야 한다.")
    @Test
    void findAllByKeywordAndActiveOrderByNumOfMembersAndId() {
        //given
        String keyword = "group1";
        Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

        for (int i = 0; i < COUNT; i++) {
            Group group = Group.builder()
                    .name("group" + i)
                    .status(Status.ACTIVE)
                    .build();
            groupRepository.save(group);
        }

        //when
        List<Group> groups
                = groupRepository.findAllByKeywordAndActiveOrderByNumOfMembersAndId(keyword, pageable);

        //then
        assertThat(groups).hasSize(1);
        assertThat(groups.get(0).getName()).contains("group1");
    }

    @DisplayName("해당 Group에 속하는 GroupMember와 fetch join한 Group이 조회되어야 한다.")
    @Test
    void findWithGroupMemberById() {
        //given
        Group group = Group.builder().status(Status.ACTIVE).build();
        groupRepository.save(group);

        for (int i = 0; i < COUNT; i++) {
            GroupMember.builder().group(group).build();
        }

        //when
        Group findGroup
                = groupRepository.findWithGroupMemberById(group.getId()).orElse(null);

        //then
        assertThat(findGroup).isNotNull();
        assertThat(findGroup.getGroupMembers()).hasSize(COUNT);
    }

    @DisplayName("groupId를 통해 Group이 조회되어야 한다.")
    @Test
    void findByIdAndStatus() {
        //given
        Group group = Group.builder().status(Status.ACTIVE).build();
        groupRepository.save(group);

        //when
        Group findGroup
                = groupRepository.findByIdAndStatus(group.getId()).orElse(null);

        //then
        assertThat(findGroup).isNotNull();
        assertThat(findGroup.getName()).isEqualTo(group.getName());
        assertThat(findGroup.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @DisplayName("Group의 status가 Inactive라면, groupId를 통해 Group이 조회되선 안된다.")
    @Test
    void findByIdAndStatus_Return_Null_If_Status_Is_Inactive() {
        Group group = Group.builder().status(Status.INACTIVE).build();
        groupRepository.save(group);

        //when
        Group findGroup
                = groupRepository.findByIdAndStatus(group.getId()).orElse(null);

        //then
        assertThat(findGroup).isNull();
    }
}