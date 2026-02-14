package com.rubinbaldoro.library.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    //Google Books Search
    private String isbn;

    @Column(name = "publish_year")
    private Integer publishYear;

    @Lob // Large Object (Binary)
    @Column(length = 1000000) // Allow up to 1MB image
    private byte[] coverImage;
}