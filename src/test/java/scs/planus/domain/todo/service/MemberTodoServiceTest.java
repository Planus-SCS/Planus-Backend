package scs.planus.domain.todo.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.dto.TodoRequestDto;
import scs.planus.domain.todo.dto.TodoResponseDto;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.entity.Todo;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.domain.todo.repository.TodoRepository;
import scs.planus.global.exception.PlanusException;
import scs.planus.support.ServiceTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scs.planus.global.exception.CustomExceptionStatus.*;

class MemberTodoServiceTest extends ServiceTest {

    private static final Long NOT_EXISTED_ID = 0L;

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final TodoCategoryRepository todoCategoryRepository;
    private final TodoRepository todoRepository;

    private final TodoQueryRepository todoQueryRepository;
    private final MemberTodoService memberTodoService;

    private Member member;
    private MemberTodoCategory memberTodoCategory;

    @Autowired
    public MemberTodoServiceTest(MemberRepository memberRepository, GroupRepository groupRepository,
                                 GroupMemberRepository groupMemberRepository, TodoCategoryRepository todoCategoryRepository,
                                 TodoRepository todoRepository, JPAQueryFactory queryFactory) {
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.todoCategoryRepository = todoCategoryRepository;
        this.todoRepository = todoRepository;

        todoQueryRepository = new TodoQueryRepository(queryFactory);
        memberTodoService = new MemberTodoService(
                memberRepository,
                todoCategoryRepository,
                todoRepository,
                todoQueryRepository,
                groupMemberRepository,
                groupRepository);
    }

    @BeforeEach
    void init() {
        member = memberRepository.findById(1L).orElseThrow();
        memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElseThrow();
    }

    @DisplayName("MemberTodo가 제대로 생성되어야 한다.")
    @Test
    void createMemberTodo(){
        //given
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("memberTodo")
                .categoryId(memberTodoCategory.getId())
                .startDate(LocalDate.now())
                .build();

        //when
        TodoResponseDto memberTodo = memberTodoService.createMemberTodo(member.getId(), requestDto);

        //then
        assertThat(memberTodo.getTodoId()).isNotNull();
    }

    @DisplayName("존재하지 않는 그룹 id로 MemberTodo를 생성시, 예외를 던진다.")
    @Test
    void createMemberTodo_Throw_Exception_If_Not_Existed_Group(){
        //given
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("memberTodo")
                .groupId(NOT_EXISTED_ID)
                .categoryId(memberTodoCategory.getId())
                .startDate(LocalDate.now())
                .build();

        //when
        assertThatThrownBy(() ->
                memberTodoService.createMemberTodo(
                        member.getId(),
                        requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_GROUP);
    }

    @DisplayName("가입하지 않는 그룹 id로 MemberTodo를 생성시, 예외를 던진다.")
    @Test
    void createMemberTodo_Throw_Exception_If_Not_Joined_Group(){
        //given
        Group group = Group.builder().build();
        groupRepository.save(group);

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("memberTodo")
                .groupId(group.getId())
                .categoryId(memberTodoCategory.getId())
                .startDate(LocalDate.now())
                .build();

        //when
        assertThatThrownBy(() ->
                memberTodoService.createMemberTodo(
                        member.getId(),
                        requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_JOINED_GROUP);
    }

    @DisplayName("MemberTodo를 생성할 때 endDate가 startDate보다 빠를 시, 예외를 던진다.")
    @Test
    void createMemberTodo_Throw_Exception_EndDate_Earlier_Than_StartDate(){
        //given
        LocalDate startDate = LocalDate.of(2023, 1, 2);
        LocalDate endDate = LocalDate.of(2023, 1, 1);

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("memberTodo")
                .startDate(startDate)
                .endDate(endDate)
                .categoryId(memberTodoCategory.getId())
                .startDate(LocalDate.now())
                .build();

        //when
        assertThatThrownBy(() ->
                memberTodoService.createMemberTodo(
                        member.getId(),
                        requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(INVALID_DATE);
    }

    @DisplayName("requestDto에 잘못된 TodoCategoryId가 포함될 시, 예외를 던진다.")
    @Test
    void createMemberTodo_Throw_Exception_If_With_Wrong_CategoryId(){
        //given
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("memberTodo")
                .categoryId(NOT_EXISTED_ID)
                .startDate(LocalDate.now())
                .build();

        //then
        assertThatThrownBy(() ->
                memberTodoService.createMemberTodo(
                        member.getId(),
                        requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_CATEGORY);
    }

    @DisplayName("저장된 MemberTodo를 제대로 조회할 수 있어야 한다.")
    @Test
    void getOneTodo() {
        //given
        MemberTodo todo = MemberTodo.builder()
                .member(member)
                .todoCategory(memberTodoCategory)
                .title("memberTodo")
                .build();
        todoRepository.save(todo);

        //when
        TodoDetailsResponseDto findTodo
                = memberTodoService.getOneTodo(member.getId(), todo.getId());

        //then
        assertThat(findTodo.getTitle()).isEqualTo(todo.getTitle());
        assertThat(findTodo.getTodoId()).isEqualTo(todo.getId());
        assertThat(findTodo.getCategoryId()).isEqualTo(memberTodoCategory.getId());
    }

    @DisplayName("잘못된 todoId로 조회시, 예외를 던진다.")
    @Test
    void getOneTodo_Throw_Exception_If_Wrong_TodoId() {
        //then
        assertThatThrownBy(() ->
                memberTodoService.getOneTodo(
                        member.getId(),
                        NOT_EXISTED_ID))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NONE_TODO);
    }

    @DisplayName("MemberTodo 변경이 제대로 이루어져야한다.")
    @Test
    void updateTodo(){
        //given
        MemberTodo todo = MemberTodo.builder()
                .title("memberTodo")
                .member(member)
                .todoCategory(memberTodoCategory)
                .build();
        todoRepository.save(todo);

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("new MemberTodo")
                .categoryId(memberTodoCategory.getId())
                .build();

        //when
        TodoResponseDto responseDto
                = memberTodoService.updateTodo(member.getId(), todo.getId(), requestDto);

        //then
        assertThat(todo.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(responseDto.getTodoId()).isEqualTo(todo.getId());
    }

    @DisplayName("TodoCompletion이 false에서 true로 변경되어야 한다.")
    @Test
    void checkCompletion_From_False_To_True(){
        //given
        MemberTodo todo = MemberTodo.builder()
                .title("memberTodo")
                .completion(false)
                .member(member)
                .build();
        todoRepository.save(todo);

        //when
        memberTodoService.checkCompletion(member.getId(), todo.getId());

        //then
        assertThat(todo.isCompletion()).isTrue();
    }

    @DisplayName("TodoCompletion이 true에서 false로 변경되어야 한다.")
    @Test
    void checkCompletion_From_True_To_False(){
        //given
        MemberTodo todo = MemberTodo.builder()
                .title("memberTodo")
                .completion(true)
                .member(member)
                .build();
        todoRepository.save(todo);

        //when
        memberTodoService.checkCompletion(member.getId(), todo.getId());

        //then
        assertThat(todo.isCompletion()).isFalse();
    }

    @DisplayName("MemberTodo가 제대로 삭제되어야 한다.")
    @Test
    void deleteTodo(){
        //given
        MemberTodo todo = MemberTodo.builder()
                .member(member)
                .todoCategory(memberTodoCategory)
                .build();
        todoRepository.save(todo);

        //when
        memberTodoService.deleteTodo(member.getId(), todo.getId());
        Todo findTodo = todoRepository.findById(todo.getId()).orElse(null);

        //then
        assertThat(findTodo).isNull();
    }
}