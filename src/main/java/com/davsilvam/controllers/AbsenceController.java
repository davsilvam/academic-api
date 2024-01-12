package com.davsilvam.controllers;

import com.davsilvam.dtos.absence.AbsenceResponse;
import com.davsilvam.dtos.absence.CreateAbsenceRequest;
import com.davsilvam.dtos.absence.UpdateAbsenceRequest;
import com.davsilvam.services.AbsenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("absences")
@RequiredArgsConstructor
public class AbsenceController {
    private final AbsenceService absenceService;

    @GetMapping("{id}")
    public ResponseEntity<AbsenceResponse> get(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        AbsenceResponse response = this.absenceService.get(id, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("subject/{subjectId}")
    public ResponseEntity<List<AbsenceResponse>> fetch(@PathVariable("subjectId") UUID subjectId, @AuthenticationPrincipal UserDetails userDetails) {
        List<AbsenceResponse> response = this.absenceService.fetch(subjectId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<AbsenceResponse> create(@RequestBody CreateAbsenceRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        AbsenceResponse response = this.absenceService.create(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<AbsenceResponse> update(@PathVariable("id") UUID id, @RequestBody UpdateAbsenceRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        AbsenceResponse response = this.absenceService.update(id, request, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        this.absenceService.delete(id, userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
