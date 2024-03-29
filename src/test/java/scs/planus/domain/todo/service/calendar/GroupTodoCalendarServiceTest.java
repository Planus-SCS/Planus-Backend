package scs.planus.domain.todo.service.calendar;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.Status;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.dto.calendar.TodoDailyDto;
import scs.planus.domain.todo.dto.calendar.TodoDailyResponseDto;
import scs.planus.domain.todo.dto.calendar.TodoPeriodResponseDto;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.repository.GroupTodoCompletionRepository;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.domain.todo.repository.TodoRepository;
import scs.planus.global.exception.PlanusException;
import scs.planus.support.ServiceTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scs.planus.global.exception.CustomExceptionStatus.NOT_JOINED_GROUP;
import static scs.planus.global.exception.CustomExceptionStatus.NOT_JOINED_MEMBER_IN_GROUP;

class GroupTodoCalendarServiceTest extends ServiceTest {

    private static final int COUNT = 7;

    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;
    private final TodoCategoryRepository todoCategoryRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupTodoCompletionRepository groupTodoCompletionRepository;

    private final TodoQueryRepository todoQueryRepository;
    private final GroupTodoCalendarService groupTodoCalendarService;

    private Member groupLeader;
    private Member groupMember;
    private Group group;
    private GroupTodoCategory groupTodoCategory;

    @Autowired
    public GroupTodoCalendarServiceTest(MemberRepository memberRepository, TodoRepository todoRepository,
                                        TodoCategoryRepository todoCategoryRepository, GroupRepository groupRepository,
                                        GroupMemberRepository groupMemberRepository, GroupTodoCompletionRepository groupTodoCompletionRepository,
                                        JPAQueryFactory queryFactory) {
        this.memberRepository = memberRepository;
        this.todoRepository = todoRepository;
        this.todoCategoryRepository = todoCategoryRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupTodoCompletionRepository = groupTodoCompletionRepository;

        todoQueryRepository = new TodoQueryRepository(queryFactory);
        groupTodoCalendarService = new GroupTodoCalendarService(
                todoQueryRepository,
                groupRepository,
                groupMemberRepository,
                groupTodoCompletionRepository);
    }

    @BeforeEach
    void init() {
        groupLeader = memberRepository.findById(1L).orElseThrow();
        groupMember = memberRepository.findById(2L).orElseThrow();
        group = groupRepository.findById(1L).orElseThrow();
        groupTodoCategory = (GroupTodoCategory) todoCategoryRepository.findById(2L).orElseThrow();
    }

    @DisplayName("기간 내의 모든 GroupTodo들을 조회할 수 있어야 한다.")
    @Test
    void getPeriodGroupTodos() {
        //given
        LocalDate from = LocalDate.of(2023, 1,1);
        LocalDate to = LocalDate.of(2023, 1, 7);

        for (int i = 0; i < COUNT; i++) {
            GroupTodo groupTodo = GroupTodo.builder()
                    .startDate(from.plusDays(i))
                    .group(group)
                    .todoCategory(groupTodoCategory)
                    .isGroupTodo(true)
                    .build();
            todoRepository.save(groupTodo);
        }

        //when
        List<TodoPeriodResponseDto> periodGroupTodos
                = groupTodoCalendarService.getPeriodGroupTodos(groupLeader.getId(), group.getId(), from, to);

        //then
        assertThat(periodGroupTodos.size()).isEqualTo(COUNT);
    }

    @DisplayName("일별 GroupTodo 조회시, 시간을 지정하지 않을 시, DailyTodos로 조회되어야 한다.")
    @Test
    void getDailyGroupTodos_If_Not_StartTime_Then_DailyTodos() {
        //given
        LocalDate date = LocalDate.of(2023, 1,1);

        for (int i = 0; i < COUNT; i++) {
            GroupTodo groupTodo = GroupTodo.builder()
                    .startDate(date)
                    .group(group)
                    .todoCategory(groupTodoCategory)
                    .isGroupTodo(true)
                    .build();
            todoRepository.save(groupTodo);
        }

        //when
        TodoDailyResponseDto dailyGroupTodos
                = groupTodoCalendarService.getDailyGroupTodos(groupLeader.getId(), group.getId(), date);

        //then
        assertThat(dailyGroupTodos.getDailyTodos().size()).isEqualTo(COUNT);
        assertThat(dailyGroupTodos.getDailySchedules()).isEmpty();
    }

    @DisplayName("일별 GroupTodo 조회시, 시간을 지정 시, DailySchedulss로 조회되어야 한다.")
    @Test
    void getDailyGroupTodos_If_StartTime_Then_DailySchedules() {
        //given
        LocalDate date = LocalDate.of(2023, 1,1);
        LocalTime time = LocalTime.of(11, 0);

        for (int i = 0; i < COUNT; i++) {
            GroupTodo groupTodo = GroupTodo.builder()
                    .startDate(date)
                    .startTime(time)
                    .group(group)
                    .todoCategory(groupTodoCategory)
                    .isGroupTodo(true)
                    .build();

            todoRepository.save(groupTodo);
        }

        //when
        TodoDailyResponseDto dailyGroupTodos
                = groupTodoCalendarService.getDailyGroupTodos(groupLeader.getId(), group.getId(), date);

        //then
        assertThat(dailyGroupTodos.getDailySchedules().size()).isEqualTo(COUNT);
        assertThat(dailyGroupTodos.getDailyTodos()).isEmpty();
    }

