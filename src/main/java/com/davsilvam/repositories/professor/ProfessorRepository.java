package com.davsilvam.repositories.professor;

import com.davsilvam.domain.professor.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {
    List<Professor> findAllByUserId(UUID userId);
}
