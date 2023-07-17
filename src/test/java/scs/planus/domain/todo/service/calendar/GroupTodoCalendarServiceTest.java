package scs.planus.domain.todo.service.calendar;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import scs.planus.domain.Status;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
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
import scs.planus.global.config.QueryDslConfig;
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

@ServiceTest
@Import(QueryDslConfig.class)
@Slf4j
class GroupTodoCalendarServiceTest {

    private static final Long MEMBER_ID = 1L;
    private static final Long GROUP_ID = 1L;
    private static final Long GROUP_TODO_CATEGORY_ID = 2L;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private TodoCategoryRepository todoCategoryRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private GroupTodoCompletionRepository groupTodoCompletionRepository;
    @Autowired
    private JPAQueryFactory queryFactory;

    private TodoQueryRepository todoQueryRepository;
    private GroupTodoCalendarService groupTodoCalendarService;

    private Member member;
    private Group group;
    private GroupTodoCategory groupTodoCategory;

    @BeforeEach
    void init() {
        todoQueryRepository = new TodoQueryRepository(queryFactory);

        groupTodoCalendarService = new GroupTodoCalendarService(
                todoQueryRepository,
                groupRepository,
                groupMemberRepository,
                groupTodoCompletionRepository
        );

        member = memberRepository.findById(MEMBER_ID).orElse(null);
        group = groupRepository.findById(GROUP_ID).orElse(null);
        groupTodoCategory = (GroupTodoCategory) todoCategoryRepository.findById(GROUP_TODO_CATEGORY_ID).orElse(null);

        GroupMember.createGroupLeader(member, group);
    }

    @DisplayName("기간 내의 모든 GroupTodo들을 조회할 수 있어야 한다.")
    @Test
    void getPeriodGroupTodos() {
        //given
        LocalDate from = LocalDate.of(2023, 1,1);
        LocalDate to = LocalDate.of(2023, 1, 7);

        for (int i = 0; i < 7; i++) {
            GroupTodo groupTodo = GroupTodo.builder()
                    .startDate(from.plusDays(i))
                    .group(group)
                    .todoCategory(groupTodoCategory)
                    .build();

            todoRepository.save(groupTodo);
        }

        //when
        List<TodoPeriodResponseDto> periodGroupTodos
                = groupTodoCalendarService.getPeriodGroupTodos(MEMBER_ID, GROUP_ID, from, to);

        //then
        assertThat(periodGroupTodos.size()).isEqualTo(7);
    }

    @DisplayName("일별 GroupTodo 조회시, 시간을 지정하지 않을 시, DailyTodos로 조회되어야 한다.")
    @Test
    void getDailyGroupTodos_If_Not_StartTime_Then_DailyTodos() {
        //given
        LocalDate date = LocalDate.of(2023, 1,1);

        for (int i = 0; i < 7; i++) {
            GroupTodo groupTodo = GroupTodo.builder()
                    .startDate(date)
                    .group(group)
                    .todoCategory(groupTodoCategory)
                    .build();

            todoRepository.save(groupTodo);
        }

        //when
        TodoDailyResponseDto dailyGroupTodos
                = groupTodoCalendarService.getDailyGroupTodos(MEMBER_ID, GROUP_ID, date);

        //then
        assertThat(dailyGroupTodos.getDailyTodos().size()).isEqualTo(7);
        assertThat(dailyGroupTodos.getDailySchedules()).isEmpty();
    }

    @DisplayName("일별 GroupTodo 조회시, 시간을 지정 시, DailySchedulss로 조회되어야 한다.")
    @Test
    void getDailyGroupTodos_If_StartTime_Then_DailySchedules() {
        //given
        LocalDate date = LocalDate.of(2023, 1,1);
        LocalTime time = LocalTime.of(11, 0);

        for (int i = 0; i < 7; i++) {
            GroupTodo groupTodo = GroupTodo.builder()
                    .startDate(date)
                    .startTime(time)
                    .group(group)
                    .todoCategory(groupTodoCategory)
                    .build();

            todoRepository.save(groupTodo);
        }

        //when
        TodoDailyResponseDto dailyGroupTodos
                = groupTodoCalendarService.getDailyGroupTodos(MEMBER_ID, GROUP_ID, date);

        //then
        assertThat(dailyGroupTodos.getDailySchedules().size()).isEqualTo(7);
        assertThat(dailyGroupTodos.getDailyTodos()).isEmpty();
    }

