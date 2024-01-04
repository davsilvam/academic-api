package com.davsilvam.services;

import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.authentication.LoginRequest;
import com.davsilvam.dtos.authentication.RegisterRequest;
import com.davsilvam.exceptions.user.EmailAlreadyUsedException;
import com.davsilvam.infra.security.TokenService;
import com.davsilvam.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Authorization Service Tests")
class AuthorizationServiceTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthorizationService authorizationService;

    private AutoCloseable closeable;

    User mockUser;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockUser = new User(UUID.randomUUID(), "Test User", "test@example.com", "password");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("should be able to register a new user")
    void registerCase1() {
        mockUser.setPasswordHash("encryptedPassword");
        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password");

        when(userRepository.findByEmail(request.email())).thenReturn(null);
        when(passwordEncoder.encode(request.password())).thenReturn("encryptedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = authorizationService.register(request);

        assertNotNull(result);
        assertEquals(mockUser.getPasswordHash(), result.getPasswordHash());
        assertEquals(mockUser, result);
        verify(userRepository).findByEmail(request.email());
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("should not be able to register a new user with an email already used")
    void registerCase2() {
        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password");

        when(userRepository.findByEmail(request.email())).thenReturn(mockUser);

        assertThrows(EmailAlreadyUsedException.class, () -> authorizationService.register(request));
        verify(userRepository).findByEmail(request.email());
    }

    @Test
    @DisplayName("should be able to login with valid credentials")
    void loginCase1() {
        LoginRequest request = new LoginRequest("test@example.com", "password");

        when(applicationContext.getBean(AuthenticationManager.class)).thenReturn(authentication -> new UsernamePasswordAuthenticationToken(mockUser, null));
        when(tokenService.generateToken(mockUser)).thenReturn("token");

        String result = authorizationService.login(request);

        assertNotNull(result);
        assertEquals("token", result);
        verify(applicationContext).getBean(AuthenticationManager.class);
        verify(tokenService).generateToken(mockUser);
    }

    @Test
    @DisplayName("should not be able to login with invalid credentials")
    void loginCase2() {
        LoginRequest request = new LoginRequest("test@example.com", "password");

        when(applicationContext.getBean(AuthenticationManager.class)).thenReturn(authentication -> {
            throw new UsernameNotFoundException("Invalid credentials.");
        });

        assertThrows(UsernameNotFoundException.class, () -> authorizationService.login(request));
        verify(applicationContext).getBean(AuthenticationManager.class);
    }
}