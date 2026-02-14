package com.rubinbaldoro.library.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rubinbaldoro.library.enums.StudentBookStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class StudentBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @JsonProperty("createdDate")
    private LocalDate assignedDate;
    private LocalDate returnedDate;

    @Enumerated(EnumType.STRING)
    private StudentBookStatus status;

}
