package com.davsilvam.infra;


import com.davsilvam.exceptions.user.EmailAlreadyUsedException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @NotNull
    @ExceptionHandler(EmailAlreadyUsedException.class)
    private ResponseEntity<String> emailAlreadyUsedHandler(@NotNull EmailAlreadyUsedException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @NotNull
    @ExceptionHandler(UsernameNotFoundException.class)
    private ResponseEntity<String> usernameNotFoundHandler(@NotNull UsernameNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
