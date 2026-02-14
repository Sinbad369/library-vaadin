package com.rubinbaldoro.library.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String studentId;

    private String login;
    private String password;
    private String role = "ROLE_STUDENT"; // Default role
    private Boolean enabled = false; // hits true after verification via email

    @Column
    private byte[] profilePicture;

}
