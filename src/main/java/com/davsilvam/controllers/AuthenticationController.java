package com.davsilvam.controllers;

import com.davsilvam.dtos.authentication.LoginRequest;
import com.davsilvam.dtos.authentication.LoginResponse;
import com.davsilvam.dtos.authentication.RegisterRequest;
import com.davsilvam.dtos.authentication.RegisterResponse;
import com.davsilvam.services.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthorizationService authorizationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@NotNull @RequestBody RegisterRequest request) {
        RegisterResponse response = this.authorizationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@NotNull @RequestBody LoginRequest request) {
        LoginResponse response = this.authorizationService.login(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
