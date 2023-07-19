package scs.planus.domain.category.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.Status;
import scs.planus.domain.category.entity.Color;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.support.RepositoryTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RepositoryTest
class TodoCategoryRepositoryTest {
    private static final Long NOT_EXIST_ID = 0L;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private TodoCategoryRepository todoCategoryRepository;

    @DisplayName("MemberTodoCategoryRepository 테스트")
    @Nested
    class MemberTodoCategoryRepositoryTest {
        private Member member;

        @BeforeEach
        void init() {
            member = Member.builder()
                    .build();

            memberRepository.save(member);
        }

        @DisplayName("TodoCategoryId, Member 로 회원의 카테고리를 조회할 수 있어야 한다.")
        @Test
        void findMemberTodoCategoryByIdAndMember_Exist() {
            //given
            MemberTodoCategory memberTodoCategory = MemberTodoCategory.builder()
                    .member(member)
                    .name("회원카테고리")
                    .color(Color.BLUE)
                    .build();

            todoCategoryRepository.save(memberTodoCategory);

            //when
            MemberTodoCategory findMemberTodoCategory = todoCategoryRepository
                    .findMemberTodoCategoryByMemberIdAndId(member.getId(), memberTodoCategory.getId())
                    .orElseThrow();

            //then
            assertThat(findMemberTodoCategory.getMember().getId()).isEqualTo(memberTodoCategory.getMember().getId());
            assertThat(findMemberTodoCategory.getName()).isEqualTo(memberTodoCategory.getName());
            assertThat(findMemberTodoCategory.getColor()).isEqualTo(memberTodoCategory.getColor());
        }

        @DisplayName("TodoCategoryId, Member 로 조회되는 회원의 카테고리가 없는 경우 Null 을 반환 해야 한다.")
        @Test
        void findMemberTodoCategoryByIdAndMember_Null() {
            //given
            //when
            MemberTodoCategory findMemberTodoCategory = todoCategoryRepository
                    .findMemberTodoCategoryByMemberIdAndId(NOT_EXIST_ID, member.getId())
                    .orElse(null);

            //then
            assertThat(findMemberTodoCategory).isNull();
        }

        @DisplayName("Member 로 회원의 모든 카테고리를 조회할 수 있어야 한다.")
        @Test
        void findMemberTodoCategoryAllByMember_Exist() {
            //given
            MemberTodoCategory memberTodoCategory1 = MemberTodoCategory.builder()
                    .member(member)
                    .build();

            MemberTodoCategory memberTodoCategory2 = MemberTodoCategory.builder()
                    .member(member)
                    .build();

            todoCategoryRepository.saveAll(List.of(memberTodoCategory1, memberTodoCategory2));

            //when
            List<MemberTodoCategory> findMemberTodoCategories = todoCategoryRepository.findMemberTodoCategoryAllByMember(member);

            //then
            assertThat(findMemberTodoCategories).hasSize(2);
        }
    }

    @DisplayName("GroupTodoCategoryRepository 테스트")
    @Nested
    class GroupTodoCategoryRepositoryTest {
        private Group group;

        @BeforeEach
        void init() {
            group = Group.builder()
                    .build();

            groupRepository.save(group);
        }
        @DisplayName("GroupId 으로 그룹의 모든 카테고리를 조회할 수 있어야 한다.")
        @Test
        void findGroupTodoCategoryAllByGroup_Exist() {
            //given
            GroupTodoCategory groupTodoCategory1 = GroupTodoCategory.builder()
                    .group(group)
                    .build();

            GroupTodoCategory groupTodoCategory2 = GroupTodoCategory.builder()
                    .group(group)
                    .build();

            todoCategoryRepository.saveAll(List.of(groupTodoCategory1, groupTodoCategory2));

            //when
            List<GroupTodoCategory> findGroupTodoCategories = todoCategoryRepository.findGroupTodoCategoryAllByGroup(group.getId());

            //then
            assertThat(findGroupTodoCategories).hasSize(2);
        }

        @DisplayName("TodoCategoryId 로 그룹의 카테고리를 조회할 수 있어야 한다.")
        @Test
        void findGroupTodoCategoryByIdAndStatus_Exist() {
            //given
            GroupTodoCategory groupTodoCategory = GroupTodoCategory.builder()
                    .group(group)
                    .name("그룹카테고리")
                    .color(Color.BLUE)
                    .build();

            todoCategoryRepository.save(groupTodoCategory);

            //when
            GroupTodoCategory findGroupTodoCategory = todoCategoryRepository
                    .findGroupTodoCategoryByIdAndStatus(groupTodoCategory.getId())
                    .orElseThrow();

            //then
            assertThat(findGroupTodoCategory.getGroup()).isEqualTo(groupTodoCategory.getGroup());
            assertThat(findGroupTodoCategory.getName()).isEqualTo(groupTodoCategory.getName());
            assertThat(findGroupTodoCategory.getColor()).isEqualTo(groupTodoCategory.getColor());
        }

        @DisplayName("TodoCategoryId 로 조회되는 그룹의 카테고리가 없는 경우 Null 을 반환 해야 한다.")
        @Test
        void findGroupTodoCategoryByIdAndStatus_Null() {
            //given
            GroupTodoCategory groupTodoCategory = GroupTodoCategory.builder()
                    .group(group)
                    .build();

            todoCategoryRepository.save(groupTodoCategory);
            groupTodoCategory.changeStatusToInactive();

            //when
            GroupTodoCategory findGroupTodoCategory1 = todoCategoryRepository
                    .findGroupTodoCategoryByIdAndStatus(groupTodoCategory.getId())
                    .orElse(null);

            GroupTodoCategory findGroupTodoCategory2 = todoCategoryRepository
                    .findGroupTodoCategoryByIdAndStatus(NOT_EXIST_ID)
                    .orElse(null);

            //then
            assertThat(findGroupTodoCategory1).isNull();
            assertThat(findGroupTodoCategory2).isNull();
        }

        @DisplayName("List<Group> 으로 각 그룹의 모든 카테고리를 조회할 수 있어야 한다.")
        @Test
        void findAllGroupTodoCategoriesInGroups_Exist() {
            //given
            Group group2 = Group.builder()
                    .name("그룹2")
                    .status(Status.ACTIVE).build();

            groupRepository.save(group2);

            GroupTodoCategory group1TodoCategory = GroupTodoCategory.builder()
                    .group(group)
                    .build();

            GroupTodoCategory group2TodoCategory = GroupTodoCategory.builder()
                    .group(group2)
                    .build();

            todoCategoryRepository.saveAll(List.of(group1TodoCategory, group2TodoCategory));

            List<Group> groups = List.of(group, group2);

            //when
            List<GroupTodoCategory> findAllGroupTodoCategories = todoCategoryRepository
                    .findAllGroupTodoCategoriesInGroups(groups);

            //then
            assertThat(findAllGroupTodoCategories).hasSize(2);
            assertThat(findAllGroupTodoCategories.get(0).getGroup()).isEqualTo(group);
            assertThat(findAllGroupTodoCategories.get(1).getGroup()).isEqualTo(group2);
        }
    }
}