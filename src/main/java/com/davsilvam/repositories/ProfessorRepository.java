package com.davsilvam.repositories;

import com.davsilvam.domain.professor.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {
    Set<Professor> findAllByUserId(UUID userId);
}
