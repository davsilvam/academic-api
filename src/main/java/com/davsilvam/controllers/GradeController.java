package com.davsilvam.controllers;

import com.davsilvam.dtos.grade.CreateGradeRequest;
import com.davsilvam.dtos.grade.GradeResponse;
import com.davsilvam.dtos.grade.UpdateGradeRequest;
import com.davsilvam.services.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("grades")
@RequiredArgsConstructor
public class GradeController {
    private final GradeService gradeService;

    @GetMapping("{id}")
    public ResponseEntity<GradeResponse> get(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        GradeResponse response = this.gradeService.get(id, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("subject/{subjectId}")
    public ResponseEntity<List<GradeResponse>> fetch(@PathVariable("subjectId") UUID subjectId, @AuthenticationPrincipal UserDetails userDetails) {
        List<GradeResponse> response = this.gradeService.fetch(subjectId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<GradeResponse> create(@RequestBody CreateGradeRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        GradeResponse response = this.gradeService.create(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<GradeResponse> update(@PathVariable("id") UUID id, @RequestBody UpdateGradeRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        GradeResponse response = this.gradeService.update(id, request, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        this.gradeService.delete(id, userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
