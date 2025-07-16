package com.gabidbr.ratelimitingdemo.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExpenseDto {

    private String name;
    private Double amount;
    private String category;
    private LocalDateTime date;
    private boolean isRecurring;
    private Long userId;
}
