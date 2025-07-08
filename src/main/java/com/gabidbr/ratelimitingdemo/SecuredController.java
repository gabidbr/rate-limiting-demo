package com.gabidbr.ratelimitingdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecuredController {

    @GetMapping("/public/hello")
    public String publicHello() {
        return "Hello from a public endpoint!";
    }

    @GetMapping("/secure/hello")
    public String secureHello() {
        return "Hello from a secured endpoint!";
    }
}
