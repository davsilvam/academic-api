package com.davsilvam.domain.subject;

import com.davsilvam.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "subjects")
@Table(name = "subjects")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Subject(String name, String description, User user) {
        this.name = name;
        this.description = description;
        this.user = user;
    }
}
