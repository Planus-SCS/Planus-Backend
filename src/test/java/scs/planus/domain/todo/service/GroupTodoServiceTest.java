package scs.planus.domain.todo.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import scs.planus.domain.category.entity.GroupTodoCategory;
import scs.planus.domain.category.entity.MemberTodoCategory;
import scs.planus.domain.category.repository.TodoCategoryRepository;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
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
    private static final Long MEMBER_ID = 1L;
    private static final Long GROUP_ID = 1L;
    private static final Long GROUP_TODO_CATEGORY_ID = 2L;

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

    private Member member;
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

        member = memberRepository.findById(MEMBER_ID).orElse(null);
        group = groupRepository.findById(MEMBER_ID).orElse(null);
        groupTodoCategory = (GroupTodoCategory) todoCategoryRepository.findById(GROUP_TODO_CATEGORY_ID).orElse(null);
    }

    @DisplayName("GroupTodo가 제대로 생성되어야 한다.")
    @Test
    void createGroupTodo(){
        //given
        GroupMember groupLeader = GroupMember.createGroupLeader(member, group);
        groupMemberRepository.save(groupLeader);

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("groupTodo")
                .groupId(GROUP_ID)
                .categoryId(GROUP_TODO_CATEGORY_ID)
                .build();

        //when
        TodoResponseDto responseDto =
                groupTodoService.createGroupTodo(MEMBER_ID, GROUP_ID, requestDto);

        //then
        assertThat(responseDto.getTodoId()).isNotNull();
    }

    @DisplayName("GroupTodo가 제대로 생성시, 이에 해당하는 GroupTodoCompletion이 생성된다.")
    @Test
    void createGroupTodo_Then_Create_GroupTodoCompletion(){
        //given
        GroupMember groupLeader = GroupMember.createGroupLeader(member, group);
        groupMemberRepository.save(groupLeader);

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("groupTodo")
                .groupId(GROUP_ID)
                .categoryId(GROUP_TODO_CATEGORY_ID)
                .build();

        //when
        TodoResponseDto responseDto =
                groupTodoService.createGroupTodo(MEMBER_ID, GROUP_ID, requestDto);

        GroupTodoCompletion todoCompletion = groupTodoCompletionRepository
                .findByMemberIdAndTodoId(MEMBER_ID, responseDto.getTodoId())
                .orElse(null);

        //then
        assertThat(todoCompletion).isNotNull();
        assertThat(todoCompletion.getMember()).isEqualTo(member);
        assertThat(todoCompletion.getGroupTodo().getId()).isEqualTo(responseDto.getTodoId());
    }

    @DisplayName("그룹이 존재하지 않다면 GroupTodo 생성시, 예외를 던진다.")
    @Test
    void createGroupTodo_Throw_Exception_If_Not_Existed_Group(){
        //given
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("groupTodo")
                .groupId(GROUP_ID)
                .categoryId(GROUP_TODO_CATEGORY_ID)
                .build();

        //then
        assertThatThrownBy(() ->
                groupTodoService.createGroupTodo(MEMBER_ID, NOT_EXIST_ID, requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_GROUP);
    }

    @DisplayName("그룹에 가입하지 않았다면 GroupTodo 생성시, 예외를 던진다.")
    @Test
    void createGroupTodo_Throw_Exception_If_Not_Join_Group(){
        //given
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("groupTodo")
                .groupId(GROUP_ID)
                .categoryId(GROUP_TODO_CATEGORY_ID)
                .build();

        //then
        assertThatThrownBy(() ->
                groupTodoService.createGroupTodo(MEMBER_ID, GROUP_ID, requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_JOINED_GROUP);
    }

    @DisplayName("Authority가 없다면 GroupTodo 생성시, 예외를 던진다.")
    @Test
    void createGroupTodo_Throw_Exception_If_Not_Authority(){
        //given
        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMemberRepository.save(groupMember);

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("groupTodo")
                .groupId(GROUP_ID)
                .categoryId(GROUP_TODO_CATEGORY_ID)
                .build();

        //then
        assertThatThrownBy(() ->
                groupTodoService.createGroupTodo(MEMBER_ID, GROUP_ID, requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(DO_NOT_HAVE_TODO_AUTHORITY);
    }

    @DisplayName("GroupTodoCategory가 존재하지 않다면 GroupTodo 생성시, 예외를 던진다.")
    @Test
    void createGroupTodo_Throw_Exception_If_Not_Existed_Category(){
        //given
        GroupMember groupLeader = GroupMember.createGroupLeader(member, group);
        groupMemberRepository.save(groupLeader);

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("groupTodo")
                .groupId(GROUP_ID)
                .categoryId(NOT_EXIST_ID)
                .build();

        //then
        assertThatThrownBy(() ->
                groupTodoService.createGroupTodo(MEMBER_ID, GROUP_ID, requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_EXIST_CATEGORY);
    }

    @DisplayName("저장된 GroupTodo를 제대로 조회할 수 있어야 한다.")
    @Test
    void getOneGroupTodo() {
        //given
        GroupMember groupLeader = GroupMember.createGroupLeader(member, group);
        groupMemberRepository.save(groupLeader);

        GroupTodo groupTodo = GroupTodo.builder()
                .title("groupTodo")
                .group(group)
                .todoCategory(groupTodoCategory)
                .build();

        todoRepository.save(groupTodo);

        //when
        TodoForGroupResponseDto responseDto
                = groupTodoService.getOneGroupTodo(MEMBER_ID, GROUP_ID, groupTodo.getId());

        //then
        assertThat(responseDto.getTodoId()).isEqualTo(groupTodo.getId());
        assertThat(responseDto.getGroupName()).isEqualTo(group.getName());
    }

    @DisplayName("GroupMember가 해당 Group으로 지정한 Todo를 조회할 수 있어야 한다.")
    @Test
    void getOneGroupMemberTodo(){
        //given
        Member loginMember = Member.builder().build();
        memberRepository.save(loginMember);

        GroupMember.createGroupMember(loginMember, group);
        GroupMember.createGroupMember(member, group);

        MemberTodoCategory memberTodoCategory = MemberTodoCategory.builder()
                .member(member)
                .build();
        todoCategoryRepository.save(memberTodoCategory);

        MemberTodo memberTodo = MemberTodo.builder()
                .member(member)
                .group(group)
                .todoCategory(memberTodoCategory)
                .build();
        todoRepository.save(memberTodo);

        //when
        TodoForGroupResponseDto responseDto
                = groupTodoService.getOneGroupMemberTodo(loginMember.getId(), MEMBER_ID, GROUP_ID, memberTodo.getId());

        //then
        assertThat(responseDto.getTodoId()).isEqualTo(memberTodo.getId());
        assertThat(responseDto.getGroupName()).isEqualTo(group.getName());
    }

    @DisplayName("GroupMember의 Todo가 존재하지 않을 시, 예외를 던진다.")
    @Test
    void getOneGroupMemberTodo_Throw_Exception_If_Not_Existed_Todo(){
        //given
        Member loginMember = Member.builder().build();
        memberRepository.save(loginMember);

        GroupMember.createGroupMember(loginMember, group);
        GroupMember.createGroupMember(member, group);

        //then
        assertThatThrownBy(() ->
                groupTodoService.getOneGroupMemberTodo(loginMember.getId(), MEMBER_ID, GROUP_ID, NOT_EXIST_ID))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NONE_TODO);
    }

    @DisplayName("GroupMember가 아닌 Member의 Todo 조회시, 예외를 던진다.")
    @Test
    void getOneGroupMemberTodo_Throw_Exception_If_Not_Joined_Group(){
        //given
        Member loginMember = Member.builder().build();
        memberRepository.save(loginMember);

        GroupMember groupMember = GroupMember.createGroupMember(loginMember, group);
        groupMemberRepository.save(groupMember);

        MemberTodoCategory memberTodoCategory = MemberTodoCategory.builder()
                .member(member)
                .build();
        todoCategoryRepository.save(memberTodoCategory);

        MemberTodo memberTodo = MemberTodo.builder()
                .member(member)
                .todoCategory(memberTodoCategory)
                .build();
        todoRepository.save(memberTodo);

        //then
        assertThatThrownBy(() ->
                groupTodoService.getOneGroupMemberTodo(loginMember.getId(), MEMBER_ID, GROUP_ID, memberTodo.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(NOT_JOINED_MEMBER_IN_GROUP);
    }

    @DisplayName("GroupTodo의 변경이 제대로 이루어져야 한다.")
    @Test
    void updateTodo(){
        //given
        GroupMember groupLeader = GroupMember.createGroupLeader(member, group);
        groupMemberRepository.save(groupLeader);

        GroupTodo groupTodo = GroupTodo.builder()
                .title("groupTodo")
                .group(group)
                .todoCategory(groupTodoCategory)
                .build();

        todoRepository.save(groupTodo);

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("new groupTodo")
                .groupId(GROUP_ID)
                .categoryId(GROUP_TODO_CATEGORY_ID)
                .build();

        //when
        TodoResponseDto responseDto =
                groupTodoService.updateTodo(MEMBER_ID, GROUP_ID, groupTodo.getId(), requestDto);

        //then
        assertThat(responseDto.getTodoId()).isEqualTo(groupTodo.getId());
        assertThat(groupTodo.getTitle()).isEqualTo(requestDto.getTitle());
    }

    @DisplayName("Authority가 없는 경우, GroupTodo 변경 시 예외를 던진다.")
    @Test
    void updateTodo_Throw_Exception_If_Not_Authority(){
        //given
        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMemberRepository.save(groupMember);

        GroupTodo groupTodo = GroupTodo.builder()
                .title("groupTodo")
                .group(group)
                .todoCategory(groupTodoCategory)
                .build();

        todoRepository.save(groupTodo);

        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("new groupTodo")
                .groupId(GROUP_ID)
                .categoryId(GROUP_TODO_CATEGORY_ID)
                .build();

        //then
        assertThatThrownBy(() ->
                groupTodoService.updateTodo(MEMBER_ID, GROUP_ID, groupTodo.getId(), requestDto))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(DO_NOT_HAVE_TODO_AUTHORITY);
    }

    @DisplayName("GroupMember가 GroupTodo 완료를 클릭시, 제대로 값이 변경되어야 한다.")
    @Test
    void checkGroupTodo(){
        //given
        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMemberRepository.save(groupMember);

        GroupTodo groupTodo = GroupTodo.builder()
                .group(group)
                .todoCategory(groupTodoCategory)
                .build();
        todoRepository.save(groupTodo);

        GroupTodoCompletion groupTodoCompletion =
                GroupTodoCompletion.createGroupTodoCompletion(member, groupTodo);
        groupTodoCompletionRepository.save(groupTodoCompletion);

        //when
        TodoResponseDto responseDto
                = groupTodoService.checkGroupTodo(MEMBER_ID, GROUP_ID, groupTodo.getId());

        //then
        assertThat(responseDto.getTodoId()).isEqualTo(groupTodo.getId());
        assertThat(groupTodoCompletion.isCompletion()).isTrue();
    }
    
    @DisplayName("GroupTodo가 제대로 삭제되어야 한다.")
    @Test
    void deleteTodo(){
        //given
        GroupMember groupLeader = GroupMember.createGroupLeader(member, group);
        groupMemberRepository.save(groupLeader);

        GroupTodo groupTodo = GroupTodo.builder()
                .group(group)
                .todoCategory(groupTodoCategory)
                .build();

        todoRepository.save(groupTodo);

        //when
        TodoResponseDto responseDto
                = groupTodoService.deleteTodo(MEMBER_ID, GROUP_ID, groupTodo.getId());
        Todo findTodo = todoRepository.findById(groupTodo.getId()).orElse(null);

        //then
        assertThat(responseDto.getTodoId()).isEqualTo(groupTodo.getId());
        assertThat(findTodo).isNull();
    }

    @DisplayName("Authority가 없는 경우, GroupTodo 삭제시 예외를 던진다.")
    @Test
    void deleteTodo_Throw_Exception_If_Not_Authority() {
        //given
        GroupMember groupMember = GroupMember.createGroupMember(member, group);
        groupMemberRepository.save(groupMember);

        GroupTodo groupTodo = GroupTodo.builder()
                .group(group)
                .todoCategory(groupTodoCategory)
                .build();

        todoRepository.save(groupTodo);

        //then
        assertThatThrownBy(() -> groupTodoService.deleteTodo(MEMBER_ID, GROUP_ID, groupTodo.getId()))
                .isInstanceOf(PlanusException.class)
                .extracting("status")
                .isEqualTo(DO_NOT_HAVE_TODO_AUTHORITY);
    }
}