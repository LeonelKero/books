package com.workbeattalent.books.rest;

import com.workbeattalent.books.author.AuthorService;
import com.workbeattalent.books.dto.AuthorRequest;
import com.workbeattalent.books.dto.AuthorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = {"/api/v1/authors"})
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService service;

    @PostMapping
    public ResponseEntity<AuthorResponse> register(final @Valid @RequestBody AuthorRequest authorRequest) {
        return new ResponseEntity<>(this.service.create(authorRequest), HttpStatus.CREATED);
    }

    @GetMapping(path = {"/{id}"})
    public ResponseEntity<AuthorResponse> author(final @PathVariable UUID id) {
        return new ResponseEntity<>(this.service.findById(id), HttpStatus.OK);
    }

    // Todo: Get author books
}
