package com.davsilvam.repositories;

import com.davsilvam.domain.grade.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GradeRepository extends JpaRepository<Grade, UUID> {
    List<Grade> findAllBySubjectId(UUID subjectId);
}
