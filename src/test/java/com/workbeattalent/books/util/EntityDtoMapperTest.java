package com.workbeattalent.books.util;

import com.workbeattalent.books.author.Author;
import com.workbeattalent.books.book.Book;
import com.workbeattalent.books.dto.AuthorRequest;
import com.workbeattalent.books.dto.AuthorResponse;
import com.workbeattalent.books.dto.BookRequest;
import com.workbeattalent.books.dto.BookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntityDtoMapperTest {

    private EntityDtoMapper underTest;

    @BeforeEach
    void setUp() {
        this.underTest = new EntityDtoMapper();
    }

    @Test
    void givenBookRequestObject_thenMappeItToBookObject() {
        // Given
        final var greg = Author.builder()
                .id(UUID.randomUUID())
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();
        final var request = new BookRequest(
                1L,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg.getId()));
        // When
        final var response = this.underTest.toBook(request);
        // Then
        assertThat(response).isInstanceOf(Book.class);
        assertThat(response.getTitle()).isEqualTo(request.title());
        assertThat(response.getSummary()).isEqualTo(request.summary());
        assertThat(response.getPages()).isEqualTo(request.pages());
    }

    @Test
    void givenBookObject_thenMappeItToBookResponseObject() {
        // Given
        final var greg = Author.builder()
                .id(UUID.randomUUID())
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();
        final var book = new Book(
                1L,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));
        // When
        final var response = this.underTest.fromBook(book);
        // Then
        assertThat(response).isInstanceOf(BookResponse.class);
        assertThat(response.title()).isEqualTo(book.getTitle());
        assertThat(response.summary()).isEqualTo(book.getSummary());
        assertThat(response.pages()).isEqualTo(book.getPages());
        assertThat(response.authors()).isNotEmpty();
    }

    @Test
    void givenAuthorRequestObject_thenMappeItToAuthorObject() {
        // Given
        final var greg = new AuthorRequest(
                null,
                "Greg",
                "Trumnist",
                "greg@mail.org");
        // When
        final var response = this.underTest.toAuthor(greg);
        // Then
        assertThat(response).isInstanceOf(Author.class);
        assertThat(response.getFirstname()).isEqualTo(greg.firstname());
        assertThat(response.getLastname()).isEqualTo(greg.lastname());
        assertThat(response.getEmail()).isEqualTo(greg.email());
    }

    @Test
    void givenAnAuthorObject_thenMappeItToAuthorResponseObject() {
        // Given
        final var greg = Author.builder()
                .id(UUID.randomUUID())
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();
        // When
        final var response = this.underTest.fromAuthor(greg);
        // Then
        assertThat(response).isInstanceOf(AuthorResponse.class);
        assertThat(response.firstname()).isEqualTo(greg.getFirstname());
        assertThat(response.lastname()).isEqualTo(greg.getLastname());
        assertThat(response.email()).isEqualTo(greg.getEmail());
    }
}