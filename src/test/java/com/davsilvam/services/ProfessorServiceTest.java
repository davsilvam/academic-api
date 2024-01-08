package com.davsilvam.services;

import com.davsilvam.domain.professor.Professor;
import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.professor.CreateProfessorRequest;
import com.davsilvam.dtos.professor.ProfessorResponse;
import com.davsilvam.dtos.professor.UpdateProfessorRequest;
import com.davsilvam.exceptions.professor.ProfessorNotFoundException;
import com.davsilvam.exceptions.user.UserUnauthorizedException;
import com.davsilvam.repositories.ProfessorRepository;
import com.davsilvam.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Professor Service Tests")
class ProfessorServiceTest {
    @Mock
    ProfessorRepository professorRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ProfessorService professorService;

    AutoCloseable closeable;

    @Mock
    UserDetails userDetails;

    User mockUser;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockUser = new User(UUID.randomUUID(), "Test User", "test@example.com", "password");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("should be able to get a professor")
    void getCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID subjectId = UUID.randomUUID();
        Professor mockProfessor = new Professor("Test Professor", "professor@example.com", mockUser);
        ProfessorResponse mockResponse = new ProfessorResponse(mockProfessor);

        when(professorRepository.findById(subjectId)).thenReturn(Optional.of(mockProfessor));

