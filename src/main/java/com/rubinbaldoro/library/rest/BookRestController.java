package com.rubinbaldoro.library.rest;

import com.rubinbaldoro.library.dto.AppResponse;
import com.rubinbaldoro.library.entity.Book;
import com.rubinbaldoro.library.service.BookService;
import com.rubinbaldoro.library.util.RestConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RestConstants.BASE_PATH_V1 + "/book")
@Tag(name = "1. Book Controller", description = "Manage Books")
public class BookRestController {

    private final BookService bookService;

    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    // POST: Create
    @PostMapping
    @Operation(summary = "Create a new book")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookService.save(book);
        return ResponseEntity.ok(savedBook);
    }

    // GET: Get All
    @GetMapping
    @Operation(summary = "Get list of all books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAll(null));
    }

    // GET: Get One
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    // PUT: Update
    @PutMapping("/{id}")
    @Operation(summary = "Update book", description = "Returns true if successful")
    public ResponseEntity<Boolean> updateBook(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        bookService.save(book);
        return ResponseEntity.ok(true);
    }

    // DELETE: Delete
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book", description = "Returns true if successful")
    public ResponseEntity<Boolean> deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        // Requirement explicitly asks for "Response -> true"
        return ResponseEntity.ok(true);
    }
}