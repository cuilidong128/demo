package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    // 测试


    
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
