package com.davsilvam.infra;


import com.davsilvam.domain.absence.exceptions.AbsenceNotFoundException;
import com.davsilvam.domain.absence.exceptions.InvalidAbsenceDateException;
import com.davsilvam.domain.grade.exceptions.GradeNotFoundException;
import com.davsilvam.domain.professor.exceptions.ProfessorNotFoundException;
import com.davsilvam.domain.subject.exceptions.SubjectNotFoundException;
import com.davsilvam.domain.user.exceptions.EmailAlreadyUsedException;
import com.davsilvam.domain.user.exceptions.UserUnauthorizedException;
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

    @NotNull
    @ExceptionHandler(SubjectNotFoundException.class)
    private ResponseEntity<String> subjectNotFoundHandler(@NotNull SubjectNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @NotNull
    @ExceptionHandler(ProfessorNotFoundException.class)
    private ResponseEntity<String> professorNotFoundHandler(@NotNull ProfessorNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @NotNull
    @ExceptionHandler(GradeNotFoundException.class)
    private ResponseEntity<String> gradeNotFoundHandler(@NotNull GradeNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @NotNull
    @ExceptionHandler(AbsenceNotFoundException.class)
    private ResponseEntity<String> absenceNotFoundHandler(@NotNull AbsenceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @NotNull
    @ExceptionHandler(InvalidAbsenceDateException.class)
    private ResponseEntity<String> invalidAbsenceDateHandler(@NotNull InvalidAbsenceDateException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @NotNull
    @ExceptionHandler(UserUnauthorizedException.class)
    private ResponseEntity<String> userUnauthorizedHandler(@NotNull UserUnauthorizedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }
}
