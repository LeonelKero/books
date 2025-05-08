package com.workbeattalent.books.author;

import com.workbeattalent.books.dto.AuthorRequest;
import com.workbeattalent.books.dto.AuthorResponse;
import com.workbeattalent.books.exceptions.EntityElementNotFoundException;
import com.workbeattalent.books.util.EntityDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository repository;
    private final EntityDtoMapper mapper;

    public Set<Author> findAll(final Set<UUID> ids) {
        return new HashSet<>(this.repository.findAllById(ids));
    }


    public AuthorResponse create(final AuthorRequest request) {
        Author saved = this.repository.save(this.mapper.toAuthor(request));
        return this.mapper.fromAuthor(saved);
    }

    public AuthorResponse findById(UUID authorId) {
        return this.repository.findById(authorId)
                .map(this.mapper::fromAuthor)
                .orElseThrow(() -> new EntityElementNotFoundException("Unable to fetch author with ID: " + authorId));
    }
}
