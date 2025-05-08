package com.workbeattalent.books.util;

import com.workbeattalent.books.author.Author;
import com.workbeattalent.books.book.Book;
import com.workbeattalent.books.dto.AuthorRequest;
import com.workbeattalent.books.dto.AuthorResponse;
import com.workbeattalent.books.dto.BookRequest;
import com.workbeattalent.books.dto.BookResponse;
import org.springframework.stereotype.Service;

@Service
public class EntityDtoMapper {
    public Book toBook(final BookRequest request) {
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .pages(request.pages())
                .summary(request.summary())
                .build();
    }

    public BookResponse fromBook(final Book savedBook) {
        return new BookResponse(
                savedBook.getId(),
                savedBook.getTitle(),
                savedBook.getPages(),
                savedBook.getSummary(),
                savedBook.getAuthors());
    }

    public Author toAuthor(AuthorRequest request) {
        return Author.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .build();
    }

    public AuthorResponse fromAuthor(Author author) {
        return new AuthorResponse(author.getId(), author.getFirstname(), author.getLastname(), author.getEmail());
    }
}
