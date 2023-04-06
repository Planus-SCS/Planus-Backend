package scs.planus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scs.planus.common.exception.PlanusException;
import scs.planus.domain.Member;
import scs.planus.domain.TodoCategory;
import scs.planus.domain.todo.MemberTodo;
import scs.planus.dto.todo.TodoCreateRequestDto;
import scs.planus.dto.todo.TodoResponseDto;
import scs.planus.repository.CategoryRepository;
import scs.planus.repository.MemberRepository;
import scs.planus.repository.TodoRepository;

import java.time.LocalDate;

import static scs.planus.common.response.CustomResponseStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public TodoResponseDto createPrivateTodo(Long memberId, TodoCreateRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new PlanusException(NONE_USER));

        TodoCategory todoCategory = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new PlanusException(NOT_EXIST_CATEGORY));

        validateDate(requestDto.getStartDate(), requestDto.getEndDate());
        MemberTodo memberTodo = requestDto.toMemberTodoEntity(member, todoCategory);
        todoRepository.save(memberTodo);
        return TodoResponseDto.of(memberTodo);
    }

    private void validateDate(LocalDate startDate, LocalDate endDate) {
        if (endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new PlanusException(INVALID_DATE);
            }
        }
    }
}
