package com.rubinbaldoro.library.service;


import com.rubinbaldoro.library.entity.StudentBook;
import com.rubinbaldoro.library.enums.StudentBookStatus;
import com.rubinbaldoro.library.repository.StudentBookRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentBookService {
    private final StudentBookRepository studentBookRepository;


    public StudentBookService(StudentBookRepository studentBookRepository) {
        this.studentBookRepository = studentBookRepository;
    }

    public List<StudentBook> findAll(String filterText){
        if(filterText == null || filterText.isEmpty()){
            return  studentBookRepository.findAll();
        } else{
            return studentBookRepository.search(filterText);
        }
    }
    //
    public StudentBook getById(Long id){
        return studentBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student Booking record not found with id: " + id));
    }

    //
    public void deleteById(Long id){
        studentBookRepository.deleteById(id);
    }

    public StudentBook save(StudentBook studentBook){
        // If this is a NEW record (no ID yet)
        if (studentBook.getId() == null) {
            studentBook.setAssignedDate(LocalDate.now());
            studentBook.setStatus(StudentBookStatus.TAKEN); // Default status
        }
        return studentBookRepository.save(studentBook);
    }

    public void delete(StudentBook studentBook){
        studentBookRepository.delete(studentBook);
    }

    public void returnBook(StudentBook studentBook) {
        studentBook.setStatus(StudentBookStatus.RETURNED);
        studentBook.setReturnedDate(LocalDate.now());
        studentBookRepository.save(studentBook);
    }

    public List<StudentBook> findAllByStudentId(Long studentId) {
        return studentBookRepository.findAllByStudentId(studentId);
    }

    public List<StudentBook> findAllByBookId(Long bookId) {
        return studentBookRepository.findAllByBookId(bookId);
    }

    // Return book by {studentId, bookId}
    public StudentBook returnBookByDetails(Long studentId, Long bookId) {
        // Find the record where this student took this book and hasn't returned it yet
        StudentBook loan = studentBookRepository.findByStudentIdAndBookIdAndStatus(studentId, bookId, StudentBookStatus.TAKEN)
                .orElseThrow(() -> new RuntimeException("No active loan found for this student and book"));

        returnBook(loan); // Reuse your existing logic
        return loan;
    }

}
