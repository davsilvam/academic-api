package com.davsilvam.repositories;

import com.davsilvam.domain.absence.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AbsenceRepository extends JpaRepository<Absence, UUID> {
    List<Absence> findAllBySubjectId(UUID subjectId);
}