    @DisplayName("GroupMember의 GroupTodos가 제대로 조회되어야 한다.")
    @Test
    void getGroupMemberPeriodTodos_GroupTodos() {
        //given
        LocalDate from = LocalDate.of(2023, 1,1);
        LocalDate to = LocalDate.of(2023, 1, 7);

        Member anotherMember = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(anotherMember);

        GroupMember groupMember = GroupMember.createGroupMember(anotherMember, group);
        groupMemberRepository.save(groupMember);

        for (int i = 0; i < 7; i++) {
            GroupTodo groupTodo = GroupTodo.builder()
                    .startDate(from)
                    .group(group)
                    .todoCategory(groupTodoCategory)
                    .build();

            todoRepository.save(groupTodo);
        }

        //when
        List<TodoPeriodResponseDto> groupMemberPeriodTodos
                = groupTodoCalendarService.getGroupMemberPeriodTodos(anotherMember.getId(), GROUP_ID, MEMBER_ID, from, to);

        //then
        assertThat(groupMemberPeriodTodos.size()).isEqualTo(7);
    }

    @DisplayName("GroupMember의 해당 Group이 지정된 MemberTodo가 제대로 조회되어야 한다.")
    @Test
    void getGroupMemberPeriodTodos_MemberTodos() {
        //given
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 7);

        MemberTodoCategory memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElse(null);

        Member loginMember = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(loginMember);

        GroupMember groupMember = GroupMember.createGroupMember(loginMember, group);
        groupMemberRepository.save(groupMember);

        for (int i = 0; i < 7; i++) {
            MemberTodo memberTodo = MemberTodo.builder()
                    .startDate(from)
                    .member(member)
                    .group(group)
                    .todoCategory(memberTodoCategory)
                    .build();
            todoRepository.save(memberTodo);
        }

        //when
        List<TodoPeriodResponseDto> groupMemberPeriodTodos
                = groupTodoCalendarService.getGroupMemberPeriodTodos(loginMember.getId(), GROUP_ID, MEMBER_ID, from, to);

        //then
        assertThat(groupMemberPeriodTodos.size()).isEqualTo(7);
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
                        GROUP_ID,
                        MEMBER_ID,
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
                        MEMBER_ID,
                        GROUP_ID,
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

        Member anotherMember = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(anotherMember);

        GroupMember groupMember = GroupMember.createGroupMember(anotherMember, group);
        groupMemberRepository.save(groupMember);

        MemberTodoCategory memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElse(null);

        todoRepository.save(MemberTodo.builder()
                .startDate(date)
                .member(member)
                .group(group)
                .todoCategory(memberTodoCategory)
                .build());

        todoRepository.save(GroupTodo.builder()
                .startDate(date)
                .startTime(time)
                .group(group)
                .isGroupTodo(true)
                .todoCategory(groupTodoCategory)
                .build());

        //when
        TodoDailyResponseDto groupMemberDailyTodos
                = groupTodoCalendarService.getGroupMemberDailyTodos(anotherMember.getId(), GROUP_ID, MEMBER_ID, date);

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

        Member anotherMember = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(anotherMember);

        GroupMember groupMember = GroupMember.createGroupMember(anotherMember, group);
        groupMemberRepository.save(groupMember);

        MemberTodoCategory memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElse(null);

        for (int i = 0; i < 7; i++) {
            MemberTodo memberTodo = MemberTodo.builder()
                    .startDate(date)
                    .startTime(time.minusHours(i))
                    .member(member)
                    .group(group)
                    .todoCategory(memberTodoCategory)
                    .build();
            todoRepository.save(memberTodo);
        }

        //when
        TodoDailyResponseDto groupMemberDailyTodos
                = groupTodoCalendarService.getGroupMemberDailyTodos(anotherMember.getId(), GROUP_ID, MEMBER_ID, date);

        List<LocalTime> startTimes = groupMemberDailyTodos.getDailySchedules().stream()
                .map(TodoDailyDto::getStartTime)
                .collect(Collectors.toList());

        //then
        assertThat(groupMemberDailyTodos.getDailySchedules().size()).isEqualTo(7);
        assertThat(startTimes).isSorted();
    }
}