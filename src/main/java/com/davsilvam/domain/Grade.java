package com.davsilvam.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "grades")
@Table(name = "grades")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private Float value;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    @JsonIgnoreProperties({"grades", "absences", "professors"})
    private Subject subject;

    public Grade(String name, Float value, Subject subject) {
        this.name = name;
        this.value = value;
        this.subject = subject;
    }
}
