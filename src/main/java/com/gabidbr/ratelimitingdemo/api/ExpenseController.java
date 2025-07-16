package com.gabidbr.ratelimitingdemo.api;

import com.gabidbr.ratelimitingdemo.api.dto.ExpenseDto;
import com.gabidbr.ratelimitingdemo.security.AuthenticatedUserProvider;
import com.gabidbr.ratelimitingdemo.security.entity.User;
import com.gabidbr.ratelimitingdemo.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseDto>> createExpense(@RequestBody @Validated ExpenseDto expenseDto) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        ExpenseDto saved = expenseService.saveExpense(expenseDto, user);
        ApiResponse<ExpenseDto> response = ApiResponse.success(saved, "Expense created", HttpStatus.CREATED);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseDto>>> getAllExpenses() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        List<ExpenseDto> expensesByUser = expenseService.getExpensesByUser(user);
        ApiResponse<List<ExpenseDto>> response = ApiResponse.success(expensesByUser, "Expense list returned", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
