package com.gabidbr.ratelimitingdemo.model;

import com.gabidbr.ratelimitingdemo.security.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "subscription", indexes = {
        @Index(name = "idx_subs_user_id", columnList = "user_id"),
        @Index(name = "idx_user_renewal_date", columnList = "user_id, renewal_date")
})
public class Subscription {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private LocalDateTime renewalDate;

    private String category;

    private String provider;

    private Integer alertBeforeDays;
}

