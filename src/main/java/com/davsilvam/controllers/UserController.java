package com.davsilvam.controllers;

import com.davsilvam.dtos.user.RegisterUserRequest;
import com.davsilvam.dtos.user.UserResponse;
import com.davsilvam.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("{id}")
    public UserResponse get(@PathVariable("id") UUID id) {
        return userService.get(id);
    }

    @PostMapping
    public UserResponse create(@RequestBody RegisterUserRequest request) {
        return userService.create(request);
    }
}
