package com.rubinbaldoro.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = Student.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "student_id")
    private Student student;

    private LocalDateTime expiryDate;

    public VerificationToken(String token, Student student) {
        this.token = token;
        this.student = student;
        this.expiryDate = LocalDateTime.now().plusHours(24); // Token expires in 24 hours
    }
}