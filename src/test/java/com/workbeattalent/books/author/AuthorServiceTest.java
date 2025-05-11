package com.workbeattalent.books.author;

import com.workbeattalent.books.dto.AuthorRequest;
import com.workbeattalent.books.dto.AuthorResponse;
import com.workbeattalent.books.exceptions.EntityElementNotFoundException;
import com.workbeattalent.books.util.EntityDtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private EntityDtoMapper mapper;

    @Captor
    private ArgumentCaptor<Author> captor;

    @InjectMocks
    private AuthorService underTest;

    @Test
    void whenFindingAllAuthors_thenReturnTheAuthorsList() {
        // Given
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
        when(this.authorRepository.findAllById(any()))
                .thenReturn(List.of(greg, imram));
        // When
        final var response = this.underTest.findAll(Set.of(UUID.randomUUID()));
        // Then
        assertThat(response.size()).isEqualTo(2);
    }

    @Test
    void whenRequestToCreateNewAuthor_thenThisAuthorIsMappedToBeSaved() {
        // Given
        final var william = new AuthorRequest(
                null,
                "William",
                "Denniss",
                "william.deniss@hotmail.com");

        final var williamAuthor = Author.builder()
                .firstname("William")
                .lastname("Denniss")
                .email("william.deniss@hotmail.com")
                .build();

        when(this.mapper.toAuthor(any())).thenReturn(williamAuthor);
        // When
        this.underTest.create(william);
        // Then
        verify(this.authorRepository, times(1))
                .save(this.captor.capture());
        final var capturedAuthor = this.captor.getValue();

        assertThat(capturedAuthor.getFirstname()).isEqualTo(william.firstname());
        assertThat(capturedAuthor.getLastname()).isEqualTo(william.lastname());
        assertThat(capturedAuthor.getEmail()).isEqualTo(william.email());
    }

    @Test
    void whenFindingExistingAuthorByItsId_thenReturnThatAuthor() {
        // Given
        final var greg = Author.builder()
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();
        when(this.authorRepository.findById(any())).thenReturn(Optional.of(greg));

        UUID id = UUID.randomUUID();
        final var gregResponse = new AuthorResponse(
                id,
                "Greg L.",
                "Turnsquist",
                "turnquist@hotmail.com");
        when(this.mapper.fromAuthor(any())).thenReturn(gregResponse);
        // When
        final var response = this.underTest.findById(id);
        // Then
        assertThat(response.firstname()).isEqualTo(greg.getFirstname());
        assertThat(response.lastname()).isEqualTo(greg.getLastname());
        assertThat(response.email()).isEqualTo(greg.getEmail());
    }

    @Test
    void whenFindingNotExistingAuthorById_thenThrowEntityElementNotFoundException() {
        // Given
        final var fakeId = UUID.randomUUID();
        when(this.authorRepository.findById(any())).thenReturn(Optional.empty());
        // When // Then
        assertThatThrownBy(() -> this.underTest.findById(fakeId))
                .isInstanceOf(EntityElementNotFoundException.class)
                .hasMessage("Unable to fetch author with ID: %s", fakeId);
    }

    @Test
    void whenLookingForExistingAuthorByItsId_thenReturnTRUE() {
        // Given
        when(this.authorRepository.existsById(any())).thenReturn(true);
        // When
        final var response = this.underTest.exists(UUID.randomUUID());
        // Then
        assertThat(response).isTrue();
    }

    @Test
    void whenLookingForNotExistingAuthorByItsId_thenReturnFALSE() {
        // Given
        when(this.authorRepository.existsById(any())).thenReturn(false);
        // When
        final var response = this.underTest.exists(UUID.randomUUID());
        // Then
        assertThat(response).isFalse();
        assertThat(response).isInstanceOf(Boolean.class);
    }
}