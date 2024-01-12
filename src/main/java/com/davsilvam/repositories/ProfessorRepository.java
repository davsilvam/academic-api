package com.davsilvam.repositories;

import com.davsilvam.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {
    List<Professor> findAllByUserId(UUID userId);
}
