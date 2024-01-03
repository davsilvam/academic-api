package com.davsilvam.services;

import com.davsilvam.domain.subject.Subject;
import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.subject.CreateSubjectRequest;
import com.davsilvam.repositories.SubjectRepository;
import com.davsilvam.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public Subject get(UUID id, UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Subject subject = this.subjectRepository.findById(id).orElse(null);

        if (subject == null) {
            throw new RuntimeException("Subject not found.");
        }

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User not allowed to access this subject.");
        }

        return subject;
    }

    public Set<Subject> fetch(UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        return this.subjectRepository.findAllByUserId(user.getId());
    }

    public Subject create(CreateSubjectRequest request, UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Subject subject = new Subject(request.name(), request.description(), user);

        return this.subjectRepository.save(subject);
    }
}
