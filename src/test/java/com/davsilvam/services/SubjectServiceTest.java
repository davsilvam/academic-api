package com.davsilvam.services;

import com.davsilvam.domain.professor.Professor;
import com.davsilvam.domain.subject.Subject;
import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.subject.CreateSubjectRequest;
import com.davsilvam.dtos.subject.SubjectResponse;
import com.davsilvam.dtos.subject.UpdateSubjectRequest;
import com.davsilvam.exceptions.subject.SubjectNotFoundException;
import com.davsilvam.exceptions.user.UserUnauthorizedException;
import com.davsilvam.repositories.ProfessorRepository;
import com.davsilvam.repositories.SubjectRepository;
import com.davsilvam.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Subject Service Tests")
class SubjectServiceTest {
    @Mock
    SubjectRepository subjectRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ProfessorRepository professorRepository;

    @InjectMocks
    SubjectService subjectService;

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
    @DisplayName("should be able to get a subject")
    void getCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID subjectId = UUID.randomUUID();
        Subject mockSubject = new Subject("Test Subject", "Description", mockUser);
        SubjectResponse mockResponse = new SubjectResponse(mockSubject);

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));

        SubjectResponse result = subjectService.get(subjectId, userDetails);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(subjectRepository, Mockito.times(1)).findById(subjectId);
        verify(userRepository, Mockito.times(1)).findByEmail(mockUser.getEmail());
    }

    @Test
    @DisplayName("should be not able to get a nonexistent subject")
    void getCase2() {
        when(userDetails.getUsername()).thenReturn("test@example.com");

        UUID nonExistingSubjectId = UUID.randomUUID();
        when(subjectRepository.findById(nonExistingSubjectId)).thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class, () -> subjectService.get(nonExistingSubjectId, userDetails));
        verify(subjectRepository, Mockito.times(1)).findById(nonExistingSubjectId);
    }

    @Test
    @DisplayName("should be able not able to get a subject from another user")
    void getCase3() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID subjectId = UUID.randomUUID();
        Subject mockSubject = new Subject("Test Subject", "Description", mockUser);

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));

        assertThrows(UserUnauthorizedException.class, () -> subjectService.get(subjectId, userDetails));
        verify(userRepository, Mockito.times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, Mockito.times(1)).findById(subjectId);
    }

    @Test
    @DisplayName("should be able to fetch subjects")
    void fetchCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        List<Subject> mockSubjects = Arrays.asList(new Subject("Subject 1", "Description 1", mockUser), new Subject("Subject 2", "Description 2", mockUser));
        ;
        List<SubjectResponse> mockResponses = Arrays.asList(new SubjectResponse(mockSubjects.get(0)), new SubjectResponse(mockSubjects.get(1)));

        when(subjectRepository.findAllByUserId(mockUser.getId())).thenReturn(mockSubjects);

        List<SubjectResponse> result = subjectService.fetch(userDetails);

        assertNotNull(result);
        assertEquals(mockResponses, result);
        verify(userRepository, Mockito.times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, Mockito.times(1)).findAllByUserId(mockUser.getId());
    }

    @Test
    @DisplayName("should be able to fetch empty subjects")
    void fetchCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        List<Subject> mockSubjects = new ArrayList<>();
        List<SubjectResponse> mockResponses = new ArrayList<>();

        when(subjectRepository.findAllByUserId(mockUser.getId())).thenReturn(mockSubjects);

        List<SubjectResponse> result = subjectService.fetch(userDetails);

        assertNotNull(result);
        assertEquals(mockResponses, result);
        verify(subjectRepository, Mockito.times(1)).findAllByUserId(mockUser.getId());
        verify(userRepository, Mockito.times(1)).findByEmail(mockUser.getEmail());
    }

    @Test
    @DisplayName("should be able to create a subject")
    void createCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        List<UUID> professorsIds = new ArrayList<>();
        List<Professor> mockProfessors = new ArrayList<>();

        when(professorRepository.findAllById(professorsIds)).thenReturn(mockProfessors);

        Subject mockSubject = new Subject("Subject 1", "Description 1", mockUser);
        mockSubject.setProfessors(mockProfessors);
        SubjectResponse mockResponse = new SubjectResponse(mockSubject);

        when(subjectRepository.save(any(Subject.class))).thenReturn(mockSubject);

        CreateSubjectRequest mockRequest = new CreateSubjectRequest("Subject 1", "Description 1", professorsIds);

        SubjectResponse result = subjectService.create(mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(userDetails, Mockito.times(1)).getUsername();
        verify(userRepository, Mockito.times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, Mockito.times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("should be able to update the name and description of a subject")
    void updateCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID subjectId = UUID.randomUUID();
        Subject mockSubject = new Subject(subjectId, "Subject 1", "Description 1", mockUser, new ArrayList<>());
        SubjectResponse mockResponse = new SubjectResponse(mockSubject);

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));
        when(subjectRepository.save(mockSubject)).thenReturn(mockSubject);

        UpdateSubjectRequest mockRequest = new UpdateSubjectRequest(Optional.of("New Subject Name"), Optional.of("New Subject Description"));

        SubjectResponse result = subjectService.update(subjectId, mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(userRepository, Mockito.times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, Mockito.times(1)).findById(subjectId);
        verify(subjectRepository, Mockito.times(1)).save(mockSubject);
    }

    @Test
    @DisplayName("should be able to update the name of a subject")
    void updateCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID subjectId = UUID.randomUUID();
        Subject mockSubject = new Subject(subjectId, "Subject 1", "Description 1", mockUser, new ArrayList<>());
        SubjectResponse mockResponse = new SubjectResponse(mockSubject);

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));
        when(subjectRepository.save(mockSubject)).thenReturn(mockSubject);

        UpdateSubjectRequest mockRequest = new UpdateSubjectRequest(Optional.of("New Subject Name"), Optional.empty());

        SubjectResponse result = subjectService.update(subjectId, mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(userRepository, Mockito.times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, Mockito.times(1)).findById(subjectId);
        verify(subjectRepository, Mockito.times(1)).save(mockSubject);
    }

    @Test
    @DisplayName("should be able to update the description of a subject")
    void updateCase3() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID subjectId = UUID.randomUUID();
        Subject mockSubject = new Subject(subjectId, "Subject 1", "Description 1", mockUser, new ArrayList<>());
        SubjectResponse mockResponse = new SubjectResponse(mockSubject);

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));
        when(subjectRepository.save(mockSubject)).thenReturn(mockSubject);

        UpdateSubjectRequest mockRequest = new UpdateSubjectRequest(Optional.empty(), Optional.of("New Subject Description"));

        SubjectResponse result = subjectService.update(subjectId, mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(userRepository, Mockito.times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, Mockito.times(1)).findById(subjectId);
        verify(subjectRepository, Mockito.times(1)).save(mockSubject);
    }

    @Test
    @DisplayName("should be not able to update a nonexistent subject")
    void updateCase4() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID nonExistingSubjectId = UUID.randomUUID();

        when(subjectRepository.findById(nonExistingSubjectId)).thenReturn(Optional.empty());

        UpdateSubjectRequest mockRequest = new UpdateSubjectRequest(Optional.empty(), Optional.of("New Subject Description"));

        assertThrows(SubjectNotFoundException.class, () -> subjectService.update(nonExistingSubjectId, mockRequest, userDetails));
        verify(userRepository, Mockito.times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, Mockito.times(1)).findById(nonExistingSubjectId);
    }

    @Test
    @DisplayName("should be not able to update a subject from another user")
    void updateCase5() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);

        UUID subjectId = UUID.randomUUID();
        Subject mockSubject = new Subject(subjectId, "Subject 1", "Description 1", mockUser, new ArrayList<>());

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));

        UpdateSubjectRequest mockRequest = new UpdateSubjectRequest(Optional.empty(), Optional.of("New Subject Description"));

        assertThrows(UserUnauthorizedException.class, () -> subjectService.update(subjectId, mockRequest, userDetails));
        verify(userRepository, Mockito.times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, Mockito.times(1)).findById(subjectId);
    }

    @Test
    @DisplayName("should be able to delete a subject")
    void deleteCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID subjectId = UUID.randomUUID();

        Subject mockSubject = new Subject(subjectId, "Subject 1", "Description 1", mockUser, new ArrayList<>());
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));

        subjectService.delete(subjectId, userDetails);

        verify(subjectRepository, Mockito.times(1)).delete(mockSubject);
    }

    @Test
    @DisplayName("should be not able to delete a nonexistent subject")
    void deleteCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID nonExistingSubjectId = UUID.randomUUID();

        when(subjectRepository.findById(nonExistingSubjectId)).thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class, () -> subjectService.delete(nonExistingSubjectId, userDetails));
        verify(userRepository, Mockito.times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, Mockito.times(1)).findById(nonExistingSubjectId);
    }

    @Test
    @DisplayName("should be not able to delete a subject from another user")
    void deleteCase3() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);

        UUID subjectId = UUID.randomUUID();
        Subject mockSubject = new Subject(subjectId, "Subject 1", "Description 1", mockUser, new ArrayList<>());

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));

        assertThrows(UserUnauthorizedException.class, () -> subjectService.delete(subjectId, userDetails));
        verify(userRepository, Mockito.times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, Mockito.times(1)).findById(subjectId);
    }
}