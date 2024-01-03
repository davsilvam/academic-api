package com.davsilvam.services;

import com.davsilvam.domain.subject.Subject;
import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.subject.CreateSubjectRequest;
import com.davsilvam.dtos.subject.UpdateSubjectRequest;
import com.davsilvam.exceptions.subjects.SubjectNotFoundException;
import com.davsilvam.exceptions.subjects.UserUnauthorizedException;
import com.davsilvam.repositories.SubjectRepository;
import com.davsilvam.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public Subject get(UUID id, @NotNull UserDetails userDetails) throws SubjectNotFoundException, UserUnauthorizedException {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Subject subject = this.subjectRepository.findById(id).orElse(null);

        if (subject == null) {
            throw new SubjectNotFoundException("Subject not found.");
        }

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        return subject;
    }

    public Set<Subject> fetch(@NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        return this.subjectRepository.findAllByUserId(user.getId());
    }

    public Subject create(@NotNull CreateSubjectRequest request, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Subject subject = new Subject(request.name(), request.description(), user);

        return this.subjectRepository.save(subject);
    }

    public Subject update(UUID id, @NotNull UpdateSubjectRequest request, @NotNull UserDetails userDetails) throws UserUnauthorizedException {
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

        return this.subjectRepository.save(subject);
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

        this.subjectRepository.delete(subject);
    }
}