    @DisplayName("GroupMember의 GroupTodos가 제대로 조회되어야 한다.")
    @Test
    void getGroupMemberPeriodTodos_GroupTodos() {
        //given
        LocalDate from = LocalDate.of(2023, 1,1);
        LocalDate to = LocalDate.of(2023, 1, 7);

        for (int i = 0; i < COUNT; i++) {
            GroupTodo groupTodo = GroupTodo.builder()
                    .startDate(from)
                    .group(group)
                    .todoCategory(groupTodoCategory)
                    .isGroupTodo(true)
                    .build();
            todoRepository.save(groupTodo);
        }

        //when
        List<TodoPeriodResponseDto> groupMemberPeriodTodos
                = groupTodoCalendarService.getGroupMemberPeriodTodos(groupMember.getId(), group.getId(), groupLeader.getId(), from, to);

        //then
        assertThat(groupMemberPeriodTodos.size()).isEqualTo(COUNT);
    }

    @DisplayName("GroupMember의 해당 Group이 지정된 MemberTodo가 제대로 조회되어야 한다.")
    @Test
    void getGroupMemberPeriodTodos_MemberTodos() {
        //given
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 7);

        MemberTodoCategory memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElseThrow();

        for (int i = 0; i < COUNT; i++) {
            MemberTodo memberTodo = MemberTodo.builder()
                    .startDate(from)
                    .member(groupLeader)
                    .group(group)
                    .todoCategory(memberTodoCategory)
                    .build();
            todoRepository.save(memberTodo);
        }

        //when
        List<TodoPeriodResponseDto> groupMemberPeriodTodos
                = groupTodoCalendarService.getGroupMemberPeriodTodos(groupMember.getId(), group.getId(), groupLeader.getId(), from, to);

        //then
        assertThat(groupMemberPeriodTodos.size()).isEqualTo(COUNT);
    }

    @DisplayName("Group에 속하지 않는 Member가 GroupMember의 Todo를 조회시, 예외를 던진다.")
    @Test
    void getGroupMemberPeriodTodos_Throw_Exception_If_Not_Joined_Member() {
        //given
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 7);

        Member anotherMember = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(anotherMember);

        //then
        assertThatThrownBy(() ->
                groupTodoCalendarService.getGroupMemberPeriodTodos(
                        anotherMember.getId(),
                        group.getId(),
                        groupMember.getId(),
                        from,
                        to))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_JOINED_GROUP);
    }

    @DisplayName("GroupMember가 Group에 속하지 않는 Member의 Todo를 조회시, 예외를 던진다.")
    @Test
    void getGroupMemberPeriodTodos_Throw_Exception_If_Not_Joined_Member_In_Group() {
        //given
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 7);

        Member anotherMember = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(anotherMember);

        //then
        assertThatThrownBy(() ->
                groupTodoCalendarService.getGroupMemberPeriodTodos(
                        groupMember.getId(),
                        group.getId(),
                        anotherMember.getId(),
                        from,
                        to))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_JOINED_MEMBER_IN_GROUP);
    }

    @DisplayName("GroupMember의 일별 Todo들을 조회할 수 있어야 한다.")
    @Test
    void getGroupMemberDailyTodos(){
        //given
        LocalDate date = LocalDate.of(2023, 1, 1);
        LocalTime time = LocalTime.of(11, 0);

        MemberTodoCategory memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElseThrow();

        todoRepository.save(MemberTodo.builder()
                .startDate(date)
                .member(groupLeader)
                .group(group)
                .todoCategory(memberTodoCategory)
                .build());

        todoRepository.save(GroupTodo.builder()
                .startDate(date)
                .startTime(time)
                .group(group)
                .todoCategory(groupTodoCategory)
                .isGroupTodo(true)
                .build());

        //when
        TodoDailyResponseDto groupMemberDailyTodos
                = groupTodoCalendarService.getGroupMemberDailyTodos(groupMember.getId(), group.getId(), groupLeader.getId(), date);

        //then
        assertThat(groupMemberDailyTodos.getDailyTodos().size()).isEqualTo(1);
        assertThat(groupMemberDailyTodos.getDailySchedules().size()).isEqualTo(1);
    }

    @DisplayName("GroupMember의 일별 Todo들을 반환할 때, DailySchedule들이 정렬된 상태여야 한다.")
    @Test
    void getGroupMemberDailyTodos_Sorted_DailyTodos(){
        //given
        LocalDate date = LocalDate.of(2023, 1, 1);
        LocalTime time = LocalTime.of(11, 0);

        MemberTodoCategory memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElseThrow();

        for (int i = 0; i < COUNT; i++) {
            MemberTodo memberTodo = MemberTodo.builder()
                    .startDate(date)
                    .startTime(time.minusHours(i))
                    .member(groupLeader)
                    .group(group)
                    .todoCategory(memberTodoCategory)
                    .build();
            todoRepository.save(memberTodo);
        }

        //when
        TodoDailyResponseDto groupMemberDailyTodos
                = groupTodoCalendarService.getGroupMemberDailyTodos(groupMember.getId(), group.getId(), groupLeader.getId(), date);

        List<LocalTime> startTimes = groupMemberDailyTodos.getDailySchedules().stream()
                .map(TodoDailyDto::getStartTime)
                .collect(Collectors.toList());

        //then
        assertThat(groupMemberDailyTodos.getDailySchedules().size()).isEqualTo(COUNT);
        assertThat(startTimes).isSorted();
    }
}