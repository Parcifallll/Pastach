package com.example.Pastach.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private RoleEnum name; // USER, ADMIN

    public Role(RoleEnum name) {
        this.name = name;
    }

}
