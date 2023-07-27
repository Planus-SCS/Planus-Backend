package scs.planus.domain.todo.service.calendar;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.dto.mygroup.GroupBelongInResponseDto;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.group.repository.GroupTagRepository;
import scs.planus.domain.group.service.MyGroupService;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.dto.calendar.AllTodoResponseDto;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.repository.GroupTodoCompletionRepository;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.domain.todo.repository.TodoRepository;
import scs.planus.support.ServiceTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTodoCalendarServiceTest extends ServiceTest {

    private static final int COUNT = 7;

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupTagRepository groupTagRepository;
    private final TodoRepository todoRepository;
    private final TodoCategoryRepository todoCategoryRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupTodoCompletionRepository groupTodoCompletionRepository;

    private final TodoQueryRepository todoQueryRepository;
    private final MyGroupService myGroupService;
    private final MemberTodoCalendarService memberTodoCalendarService;

    private Member member;
    private MemberTodoCategory memberTodoCategory;
    private Group group;
    private GroupTodoCategory groupTodoCategory;

    @Autowired
    public MemberTodoCalendarServiceTest(MemberRepository memberRepository, GroupRepository groupRepository,
                                         GroupTagRepository groupTagRepository, TodoRepository todoRepository,
                                         TodoCategoryRepository todoCategoryRepository, GroupMemberRepository groupMemberRepository,
                                         GroupTodoCompletionRepository groupTodoCompletionRepository, JPAQueryFactory queryFactory) {
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
        this.groupTagRepository = groupTagRepository;
        this.todoRepository = todoRepository;
        this.todoCategoryRepository = todoCategoryRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupTodoCompletionRepository = groupTodoCompletionRepository;

        todoQueryRepository = new TodoQueryRepository(queryFactory);
        myGroupService = new MyGroupService(
                memberRepository,
                groupRepository,
                groupMemberRepository,
                groupTagRepository);
        memberTodoCalendarService = new MemberTodoCalendarService(
                myGroupService,
                todoQueryRepository,
                groupMemberRepository,
                groupTodoCompletionRepository);
    }

    @BeforeEach
    void init() {
        member = memberRepository.findById(1L).orElseThrow();
        memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElseThrow();
        group = groupRepository.findById(1L).orElseThrow();
        groupTodoCategory = (GroupTodoCategory) todoCategoryRepository.findById(2L).orElseThrow();
    }

    @DisplayName("요청된 기간 동안의 Todo들을 조회할 수 있어야 한다.")
    @Test
    void getPeriodDetailTodos() {
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 31);

        //given
        for (int i = 0; i < COUNT; i++) {
            MemberTodo memberTodo = MemberTodo.builder()
                    .startDate(from.plusDays(i))
                    .member(member)
                    .todoCategory(memberTodoCategory)
                    .build();
            todoRepository.save(memberTodo);

            GroupTodo groupTodo = GroupTodo.builder()
                    .startDate(from.plusDays(i))
                    .group(group)
                    .todoCategory(groupTodoCategory)
                    .build();
            todoRepository.save(groupTodo);
        }

        //when
        AllTodoResponseDto periodDetailTodos
                = memberTodoCalendarService.getPeriodDetailTodos(member.getId(), from, to);

        //then
        assertThat(periodDetailTodos.getMemberTodos().size()).isEqualTo(COUNT);
        assertThat(periodDetailTodos.getGroupTodos().size()).isEqualTo(COUNT);
    }

    @DisplayName("가입한 모든 그룹들을 조회할 수 있어야 한다.")
    @Test
    void getAllMyGroup() {
        //when
        List<GroupBelongInResponseDto> allMyGroup
                = memberTodoCalendarService.getAllMyGroup(member.getId());

        //then
        assertThat(allMyGroup.size()).isEqualTo(1);
    }

}