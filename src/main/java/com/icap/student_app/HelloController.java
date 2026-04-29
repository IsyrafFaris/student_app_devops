package com.icap.student_app;   // change to your package

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Hello DevOps - Simple Spring Boot App";
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\": \"UP\"}";
    }
}