package com.davsilvam.services;

import com.davsilvam.domain.professor.Professor;
import com.davsilvam.domain.subject.Subject;
import com.davsilvam.domain.user.User;
import com.davsilvam.domain.subject.dtos.CreateSubjectRequest;
import com.davsilvam.domain.subject.dtos.UpdateSubjectProfessorsRequest;
import com.davsilvam.domain.subject.dtos.UpdateSubjectRequest;
import com.davsilvam.domain.subject.exceptions.SubjectNotFoundException;
import com.davsilvam.domain.user.exceptions.UserUnauthorizedException;
import com.davsilvam.repositories.ProfessorRepository;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));

        Subject result = subjectService.get(subjectId, userDetails);

        assertNotNull(result);
        assertEquals(mockSubject, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(subjectId);
    }

    @Test
    @DisplayName("should be not able to get a nonexistent subject")
    void getCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());

        UUID nonExistingSubjectId = UUID.randomUUID();
        when(subjectRepository.findById(nonExistingSubjectId)).thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class, () -> subjectService.get(nonExistingSubjectId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(subjectRepository, times(1)).findById(nonExistingSubjectId);
    }

    @Test
    @DisplayName("should be able not able to get a subject from another user")
    void getCase3() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);

        UUID subjectId = UUID.randomUUID();
        Subject mockSubject = new Subject("Test Subject", "Description", mockUser);

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));

        assertThrows(UserUnauthorizedException.class, () -> subjectService.get(subjectId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, times(1)).findById(subjectId);
    }

    @Test
    @DisplayName("should be able to fetch subjects")
    void fetchCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        List<Subject> mockSubjects = Arrays.asList(new Subject("Subject 1", "Description 1", mockUser), new Subject("Subject 2", "Description 2", mockUser));

        when(subjectRepository.findAllByUserId(mockUser.getId())).thenReturn(mockSubjects);

        List<Subject> result = subjectService.fetch(userDetails);

        assertNotNull(result);
        assertEquals(mockSubjects, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findAllByUserId(mockUser.getId());
    }

    @Test
    @DisplayName("should be able to fetch empty subjects")
    void fetchCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        List<Subject> mockSubjects = new ArrayList<>();

        when(subjectRepository.findAllByUserId(mockUser.getId())).thenReturn(mockSubjects);

        List<Subject> result = subjectService.fetch(userDetails);

        assertNotNull(result);
        assertEquals(mockSubjects, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findAllByUserId(mockUser.getId());
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

        when(subjectRepository.save(any(Subject.class))).thenReturn(mockSubject);

        CreateSubjectRequest mockRequest = new CreateSubjectRequest("Subject 1", "Description 1", professorsIds);

        Subject result = subjectService.create(mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(mockSubject, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(professorRepository, times(1)).findAllById(professorsIds);
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("should be able to update the name and description of a subject")
    void updateCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID subjectId = UUID.randomUUID();
        Subject mockSubject = new Subject(subjectId, "Subject 1", "Description 1", mockUser, new ArrayList<>());

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));

        UpdateSubjectRequest mockRequest = new UpdateSubjectRequest(Optional.of("New Subject Name"), Optional.of("New Subject Description"));
        Subject updatedMockSubject = new Subject(subjectId, "New Subject Name", "New Subject Description", mockUser, new ArrayList<>());

        when(subjectRepository.save(mockSubject)).thenReturn(updatedMockSubject);

        Subject result = subjectService.update(subjectId, mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(updatedMockSubject, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(subjectId);
        verify(subjectRepository, times(1)).save(mockSubject);
    }

    @Test
    @DisplayName("should be able to update the name of a subject")
    void updateCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID subjectId = UUID.randomUUID();
        Subject mockSubject = new Subject(subjectId, "Subject 1", "Description 1", mockUser, new ArrayList<>());

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));

        UpdateSubjectRequest mockRequest = new UpdateSubjectRequest(Optional.of("New Subject Name"), Optional.empty());
        Subject updatedMockSubject = new Subject(subjectId, "New Subject Name", mockSubject.getDescription(), mockUser, new ArrayList<>());

        when(subjectRepository.save(mockSubject)).thenReturn(updatedMockSubject);

        Subject result = subjectService.update(subjectId, mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(updatedMockSubject, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(subjectId);
        verify(subjectRepository, times(1)).save(mockSubject);
    }

    @Test
    @DisplayName("should be able to update the description of a subject")
    void updateCase3() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID subjectId = UUID.randomUUID();
        Subject mockSubject = new Subject(subjectId, "Subject 1", "Description 1", mockUser, new ArrayList<>());

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));

        UpdateSubjectRequest mockRequest = new UpdateSubjectRequest(Optional.empty(), Optional.of("New Subject Description"));
        Subject updatedMockSubject = new Subject(subjectId, mockSubject.getName(), "New Subject Description", mockUser, new ArrayList<>());

        when(subjectRepository.save(mockSubject)).thenReturn(updatedMockSubject);

        Subject result = subjectService.update(subjectId, mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(updatedMockSubject, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(subjectId);
        verify(subjectRepository, times(1)).save(mockSubject);
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
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(nonExistingSubjectId);
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
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, times(1)).findById(subjectId);
    }

    @Test
    @DisplayName("should be able to update the professors of a subject")
    void updateProfessorsCase1() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID subjectId = UUID.randomUUID();
        Subject mockSubject = new Subject(subjectId, "Subject 1", "Description 1", mockUser, new ArrayList<>());

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));

        List<UUID> professorsIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<Professor> mockProfessors = List.of(new Professor("Professor 1", "professor@test.com", mockUser), new Professor("Professor 2", "professor2@test.com", mockUser));

        when(professorRepository.findAllById(professorsIds)).thenReturn(mockProfessors);

        UpdateSubjectProfessorsRequest mockRequest = new UpdateSubjectProfessorsRequest(professorsIds);
        Subject updatedMockSubject = new Subject(subjectId, mockSubject.getName(), mockSubject.getDescription(), mockUser, mockProfessors);

        when(subjectRepository.save(mockSubject)).thenReturn(updatedMockSubject);

        Subject result = subjectService.updateProfessors(subjectId, mockRequest, userDetails);

        assertNotNull(result);
        assertEquals(updatedMockSubject, result);
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(subjectId);
        verify(professorRepository, times(1)).findAllById(professorsIds);
        verify(subjectRepository, times(1)).save(mockSubject);
    }

    @Test
    @DisplayName("should be not able to update the professors of a nonexistent subject")
    void updateProfessorsCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID nonExistingSubjectId = UUID.randomUUID();

        when(subjectRepository.findById(nonExistingSubjectId)).thenReturn(Optional.empty());

        List<UUID> professorsIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        UpdateSubjectProfessorsRequest mockRequest = new UpdateSubjectProfessorsRequest(professorsIds);

        assertThrows(SubjectNotFoundException.class, () -> subjectService.updateProfessors(nonExistingSubjectId, mockRequest, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(nonExistingSubjectId);
    }

    @Test
    @DisplayName("should be not able to update the professors of a subject from another user")
    void updateProfessorsCase3() {
        User unauthorizedMockUser = new User(UUID.randomUUID(), "Unauthorized User", "unauthorized@email.com", "password");

        when(userDetails.getUsername()).thenReturn(unauthorizedMockUser.getEmail());
        when(userRepository.findByEmail(unauthorizedMockUser.getEmail())).thenReturn(unauthorizedMockUser);

        UUID subjectId = UUID.randomUUID();

        Subject mockSubject = new Subject(subjectId, "Subject 1", "Description 1", mockUser, new ArrayList<>());
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(mockSubject));

        List<UUID> professorsIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        UpdateSubjectProfessorsRequest mockRequest = new UpdateSubjectProfessorsRequest(professorsIds);

        assertThrows(UserUnauthorizedException.class, () -> subjectService.updateProfessors(subjectId, mockRequest, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, times(1)).findById(subjectId);
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
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).delete(mockSubject);
    }

    @Test
    @DisplayName("should be not able to delete a nonexistent subject")
    void deleteCase2() {
        when(userDetails.getUsername()).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        UUID nonExistingSubjectId = UUID.randomUUID();

        when(subjectRepository.findById(nonExistingSubjectId)).thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class, () -> subjectService.delete(nonExistingSubjectId, userDetails));
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
        verify(subjectRepository, times(1)).findById(nonExistingSubjectId);
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
        verify(userDetails, times(1)).getUsername();
        verify(userRepository, times(1)).findByEmail(unauthorizedMockUser.getEmail());
        verify(subjectRepository, times(1)).findById(subjectId);
    }
}