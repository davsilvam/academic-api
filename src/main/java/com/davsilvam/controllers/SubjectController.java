package com.davsilvam.controllers;

import com.davsilvam.domain.subject.Subject;
import com.davsilvam.dtos.subject.CreateSubjectRequest;
import com.davsilvam.dtos.subject.SubjectResponse;
import com.davsilvam.dtos.subject.UpdateSubjectProfessorsRequest;
import com.davsilvam.dtos.subject.UpdateSubjectRequest;
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
    public ResponseEntity<SubjectResponse> get(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        Subject subject = this.subjectService.get(id, userDetails);
        SubjectResponse response = new SubjectResponse(subject);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> fetch(@AuthenticationPrincipal UserDetails userDetails) {
        List<Subject> subjects = this.subjectService.fetch(userDetails);
        List<SubjectResponse> response = subjects.stream().map(SubjectResponse::new).toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<SubjectResponse> create(@RequestBody CreateSubjectRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Subject createdSubject = this.subjectService.create(request, userDetails);
        SubjectResponse response = new SubjectResponse(createdSubject);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<SubjectResponse> update(@PathVariable("id") UUID id, @RequestBody UpdateSubjectRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Subject updatedSubject = this.subjectService.update(id, request, userDetails);
        SubjectResponse response = new SubjectResponse(updatedSubject);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("{id}/professors")
    public ResponseEntity<SubjectResponse> updateProfessors(@PathVariable("id") UUID id, @RequestBody UpdateSubjectProfessorsRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Subject updatedSubject = this.subjectService.updateProfessors(id, request, userDetails);
        SubjectResponse response = new SubjectResponse(updatedSubject);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        this.subjectService.delete(id, userDetails);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
