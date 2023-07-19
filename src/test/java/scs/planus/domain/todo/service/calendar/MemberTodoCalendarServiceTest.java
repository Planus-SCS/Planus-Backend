package scs.planus.domain.todo.service.calendar;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
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
import scs.planus.global.config.QueryDslConfig;
import scs.planus.support.ServiceTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ServiceTest
@Import(QueryDslConfig.class)
class MemberTodoCalendarServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupTagRepository groupTagRepository;
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private TodoCategoryRepository todoCategoryRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private GroupTodoCompletionRepository groupTodoCompletionRepository;
    @Autowired
    private JPAQueryFactory queryFactory;

    private TodoQueryRepository todoQueryRepository;
    private MyGroupService myGroupService;
    private MemberTodoCalendarService memberTodoCalendarService;

    private Member member;
    private MemberTodoCategory memberTodoCategory;
    private Group group;
    private GroupTodoCategory groupTodoCategory;

    @BeforeEach
    void init() {
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
        for (int i = 0; i < 7; i++) {
            MemberTodo memberTodo = MemberTodo.builder()
                    .startDate(from.plusDays(1))
                    .member(member)
                    .todoCategory(memberTodoCategory)
                    .build();
            todoRepository.save(memberTodo);

            GroupTodo groupTodo = GroupTodo.builder()
                    .startDate(from.plusDays(1))
                    .group(group)
                    .todoCategory(groupTodoCategory)
                    .build();
            todoRepository.save(groupTodo);
        }

        //when
        AllTodoResponseDto periodDetailTodos
                = memberTodoCalendarService.getPeriodDetailTodos(member.getId(), from, to);

        //then
        assertThat(periodDetailTodos.getMemberTodos().size()).isEqualTo(7);
        assertThat(periodDetailTodos.getGroupTodos().size()).isEqualTo(7);
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