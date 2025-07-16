package com.gabidbr.ratelimitingdemo.repository;

import com.gabidbr.ratelimitingdemo.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
