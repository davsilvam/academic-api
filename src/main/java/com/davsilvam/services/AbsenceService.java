package com.davsilvam.services;

import com.davsilvam.domain.Absence;
import com.davsilvam.domain.Subject;
import com.davsilvam.domain.User;
import com.davsilvam.dtos.absence.CreateAbsenceRequest;
import com.davsilvam.dtos.absence.UpdateAbsenceRequest;
import com.davsilvam.exceptions.absence.AbsenceNotFoundException;
import com.davsilvam.exceptions.absence.InvalidAbsenceDateException;
import com.davsilvam.exceptions.subject.SubjectNotFoundException;
import com.davsilvam.exceptions.user.UserUnauthorizedException;
import com.davsilvam.repositories.AbsenceRepository;
import com.davsilvam.repositories.SubjectRepository;
import com.davsilvam.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AbsenceService {
    private final AbsenceRepository absenceRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    public Absence get(UUID id, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Absence absence = this.absenceRepository.findById(id).orElseThrow(() -> new AbsenceNotFoundException("Absence not found."));

        Subject subject = this.subjectRepository.findById(absence.getSubject().getId()).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        return absence;
    }

    public List<Absence> fetch(UUID subjectId, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Subject subject = this.subjectRepository.findById(subjectId).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        return this.absenceRepository.findAllBySubjectId(subjectId);
    }

    public Absence create(@NotNull CreateAbsenceRequest request, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Subject subject = this.subjectRepository.findById(request.subject_id()).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(request.date());

            Date today = new Date();

            if (date.after(today)) {
                throw new InvalidAbsenceDateException("Absence date cannot be in the future.");
            }

            Absence absence = new Absence(date, request.amount(), subject);

            return this.absenceRepository.save(absence);
        } catch (ParseException exception) {
            throw new InvalidAbsenceDateException("Invalid date format, please use dd/MM/yyyy.");
        }
    }

    public Absence update(UUID id, @NotNull UpdateAbsenceRequest request, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Absence absence = this.absenceRepository.findById(id).orElseThrow(() -> new AbsenceNotFoundException("Absence not found."));

        Subject subject = this.subjectRepository.findById(absence.getSubject().getId()).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        try {
            if (request.date().isPresent()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = dateFormat.parse(request.date().get());

                Date today = new Date();

                if (date.after(today)) {
                    throw new InvalidAbsenceDateException("Absence date cannot be in the future.");
                }

                absence.setDate(date);
            }

            absence.setAmount(request.amount().orElse(absence.getAmount()));

            return this.absenceRepository.save(absence);
        } catch (ParseException exception) {
            throw new InvalidAbsenceDateException("Invalid date format, please use dd/MM/yyyy.");
        }
    }

    public void delete(UUID id, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Absence absence = this.absenceRepository.findById(id).orElseThrow(() -> new AbsenceNotFoundException("Absence not found."));

        Subject subject = this.subjectRepository.findById(absence.getSubject().getId()).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        this.absenceRepository.delete(absence);
    }
}
