package scs.planus.domain.todo.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.domain.Status;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.GroupTodoCompletion;
import scs.planus.support.RepositoryTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GroupTodoCompletionRepositoryTest extends RepositoryTest {

    private Member member;

    private Group group;

    private GroupTodo groupTodo;

    private GroupTodoCompletion groupTodoCompletion;

    @BeforeEach
    void init() {
        member = memberRepository.findById(1L).orElse(null);

        group = Group.builder()
                .status(Status.ACTIVE)
                .build();
        groupRepository.save(group);

        groupTodo = GroupTodo.builder()
                .group(group)
                .build();
        todoRepository.save(groupTodo);

        groupTodoCompletion = GroupTodoCompletion.createGroupTodoCompletion(member, groupTodo);
        groupTodoCompletionRepository.save(groupTodoCompletion);
    }

    @DisplayName("memberId와 todoId로, 단일 GroupTodoCompletion이 제대로 조회되어야 한다.")
    @Test
    void findByMemberIdAndTodoId(){
        //when
        groupTodoCompletion = groupTodoCompletionRepository
                .findByMemberIdAndTodoId(member.getId(), groupTodo.getId())
                .orElse(null);

        //then
        assertThat(groupTodoCompletion).isNotNull();
    }

    @DisplayName("사용자가 가지고 있는 GroupTodo들에 대한 모든 GroupTodoCompletion을 List 타입으로 조회한다.")
    @Test
    void findAllByMemberIdAndInGroupTodos(){
        //given
        GroupTodo groupTodo2 = GroupTodo.builder()
                .group(group)
                .build();
        todoRepository.save(groupTodo2);

        groupTodoCompletion = GroupTodoCompletion.createGroupTodoCompletion(member, groupTodo2);
        List<GroupTodo> groupTodos = List.of(groupTodo, groupTodo2);

        //when
        List<GroupTodoCompletion> groupTodoCompletions =
                groupTodoCompletionRepository.findAllByMemberIdAndInGroupTodos(member.getId(), groupTodos);

        //then
        assertThat(groupTodoCompletions.size()).isEqualTo(2);
        assertThat(groupTodoCompletions.get(0).getGroupTodo()).isEqualTo(groupTodo);
        assertThat(groupTodoCompletions.get(1).getGroupTodo()).isEqualTo(groupTodo2);
    }

    @DisplayName("memberId와 groupId로 List 타입의 GroupTodoCompletion가 조회되어야 한다.")
    @Test
    void findAllByMemberIdOnGroupId(){
        //when
        List<GroupTodoCompletion> groupTodoCompletions =
                groupTodoCompletionRepository.findAllByMemberIdOnGroupId(member.getId(), group.getId());

        //then
        assertThat(groupTodoCompletions).isNotEmpty();
        assertThat(groupTodoCompletions.get(0)).isEqualTo(groupTodoCompletion);
    }
}