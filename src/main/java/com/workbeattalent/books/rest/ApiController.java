package com.workbeattalent.books.rest;

import com.workbeattalent.books.author.AuthorService;
import com.workbeattalent.books.book.BookService;
import com.workbeattalent.books.dto.BookRequest;
import com.workbeattalent.books.dto.BookResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = {"/api/v1/books"})
public class ApiController {

    private final BookService bookService;
    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<BookResponse> create(final @Valid @RequestBody BookRequest request) {
        return new ResponseEntity<>(this.bookService.store(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> allBooks() {
        return new ResponseEntity<>(this.bookService.findAll(), HttpStatus.OK);
    }

    @GetMapping(path = {"/{id}"})
    public ResponseEntity<BookResponse> getBook(final @PathVariable Long id) {
        return new ResponseEntity<>(this.bookService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping(path = {"/{id}"})
    public ResponseEntity<Long> delete(final @PathVariable Long id) {
        return new ResponseEntity<>(this.bookService.delete(id), HttpStatus.ACCEPTED);
    }

    @PutMapping(path = {"/{id}"})
    public ResponseEntity<BookResponse> update(final @PathVariable Long id, final @Valid @RequestBody BookRequest updateRequest) {
        return new ResponseEntity<>(this.bookService.update(id, updateRequest), HttpStatus.ACCEPTED);
    }
}
