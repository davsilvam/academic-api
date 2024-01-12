package com.davsilvam.services;

import com.davsilvam.domain.absences.Absence;
import com.davsilvam.domain.subject.Subject;
import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.absence.AbsenceResponse;
import com.davsilvam.dtos.absence.CreateAbsenceRequest;
import com.davsilvam.dtos.absence.UpdateAbsenceRequest;
import com.davsilvam.exceptions.absence.AbsenceNotFoundException;
import com.davsilvam.exceptions.absence.InvalidAbsenceDateException;
import com.davsilvam.exceptions.subject.SubjectNotFoundException;
import com.davsilvam.exceptions.user.UserUnauthorizedException;
import com.davsilvam.repositories.absence.AbsenceRepository;
import com.davsilvam.repositories.subject.SubjectRepository;
import com.davsilvam.repositories.user.UserRepository;
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

    public AbsenceResponse get(UUID id, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());

        Absence absence = this.absenceRepository.findById(id).orElseThrow(() -> new AbsenceNotFoundException("Absence not found."));

        Subject subject = this.subjectRepository.findById(absence.getSubject().getId()).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        return new AbsenceResponse(absence);
    }

    public List<AbsenceResponse> fetch(UUID subjectId, @NotNull UserDetails userDetails) {
        User user = this.userRepository.findByEmail(userDetails.getUsername());
        Subject subject = this.subjectRepository.findById(subjectId).orElseThrow(() -> new SubjectNotFoundException("Subject not found."));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("User not allowed to access this subject.");
        }

        List<Absence> absences = this.absenceRepository.findAllBySubjectId(subjectId);

        return absences.stream().map(AbsenceResponse::new).toList();
    }

    public AbsenceResponse create(@NotNull CreateAbsenceRequest request, @NotNull UserDetails userDetails) {
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

            this.absenceRepository.save(absence);

            return new AbsenceResponse(absence);
        } catch (ParseException exception) {
            throw new InvalidAbsenceDateException("Invalid date format, please use dd/MM/yyyy.");
        }

    }

    public AbsenceResponse update(UUID id, @NotNull UpdateAbsenceRequest request, @NotNull UserDetails userDetails) {
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

            this.absenceRepository.save(absence);

            return new AbsenceResponse(absence);
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
