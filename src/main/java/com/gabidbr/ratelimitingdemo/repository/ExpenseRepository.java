package com.gabidbr.ratelimitingdemo.repository;

import com.gabidbr.ratelimitingdemo.model.Expense;
import com.gabidbr.ratelimitingdemo.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByUser(User user);
}
