package com.davsilvam.domain.absence;

import com.davsilvam.domain.subject.Subject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity(name = "absences")
@Table(name = "absences")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Date date;

    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    @JsonIgnoreProperties({"absences", "grades", "professors"})
    private Subject subject;

    public Absence(Date date, Integer amount, Subject subject) {
        this.date = date;
        this.amount = amount;
        this.subject = subject;
    }
}
