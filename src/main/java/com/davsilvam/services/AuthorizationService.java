package com.davsilvam.services;

import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.authentication.LoginRequest;
import com.davsilvam.dtos.authentication.LoginResponse;
import com.davsilvam.dtos.authentication.RegisterRequest;
import com.davsilvam.dtos.authentication.RegisterResponse;
import com.davsilvam.exceptions.user.EmailAlreadyUsedException;
import com.davsilvam.infra.security.TokenService;
import com.davsilvam.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {
    private final ApplicationContext applicationContext;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(username);
    }

    public RegisterResponse register(@NotNull RegisterRequest request) throws EmailAlreadyUsedException {
        if (userRepository.findByEmail(request.email()) != null) {
            throw new EmailAlreadyUsedException("Email already used!");
        }

        String encryptedPassword = this.passwordEncoder.encode(request.password());
        User user = new User(request.name(), request.email(), encryptedPassword);

        User createdUser = this.userRepository.save(user);

        return new RegisterResponse(createdUser.getId(), createdUser.getName(), createdUser.getEmail());
    }

    public LoginResponse login(@NotNull LoginRequest request) {
        AuthenticationManager authenticationManager = this.applicationContext.getBean(AuthenticationManager.class);

        try {
            UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(request.email(), request.password());
            Authentication auth = authenticationManager.authenticate(usernamePassword);

            return new LoginResponse(this.tokenService.generateToken((User) auth.getPrincipal()));
        } catch (AuthenticationException exception) {
            throw new UsernameNotFoundException("Invalid credentials.");
        }
    }
}
