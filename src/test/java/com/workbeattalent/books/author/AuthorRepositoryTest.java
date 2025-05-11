package com.workbeattalent.books.author;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository underTest;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        final var greg = Author.builder()
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();

        final var imram = Author.builder()
                .firstname("Imram")
                .lastname("Bachir")
                .email("imram.bachir@gmail.com")
                .build();

        this.entityManager.persist(greg);
        this.entityManager.persist(imram);

        this.entityManager.flush();
    }

    @Test
    void whenAuthorsArePresent_thenReturnAuthorsList() {
        // Given // When
        final var result = this.underTest.findAll();
        // Then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void whenRequestingToCreateNewAuthor_thenNewAuthorIsCreated() {
        // Given
        final var william = Author.builder()
                .firstname("William")
                .lastname("Denniss")
                .email("william.deniss@hotmail.com")
                .build();
        this.underTest.save(william);
        // When
        final var result = this.underTest.findAll();
        // Then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    void whenFindingExistingAuthorByItsID_thenReturnThatAuthor() {
        // Given
        final var william = Author.builder()
                .firstname("William")
                .lastname("Denniss")
                .email("william.deniss@hotmail.com")
                .build();

        final var authorId = this.underTest.save(william).getId();
        // When
        final var optionalResponse = this.underTest.findById(authorId);
        // Then
        assertThat(optionalResponse).isPresent();
        assertThat(optionalResponse.get().getFirstname()).isEqualTo(william.getFirstname());
        assertThat(optionalResponse.get().getLastname()).isEqualTo(william.getLastname());
        assertThat(optionalResponse.get().getEmail()).isEqualTo(william.getEmail());
    }

    @Test
    void whenFindingNotExistingAuthorByItsID_thenReturnEmpty() {
        // Given
        final var fakeId = UUID.randomUUID();
        // When
        final var optionalResponse = this.underTest.findById(fakeId);
        // Then
        assertThat(optionalResponse).isEmpty();
    }

}