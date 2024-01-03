package com.davsilvam.services;

import com.davsilvam.domain.user.User;
import com.davsilvam.exceptions.user.EmailAlreadyUsedException;
import com.davsilvam.infra.security.TokenService;
import com.davsilvam.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
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

    private AuthenticationManager authenticationManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(username);
    }

    public User register(String name, String email, String password) throws EmailAlreadyUsedException {
        if (userRepository.findByEmail(email) != null) {
            throw new EmailAlreadyUsedException("Email already used!");
        }

        String encryptedPassword = this.passwordEncoder.encode(password);
        User user = new User(name, email, encryptedPassword);

        return this.userRepository.save(user);
    }

    public String login(String email, String password) {
        authenticationManager = this.applicationContext.getBean(AuthenticationManager.class);

        try {
            UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(email, password);
            Authentication auth = this.authenticationManager.authenticate(usernamePassword);

            return this.tokenService.generateToken((User) auth.getPrincipal());
        } catch (AuthenticationException exception) {
            throw new UsernameNotFoundException("Invalid credentials.");
        }
    }
}
