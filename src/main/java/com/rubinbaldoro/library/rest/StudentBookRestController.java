package com.rubinbaldoro.library.rest;

import com.rubinbaldoro.library.dto.AppResponse;

import com.rubinbaldoro.library.entity.StudentBook;
import com.rubinbaldoro.library.service.StudentBookService;
import com.rubinbaldoro.library.util.RestConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RestConstants.BASE_PATH_V1 + "/student_book")
@Tag(name = "3. Student-Book Controller", description = "Manage Loans (Taking/Returning)")
public class StudentBookRestController {

    private final StudentBookService studentBookService;

    public StudentBookRestController(StudentBookService studentBookService) {
        this.studentBookService = studentBookService;
    }

    // POST: assign book - aka create loan
    @PostMapping
    @Operation(summary = "Take a book (Create loan)")
    public ResponseEntity<StudentBook> assignBook(@RequestBody StudentBook studentBook) {
        return ResponseEntity.ok(studentBookService.save(studentBook));
    }

    @PutMapping
    @Operation(summary = "Return a book", description = "Request: {studentId, bookId}")
    public ResponseEntity<StudentBook> returnBookStrict(@RequestBody LoanRequest req) {
        StudentBook updatedLoan = studentBookService.returnBookByDetails(req.getStudentId(), req.getBookId());
        return ResponseEntity.ok(updatedLoan);
    }

    // GET: Get All
    @GetMapping
    @Operation(summary = "Get all loans")
    public ResponseEntity<List<StudentBook>> getAllStudentBook() {
        return ResponseEntity.ok(studentBookService.findAll(null));
    }

    // GET: Get One
    @GetMapping("/{id}")
    @Operation(summary = "Get loan by ID")
    public ResponseEntity<StudentBook> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentBookService.getById(id));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all books taken by a specific student")
    public ResponseEntity<List<StudentBook>> getByStudentId(@PathVariable Long studentId) {
        // You need to add this method to your Service/Repository!
        return ResponseEntity.ok(studentBookService.findAllByStudentId(studentId));
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get history of who took a specific book")
    public ResponseEntity<List<StudentBook>> getByBookId(@PathVariable Long bookId) {
        // You need to add this method to your Service/Repository!
        return ResponseEntity.ok(studentBookService.findAllByBookId(bookId));
    }

    // DELETE: Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponse<String>> deleteStudentBook(@PathVariable Long id) {
        studentBookService.deleteById(id);
        return ResponseEntity.ok(new AppResponse<>("Loan record deleted"));
    }
}

class LoanRequest {
    private Long studentId;
    private Long bookId;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}