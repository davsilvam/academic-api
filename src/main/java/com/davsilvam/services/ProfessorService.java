package com.davsilvam.services;

import com.davsilvam.domain.professor.Professor;
import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.professor.CreateProfessorRequest;
import com.davsilvam.dtos.professor.ProfessorResponse;
import com.davsilvam.dtos.professor.UpdateProfessorRequest;
import com.davsilvam.exceptions.professor.ProfessorNotFoundException;
import com.davsilvam.exceptions.user.UserUnauthorizedException;
import com.davsilvam.repositories.professor.ProfessorRepository;
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
public class ProfessorService {
    private final ProfessorRepository professorRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    public ProfessorResponse get(UUID id, @NotNull UserDetails userDetails) throws ProfessorNotFoundException, UserUnauthorizedException {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Professor professor = this.professorRepository.findById(id).orElseThrow(() -> new ProfessorNotFoundException("Professor not found."));

        if (!professor.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        return new ProfessorResponse(professor);
    }

    public List<ProfessorResponse> fetch(@NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        return this.professorRepository.findAllByUserId(user.getId()).stream().map(ProfessorResponse::new).toList();
    }

    public ProfessorResponse create(@NotNull CreateProfessorRequest request, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Professor professor = new Professor(request.name(), request.email(), user);

        Professor createdProfessor = this.professorRepository.save(professor);

        return new ProfessorResponse(createdProfessor);
    }

    public ProfessorResponse update(UUID id, @NotNull UpdateProfessorRequest request, @NotNull UserDetails userDetails) throws ProfessorNotFoundException, UserUnauthorizedException {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Professor professor = this.professorRepository.findById(id).orElseThrow(() -> new ProfessorNotFoundException("Professor not found."));

        if (!professor.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        professor.setName(request.name().orElse(professor.getName()));
        professor.setEmail(request.email().orElse(professor.getEmail()));

        return new ProfessorResponse(this.professorRepository.save(professor));
    }

    public void delete(UUID id, @NotNull UserDetails userDetails) throws ProfessorNotFoundException, UserUnauthorizedException {
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
