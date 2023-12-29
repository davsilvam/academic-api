package com.davsilvam.repositories;

import com.davsilvam.domain.subject.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {
    Set<Subject> findAllByUserId(UUID userId);
}
