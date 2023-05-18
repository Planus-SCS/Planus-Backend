package scs.planus.domain.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.group.entity.GroupMember;
import scs.planus.domain.group.repository.GroupMemberRepository;
import scs.planus.domain.group.repository.GroupRepository;
import scs.planus.domain.todo.dto.TodoPeriodResponseDto;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.util.validator.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static scs.planus.global.exception.CustomExceptionStatus.NOT_EXIST_GROUP;
import static scs.planus.global.exception.CustomExceptionStatus.NOT_JOINED_GROUP;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupTodoCalendarService {

    private final TodoQueryRepository todoQueryRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public List<TodoPeriodResponseDto> getPeriodGroupTodos(Long memberId, Long groupId, LocalDate from, LocalDate to) {
        GroupMember groupMember = groupMemberRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> {
                    groupRepository.findById(groupId)
                            .orElseThrow(() -> new PlanusException(NOT_EXIST_GROUP));
                    return new PlanusException(NOT_JOINED_GROUP);
                });

        Validator.validateStartDateBeforeEndDate(from, to);

        List<GroupTodo> todos = todoQueryRepository.findPeriodGroupTodosByDate(groupId, from, to);
        List<TodoPeriodResponseDto> responseDtos = todos.stream()
                .map(TodoPeriodResponseDto::of)
                .collect(Collectors.toList());
        return responseDtos;
    }
}
