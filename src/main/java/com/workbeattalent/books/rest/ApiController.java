package com.workbeattalent.books.rest;

import com.workbeattalent.books.book.BookService;
import com.workbeattalent.books.dto.AuthorRequest;
import com.workbeattalent.books.dto.BookRequest;
import com.workbeattalent.books.dto.BookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = {"/api/v1/books"})
@Tag(name = "API Controller", description = "Main Book Api Controller")
public class ApiController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "Store new Books", description = "Record new books onces author(s) is/are already saved")
    public ResponseEntity<BookResponse> create(final @Valid @RequestBody BookRequest request) {
        return new ResponseEntity<>(this.bookService.store(request), HttpStatus.CREATED);
    }

    @GetMapping(path = {"/search"})
    public ResponseEntity<List<BookResponse>> searchByTitleLikely(final @RequestParam String title) {
        return new ResponseEntity<>(this.bookService.findByTitleContaining(title), HttpStatus.OK);
    }

    @PostMapping(path = {"/authors"})
    public ResponseEntity<List<BookResponse>> searchAuthorBooks(final @Valid @RequestBody AuthorRequest request) {
        return new ResponseEntity<>(this.bookService.findByAuthorName(request), HttpStatus.OK);
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
