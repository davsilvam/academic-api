package com.davsilvam.services;

import com.davsilvam.domain.professor.Professor;
import com.davsilvam.domain.subject.Subject;
import com.davsilvam.domain.user.User;
import com.davsilvam.domain.subject.dtos.CreateSubjectRequest;
import com.davsilvam.domain.subject.dtos.UpdateSubjectProfessorsRequest;
import com.davsilvam.domain.subject.dtos.UpdateSubjectRequest;
import com.davsilvam.domain.subject.exceptions.SubjectNotFoundException;
import com.davsilvam.domain.user.exceptions.UserUnauthorizedException;
import com.davsilvam.repositories.ProfessorRepository;
import com.davsilvam.repositories.SubjectRepository;
import com.davsilvam.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;

    public Subject get(UUID id, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Subject subject = this.subjectRepository.findById(id).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        return subject;
    }

    public List<Subject> fetch(@NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        return this.subjectRepository.findAllByUserId(user.getId());
    }

    public Subject create(@NotNull CreateSubjectRequest request, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        List<Professor> professors = this.professorRepository.findAllById(request.professors_ids());

        Subject subject = new Subject(request.name(), request.description(), user);
        subject.setProfessors(professors);

        if (!professors.isEmpty()) {
            professors.forEach(professor -> professor.addSubject(subject));
        }

        return this.subjectRepository.save(subject);
    }

    public Subject update(UUID id, @NotNull UpdateSubjectRequest request, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Subject subject = this.subjectRepository.findById(id).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        subject.setName(request.name().orElse(subject.getName()));
        subject.setDescription(request.description().orElse(subject.getDescription()));

        return this.subjectRepository.save(subject);
    }

    public Subject updateProfessors(UUID id, @NotNull UpdateSubjectProfessorsRequest request, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Subject subject = this.subjectRepository.findById(id).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        List<Professor> subjectProfessors = subject.getProfessors();

        if (subjectProfessors != null) {
            subjectProfessors.forEach(professor -> professor.removeSubject(subject));
        }

        List<Professor> professors = this.professorRepository.findAllById(request.professors_ids());
        subject.setProfessors(professors);
        professors.forEach(professor -> professor.addSubject(subject));

        return this.subjectRepository.save(subject);
    }

    public void delete(UUID id, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Subject subject = this.subjectRepository.findById(id).orElse(null);

        if (subject == null) {
            throw new SubjectNotFoundException("Subject not found.");
        }

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        subject.getProfessors().forEach(professor -> {
            professor.removeSubject(subject);
            this.professorRepository.save(professor);
        });

        this.subjectRepository.delete(subject);
    }
}
