package com.davsilvam.services;

import com.davsilvam.domain.professor.Professor;
import com.davsilvam.domain.subject.Subject;
import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.subject.CreateSubjectRequest;
import com.davsilvam.dtos.subject.SubjectResponse;
import com.davsilvam.dtos.subject.UpdateSubjectProfessorsRequest;
import com.davsilvam.dtos.subject.UpdateSubjectRequest;
import com.davsilvam.exceptions.subject.SubjectNotFoundException;
import com.davsilvam.exceptions.user.UserUnauthorizedException;
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

    public SubjectResponse get(UUID id, @NotNull UserDetails userDetails) throws SubjectNotFoundException, UserUnauthorizedException {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Subject subject = this.subjectRepository.findById(id).orElse(null);

        if (subject == null) {
            throw new SubjectNotFoundException("Subject not found.");
        }

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        return new SubjectResponse(subject);
    }

    public List<SubjectResponse> fetch(@NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        return this.subjectRepository.findAllByUserId(user.getId()).stream().map(SubjectResponse::new).toList();
    }

    public SubjectResponse create(@NotNull CreateSubjectRequest request, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        List<Professor> professors = this.professorRepository.findAllById(request.professors_ids());

        Subject subject = new Subject(request.name(), request.description(), user);
        subject.setProfessors(professors);

        if (!professors.isEmpty()) {
            professors.forEach(professor -> professor.addSubject(subject));
        }

        Subject createdSubject = this.subjectRepository.save(subject);

        return new SubjectResponse(createdSubject);
    }

    public SubjectResponse update(UUID id, @NotNull UpdateSubjectRequest request, @NotNull UserDetails userDetails) throws UserUnauthorizedException {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Subject subject = this.subjectRepository.findById(id).orElse(null);

        if (subject == null) {
            throw new SubjectNotFoundException("Subject not found.");
        }

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        subject.setName(request.name().orElse(subject.getName()));
        subject.setDescription(request.description().orElse(subject.getDescription()));

        return new SubjectResponse(this.subjectRepository.save(subject));
    }

    public SubjectResponse updateProfessors(UUID id, @NotNull UpdateSubjectProfessorsRequest request, @NotNull UserDetails userDetails) throws UserUnauthorizedException {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Subject subject = this.subjectRepository.findById(id).orElse(null);

        if (subject == null) {
            throw new SubjectNotFoundException("Subject not found.");
        }

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

        return new SubjectResponse(this.subjectRepository.save(subject));
    }

    public void delete(UUID id, @NotNull UserDetails userDetails) throws UserUnauthorizedException {
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
