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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.DateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbsenceServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    SubjectRepository subjectRepository;

    @Mock
    AbsenceRepository absenceRepository;

    @InjectMocks
    AbsenceService absenceService;

    AutoCloseable closeable;

    @Mock
    UserDetails userDetails;

    User mockUser;
    Subject mockSubject;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockUser = new User(UUID.randomUUID(), "Test User", "test@example.com", "password");
        mockSubject = new Subject(UUID.randomUUID(), "Test Subject", "Test Description", mockUser, new ArrayList<>());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("should be able to get a absence")
    void getCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID absenceId = UUID.randomUUID();
        Absence mockAbsence = new Absence(absenceId, new Date(), 2, mockSubject);

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(mockAbsence));

        Absence result = absenceService.get(absenceId, userDetails);

        assertNotNull(result);
        assertEquals(absenceId, result.getId());
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(absenceRepository, times(1)).findById(absenceId);
    }

    @Test
    @DisplayName("should be not able to get a nonexistent absence")
    void getCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID absenceId = UUID.randomUUID();

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.empty());

        assertThrows(AbsenceNotFoundException.class, () -> absenceService.get(absenceId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(absenceRepository, times(1)).findById(absenceId);
    }

    @Test
    @DisplayName("should be not able to get a absence from a nonexistent subject")
    void getCase3() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID absenceId = UUID.randomUUID();
        Absence mockAbsence = new Absence(absenceId, new Date(), 2, mockSubject);

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(mockAbsence));

        assertThrows(SubjectNotFoundException.class, () -> absenceService.get(absenceId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(absenceRepository, times(1)).findById(absenceId);
    }

    @Test
    @DisplayName("should be able not able to get a absence from another user")
    void getCase4() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID absenceId = UUID.randomUUID();
        Absence mockAbsence = new Absence(absenceId, new Date(), 2, mockSubject);

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(mockAbsence));

        assertThrows(UserUnauthorizedException.class, () -> absenceService.get(absenceId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(absenceRepository, times(1)).findById(absenceId);
    }

    @Test
    @DisplayName("should be able to fetch absences from a subject")
    void fetchCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        List<UUID> mockAbsenceIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<Absence> mockAbsences = List.of(new Absence(mockAbsenceIds.get(0), new Date(), 2, mockSubject), new Absence(mockAbsenceIds.get(0), new Date(), 2, mockSubject));

        when(absenceRepository.findAllBySubjectId(mockSubject.getId())).thenReturn(mockAbsences);

        List<Absence> result = absenceService.fetch(mockSubject.getId(), userDetails);

        assertNotNull(result);
        assertEquals(result, mockAbsences);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(absenceRepository, times(1)).findAllBySubjectId(mockSubject.getId());
    }

    @Test
    @DisplayName("should be able to fetch empty absences from a subject")
    void fetchCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        List<Absence> mockAbsences = new ArrayList<>();

        when(absenceRepository.findAllBySubjectId(mockSubject.getId())).thenReturn(mockAbsences);

        List<Absence> result = absenceService.fetch(mockSubject.getId(), userDetails);

        assertNotNull(result);
        assertEquals(result, mockAbsences);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(absenceRepository, times(1)).findAllBySubjectId(mockSubject.getId());
    }

    @Test
    @DisplayName("should be able to create a absence")
    void createCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID absenceId = UUID.randomUUID();
        Absence mockAbsence = new Absence(absenceId, new Date(), 2, mockSubject);

        when(absenceRepository.save(any(Absence.class))).thenReturn(mockAbsence);

        CreateAbsenceRequest request = new CreateAbsenceRequest("01/01/2024", 2, mockSubject.getId());

        Absence result = absenceService.create(request, userDetails);

        assertNotNull(result);
        assertEquals(result, mockAbsence);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(absenceRepository, times(1)).save(any(Absence.class));
    }

    @Test
    @DisplayName("should be not able to create a absence from a nonexistent subject")
    void createCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.empty());

        CreateAbsenceRequest request = new CreateAbsenceRequest("01/01/2024", 2, mockSubject.getId());

        assertThrows(SubjectNotFoundException.class, () -> absenceService.create(request, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
    }

    @Test
    @DisplayName("should be not able to create a absence from another user")
    void createCase3() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        CreateAbsenceRequest request = new CreateAbsenceRequest("01/01/2024", 2, mockSubject.getId());

        assertThrows(UserUnauthorizedException.class, () -> absenceService.create(request, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
    }

    @Test
    @DisplayName("should be not able to create a absence with a invalid date")
    void createCase4() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        CreateAbsenceRequest request = new CreateAbsenceRequest("3143214512412412", 2, mockSubject.getId());

        assertThrows(InvalidAbsenceDateException.class, () -> absenceService.create(request, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
    }

    @Test
    @DisplayName("should be not able to create a absence with a date in the future")
    void createCase5() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        long futureDateMillis = System.currentTimeMillis() + 1000000000;
        String futureDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(futureDateMillis));

        CreateAbsenceRequest request = new CreateAbsenceRequest(futureDateString, 2, mockSubject.getId());

        assertThrows(InvalidAbsenceDateException.class, () -> absenceService.create(request, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
    }

    @Test
    @DisplayName("should be able to update the date and value of a absence")
    void updateCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID absenceId = UUID.randomUUID();
        Absence mockAbsence = new Absence(absenceId, new Date(), 2, mockSubject);

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(mockAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(mockAbsence);

        UpdateAbsenceRequest request = new UpdateAbsenceRequest(Optional.of("01/01/2024"), Optional.of(4));

        Absence result = absenceService.update(absenceId, request, userDetails);

        assertNotNull(result);
        assertEquals(result, mockAbsence);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(absenceRepository, times(1)).findById(absenceId);
    }

    @Test
    @DisplayName("should be able to update the date of a absence")
    void updateCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID absenceId = UUID.randomUUID();
        Absence mockAbsence = new Absence(absenceId, new Date(), 2, mockSubject);

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(mockAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(mockAbsence);

        UpdateAbsenceRequest request = new UpdateAbsenceRequest(Optional.of("01/01/2024"), Optional.empty());

        Absence result = absenceService.update(absenceId, request, userDetails);

        assertNotNull(result);
        assertEquals(result, mockAbsence);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(absenceRepository, times(1)).findById(absenceId);
    }

    @Test
    @DisplayName("should be able to update the value of a absence")
    void updateCase3() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID absenceId = UUID.randomUUID();
        Absence mockAbsence = new Absence(absenceId, new Date(), 2, mockSubject);

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(mockAbsence));
        when(absenceRepository.save(any(Absence.class))).thenReturn(mockAbsence);

        UpdateAbsenceRequest request = new UpdateAbsenceRequest(Optional.empty(), Optional.of(4));

        Absence result = absenceService.update(absenceId, request, userDetails);

        assertNotNull(result);
        assertEquals(result, mockAbsence);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(absenceRepository, times(1)).findById(absenceId);
    }

    @Test
    @DisplayName("should be not able to update a nonexistent absence")
    void updateCase4() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID absenceId = UUID.randomUUID();

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.empty());

        UpdateAbsenceRequest request = new UpdateAbsenceRequest(Optional.of("01/01/2024"), Optional.of(4));

        assertThrows(AbsenceNotFoundException.class, () -> absenceService.update(absenceId, request, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(absenceRepository, times(1)).findById(absenceId);
    }

    @Test
    @DisplayName("should be not able to update a absence from another user")
    void updateCase5() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID absenceId = UUID.randomUUID();
        Absence mockAbsence = new Absence(absenceId, new Date(), 2, mockSubject);

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(mockAbsence));

        UpdateAbsenceRequest request = new UpdateAbsenceRequest(Optional.of("01/01/2024"), Optional.of(4));

        assertThrows(UserUnauthorizedException.class, () -> absenceService.update(absenceId, request, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(absenceRepository, times(1)).findById(absenceId);
    }

    @Test
    @DisplayName("should be not able to update a absence with a invalid date")
    void updateCase6() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID absenceId = UUID.randomUUID();
        Absence mockAbsence = new Absence(absenceId, new Date(), 2, mockSubject);

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(mockAbsence));

        UpdateAbsenceRequest request = new UpdateAbsenceRequest(Optional.of("3143214512412412"), Optional.of(4));

        assertThrows(InvalidAbsenceDateException.class, () -> absenceService.update(absenceId, request, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(absenceRepository, times(1)).findById(absenceId);
        verify(absenceRepository, times(1)).findById(absenceId);
    }

    @Test
    @DisplayName("should be not able to update a absence with a date in the future")
    void updateCase7() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID absenceId = UUID.randomUUID();
        Absence mockAbsence = new Absence(absenceId, new Date(), 2, mockSubject);

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(mockAbsence));

        long futureDateMillis = System.currentTimeMillis() + 1000000000;
        String futureDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(futureDateMillis));

        UpdateAbsenceRequest request = new UpdateAbsenceRequest(Optional.of(futureDateString), Optional.of(4));

        assertThrows(InvalidAbsenceDateException.class, () -> absenceService.update(absenceId, request, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(absenceRepository, times(1)).findById(absenceId);
    }

    @Test
    @DisplayName("should be able to delete a absence")
    void deleteCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID absenceId = UUID.randomUUID();
        Absence mockAbsence = new Absence(absenceId, new Date(), 2, mockSubject);

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(mockAbsence));

        absenceService.delete(absenceId, userDetails);

        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(absenceRepository, times(1)).findById(absenceId);
        verify(absenceRepository, times(1)).delete(mockAbsence);
    }

    @Test
    @DisplayName("should be not able to delete a nonexistent absence")
    void deleteCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID absenceId = UUID.randomUUID();

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.empty());

        assertThrows(AbsenceNotFoundException.class, () -> absenceService.delete(absenceId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(absenceRepository, times(1)).findById(absenceId);
    }

    @Test
    @DisplayName("should be not able to delete a absence from another user")
    void deleteCase3() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID absenceId = UUID.randomUUID();
        Absence mockAbsence = new Absence(absenceId, new Date(), 2, mockSubject);

        when(absenceRepository.findById(absenceId)).thenReturn(Optional.of(mockAbsence));

        assertThrows(UserUnauthorizedException.class, () -> absenceService.delete(absenceId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(absenceRepository, times(1)).findById(absenceId);
    }
}