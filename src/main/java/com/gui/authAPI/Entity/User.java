package com.gui.authAPI.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = true)
    private String userName;

    @Column(unique = true, nullable = true)
    private String password;

    @Column(nullable = false)
    private String role;

}
