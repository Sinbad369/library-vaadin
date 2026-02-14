package com.rubinbaldoro.library.rest;

import com.rubinbaldoro.library.dto.AppResponse;

import com.rubinbaldoro.library.entity.Student;
import com.rubinbaldoro.library.service.StudentService;
import com.rubinbaldoro.library.util.RestConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RestConstants.BASE_PATH_V1 + "/student")
@Tag(name = "2. Student Controller", description = "Manage Students")
public class StudentRestController {

    private final StudentService studentService;

    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    // POST: Create
    @PostMapping
    @Operation(summary = "Create a new student")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.save(student));
    }

    // GET: Get All
    @GetMapping
    @Operation(summary = "Get list of all students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.findAll(null));
    }

    // GET: Get One
    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    // PUT: Update
    @PutMapping("/{id}")
    @Operation(summary = "Update student", description = "Returns true if successful")
    public ResponseEntity<Boolean> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        student.setId(id);
        studentService.save(student);
        return ResponseEntity.ok(true);
    }

    // DELETE: Delete
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student", description = "Returns true if successful")
    public ResponseEntity<Boolean> deleteStudent(@PathVariable Long id) {
        studentService.deleteById(id);
        return ResponseEntity.ok(true);
    }
}