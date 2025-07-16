package com.gabidbr.ratelimitingdemo.service;

import com.gabidbr.ratelimitingdemo.api.dto.ExpenseDto;
import com.gabidbr.ratelimitingdemo.model.Expense;
import com.gabidbr.ratelimitingdemo.repository.ExpenseRepository;
import com.gabidbr.ratelimitingdemo.security.entity.User;
import com.gabidbr.ratelimitingdemo.service.mapper.ExpenseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseDto saveExpense(ExpenseDto expenseDto, User user) {
        Expense expense = expenseMapper.map(expenseDto, user);
        Expense savedExpense = expenseRepository.save(expense);
        return expenseMapper.toDto(savedExpense);
    }

    public List<ExpenseDto> getExpensesByUser(User user) {
        return expenseRepository.findAllByUser(user)
                .stream().map(expenseMapper::toDto).toList();
    }
}
