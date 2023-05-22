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
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.dto.calendar.AllTodoResponseDto;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.entity.GroupTodo;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.global.util.validator.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TodoCalendarService {

    private final MyGroupService myGroupService;
    private final MemberRepository memberRepository;
    private final TodoQueryRepository todoQueryRepository;
    private final GroupMemberRepository groupMemberRepository;

    public AllTodoResponseDto getPeriodDetailTodos(Long memberId, LocalDate from, LocalDate to) {
        Validator.validateStartDateBeforeEndDate(from, to);

        // 내가 속한 모든 그룹 조회
        List<GroupMember> groupMembers = groupMemberRepository.findAllByActiveGroupAndMemberId(memberId);
        List<Group> groups = groupMembers.stream()
                .map(GroupMember::getGroup)
                .collect(Collectors.toList());

        List<MemberTodo> todos = todoQueryRepository.findPeriodMemberTodosByDate(memberId, from, to);
        List<GroupTodo> groupTodos = todoQueryRepository.findAllPeriodGroupTodos(groups, from, to);

        List<TodoDetailsResponseDto> memberTodos = todos.stream()
                .map(TodoDetailsResponseDto::of)
                .collect(Collectors.toList());

        List<TodoDetailsResponseDto> myGroupTodos = groupTodos.stream()
                .map(TodoDetailsResponseDto::of)
                .collect(Collectors.toList());
        return AllTodoResponseDto.of(memberTodos, myGroupTodos);
    }

    public List<GroupBelongInResponseDto> getAllMyGroup(Long memberId) {
        List<GroupBelongInResponseDto> responseDtos = myGroupService.getMyGroupsInDropDown(memberId);
        return responseDtos;
    }
}
