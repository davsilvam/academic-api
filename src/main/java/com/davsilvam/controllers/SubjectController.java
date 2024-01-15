package com.davsilvam.controllers;

import com.davsilvam.domain.subject.Subject;
import com.davsilvam.domain.subject.dtos.CreateSubjectRequest;
import com.davsilvam.domain.subject.dtos.UpdateSubjectProfessorsRequest;
import com.davsilvam.domain.subject.dtos.UpdateSubjectRequest;
import com.davsilvam.services.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping("{id}")
    public ResponseEntity<Subject> get(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        Subject response = this.subjectService.get(id, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<Subject>> fetch(@AuthenticationPrincipal UserDetails userDetails) {
        List<Subject> response = this.subjectService.fetch(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<Subject> create(@RequestBody CreateSubjectRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Subject response = this.subjectService.create(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<Subject> update(@PathVariable("id") UUID id, @RequestBody UpdateSubjectRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Subject response = this.subjectService.update(id, request, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("{id}/professors")
    public ResponseEntity<Subject> updateProfessors(@PathVariable("id") UUID id, @RequestBody UpdateSubjectProfessorsRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Subject response = this.subjectService.updateProfessors(id, request, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        this.subjectService.delete(id, userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
