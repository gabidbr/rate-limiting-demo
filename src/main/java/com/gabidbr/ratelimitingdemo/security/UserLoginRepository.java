package com.gabidbr.ratelimitingdemo.security;

import com.gabidbr.ratelimitingdemo.security.entity.User;
import com.gabidbr.ratelimitingdemo.security.entity.UserLogin;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;

public interface UserLoginRepository extends CrudRepository<UserLogin, Long> {

    List<UserLogin> findByUser_Username(String username);
    List<UserLogin> findByUserAndLoginTimestampAfter(User user, Instant loginTimestampAfter);
}
