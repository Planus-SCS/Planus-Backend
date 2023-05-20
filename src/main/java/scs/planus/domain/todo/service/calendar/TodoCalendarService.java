package scs.planus.domain.todo.service.calendar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.domain.group.dto.mygroup.GroupBelongInResponseDto;
import scs.planus.domain.group.service.MyGroupService;
import scs.planus.domain.member.entity.Member;
import scs.planus.domain.member.repository.MemberRepository;
import scs.planus.domain.todo.dto.TodoDetailsResponseDto;
import scs.planus.domain.todo.entity.MemberTodo;
import scs.planus.domain.todo.repository.TodoQueryRepository;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.util.validator.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static scs.planus.global.exception.CustomExceptionStatus.NONE_USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TodoCalendarService {

    private final MyGroupService myGroupService;
    private final MemberRepository memberRepository;
    private final TodoQueryRepository todoQueryRepository;

    public List<TodoDetailsResponseDto> getPeriodDetailTodos(Long memberId, LocalDate from, LocalDate to) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PlanusException(NONE_USER));

        Validator.validateStartDateBeforeEndDate(from, to);
        List<MemberTodo> todos = todoQueryRepository.findPeriodMemberTodosByDate(member.getId(), from, to);
        List<TodoDetailsResponseDto> responseDtos = todos.stream()
                .map(TodoDetailsResponseDto::of)
                .collect(Collectors.toList());

        return responseDtos;
    }

    public List<GroupBelongInResponseDto> getAllMyGroup(Long memberId) {
        List<GroupBelongInResponseDto> responseDtos = myGroupService.getMyGroupsInDropDown(memberId);
        return responseDtos;
    }
}
