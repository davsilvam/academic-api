package com.davsilvam.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "professors")
@Table(name = "professors")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"professors", "subjects"})
    private User user;

    @ManyToMany(mappedBy = "professors")
    @JsonIgnoreProperties({"professors", "user", "absences", "grades"})
    private List<Subject> subjects;

    public Professor(String name, String email, User user) {
        this.name = name;
        this.email = email;
        this.user = user;
        this.subjects = new ArrayList<>();
    }

    public Professor(UUID id, String name, String email, User user) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.user = user;
        this.subjects = new ArrayList<>();
    }

    public void addSubject(Subject subject) {
        this.subjects.add(subject);
    }

    public void removeSubject(Subject subject) {
        this.subjects.remove(subject);
    }
}
