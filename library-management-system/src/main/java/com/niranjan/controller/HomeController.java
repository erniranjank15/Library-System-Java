package com.niranjan.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Welcome to the Library Management System API!");
        response.put("author", "Niranjan Kasote");
        response.put("endpoints", Map.of(
                "books", "/api/books",
                "members", "/api/members",
                "borrow", "/api/borrow"
        ));
        
        return response;
    }
}