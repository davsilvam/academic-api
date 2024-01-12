package com.davsilvam.services;

import com.davsilvam.domain.grades.Grade;
import com.davsilvam.domain.subject.Subject;
import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.grade.CreateGradeRequest;
import com.davsilvam.dtos.grade.GradeResponse;
import com.davsilvam.dtos.grade.UpdateGradeRequest;
import com.davsilvam.exceptions.grade.GradeNotFoundException;
import com.davsilvam.exceptions.subject.SubjectNotFoundException;
import com.davsilvam.exceptions.user.UserUnauthorizedException;
import com.davsilvam.repositories.grade.GradeRepository;
import com.davsilvam.repositories.subject.SubjectRepository;
import com.davsilvam.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeService;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    public GradeResponse get(UUID id, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Grade grade = this.gradeService.findById(id).orElseThrow(() -> new GradeNotFoundException("Grade not found."));

        Subject subject = this.subjectRepository.findById(grade.getSubject().getId()).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        return new GradeResponse(grade);
    }

    public List<GradeResponse> fetch(UUID subjectId, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Subject subject = this.subjectRepository.findById(subjectId).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        List<Grade> grades = this.gradeService.findAllBySubjectId(subjectId);

        return grades.stream().map(GradeResponse::new).toList();
    }

    public GradeResponse create(@NotNull CreateGradeRequest request, @NotNull UserDetails userDetails)  {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Subject subject = this.subjectRepository.findById(request.subject_id()).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        Grade grade = new Grade(request.name(), request.value(), subject);

        Grade createdGrade = this.gradeService.save(grade);

        return new GradeResponse(createdGrade);
    }

    public GradeResponse update(UUID id, @NotNull UpdateGradeRequest request, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Grade grade = this.gradeService.findById(id).orElseThrow(() -> new GradeNotFoundException("Grade not found."));

        Subject subject = this.subjectRepository.findById(request.subject_id()).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        grade.setName(request.name().orElse(grade.getName()));
        grade.setValue(request.value().orElse(grade.getValue()));

        return new GradeResponse(this.gradeService.save(grade));

    }

    public void delete(UUID id, @NotNull UserDetails userDetails)  {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Grade grade = this.gradeService.findById(id).orElseThrow(() -> new GradeNotFoundException("Grade not found."));

        Subject subject = this.subjectRepository.findById(grade.getSubject().getId()).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        this.gradeService.deleteById(id);
    }
}
