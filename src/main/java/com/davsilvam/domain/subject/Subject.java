package com.davsilvam.domain.subject;

import com.davsilvam.domain.absences.Absence;
import com.davsilvam.domain.grades.Grade;
import com.davsilvam.domain.professor.Professor;
import com.davsilvam.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "subjects")
@Table(name = "subjects")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"subjects", "professors"})
    private User user;

    @ManyToMany
    @JoinTable(name = "subjects_professors", joinColumns = @JoinColumn(name = "subject_id"), inverseJoinColumns = @JoinColumn(name = "professor_id"))
    @JsonIgnoreProperties({"user", "subjects"})
    private List<Professor> professors;

    @OneToMany(mappedBy = "subject")
    @JsonIgnoreProperties("subject")
    private List<Grade> grades;

    @OneToMany(mappedBy = "subject")
    @JsonIgnoreProperties("subject")
    private List<Absence> absences;

    public Subject(String name, String description, User user) {
        this.name = name;
        this.description = description;
        this.user = user;
    }

    public Subject(UUID id, String name, String description, User user, List<Professor> professors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.user = user;
        this.professors = professors;
    }

    public void removeProfessor(Professor professor) {
        this.professors.remove(professor);
    }
}
