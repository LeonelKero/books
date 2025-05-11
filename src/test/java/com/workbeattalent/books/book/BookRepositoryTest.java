package com.workbeattalent.books.book;

import com.workbeattalent.books.author.Author;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository underTest;

    @Autowired
    private EntityManager entityManager;

    private Author greg;

    @BeforeEach
    void setUp() {
        greg = Author.builder()
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();

        final var imram = Author.builder()
                .firstname("Imram")
                .lastname("Bachir")
                .email("imram.bachir@gmail.com")
                .build();

        final var book1 = Book.builder()
                .title("Learning Spring Boot 3.0")
                .summary("Simplify the development of production grade application with java and spring boot")
                .pages(248)
                .authors(Set.of(greg))
                .build();

        final var book2 = Book.builder()
                .title("Master Blockchain")
                .summary("A technical guide for the inner working of blockchain")
                .pages(747)
                .authors(Set.of(imram))
                .build();

        this.entityManager.persist(greg);
        this.entityManager.persist(imram);
        this.entityManager.persist(book1);
        this.entityManager.persist(book2);

        this.entityManager.flush();
    }

    @Test
    void whenFindingBooksByTitleIgnoringCase_thenReturnTheListOfBooks() {
        // Given
        final var keyword = "sPRing";
        // When
        final var response = this.underTest.findByTitleContainingIgnoreCaseOrderByAuthors_FirstnameAsc(keyword);
        // Then
        assertThat(response.size()).isEqualTo(1);
    }

    @Test
    void whenTryingToFindBookWithUnknownKeyword_thenReturnAnEmptyList() {
        // Given
        final var wrongKeyword = "FiSHing";
        // When
        final var response = this.underTest.findByTitleContainingIgnoreCaseOrderByAuthors_FirstnameAsc(wrongKeyword);
        // Then
        assertThat(response).isEmpty();
    }

    @Test
    void whenFindingAuthorByItsNames_thenReturnItsListOfBooks() {
        // Given
        final var book3 = Book.builder()
                .title("Hacking with Spring Boot 2.3")
                .summary("Spring Boot 2.3 is the hottest ticket in town...")
                .pages(428)
                .authors(Set.of(greg))
                .build();
        this.underTest.save(book3);
        // When
        final var response = this.underTest.findByAuthors_FirstnameIgnoreCaseOrAuthors_LastnameIgnoreCase(greg.getFirstname(), greg.getLastname());
        // Then
        assertThat(response.size()).isEqualTo(2);
    }

    @Test
    void whenFindingNotAuthorByNames_thenReturnAnEmptyList() {
        // Given
        final var fakeFirstname = "Bo";
        final var fakeLastname = "Joe";
        // When
        final var result = this.underTest.findByAuthors_FirstnameIgnoreCaseOrAuthors_LastnameIgnoreCase(fakeFirstname, fakeLastname);
        // Then
        assertThat(result).isEmpty();
    }
}