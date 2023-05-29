package scs.planus.domain.todo.service.calendar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.group.dto.mygroup.GroupBelongInResponseDto;
import scs.planus.domain.group.entity.Group;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.service.MyGroupService;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.dto.calendar.AllTodoResponseDto;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.GroupTodoCompletion;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.repository.GroupTodoCompletionRepository;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.util.validator.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static scs.planus.global.exception.CustomExceptionStatus.NOT_EXIST_GROUP_TODO_COMPLETION;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberTodoCalendarService {

    private final MyGroupService myGroupService;
    private final TodoQueryRepository todoQueryRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupTodoCompletionRepository groupTodoCompletionRepository;

    public AllTodoResponseDto getPeriodDetailTodos(Long memberId, LocalDate from, LocalDate to) {
        Validator.validateStartDateBeforeEndDate(from, to);

        List<GroupMember> groupMembers = groupMemberRepository.findAllByActiveGroupAndMemberId(memberId);
        List<Group> groups = groupMembers.stream()
                .map(GroupMember::getGroup)
                .collect(Collectors.toList());

        List<MemberTodo> todos = todoQueryRepository.findAllPeriodMemberTodosByDate(memberId, from, to);
        List<GroupTodo> groupTodos = todoQueryRepository.findAllPeriodGroupTodosByDate(groups, from, to);
        List<GroupTodoCompletion> groupTodoCompletions = groupTodoCompletionRepository.findAllByMemberIdAndInGroupTodos(memberId, groupTodos);

        List<TodoDetailsResponseDto> memberTodos = todos.stream()
                .map(TodoDetailsResponseDto::of)
                .collect(Collectors.toList());

        List<TodoDetailsResponseDto> myGroupTodos = groupTodos.stream()
                .map(todo -> {
                    GroupTodoCompletion todoCompletion = groupTodoCompletions.stream()
                            .filter(groupTodoCompletion -> groupTodoCompletion.getGroupTodo().equals(todo))
                            .findFirst()
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP_TODO_COMPLETION));
                    return TodoDetailsResponseDto.ofGroupTodo(todo, todoCompletion);
                })
                .collect(Collectors.toList());
        return AllTodoResponseDto.of(memberTodos, myGroupTodos);
    }

    public List<GroupBelongInResponseDto> getAllMyGroup(Long memberId) {
        List<GroupBelongInResponseDto> responseDtos = myGroupService.getMyGroupsInDropDown(memberId);
        return responseDtos;
    }
}
