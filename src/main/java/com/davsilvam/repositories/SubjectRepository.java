package com.davsilvam.repositories;

import com.davsilvam.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {
    List<Subject> findAllByUserId(UUID userId);
}
