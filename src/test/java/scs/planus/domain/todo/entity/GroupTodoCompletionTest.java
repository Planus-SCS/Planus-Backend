package scs.planus.domain.todo.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        Assertions.assertThat(groupTodoCompletion.isCompletion()).isFalse();
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
        Assertions.assertThat(groupTodoCompletion.isCompletion()).isTrue();
    }

}