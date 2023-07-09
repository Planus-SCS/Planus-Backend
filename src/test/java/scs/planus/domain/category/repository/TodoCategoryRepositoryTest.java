package scs.planus.domain.category.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import scs.planus.domain.Status;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestClassOrder(ClassOrderer.OrderAnnotation.class) // Group 을 먼저하면 Member 쪽 첫번째 테스트가 계속 실패함..
class TodoCategoryRepositoryTest {
    private static final Long NOT_EXIST_ID = 0L;
    @Autowired
    private TodoCategoryRepository todoCategoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupRepository groupRepository;

    @DisplayName("MemberTodoCategoryRepository 테스트")
    @Order(1)
    @Nested
    class MemberTodoCategoryRepositoryTest {
        private Member member;

        @BeforeEach
        void init() {
            member = Member.builder()
                    .name("회원")
                    .build();

            memberRepository.save(member);
        }

        @DisplayName("TodoCategoryId, Member 로 회원의 카테고리를 조회할 수 있어야 한다.")
        @Test
        void findMemberTodoCategoryByIdAndMember_Exist() {
            //given
            MemberTodoCategory testMemberTodoCategory = MemberTodoCategory.builder()
                    .member(member)
                    .name("회원카테고리")
                    .color(Color.BLUE)
                    .build();

            todoCategoryRepository.save(testMemberTodoCategory);

            //when
            MemberTodoCategory findMemberTodoCategory = todoCategoryRepository
                    .findMemberTodoCategoryByIdAndMember(testMemberTodoCategory.getId(), member.getId())
                    .orElse(null);

            //then
            assertThat(findMemberTodoCategory.getMember().getId()).isEqualTo(testMemberTodoCategory.getMember().getId());
            assertThat(findMemberTodoCategory.getName()).isEqualTo(testMemberTodoCategory.getName());
            assertThat(findMemberTodoCategory.getColor()).isEqualTo(testMemberTodoCategory.getColor());
        }

        @DisplayName("TodoCategoryId, Member 로 조회되는 회원의 카테고리가 없는 경우 Null 을 반환 해야 한다.")
        @Test
        void findMemberTodoCategoryByIdAndMember_Null() {
            //given
            //when
            MemberTodoCategory findMemberTodoCategory = todoCategoryRepository
                    .findMemberTodoCategoryByIdAndMember(NOT_EXIST_ID, member.getId())
                    .orElse(null);

            //then
            assertThat(findMemberTodoCategory).isNull();
        }

        @DisplayName("Member 로 회원의 모든 카테고리를 조회할 수 있어야 한다.")
        @Test
        void findMemberTodoCategoryAllByMember_Exist() {
            //given
            MemberTodoCategory testMemberTodoCategory1 = MemberTodoCategory.builder()
                    .member(member)
                    .name("회원카테고리1")
                    .color(Color.BLUE)
                    .build();

            MemberTodoCategory testMemberTodoCategory2 = MemberTodoCategory.builder()
                    .member(member)
                    .name("회원카테고리2")
                    .color(Color.RED)
                    .build();

            todoCategoryRepository.save(testMemberTodoCategory1);
            todoCategoryRepository.save(testMemberTodoCategory2);

            //when
            List<MemberTodoCategory> findMemberTodoCategoryList = todoCategoryRepository.findMemberTodoCategoryAllByMember(member);

            //then
            assertThat(findMemberTodoCategoryList.size()).isEqualTo(2);
            assertThat(findMemberTodoCategoryList.get(0).getName()).isEqualTo(testMemberTodoCategory1.getName());
            assertThat(findMemberTodoCategoryList.get(1).getName()).isEqualTo(testMemberTodoCategory2.getName());

        }

        @DisplayName("Member 로 조회되는 회원의 카테고리가 존재하지 않는 경우 빈 리스트를 반환 해야 한다.")
        @Test
        void findMemberTodoCategoryAllByMember_Empty() {
            //given
            //when
            List<MemberTodoCategory> findMemberTodoCategory = todoCategoryRepository.findMemberTodoCategoryAllByMember(member);

            //then
            assertThat(findMemberTodoCategory.size()).isEqualTo(0);
        }
    }

    @DisplayName("GroupTodoCategoryRepository 테스트")
    @Order(2)
    @Nested
    class GroupTodoCategoryRepositoryTest {
        private Group group;

