package com.davsilvam.controllers;

import com.davsilvam.dtos.professor.CreateProfessorRequest;
import com.davsilvam.dtos.professor.ProfessorResponse;
import com.davsilvam.dtos.professor.UpdateProfessorRequest;
import com.davsilvam.services.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("professors")
@RequiredArgsConstructor
public class ProfessorController {
    private final ProfessorService professorService;

    @GetMapping("{id}")
    public ResponseEntity<ProfessorResponse> get(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        ProfessorResponse response = this.professorService.get(id, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProfessorResponse>> fetch(@AuthenticationPrincipal UserDetails userDetails) {
        List<ProfessorResponse> response = this.professorService.fetch(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<ProfessorResponse> create(@RequestBody CreateProfessorRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        ProfessorResponse response = this.professorService.create(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<ProfessorResponse> update(@PathVariable("id") UUID id, @RequestBody UpdateProfessorRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        ProfessorResponse response = this.professorService.update(id, request, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        this.professorService.delete(id, userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
