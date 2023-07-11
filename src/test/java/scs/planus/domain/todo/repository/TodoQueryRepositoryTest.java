package scs.planus.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import scs.planus.domain.Status;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.entity.TodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.entity.Todo;
import scs.planus.global.config.QueryDslConfig;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class)
@Slf4j
class TodoQueryRepositoryTest {

    private TodoQueryRepository todoQueryRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoCategoryRepository todoCategoryRepository;

    private Member member;

    private Todo todo;

    private Group group;

    private TodoCategory todoCategory;

    @BeforeEach
    void initRepository() {
        todoQueryRepository = new TodoQueryRepository(queryFactory);
    }

    @Nested
    @DisplayName("MemberTodoQuery 테스트")
    class MemberTodoQueryTest {

        @BeforeEach
        void init() {
            member = Member.builder()
                    .status(Status.ACTIVE)
                    .build();

            memberRepository.save(member);
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
        void findAllPeriodMemberTodosByDate(){
            //given
            LocalDate date = LocalDate.of(2023, 1, 1);

            for (int i = 0; i < 10; i++) {
                todo = MemberTodo.builder()
                        .startDate(date.plusDays(i))
                        .member(member)
                        .build();

                todoRepository.save(todo);
            }

            //when
            LocalDate from = LocalDate.of(2023, 1, 1);
            LocalDate to = LocalDate.of(2023, 1, 5);
            List<MemberTodo> memberTodos =
                    todoQueryRepository.findAllPeriodMemberTodosByDate(member.getId(), from, to);

            //then
            assertThat(memberTodos.size()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("GroupTodoQuery 테스트")
    class GroupTodoQueryTest {

        @BeforeEach
        void init() {
            group = Group.builder()
                    .status(Status.ACTIVE)
                    .build();

            groupRepository.save(group);

            todoCategory = GroupTodoCategory.builder()
                    .group(group)
                    .build();

            todoCategoryRepository.save(todoCategory);
        }

        @DisplayName("단일 그룹 투두가 조회되어야 한다.")
        @Test
        void findOneGroupTodoById() {
            //given
            todo = GroupTodo.builder()
                    .todoCategory(todoCategory)
                    .group(group)
                    .build();

            todoRepository.save(todo);

            //when
            GroupTodo findTodo =
                    todoQueryRepository.findOneGroupTodoById(group.getId(), todo.getId()).orElse(null);

            //then
            assertThat(findTodo).isNotNull();
            assertThat(findTodo.getGroup()).isEqualTo(group);
            assertThat(findTodo.getTodoCategory()).isEqualTo(todoCategory);
        }

        @DisplayName("기간 내의 그룹 투두가 리스트 형식으로 조회되어야 한다.")
        @Test
        void findPeriodGroupTodosByDate(){
            //given
            LocalDate date = LocalDate.of(2023, 1, 1);

            for (int i = 0; i < 10; i++) {
                todo = GroupTodo.builder()
                        .startDate(date.plusDays(i))
                        .todoCategory(todoCategory)
                        .group(group)
                        .build();

                todoRepository.save(todo);
            }

            //when
            LocalDate from = LocalDate.of(2023, 1, 1);
            LocalDate to = LocalDate.of(2023, 1, 5);
            List<GroupTodo> groupTodos =
                    todoQueryRepository.findPeriodGroupTodosByDate(group.getId(), from, to);

            //then
            assertThat(groupTodos.size()).isEqualTo(5);
        }

        @DisplayName("일별 그룹 투두가 존재한다면 리스트 형식으로 조회되어야 한다.")
        @Test
        void findDailyGroupTodosByDate(){
            //given
            LocalDate date = LocalDate.of(2023, 1, 1);

            todo = GroupTodo.builder()
                    .startDate(date)
                    .todoCategory(todoCategory)
                    .group(group)
                    .build();

            todoRepository.save(todo);

            //when
            List<GroupTodo> groupTodos = todoQueryRepository.findDailyGroupTodosByDate(group.getId(), date);

            //then
            assertThat(groupTodos.size()).isEqualTo(1);
        }

        @DisplayName("일별 조회시, 기간 투두의 경우 기간내에 조회하고자하는 일이 포함된다면 조회되어야 한다.")
        @Test
        void findDailyGroupTodosByDate_Period_Todo(){
            //given
            LocalDate date = LocalDate.of(2022, 12, 31);

            todo = GroupTodo.builder()
                    .startDate(date)
                    .endDate(date.plusDays(5))
                    .todoCategory(todoCategory)
                    .group(group)
                    .build();

            todoRepository.save(todo);

            //when
            List<GroupTodo> groupTodos = todoQueryRepository.findDailyGroupTodosByDate(group.getId(), date);

            //then
            assertThat(groupTodos.size()).isEqualTo(1);
        }

        @DisplayName("기간 내의 여러 그룹의 그룹 투두들이 반환되어야 한다.")
        @Test
        void findAllPeriodGroupTodosByDate(){
            //given
            LocalDate date = LocalDate.of(2023, 1, 1);

            Group group2 = Group.builder().status(Status.ACTIVE).build();
            groupRepository.save(group2);

            List<Group> groups = List.of(group, group2);

            for (int i = 0; i < 5; i++) {
                todo = GroupTodo.builder()
                        .startDate(date)
                        .endDate(date.plusDays(i))
                        .todoCategory(todoCategory)
                        .group(group)
                        .build();

                todoRepository.save(todo);

                todo = GroupTodo.builder()
                        .startDate(date)
                        .endDate(date.plusDays(i))
                        .todoCategory(todoCategory)
                        .group(group2)
                        .build();

                todoRepository.save(todo);
            }

            //when
            LocalDate from = LocalDate.of(2023, 1, 1);
            LocalDate to = LocalDate.of(2023, 1, 5);
            List<GroupTodo> groupTodos = todoQueryRepository.findAllPeriodGroupTodosByDate(groups, from, to);

            //then
            assertThat(groupTodos.size()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("GroupMemberTodoQuery 테스트")
    class GroupMemberTodoQueryTest {

        @BeforeEach
        void init() {
            member = Member.builder()
                    .status(Status.ACTIVE)
                    .build();

            memberRepository.save(member);

            group = Group.builder()
                    .status(Status.ACTIVE)
                    .build();

            groupRepository.save(group);
        }

        @DisplayName("같은 그룹 멤버의 해당 그룹 투두가 조회되어야 한다.")
        @Test
        void findOneGroupMemberTodoById_GroupTodo(){
            //given
            todoCategory = GroupTodoCategory.builder()
                    .group(group)
                    .build();

            todoCategoryRepository.save(todoCategory);

            todo = GroupTodo.builder()
                    .todoCategory(todoCategory)
                    .group(group)
                    .build();

            todoRepository.save(todo);

            //when
            Todo findTodo = todoQueryRepository
                    .findOneGroupMemberTodoById(member.getId(), group.getId(), todo.getId())
                    .orElse(null);

            //then
            assertThat(findTodo).isNotNull();
            assertThat(findTodo).isEqualTo(todo);
        }

        @DisplayName("같은 그룹 멤버가 해당 그룹을 지정한 개인 투두가 조회되어야 한다.")
        @Test
        void findOneGroupMemberTodoById_MemberTodo(){
            //given
            todoCategory = MemberTodoCategory.builder()
                    .member(member)
                    .build();

            todoCategoryRepository.save(todoCategory);

            todo = MemberTodo.builder()
                    .todoCategory(todoCategory)
                    .member(member)
                    .group(group)
                    .build();

            todoRepository.save(todo);

            //when
            Todo findTodo = todoQueryRepository
                    .findOneGroupMemberTodoById(member.getId(), group.getId(), todo.getId())
                    .orElse(null);

            //then
            assertThat(findTodo).isNotNull();
            assertThat(findTodo).isEqualTo(todo);
        }

        @DisplayName("같은 그룹 멤버가 해당 그룹을 지정하지 않은 개인 투두는 조회되서는 안된다.")
        @Test
        void findOneGroupMemberTodoById_MemberTodo_Fail_If_Not_Assign_Group(){
            //given
            todoCategory = MemberTodoCategory.builder()
                    .member(member)
                    .build();

            todoCategoryRepository.save(todoCategory);

            todo = MemberTodo.builder()
                    .todoCategory(todoCategory)
                    .member(member)
                    .build();

            todoRepository.save(todo);

            //when
            Todo findTodo = todoQueryRepository
                    .findOneGroupMemberTodoById(member.getId(), group.getId(), todo.getId())
                    .orElse(null);

            //then
            assertThat(findTodo).isNull();
        }

        @DisplayName("기간 내의 그룹 멤버 투두가 리스트 형식으로 조회되어야 한다.")
        @Test
        void findPeriodGroupTodosByDate(){
            //given
            LocalDate date = LocalDate.of(2023, 1, 1);
            todoCategory = MemberTodoCategory.builder()
                    .member(member)
                    .build();

            todoCategoryRepository.save(todoCategory);

            for (int i = 0; i < 5; i++) {
                todo = MemberTodo.builder()
                        .startDate(date.plusDays(i))
                        .todoCategory(todoCategory)
                        .member(member)
                        .group(group)
                        .build();

                todoRepository.save(todo);
            }

            //when
            LocalDate from = LocalDate.of(2023, 1, 1);
            LocalDate to = LocalDate.of(2023, 1, 5);
            List<Todo> groupMemberTodos =
                    todoQueryRepository.findGroupMemberPeriodTodosByDate(member.getId(), group.getId(), from, to);

            //then
            assertThat(groupMemberTodos.size()).isEqualTo(5);
        }

        @DisplayName("일별 그룹 멤버 투두가 존재한다면 리스트 형식으로 조회되어야 한다.")
        @Test
        void findGroupMemberDailyTodosByDate(){
            //given
            LocalDate date = LocalDate.of(2023, 1, 1);
            todoCategory = MemberTodoCategory.builder()
                    .member(member)
                    .build();

            todoCategoryRepository.save(todoCategory);

            todo = MemberTodo.builder()
                    .startDate(date)
                    .todoCategory(todoCategory)
                    .member(member)
                    .group(group)
                    .build();

            todoRepository.save(todo);

            //when
            List<Todo> groupMemberTodos
                    = todoQueryRepository.findGroupMemberDailyTodosByDate(member.getId(), group.getId(), date);

            //then
            assertThat(groupMemberTodos.size()).isEqualTo(1);
        }
    }
}