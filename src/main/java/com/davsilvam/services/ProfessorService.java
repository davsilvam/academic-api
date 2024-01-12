package com.davsilvam.services;

import com.davsilvam.domain.Professor;
import com.davsilvam.domain.User;
import com.davsilvam.dtos.professor.CreateProfessorRequest;
import com.davsilvam.dtos.professor.UpdateProfessorRequest;
import com.davsilvam.exceptions.professor.ProfessorNotFoundException;
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
public class ProfessorService {
    private final ProfessorRepository professorRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    public Professor get(UUID id, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Professor professor = this.professorRepository.findById(id).orElseThrow(() -> new ProfessorNotFoundException("Professor not found."));

        if (!professor.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        return professor;
    }

    public List<Professor> fetch(@NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        return this.professorRepository.findAllByUserId(user.getId());
    }

    public Professor create(@NotNull CreateProfessorRequest request, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Professor professor = new Professor(request.name(), request.email(), user);

        return this.professorRepository.save(professor);
    }

    public Professor update(UUID id, @NotNull UpdateProfessorRequest request, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Professor professor = this.professorRepository.findById(id).orElseThrow(() -> new ProfessorNotFoundException("Professor not found."));

        if (!professor.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        professor.setName(request.name().orElse(professor.getName()));
        professor.setEmail(request.email().orElse(professor.getEmail()));

        return this.professorRepository.save(professor);
    }

    public void delete(UUID id, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Professor professor = this.professorRepository.findById(id).orElseThrow(() -> new ProfessorNotFoundException("Professor not found."));

        if (!professor.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        professor.getSubjects().forEach(subject -> {
            subject.removeProfessor(professor);
            this.subjectRepository.save(subject);
        });

        this.professorRepository.delete(professor);
    }
}
