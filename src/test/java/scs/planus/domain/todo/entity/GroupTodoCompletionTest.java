package scs.planus.domain.todo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scs.planus.domain.member.entity.Member;

import static org.assertj.core.api.Assertions.assertThat;

class GroupTodoCompletionTest {

    @DisplayName("그룹 투두 완료 상태를 true에서 false로 변경할 수 있어야 한다.")
    @Test
    void changeCompletion_From_True_To_False(){
        //given
        GroupTodoCompletion groupTodoCompletion = GroupTodoCompletion.builder()
                .completion(true)
                .build();

        //when
        groupTodoCompletion.changeCompletion();

        //then
        assertThat(groupTodoCompletion.isCompletion()).isFalse();
    }

    @DisplayName("그룹 투두 완료 상태를 false에서 true로 변경할 수 있어야 한다.")
    @Test
    void changeCompletion_From_False_To_True(){
        //given
        GroupTodoCompletion groupTodoCompletion = GroupTodoCompletion.builder()
                .completion(false)
                .build();

        //when
        groupTodoCompletion.changeCompletion();

        //then
        assertThat(groupTodoCompletion.isCompletion()).isTrue();
    }

    @DisplayName("그룹 투두 완료가 제대로 생성되어야 한다.")
    @Test
    void createGroupTodoCompletion(){
        //given
        Member member = Member.builder()
                .name("member")
                .build();

        GroupTodo groupTodo = GroupTodo.builder()
                .title("groupTodo")
                .build();

        //when
        GroupTodoCompletion groupTodoCompletion = GroupTodoCompletion.createGroupTodoCompletion(member, groupTodo);

        //then
        assertThat(groupTodoCompletion.getMember()).isEqualTo(member);
        assertThat(groupTodoCompletion.getGroupTodo()).isEqualTo(groupTodo);
    }

}