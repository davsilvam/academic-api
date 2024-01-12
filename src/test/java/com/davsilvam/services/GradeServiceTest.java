package com.davsilvam.services;

import com.davsilvam.domain.Grade;
import com.davsilvam.domain.Subject;
import com.davsilvam.domain.User;
import com.davsilvam.dtos.grade.CreateGradeRequest;
import com.davsilvam.dtos.grade.UpdateGradeRequest;
import com.davsilvam.exceptions.grade.GradeNotFoundException;
import com.davsilvam.exceptions.subject.SubjectNotFoundException;
import com.davsilvam.exceptions.user.UserUnauthorizedException;
import com.davsilvam.repositories.GradeRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GradeServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    SubjectRepository subjectRepository;

    @Mock
    GradeRepository gradeRepository;

    @InjectMocks
    GradeService gradeService;

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
    @DisplayName("should be able to get a grade")
    void getCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID gradeId = UUID.randomUUID();
        Grade mockGrade = new Grade(gradeId, "Test Grade", 10.0f, mockSubject);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(mockGrade));

        Grade result = gradeService.get(gradeId, userDetails);

        assertNotNull(result);
        assertEquals(gradeId, result.getId());
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(gradeRepository, times(1)).findById(gradeId);
    }

    @Test
    @DisplayName("should be not able to get a nonexistent grade")
    void getCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID gradeId = UUID.randomUUID();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        assertThrows(GradeNotFoundException.class, () -> gradeService.get(gradeId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(gradeRepository, times(1)).findById(gradeId);
    }

    @Test
    @DisplayName("should be not able to get a grade from a nonexistent subject")
    void getCase3() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID gradeId = UUID.randomUUID();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        assertThrows(GradeNotFoundException.class, () -> gradeService.get(gradeId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(gradeRepository, times(1)).findById(gradeId);
    }

    @Test
    @DisplayName("should be able not able to get a grade from another user")
    void getCase4() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID gradeId = UUID.randomUUID();
        Grade mockGrade = new Grade(gradeId, "Test Grade", 10.0f, mockSubject);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(mockGrade));

        assertThrows(UserUnauthorizedException.class, () -> gradeService.get(gradeId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(gradeRepository, times(1)).findById(gradeId);
    }

    @Test
    @DisplayName("should be able to fetch grades from a subject")
    void fetchCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        List<UUID> mockGradeIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<Grade> mockGrades = List.of(
                new Grade(mockGradeIds.get(0), "Test Grade 1", 10.0f, mockSubject),
                new Grade(mockGradeIds.get(1), "Test Grade 2", 10.0f, mockSubject)
        );

        when(gradeRepository.findAllBySubjectId(mockSubject.getId())).thenReturn(mockGrades);

        List<Grade> result = gradeService.fetch(mockSubject.getId(), userDetails);

        assertNotNull(result);
        assertEquals(result, mockGrades);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(gradeRepository, times(1)).findAllBySubjectId(mockSubject.getId());
    }

    @Test
    @DisplayName("should be able to fetch empty grades from a subject")
    void fetchCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        List<Grade> mockGrades = new ArrayList<>();

        when(gradeRepository.findAllBySubjectId(mockSubject.getId())).thenReturn(mockGrades);

        List<Grade> result = gradeService.fetch(mockSubject.getId(), userDetails);

        assertNotNull(result);
        assertEquals(result, mockGrades);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(gradeRepository, times(1)).findAllBySubjectId(mockSubject.getId());
    }

    @Test
    @DisplayName("should be able to create a grade")
    void createCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID gradeId = UUID.randomUUID();
        Grade mockGrade = new Grade(gradeId, "Test Grade", 10.0f, mockSubject);

        when(gradeRepository.save(any(Grade.class))).thenReturn(mockGrade);

        CreateGradeRequest request = new CreateGradeRequest("Test Grade", 10.0f, mockSubject.getId());

        Grade result = gradeService.create(request, userDetails);

        assertNotNull(result);
        assertEquals(result, mockGrade);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(gradeRepository, times(1)).save(any(Grade.class));
    }

    @Test
    @DisplayName("should be not able to create a grade from a nonexistent subject")
    void createCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.empty());

        CreateGradeRequest request = new CreateGradeRequest("Test Grade", 10.0f, mockSubject.getId());

        assertThrows(SubjectNotFoundException.class, () -> gradeService.create(request, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
    }

    @Test
    @DisplayName("should be not able to create a grade from another user")
    void createCase3() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        CreateGradeRequest request = new CreateGradeRequest("Test Grade", 10.0f, mockSubject.getId());

        assertThrows(UserUnauthorizedException.class, () -> gradeService.create(request, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
    }

    @Test
    @DisplayName("should be able to update the date and value of a grade")
    void updateCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID gradeId = UUID.randomUUID();
        Grade mockGrade = new Grade(gradeId, "Test Grade", 10.0f, mockSubject);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(mockGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(mockGrade);

        UpdateGradeRequest request = new UpdateGradeRequest(Optional.of("Test Grade"), Optional.of(10.0f), mockSubject.getId());

        Grade result = gradeService.update(gradeId, request, userDetails);

        assertNotNull(result);
        assertEquals(result, mockGrade);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(gradeRepository, times(1)).findById(gradeId);
    }

    @Test
    @DisplayName("should be able to update the date of a grade")
    void updateCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID gradeId = UUID.randomUUID();
        Grade mockGrade = new Grade(gradeId, "Test Grade", 10.0f, mockSubject);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(mockGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(mockGrade);

        UpdateGradeRequest request = new UpdateGradeRequest(Optional.of("Test Grade"), Optional.empty(), mockSubject.getId());

        Grade result = gradeService.update(gradeId, request, userDetails);

        assertNotNull(result);
        assertEquals(result, mockGrade);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(gradeRepository, times(1)).findById(gradeId);
    }

    @Test
    @DisplayName("should be able to update the value of a grade")
    void updateCase3() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID gradeId = UUID.randomUUID();
        Grade mockGrade = new Grade(gradeId, "Test Grade", 10.0f, mockSubject);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(mockGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(mockGrade);

        UpdateGradeRequest request = new UpdateGradeRequest(Optional.empty(), Optional.of(10.0f), mockSubject.getId());

        Grade result = gradeService.update(gradeId, request, userDetails);

        assertNotNull(result);
        assertEquals(result, mockGrade);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(gradeRepository, times(1)).findById(gradeId);
    }

    @Test
    @DisplayName("should be not able to update a nonexistent grade")
    void updateCase4() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID gradeId = UUID.randomUUID();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        UpdateGradeRequest request = new UpdateGradeRequest(Optional.of("Test Grade"), Optional.of(10.0f), mockSubject.getId());

        assertThrows(GradeNotFoundException.class, () -> gradeService.update(gradeId, request, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(gradeRepository, times(1)).findById(gradeId);
    }

    @Test
    @DisplayName("should be not able to update a grade from another user")
    void updateCase5() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID gradeId = UUID.randomUUID();
        Grade mockGrade = new Grade(gradeId, "Test Grade", 10.0f, mockSubject);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(mockGrade));

        UpdateGradeRequest request = new UpdateGradeRequest(Optional.of("Test Grade"), Optional.of(10.0f), mockSubject.getId());

        assertThrows(UserUnauthorizedException.class, () -> gradeService.update(gradeId, request, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(gradeRepository, times(1)).findById(gradeId);
    }

    @Test
    @DisplayName("should be able to delete a grade")
    void deleteCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID gradeId = UUID.randomUUID();
        Grade mockGrade = new Grade(gradeId, "Test Grade", 10.0f, mockSubject);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(mockGrade));

        gradeService.delete(gradeId, userDetails);

        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(gradeRepository, times(1)).findById(gradeId);
        verify(gradeRepository, times(1)).deleteById(gradeId);
    }

    @Test
    @DisplayName("should be not able to delete a nonexistent grade")
    void deleteCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID gradeId = UUID.randomUUID();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        assertThrows(GradeNotFoundException.class, () -> gradeService.delete(gradeId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(gradeRepository, times(1)).findById(gradeId);
    }

    @Test
    @DisplayName("should be not able to delete a grade from another user")
    void deleteCase3() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);
        when(subjectRepository.findById(mockSubject.getId())).thenReturn(Optional.of(mockSubject));

        UUID gradeId = UUID.randomUUID();
        Grade mockGrade = new Grade(gradeId, "Test Grade", 10.0f, mockSubject);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(mockGrade));

        assertThrows(UserUnauthorizedException.class, () -> gradeService.delete(gradeId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, times(1)).findById(mockSubject.getId());
        verify(gradeRepository, times(1)).findById(gradeId);
    }
}