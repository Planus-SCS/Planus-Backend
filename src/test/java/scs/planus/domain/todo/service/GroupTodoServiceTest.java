package scs.planus.domain.todo.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
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
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.dto.TodoForGroupResponseDto;
import scs.planus.domain.todo.dto.TodoRequestDto;
import scs.planus.domain.todo.dto.TodoResponseDto;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.GroupTodoCompletion;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.entity.Todo;
import scs.planus.domain.todo.repository.GroupTodoCompletionRepository;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.domain.todo.repository.TodoRepository;
import scs.planus.global.config.QueryDslConfig;
import scs.planus.global.exception.PlanusException;
import scs.planus.support.ServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static scs.planus.global.exception.CustomExceptionStatus.*;

@ServiceTest
@Import(QueryDslConfig.class)
class GroupTodoServiceTest {

    private static final Long NOT_EXIST_ID = 0L;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private GroupTodoCompletionRepository groupTodoCompletionRepository;
    @Autowired
    private TodoCategoryRepository todoCategoryRepository;
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private JPAQueryFactory queryFactory;

    private TodoQueryRepository todoQueryRepository;
    private GroupTodoService groupTodoService;

    private Member groupLeader;
    private Member groupMember;
    private Group group;
    private GroupTodoCategory groupTodoCategory;

    @BeforeEach
    void init() {
        todoQueryRepository = new TodoQueryRepository(queryFactory);

        groupTodoService = new GroupTodoService(
                groupRepository,
                groupMemberRepository,
                groupTodoCompletionRepository,
                todoCategoryRepository,
                todoRepository,
                todoQueryRepository
        );

        groupLeader = memberRepository.findById(1L).orElse(null);
        groupMember = memberRepository.findById(2L).orElse(null);
        group = groupRepository.findById(1L).orElse(null);
        groupTodoCategory = (GroupTodoCategory) todoCategoryRepository.findById(2L).orElse(null);
    }

    @DisplayName("GroupTodo가 제대로 생성되어야 한다.")
    @Test
    void createGroupTodo(){
        //given
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("groupTodo")
                .groupId(group.getId())
                .categoryId(groupTodoCategory.getId())
                .build();

        //when
        TodoResponseDto responseDto =
                groupTodoService.createGroupTodo(groupLeader.getId(), group.getId(), requestDto);

        //then
        assertThat(responseDto.getTodoId()).isNotNull();
    }

    @DisplayName("GroupTodo가 제대로 생성시, 이에 해당하는 GroupTodoCompletion이 생성된다.")
    @Test
    void createGroupTodo_Then_Create_GroupTodoCompletion(){
        //given
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("groupTodo")
                .groupId(group.getId())
                .categoryId(groupTodoCategory.getId())
                .build();

        //when
        TodoResponseDto responseDto =
                groupTodoService.createGroupTodo(groupLeader.getId(), group.getId(), requestDto);

        GroupTodoCompletion todoCompletion = groupTodoCompletionRepository
                .findByMemberIdAndTodoId(groupLeader.getId(), responseDto.getTodoId())
                .orElse(null);

        //then
        assertThat(todoCompletion).isNotNull();
        assertThat(todoCompletion.getMember()).isEqualTo(groupLeader);
        assertThat(todoCompletion.getGroupTodo().getId()).isEqualTo(responseDto.getTodoId());
    }