        @BeforeEach
        void init() {
            group = Group.builder()
                    .name("그룹")
                    .status(Status.ACTIVE)
                    .build();

            groupRepository.save(group);
        }
        @DisplayName("Group 으로 그룹의 모든 카테고리를 조회할 수 있어야 한다.")
        @Test
        void findGroupTodoCategoryAllByGroup_Exist() {
            //given
            GroupTodoCategory testGroupTodoCategory1 = GroupTodoCategory.builder()
                    .group(group)
                    .name("그룹카테고리1")
                    .color(Color.BLUE)
                    .build();

            GroupTodoCategory testGroupTodoCategory2 = GroupTodoCategory.builder()
                    .group(group)
                    .name("그룹카테고리2")
                    .color(Color.RED)
                    .build();

            todoCategoryRepository.save(testGroupTodoCategory1);
            todoCategoryRepository.save(testGroupTodoCategory2);

            //when
            List<GroupTodoCategory> findGroupTodoCategoryList = todoCategoryRepository
                    .findGroupTodoCategoryAllByGroup(group.getId());

            //then
            assertThat(findGroupTodoCategoryList.size()).isEqualTo(2);
            assertThat(findGroupTodoCategoryList.get(0).getName()).isEqualTo(testGroupTodoCategory1.getName());
            assertThat(findGroupTodoCategoryList.get(1).getName()).isEqualTo(testGroupTodoCategory2.getName());
        }

        @DisplayName("Group 으로 조회되는 그룹의 카테고리가 존재하지 않는 경우 빈 리스트를 반환 해야 한다.")
        @Test
        void findGroupTodoCategoryAllByGroup_Empty() {
            //given
            //when
            List<GroupTodoCategory> findGroupTodoCategoryList = todoCategoryRepository
                    .findGroupTodoCategoryAllByGroup(group.getId());

            //then
            assertThat(findGroupTodoCategoryList.size()).isEqualTo(0);
        }

        @DisplayName("TodoCategoryId 로 그룹의 카테고리를 조회할 수 있어야 한다.")
        @Test
        void findGroupTodoCategoryByIdAndStatus_Exist() {
            //given
            GroupTodoCategory testGroupTodoCategory = GroupTodoCategory.builder()
                    .group(group)
                    .name("그룹카테고리")
                    .color(Color.BLUE)
                    .build();

            todoCategoryRepository.save(testGroupTodoCategory);

            //when
            GroupTodoCategory findGroupTodoCategory = todoCategoryRepository
                    .findGroupTodoCategoryByIdAndStatus(testGroupTodoCategory.getId())
                    .orElse(null);

            //then
            assertThat(findGroupTodoCategory.getGroup()).isEqualTo(testGroupTodoCategory.getGroup());
            assertThat(findGroupTodoCategory.getName()).isEqualTo(testGroupTodoCategory.getName());
            assertThat(findGroupTodoCategory.getColor()).isEqualTo(testGroupTodoCategory.getColor());
        }

        @DisplayName("TodoCategoryId 로 조회되는 그룹의 카테고리가 없는 경우 Null 을 반환 해야 한다.")
        @Test
        void findGroupTodoCategoryByIdAndStatus_Null() {
            //given
            //when
            GroupTodoCategory findGroupTodoCategory = todoCategoryRepository
                    .findGroupTodoCategoryByIdAndStatus(NOT_EXIST_ID)
                    .orElse(null);

            //then
            assertThat(findGroupTodoCategory).isNull();
        }

        @DisplayName("List<Group> 으로 각 그룹의 모든 카테고리를 조회할 수 있어야 한다.")
        @Test
        void findAllGroupTodoCategoriesInGroups_Exist() {
            //given
            Group group2 = Group.builder()
                    .name("그룹2")
                    .status(Status.ACTIVE).build();

            groupRepository.save(group2);

            GroupTodoCategory testGroup1TodoCategory = GroupTodoCategory.builder()
                    .group(group)
                    .name("그룹1카테고리")
                    .color(Color.BLUE)
                    .build();

            GroupTodoCategory testGroup2TodoCategory = GroupTodoCategory.builder()
                    .group(group2)
                    .name("그룹2카테고리")
                    .color(Color.RED)
                    .build();

            todoCategoryRepository.save(testGroup1TodoCategory);
            todoCategoryRepository.save(testGroup2TodoCategory);

            List<Group> groupList = new ArrayList<>();
            groupList.add(group);
            groupList.add(group2);

            //when
            List<GroupTodoCategory> findAllGroupTodoCategoriesList = todoCategoryRepository
                    .findAllGroupTodoCategoriesInGroups(groupList);

            //then
            assertThat(findAllGroupTodoCategoriesList.size()).isEqualTo(2);
            assertThat(findAllGroupTodoCategoriesList.get(0).getGroup()).isEqualTo(group);
            assertThat(findAllGroupTodoCategoriesList.get(1).getGroup()).isEqualTo(group2);
        }

        @DisplayName("List<Group> 으로 조회되는 그룹의 카테고리가 존재하지 않는 경우 빈 리스트를 반환 해야 한다.")
        @Test
        void findAllGroupTodoCategoriesInGroups_Empty() {
            //given
            Group group2 = Group.builder()
                    .name("그룹2")
                    .status(Status.ACTIVE)
                    .build();

            groupRepository.save(group2);

            List<Group> groupList = new ArrayList<>();
            groupList.add(group);
            groupList.add(group2);

            //when
            List<GroupTodoCategory> findAllGroupTodoCategoriesList = todoCategoryRepository
                    .findAllGroupTodoCategoriesInGroups(groupList);

            //then
            assertThat(findAllGroupTodoCategoriesList.size()).isEqualTo(0);
        }
    }
}