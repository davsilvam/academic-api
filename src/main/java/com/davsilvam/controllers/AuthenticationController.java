package com.davsilvam.controllers;

import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.authentication.LoginRequest;
import com.davsilvam.dtos.authentication.LoginResponse;
import com.davsilvam.dtos.authentication.RegisterRequest;
import com.davsilvam.dtos.authentication.RegisterResponse;
import com.davsilvam.infra.security.TokenService;
import com.davsilvam.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        String token = this.tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(request.password());
        User user = new User(request.name(), request.email(), encryptedPassword);

        RegisterResponse response = this.authenticationService.register(user.getName(), user.getEmail(), user.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
