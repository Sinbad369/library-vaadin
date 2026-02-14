package com.rubinbaldoro.library.repository;

import com.rubinbaldoro.library.entity.StudentBook;
import com.rubinbaldoro.library.enums.StudentBookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface StudentBookRepository extends JpaRepository<StudentBook, Long> {
    @Query("select sb from StudentBook sb " +
            "where lower(sb.student.firstName) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(sb.book.title) like lower(concat('%', :searchTerm, '%'))")
    List<StudentBook> search(@Param("searchTerm") String searchTerm);

    // 1. Find all loans for a specific student
    List<StudentBook> findAllByStudentId(Long studentId);

    // 2. Find all loans for a specific book
    List<StudentBook> findAllByBookId(Long bookId);

    // 3. Find the specific 'TAKEN' book for return logic (Student + Book)
    Optional<StudentBook> findByStudentIdAndBookIdAndStatus(Long studentId, Long bookId, StudentBookStatus status);

}
