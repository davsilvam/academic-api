package com.davsilvam.controllers;

import com.davsilvam.domain.subject.Subject;
import com.davsilvam.dtos.subject.CreateSubjectRequest;
import com.davsilvam.dtos.subject.CreateSubjectResponse;
import com.davsilvam.dtos.subject.GetSubjectResponse;
import com.davsilvam.infra.security.TokenService;
import com.davsilvam.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/subjects")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;

    @Autowired
    private TokenService tokenService;

    @GetMapping("{id}")
    public ResponseEntity<GetSubjectResponse> get(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        Subject subject = this.subjectService.get(id, userDetails);
        GetSubjectResponse response = new GetSubjectResponse(subject.getId(), subject.getName(), subject.getDescription(), subject.getUser().getId());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GetSubjectResponse>> fetch(@AuthenticationPrincipal UserDetails userDetails) {
        Set<Subject> subjects = this.subjectService.fetch(userDetails);
        List<GetSubjectResponse> response = subjects.stream().map(subject -> new GetSubjectResponse(subject.getId(), subject.getName(), subject.getDescription(), subject.getUser().getId())).toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<CreateSubjectResponse> create(@RequestBody CreateSubjectRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Subject createdSubject = this.subjectService.create(request, userDetails);
        CreateSubjectResponse response = new CreateSubjectResponse(createdSubject.getId(), createdSubject.getName(), createdSubject.getDescription(), createdSubject.getUser().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