        ProfessorResponse result = professorService.get(subjectId, userDetails);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(professorRepository, times(1)).findById(subjectId);
    }

    @Test
    @DisplayName("should be not able to get a nonexistent professor")
    void getCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID professorId = UUID.randomUUID();

        when(professorRepository.findById(professorId)).thenReturn(Optional.empty());

        assertThrows(ProfessorNotFoundException.class, () -> professorService.get(professorId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(professorRepository, times(1)).findById(professorId);
    }

    @Test
    @DisplayName("should be able not able to get a professor from another user")
    void getCase3() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);

        UUID professorId = UUID.randomUUID();
        Professor mockProfessor = new Professor("Test Professor", "professor@example.com", mockUser);

        when(professorRepository.findById(professorId)).thenReturn(Optional.of(mockProfessor));

        assertThrows(UserUnauthorizedException.class, () -> professorService.get(professorId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(professorRepository, times(1)).findById(professorId);
    }

    @Test
    @DisplayName("should be able to fetch professors")
    void fetchCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        List<Professor> mockProfessors = Arrays.asList(new Professor("Test Professor 1", "professor@example.com", mockUser), new Professor("Test Professor 2", "professor2@example.com", mockUser));
        List<ProfessorResponse> mockResponses = Arrays.asList(new ProfessorResponse(mockProfessors.get(0)), new ProfessorResponse(mockProfessors.get(1)));

        when(professorRepository.findAllByUserId(mockUser.getId())).thenReturn(mockProfessors);

        List<ProfessorResponse> result = professorService.fetch(userDetails);

        assertNotNull(result);
        assertEquals(mockResponses, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(professorRepository, times(1)).findAllByUserId(mockUser.getId());
    }

    @Test
    @DisplayName("should be able to fetch empty professors")
    void fetchCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        List<Professor> mockProfessors = List.of();
        List<ProfessorResponse> mockResponses = List.of();

        when(professorRepository.findAllByUserId(mockUser.getId())).thenReturn(mockProfessors);

        List<ProfessorResponse> result = professorService.fetch(userDetails);

        assertNotNull(result);
        assertEquals(mockResponses, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(professorRepository, times(1)).findAllByUserId(mockUser.getId());
    }

    @Test
    @DisplayName("should be able to create a professor")
    void createCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        Professor mockProfessor = new Professor("Test Professor", "professor@example.com", mockUser);
        ProfessorResponse mockResponse = new ProfessorResponse(mockProfessor);

        when(professorRepository.save(any(Professor.class))).thenReturn(mockProfessor);

        CreateProfessorRequest mockRequest = new CreateProfessorRequest(mockProfessor.getName(), mockProfessor.getEmail());

        ProfessorResponse result = professorService.create(mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(professorRepository, times(1)).save(any(Professor.class));
    }

    @Test
    @DisplayName("should be able to update the name and email of a professor")
    void updateCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID professorId = UUID.randomUUID();
        Professor mockProfessor = new Professor(professorId, "Test Professor", "professor@example.com", mockUser);

        when(professorRepository.findById(professorId)).thenReturn(Optional.of(mockProfessor));

        UpdateProfessorRequest mockRequest = new UpdateProfessorRequest(Optional.of("Test Professor 2"), Optional.of("professor@newemail.com"));
        Professor updatedMockProfessor = new Professor(professorId, "Test Professor 2", "professor@newemail.com", mockUser);
        ProfessorResponse mockResponse = new ProfessorResponse(updatedMockProfessor);

        when(professorRepository.save(mockProfessor)).thenReturn(updatedMockProfessor);

        ProfessorResponse result = professorService.update(professorId, mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(professorRepository, times(1)).findById(professorId);
        verify(professorRepository, times(1)).save(mockProfessor);
    }

    @Test
    @DisplayName("should be able to update the name of a professor")
    void updateCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID professorId = UUID.randomUUID();
        Professor mockProfessor = new Professor(professorId, "Test Professor", "professor@example.com", mockUser);

        when(professorRepository.findById(professorId)).thenReturn(Optional.of(mockProfessor));

        UpdateProfessorRequest mockRequest = new UpdateProfessorRequest(Optional.of("Test Professor 2"), Optional.empty());
        Professor updatedMockProfessor = new Professor(professorId, "Test Professor 2", "professor@example.com", mockUser);
        ProfessorResponse mockResponse = new ProfessorResponse(updatedMockProfessor);

        when(professorRepository.save(mockProfessor)).thenReturn(updatedMockProfessor);

        ProfessorResponse result = professorService.update(professorId, mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(professorRepository, times(1)).findById(professorId);
        verify(professorRepository, times(1)).save(mockProfessor);
    }

    @Test
    @DisplayName("should be able to update the email of a professor")
    void updateCase3() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID professorId = UUID.randomUUID();
        Professor mockProfessor = new Professor(professorId, "Test Professor", "professor@example.com", mockUser);

        when(professorRepository.findById(professorId)).thenReturn(Optional.of(mockProfessor));

        UpdateProfessorRequest mockRequest = new UpdateProfessorRequest(Optional.empty(), Optional.of("professor@newemail.com"));
        Professor updatedMockProfessor = new Professor(professorId, "Test Professor", "professor@newemail.com", mockUser);
        ProfessorResponse mockResponse = new ProfessorResponse(updatedMockProfessor);

        when(professorRepository.save(mockProfessor)).thenReturn(updatedMockProfessor);

        ProfessorResponse result = professorService.update(professorId, mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(professorRepository, times(1)).findById(professorId);
        verify(professorRepository, times(1)).save(mockProfessor);
    }

    @Test
    @DisplayName("should be not able to update a nonexistent professor")
    void updateCase4() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID nonExistingSubjectId = UUID.randomUUID();

        when(professorRepository.findById(nonExistingSubjectId)).thenReturn(Optional.empty());

        UpdateProfessorRequest mockRequest = new UpdateProfessorRequest(Optional.empty(), Optional.of("New Subject Description"));

        assertThrows(ProfessorNotFoundException.class, () -> professorService.update(nonExistingSubjectId, mockRequest, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(professorRepository, times(1)).findById(nonExistingSubjectId);
    }

    @Test
    @DisplayName("should be not able to update a professor from another user")
    void updateCase5() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);

        UUID professorId = UUID.randomUUID();
        Professor mockProfessor = new Professor(professorId, "Test Professor", "professor@example.com", mockUser);

        when(professorRepository.findById(professorId)).thenReturn(Optional.of(mockProfessor));

        UpdateProfessorRequest mockRequest = new UpdateProfessorRequest(Optional.empty(), Optional.of("New Subject Description"));

        assertThrows(UserUnauthorizedException.class, () -> professorService.update(professorId, mockRequest, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(professorRepository, times(1)).findById(professorId);
    }

    @Test
    @DisplayName("should be able to delete a professor")
    void deleteCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID professorId = UUID.randomUUID();
        Professor mockProfessor = new Professor(professorId, "Test Professor", "professor@example.com", mockUser);

        when(professorRepository.findById(professorId)).thenReturn(Optional.of(mockProfessor));

        professorService.delete(professorId, userDetails);

        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(professorRepository, times(1)).findById(professorId);
        verify(professorRepository, times(1)).delete(mockProfessor);
    }

    @Test
    @DisplayName("should be not able to delete a nonexistent professor")
    void deleteCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID nonExistingProfessorId = UUID.randomUUID();

        when(professorRepository.findById(nonExistingProfessorId)).thenReturn(Optional.empty());

        assertThrows(ProfessorNotFoundException.class, () -> professorService.delete(nonExistingProfessorId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(professorRepository, times(1)).findById(nonExistingProfessorId);
    }

    @Test
    @DisplayName("should be not able to delete a professor from another user")
    void deleteCase3() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);

        UUID professorId = UUID.randomUUID();
        Professor mockProfessor = new Professor(professorId, "Test Professor", "professor@example.com", mockUser);

        when(professorRepository.findById(professorId)).thenReturn(Optional.of(mockProfessor));

        assertThrows(UserUnauthorizedException.class, () -> professorService.delete(professorId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(professorRepository, times(1)).findById(professorId);
    }
}