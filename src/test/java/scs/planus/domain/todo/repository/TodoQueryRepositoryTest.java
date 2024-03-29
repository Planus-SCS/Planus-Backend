package scs.planus.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.Status;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.entity.Todo;
import scs.planus.support.RepositoryTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TodoQueryRepositoryTest extends RepositoryTest {

    private static final int COUNT = 7;

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final TodoCategoryRepository todoCategoryRepository;
    private final TodoRepository todoRepository;

    private final TodoQueryRepository todoQueryRepository;

    private Member member;
    private Todo todo;
    private Group group;
    private GroupTodoCategory groupTodoCategory;

    @Autowired
    public TodoQueryRepositoryTest(MemberRepository memberRepository, GroupRepository groupRepository,
                                   TodoCategoryRepository todoCategoryRepository, TodoRepository todoRepository,
                                   JPAQueryFactory queryFactory) {
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
        this.todoCategoryRepository = todoCategoryRepository;
        this.todoRepository = todoRepository;

        todoQueryRepository = new TodoQueryRepository(queryFactory);
    }

    @Nested
    @DisplayName("MemberTodoQuery 테스트")
    class MemberTodoQueryTest {

        @BeforeEach
        void init() {
            member = memberRepository.findById(1L).orElseThrow();
        }

        @DisplayName("단일 멤버 투두가 조회되어야 한다.")
        @Test
        void findOneMemberTodoById() {
            //given
            todo = MemberTodo.builder()
                    .title("title")
                    .member(member)
                    .build();
            todoRepository.save(todo);

            //when
            MemberTodo findTodo =
                    todoQueryRepository.findOneMemberTodoById(member.getId(), todo.getId()).orElse(null);

            //then
            assertThat(findTodo).isNotNull();
            assertThat(findTodo.getTitle()).isEqualTo(todo.getTitle());
            assertThat(findTodo.getMember()).isEqualTo(member);
        }

        @DisplayName("기간 내의 멤버 투두가 리스트 형식으로 조회되어야 한다.")
        @Test
        void findAllPeriodMemberTodosByDate() {
            //given
            LocalDate date = LocalDate.of(2023, 1, 1);

            for (int i = 0; i < COUNT; i++) {
                todo = MemberTodo.builder()
                        .startDate(date.plusDays(i))
                        .member(member)
                        .build();
                todoRepository.save(todo);
            }

            //when
            LocalDate from = LocalDate.of(2023, 1, 1);
            LocalDate to = LocalDate.of(2023, 1, 7);

            List<MemberTodo> memberTodos =
                    todoQueryRepository.findAllPeriodMemberTodosByDate(member.getId(), from, to);

            //then
            assertThat(memberTodos).hasSize(COUNT);
        }
    }

    @Nested
    @DisplayName("GroupTodoQuery 테스트")
    class GroupTodoQueryTest {

        @BeforeEach
        void init() {
            group = groupRepository.findById(1L).orElseThrow();
            groupTodoCategory = (GroupTodoCategory) todoCategoryRepository.findById(2L).orElseThrow();
        }

        @DisplayName("단일 그룹 투두가 조회되어야 한다.")
        @Test
        void findOneGroupTodoById() {
            //given
            todo = GroupTodo.builder()
                    .todoCategory(groupTodoCategory)
                    .group(group)
                    .build();
            todoRepository.save(todo);

            //when
            GroupTodo findTodo =
                    todoQueryRepository.findOneGroupTodoById(group.getId(), todo.getId()).orElse(null);

            //then
            assertThat(findTodo).isNotNull();
            assertThat(findTodo.getGroup()).isEqualTo(group);
            assertThat(findTodo.getTodoCategory()).isEqualTo(groupTodoCategory);
        }

        @DisplayName("기간 내의 그룹 투두가 리스트 형식으로 조회되어야 한다.")
        @Test
        void findPeriodGroupTodosByDate() {
            //given
            LocalDate date = LocalDate.of(2023, 1, 1);

            for (int i = 0; i < COUNT; i++) {
                todo = GroupTodo.builder()
                        .startDate(date.plusDays(i))
                        .todoCategory(groupTodoCategory)
                        .group(group)
                        .build();
                todoRepository.save(todo);
            }

            //when
            LocalDate from = LocalDate.of(2023, 1, 1);
            LocalDate to = LocalDate.of(2023, 1, 7);

            List<GroupTodo> groupTodos =
                    todoQueryRepository.findPeriodGroupTodosByDate(group.getId(), from, to);

            //then
            assertThat(groupTodos).hasSize(COUNT);
        }

        @DisplayName("일별 그룹 투두가 존재한다면 리스트 형식으로 조회되어야 한다.")
        @Test
        void findDailyGroupTodosByDate() {
            //given
            LocalDate date = LocalDate.of(2023, 1, 1);

            todo = GroupTodo.builder()
                    .startDate(date)
                    .todoCategory(groupTodoCategory)
                    .group(group)
                    .build();
            todoRepository.save(todo);

            //when
            List<GroupTodo> groupTodos
                    = todoQueryRepository.findDailyGroupTodosByDate(group.getId(), date);

            //then
            assertThat(groupTodos).hasSize(1);
        }

        @DisplayName("일별 조회시, 기간 투두의 경우 기간내에 조회하고자하는 일이 포함된다면 조회되어야 한다.")
        @Test
        void findDailyGroupTodosByDate_Period_Todo() {
            //given
            LocalDate date = LocalDate.of(2022, 12, 31);

            todo = GroupTodo.builder()
                    .startDate(date)
                    .endDate(date.plusDays(5))
                    .todoCategory(groupTodoCategory)
                    .group(group)
                    .build();
            todoRepository.save(todo);

            //when
            List<GroupTodo> groupTodos
                    = todoQueryRepository.findDailyGroupTodosByDate(group.getId(), date);

            //then
            assertThat(groupTodos).hasSize(1);
        }

        @DisplayName("기간 내의 여러 그룹의 그룹 투두들이 반환되어야 한다.")
        @Test
        void findAllPeriodGroupTodosByDate() {
            //given
            LocalDate date = LocalDate.of(2023, 1, 1);

            Group group2 = Group.builder().status(Status.ACTIVE).build();
            groupRepository.save(group2);

            for (int i = 0; i < COUNT; i++) {
                todo = GroupTodo.builder()
                        .startDate(date)
                        .endDate(date.plusDays(i))
                        .todoCategory(groupTodoCategory)
                        .group(group)
                        .build();
                todoRepository.save(todo);

                todo = GroupTodo.builder()
                        .startDate(date)
                        .endDate(date.plusDays(i))
                        .todoCategory(groupTodoCategory)
                        .group(group2)
                        .build();
                todoRepository.save(todo);
            }

            //when
            LocalDate from = LocalDate.of(2023, 1, 1);
            LocalDate to = LocalDate.of(2023, 1, 10);
            List<Group> groups = List.of(group, group2);

            List<GroupTodo> groupTodos
                    = todoQueryRepository.findAllPeriodGroupTodosByDate(groups, from, to);

            //then
            assertThat(groupTodos.size()).isEqualTo(COUNT * 2);
        }
    }

    @Nested
    @DisplayName("GroupMemberTodoQuery 테스트")
    class GroupMemberTodoQueryTest {

        @BeforeEach
        void init() {
            member = memberRepository.findById(1L).orElseThrow();
            group = groupRepository.findById(1L).orElseThrow();
        }

        @DisplayName("같은 그룹 멤버의 해당 그룹 투두가 조회되어야 한다.")
        @Test
        void findOneGroupMemberTodoById_GroupTodo() {
            //given
            groupTodoCategory = GroupTodoCategory.builder()
                    .group(group)
                    .build();
            todoCategoryRepository.save(groupTodoCategory);

            todo = GroupTodo.builder()
                    .todoCategory(groupTodoCategory)
                    .group(group)
                    .build();
            todoRepository.save(todo);

            //when
            Todo findTodo
                    = todoQueryRepository.findOneGroupMemberTodoById(member.getId(), group.getId(), todo.getId()).orElse(null);

            //then
            assertThat(findTodo).isNotNull();
            assertThat(findTodo).isEqualTo(todo);
        }

        @DisplayName("같은 그룹 멤버가 해당 그룹을 지정한 개인 투두가 조회되어야 한다.")
        @Test
        void findOneGroupMemberTodoById_MemberTodo() {
            //given
            MemberTodoCategory memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElseThrow();

            todo = MemberTodo.builder()
                    .todoCategory(memberTodoCategory)
                    .member(member)
                    .group(group)
                    .build();
            todoRepository.save(todo);

            //when
            Todo findTodo
                    = todoQueryRepository.findOneGroupMemberTodoById(member.getId(), group.getId(), todo.getId()).orElse(null);

            //then
            assertThat(findTodo).isNotNull();
            assertThat(findTodo).isEqualTo(todo);
        }

        @DisplayName("같은 그룹 멤버가 해당 그룹을 지정하지 않은 개인 투두는 조회되서는 안된다.")
        @Test
        void findOneGroupMemberTodoById_MemberTodo_Fail_If_Not_Assign_Group() {
            //given
            MemberTodoCategory memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElseThrow();

            todo = MemberTodo.builder()
                    .todoCategory(memberTodoCategory)
                    .member(member)
                    .build();
            todoRepository.save(todo);

            //when
            Todo findTodo
                    = todoQueryRepository.findOneGroupMemberTodoById(member.getId(), group.getId(), todo.getId()).orElse(null);

            //then
            assertThat(findTodo).isNull();
        }

        @DisplayName("기간 내의 그룹 멤버 투두가 리스트 형식으로 조회되어야 한다.")
        @Test
        void findPeriodGroupTodosByDate() {
            //given
            LocalDate date = LocalDate.of(2023, 1, 1);
            MemberTodoCategory memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElseThrow();

            for (int i = 0; i < COUNT; i++) {
                todo = MemberTodo.builder()
                        .startDate(date.plusDays(i))
                        .todoCategory(memberTodoCategory)
                        .member(member)
                        .group(group)
                        .build();
                todoRepository.save(todo);
            }

            //when
            LocalDate from = LocalDate.of(2023, 1, 1);
            LocalDate to = LocalDate.of(2023, 1, 7);

            List<Todo> groupMemberTodos =
                    todoQueryRepository.findGroupMemberPeriodTodosByDate(member.getId(), group.getId(), from, to);

            //then
            assertThat(groupMemberTodos).hasSize(COUNT);
        }

        @DisplayName("일별 그룹 멤버 투두가 존재한다면 리스트 형식으로 조회되어야 한다.")
        @Test
        void findGroupMemberDailyTodosByDate() {
            //given
            LocalDate date = LocalDate.of(2023, 1, 1);
            MemberTodoCategory memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElseThrow();

            todo = MemberTodo.builder()
                    .startDate(date)
                    .todoCategory(memberTodoCategory)
                    .member(member)
                    .group(group)
                    .build();
            todoRepository.save(todo);

            //when
            List<Todo> groupMemberTodos
                    = todoQueryRepository.findGroupMemberDailyTodosByDate(member.getId(), group.getId(), date);

            //then
            assertThat(groupMemberTodos).hasSize(1);
        }
    }
}