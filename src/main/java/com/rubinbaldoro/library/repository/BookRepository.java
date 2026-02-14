package com.rubinbaldoro.library.repository;

import com.rubinbaldoro.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("select b from Book b " +
            "where lower(b.title) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(b.author) like lower(concat('%', :searchTerm, '%'))")
    List<Book> search(@Param("searchTerm") String searchTerm);
}
