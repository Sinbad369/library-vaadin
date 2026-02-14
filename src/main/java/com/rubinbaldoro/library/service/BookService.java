package com.rubinbaldoro.library.service;

import com.rubinbaldoro.library.entity.Book;
import com.rubinbaldoro.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll(String filterText){
        if(filterText == null || filterText.isEmpty()){
            return bookRepository.findAll();
        } else {
            return bookRepository.search(filterText);
        }
    }

    public Book save(Book book){
        return bookRepository.save(book);
    }

    public Book getById(Long id){
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " +  id));
    }

    public void delete(Long id){
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
}