    @DisplayName("그룹이 존재하지 않다면 GroupTodo 생성시, 예외를 던진다.")
    @Test
    void createGroupTodo_Throw_Exception_If_Not_Existed_Group(){
        //given
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("groupTodo")
                .groupId(group.getId())
                .categoryId(groupTodoCategory.getId())
                .build();

        //then
        assertThatThrownBy(() ->
                groupTodoService.createGroupTodo(groupLeader.getId(), NOT_EXIST_ID, requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_GROUP);
    }

    @DisplayName("그룹에 가입하지 않았다면 GroupTodo 생성시, 예외를 던진다.")
    @Test
    void createGroupTodo_Throw_Exception_If_Not_Join_Group(){
        //given
        Member anotherMember = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(anotherMember);

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("groupTodo")
                .groupId(group.getId())
                .categoryId(groupTodoCategory.getId())
                .build();

        //then
        assertThatThrownBy(() ->
                groupTodoService.createGroupTodo(
                        anotherMember.getId(),
                        group.getId(),
                        requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_JOINED_GROUP);
    }

    @DisplayName("Authority가 없다면 GroupTodo 생성시, 예외를 던진다.")
    @Test
    void createGroupTodo_Throw_Exception_If_Not_Authority(){
        //given
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("groupTodo")
                .groupId(group.getId())
                .categoryId(groupTodoCategory.getId())
                .build();

        //then
        assertThatThrownBy(() ->
                groupTodoService.createGroupTodo(
                        groupMember.getId(),
                        group.getId(),
                        requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(DO_NOT_HAVE_TODO_AUTHORITY);
    }

    @DisplayName("GroupTodoCategory가 존재하지 않다면 GroupTodo 생성시, 예외를 던진다.")
    @Test
    void createGroupTodo_Throw_Exception_If_Not_Existed_Category(){
        //given
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("groupTodo")
                .groupId(group.getId())
                .categoryId(NOT_EXIST_ID)
                .build();

        //then
        assertThatThrownBy(() ->
                groupTodoService.createGroupTodo(
                        groupLeader.getId(),
                        group.getId(),
                        requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_CATEGORY);
    }

    @DisplayName("저장된 GroupTodo를 제대로 조회할 수 있어야 한다.")
    @Test
    void getOneGroupTodo() {
        //given
        GroupTodo groupTodo = GroupTodo.builder()
                .title("groupTodo")
                .group(group)
                .todoCategory(groupTodoCategory)
                .build();

        todoRepository.save(groupTodo);

        //when
        TodoForGroupResponseDto responseDto
                = groupTodoService.getOneGroupTodo(groupLeader.getId(), group.getId(), groupTodo.getId());

        //then
        assertThat(responseDto.getTodoId()).isEqualTo(groupTodo.getId());
        assertThat(responseDto.getGroupName()).isEqualTo(group.getName());
    }

    @DisplayName("GroupMember가 해당 Group으로 지정한 Todo를 조회할 수 있어야 한다.")
    @Test
    void getOneGroupMemberTodo(){
        //given
        MemberTodoCategory memberTodoCategory = (MemberTodoCategory) todoCategoryRepository.findById(1L).orElse(null);

        MemberTodo memberTodo = MemberTodo.builder()
                .member(groupLeader)
                .group(group)
                .todoCategory(memberTodoCategory)
                .build();
        todoRepository.save(memberTodo);

        //when
        TodoForGroupResponseDto responseDto
                = groupTodoService.getOneGroupMemberTodo(groupMember.getId(), groupLeader.getId(), group.getId(), memberTodo.getId());

        //then
        assertThat(responseDto.getTodoId()).isEqualTo(memberTodo.getId());
        assertThat(responseDto.getGroupName()).isEqualTo(group.getName());
    }

    @DisplayName("GroupMember의 Todo가 존재하지 않을 시, 예외를 던진다.")
    @Test
    void getOneGroupMemberTodo_Throw_Exception_If_Not_Existed_Todo(){
        //then
        assertThatThrownBy(() ->
                groupTodoService.getOneGroupMemberTodo(
                        groupMember.getId(),
                        groupLeader.getId(),
                        group.getId(),
                        NOT_EXIST_ID))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NONE_TODO);
    }

    @DisplayName("GroupMember가 아닌 Member의 Todo 조회시, 예외를 던진다.")
    @Test
    void getOneGroupMemberTodo_Throw_Exception_If_Not_Joined_Group(){
        //given
        Member anotherMember = Member.builder().status(Status.ACTIVE).build();
        memberRepository.save(anotherMember);

        MemberTodoCategory memberTodoCategory = MemberTodoCategory.builder()
                .member(anotherMember)
                .build();
        todoCategoryRepository.save(memberTodoCategory);

        MemberTodo memberTodo = MemberTodo.builder()
                .member(anotherMember)
                .todoCategory(memberTodoCategory)
                .build();
        todoRepository.save(memberTodo);

        //then
        assertThatThrownBy(() ->
                groupTodoService.getOneGroupMemberTodo(
                        groupLeader.getId(),
                        anotherMember.getId(),
                        group.getId(),
                        memberTodo.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_JOINED_MEMBER_IN_GROUP);
    }

    @DisplayName("GroupTodo의 변경이 제대로 이루어져야 한다.")
    @Test
    void updateTodo(){
        //given
        GroupTodo groupTodo = GroupTodo.builder()
                .title("groupTodo")
                .group(group)
                .todoCategory(groupTodoCategory)
                .build();

        todoRepository.save(groupTodo);

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("new groupTodo")
                .groupId(group.getId())
                .categoryId(groupTodoCategory.getId())
                .build();

        //when
        TodoResponseDto responseDto =
                groupTodoService.updateTodo(groupLeader.getId(), group.getId(), groupTodo.getId(), requestDto);

        //then
        assertThat(responseDto.getTodoId()).isEqualTo(groupTodo.getId());
        assertThat(groupTodo.getTitle()).isEqualTo(requestDto.getTitle());
    }

    @DisplayName("Authority가 없는 경우, GroupTodo 변경 시 예외를 던진다.")
    @Test
    void updateTodo_Throw_Exception_If_Not_Authority(){
        //given
        GroupTodo groupTodo = GroupTodo.builder()
                .title("groupTodo")
                .group(group)
                .todoCategory(groupTodoCategory)
                .build();

        todoRepository.save(groupTodo);

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("new groupTodo")
                .groupId(group.getId())
                .categoryId(groupTodoCategory.getId())
                .build();

        //then
        assertThatThrownBy(() ->
                groupTodoService.updateTodo(
                        groupMember.getId(),
                        group.getId(),
                        groupTodo.getId(),
                        requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(DO_NOT_HAVE_TODO_AUTHORITY);
    }

    @DisplayName("GroupMember가 GroupTodo 완료를 클릭시, 제대로 값이 변경되어야 한다.")
    @Test
    void checkGroupTodo(){
        //given
        GroupTodo groupTodo = GroupTodo.builder()
                .group(group)
                .todoCategory(groupTodoCategory)
                .build();
        todoRepository.save(groupTodo);

        GroupTodoCompletion groupTodoCompletion =
                GroupTodoCompletion.createGroupTodoCompletion(groupMember, groupTodo);
        groupTodoCompletionRepository.save(groupTodoCompletion);

        //when
        TodoResponseDto responseDto
                = groupTodoService.checkGroupTodo(groupMember.getId(), group.getId(), groupTodo.getId());

        //then
        assertThat(responseDto.getTodoId()).isEqualTo(groupTodo.getId());
        assertThat(groupTodoCompletion.isCompletion()).isTrue();
    }
    
    @DisplayName("GroupTodo가 제대로 삭제되어야 한다.")
    @Test
    void deleteTodo(){
        //given
        GroupTodo groupTodo = GroupTodo.builder()
                .group(group)
                .todoCategory(groupTodoCategory)
                .build();

        todoRepository.save(groupTodo);

        //when
        TodoResponseDto responseDto
                = groupTodoService.deleteTodo(groupLeader.getId(), group.getId(), groupTodo.getId());
        Todo findTodo = todoRepository.findById(groupTodo.getId()).orElse(null);

        //then
        assertThat(responseDto.getTodoId()).isEqualTo(groupTodo.getId());
        assertThat(findTodo).isNull();
    }

    @DisplayName("Authority가 없는 경우, GroupTodo 삭제시 예외를 던진다.")
    @Test
    void deleteTodo_Throw_Exception_If_Not_Authority() {
        //given
        GroupTodo groupTodo = GroupTodo.builder()
                .group(group)
                .todoCategory(groupTodoCategory)
                .build();

        todoRepository.save(groupTodo);

        //then
        assertThatThrownBy(() ->
                groupTodoService.deleteTodo(
                        groupMember.getId(),
                        group.getId(),
                        groupTodo.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(DO_NOT_HAVE_TODO_AUTHORITY);
    }
}