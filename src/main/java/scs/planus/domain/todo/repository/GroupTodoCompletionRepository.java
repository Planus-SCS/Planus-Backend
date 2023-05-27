package scs.planus.domain.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.GroupTodoCompletion;

import java.util.List;
import java.util.Optional;

public interface GroupTodoCompletionRepository extends JpaRepository<GroupTodoCompletion, Long> {

    @Query("select gtc from GroupTodoCompletion gtc " +
            "join gtc.member m " +
            "join gtc.groupTodo gt " +
            "where m.id= :memberId and gt.id = :todoId")
    Optional<GroupTodoCompletion> findByMemberIdAndTodoId(@Param("memberId") Long memberId,
                                                          @Param("todoId") Long todoId);

    @Query("select gtc from GroupTodoCompletion gtc " +
            "join gtc.member m " +
            "join gtc.groupTodo gt " +
            "where m.id= :memberId and gt in :todos")
    List<GroupTodoCompletion> findByMemberIdAndInGroupTodos(@Param("memberId") Long memberId,
                                                          @Param("todos")List<GroupTodo> groupTodos);
}
