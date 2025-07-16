package com.gabidbr.ratelimitingdemo.service.mapper;

import com.gabidbr.ratelimitingdemo.api.dto.ExpenseDto;
import com.gabidbr.ratelimitingdemo.model.Expense;
import com.gabidbr.ratelimitingdemo.security.UserRepository;
import com.gabidbr.ratelimitingdemo.security.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpenseMapper {

    private final UserRepository userRepository;

    public Expense map(ExpenseDto dto, User user) {
        return Expense.builder()
                .name(dto.getName())
                .amount(dto.getAmount())
                .category(dto.getCategory())
                .date(dto.getDate())
                .isRecurring(dto.isRecurring())
                .user(user)
                .build();
    }

    public ExpenseDto toDto(Expense expense) {
        return ExpenseDto.builder()
                .userId(expense.getUser().getId())
                .name(expense.getName())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .date(expense.getDate())
                .isRecurring(expense.isRecurring())
                .build();
    }
}
