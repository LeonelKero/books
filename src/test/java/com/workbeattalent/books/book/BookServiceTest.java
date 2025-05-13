package com.workbeattalent.books.book;

import com.workbeattalent.books.author.Author;
import com.workbeattalent.books.author.AuthorService;
import com.workbeattalent.books.dto.AuthorRequest;
import com.workbeattalent.books.dto.BookRequest;
import com.workbeattalent.books.dto.BookResponse;
import com.workbeattalent.books.exceptions.BookManagementInvalidException;
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
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private EntityDtoMapper mapper;

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private BookService underTest;

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    @Test
    void whenStoringNewBookWithExistingAuthor_thenReturnStoredBook() {
        // Given
        final var greg = Author.builder()
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();

        when(this.authorService.findAll(any())).thenReturn(Set.of(greg));

        final var book1 = Book.builder()
                .id(1L)
                .title("Learning Spring Boot 3.0")
                .summary("Simplify the development of production grade application with java and spring boot")
                .pages(248)
                .build();

        final var bookRequest = new BookRequest(
                null,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(UUID.randomUUID()));

        final var bookResponse = new BookResponse(
                1L,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));

        when(this.mapper.toBook(any())).thenReturn(book1);

        book1.setAuthors(Set.of(greg));
        when(this.bookRepository.save(any())).thenReturn(book1);

        when(this.mapper.fromBook(any())).thenReturn(bookResponse);
        // When
        final var response = this.underTest.store(bookRequest);
        // Then
        assertThat(response.authors().iterator().next().getFirstname()).isEqualTo(greg.getFirstname());
        assertThat(response.title()).isEqualTo(bookRequest.title());
    }

    @Test
    void whenAttemptedToStoreNewBookWithNoAuthor_thenThrowEntityElementNotFoundException() {
        // Given
        final var bookRequest = new BookRequest(
                null,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(UUID.randomUUID()));
        when(this.authorService.findAll(any())).thenReturn(Set.of());
        // When // Then
        assertThatThrownBy(() -> this.underTest.store(bookRequest))
                .isInstanceOf(EntityElementNotFoundException.class)
                .hasMessage("One or more author not found");

        verify(this.mapper, never()).toBook(any());
        verify(this.bookRepository, never()).save(any());
        verify(this.mapper, never()).fromBook(any());
    }

    @Test
    void whenFindingAllExistingBooks_thenReturnListOfBooks() {
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

        final var book1 = new Book(
                1L,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));
        final var book1Response = new BookResponse(
                1L,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));

        final var book2 = new Book(
                2L,
                "Master Blockchain",
                747,
                "A technical guide for the inner working of blockchain",
                Set.of(imram));
        final var book2Response = new BookResponse(
                2L,
                "Master Blockchain",
                747,
                "A technical guide for the inner working of blockchain",
                Set.of(imram));

        when(this.bookRepository.findAll()).thenReturn(List.of(book1, book2));
        when(this.mapper.fromBook(book1)).thenReturn(book1Response);
        when(this.mapper.fromBook(book2)).thenReturn(book2Response);
        // When
        final var response = this.underTest.findAll();
        // Then
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.get(0).title()).isEqualTo(book1.getTitle());
        assertThat(response.get(1).title()).isEqualTo(book2.getTitle());
    }

    @Test
    void whenFindingBooksByTitleContainingSomeKeyword_thenReturnListOfBooks() {
        // Given
        final var keyword = "sprING";
        final var greg = Author.builder()
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();
        final var book1 = new Book(
                1L,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));
        final var bookResponse = new BookResponse(
                1L,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));

        when(this.bookRepository.findByTitleContainingIgnoreCaseOrderByAuthors_FirstnameAsc(any()))
                .thenReturn(List.of(book1));
        when(this.mapper.fromBook(book1)).thenReturn(bookResponse);

        // When
        final var response = this.underTest.findByTitleContaining(keyword);
        // Then
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.getFirst().title()).isEqualTo(book1.getTitle());
        assertThat(response.getFirst().pages()).isEqualTo(book1.getPages());
        assertThat(response.getFirst().summary()).isEqualTo(book1.getSummary());
    }

    @Test
    void whenFindingByExistingAuthorName_thenReturnRelatedBooks() {
        // Given
        UUID id = UUID.randomUUID();
        String firstname = "Greg L.";
        String lastname = "Turnsquist";
        final var request = new AuthorRequest(
                id,
                firstname,
                lastname,
                "turnquist@hotmail.com");

        final var greg = Author.builder()
                .id(id)
                .firstname(firstname)
                .lastname(lastname)
                .email("turnquist@hotmail.com")
                .build();
        final var book1 = new Book(
                1L,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));

        final var bookResponse = new BookResponse(
                1L,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));

        when(this.authorService.exists(any())).thenReturn(true);
        when(this.mapper.fromBook(book1)).thenReturn(bookResponse);
        when(this.bookRepository.findByAuthors_FirstnameIgnoreCaseOrAuthors_LastnameIgnoreCase(firstname, lastname))
                .thenReturn(List.of(book1));
        // When
        final var response = this.underTest.findByAuthorName(request);
        // Then
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.getFirst().title()).isEqualTo(book1.getTitle());
    }

    @Test
    void whenFindingByNotExistingAuthorName_thenThrowBookManagementInvalidException() {
        // Given
        UUID id = UUID.randomUUID();
        String firstname = "John";
        String lastname = "Doe";
        final var john = Author.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email("doe.john@mail.com")
                .build();

        final var request = new AuthorRequest(
                id,
                firstname,
                lastname,
                "doe.john@mail.com");

        when(this.authorService.exists(any(UUID.class))).thenReturn(false);
        // When // Then
        assertThatThrownBy(() -> this.underTest.findByAuthorName(request))
                .isInstanceOf(BookManagementInvalidException.class)
                .hasMessage("Author not found. So, unable to retrieve related books");
        verify(this.bookRepository, never()).findByAuthors_FirstnameIgnoreCaseOrAuthors_LastnameIgnoreCase(anyString(), anyString());
        verify(this.mapper, never()).fromBook(any(Book.class));
    }

    @Test
    void whenFindingExistingBookById_thenReturnBookResponse() {
        // Given
        final var id = 1L;
        final var greg = Author.builder()
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();
        final var book1 = new Book(
                1L,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));
        final var book1Response = new BookResponse(
                1L,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));
        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.of(book1));
        when(this.mapper.fromBook(book1)).thenReturn(book1Response);
        // When
        final var response = this.underTest.findById(id);
        // Then
        assertThat(response.title()).isEqualTo(book1Response.title());
        assertThat(response.pages()).isEqualTo(book1Response.pages());
        assertThat(response.summary()).isEqualTo(book1Response.summary());
        assertThat(response.authors().iterator().next().getFirstname()).isEqualTo(book1Response.authors().iterator().next().getFirstname());
        assertThat(response.authors().iterator().next().getLastname()).isEqualTo(book1Response.authors().iterator().next().getLastname());
        assertThat(response.authors().iterator().next().getEmail()).isEqualTo(book1Response.authors().iterator().next().getEmail());
    }

    @Test
    void whenFindingNotExistingBookById_thenThrowEntityElementNotFoundException() {
        // Given
        final var fakeId = -1L;
        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        // When // Then
        assertThatThrownBy(() -> this.underTest.findById(fakeId))
                .isInstanceOf(EntityElementNotFoundException.class)
                .hasMessage("No Book found with id: %s", fakeId);
        verify(this.mapper, never()).fromBook(any(Book.class));
    }

    @Test
    void whenBookExistsDeletingById_thenReturnTheIdOfDeletedBookId() {
        // Given
        final var id = 1L;
        final var greg = Author.builder()
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();
        final var book1 = new Book(
                id,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));
        final var book1Response = new BookResponse(
                id,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));
        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.of(book1));
        // When
        final var response = this.underTest.delete(id);
        // Then
        verify(this.bookRepository, times(1)).delete(bookCaptor.capture());
        final var capturedValue = bookCaptor.getValue();
        assertThat(capturedValue.getTitle()).isEqualTo(book1.getTitle());
        assertThat(capturedValue.getSummary()).isEqualTo(book1.getSummary());
        assertThat(capturedValue.getPages()).isEqualTo(book1.getPages());
        assertThat(capturedValue.getAuthors()).isEqualTo(book1.getAuthors());

        assertThat(response).isEqualTo(id);
    }

    @Test
    void whenTryingToDeleteNotExistingBookById_thenThrowEntityElementNotFoundException() {
        // Given
        final var fakeId = -1L;
        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        // When // Then
        assertThatThrownBy(() -> this.underTest.delete(fakeId))
                .isInstanceOf(EntityElementNotFoundException.class)
                .hasMessage("Unable to delete book with id: %s", fakeId);
        verify(this.bookRepository, never()).delete(any(Book.class));
    }

    @Test
    void whenTryingToUpdateExistingBook_thenReturnUpdatedBook() {
        // Given
        final var id = 1L;
        final var authorId = UUID.randomUUID();
        final var greg = Author.builder()
                .id(UUID.randomUUID())
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();
        final var john = Author.builder()
                .id(authorId)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@hotmail.com")
                .build();
        final var updateRequest = new BookRequest(
                id,
                "Learning Spring Boot 4.0",
                300,
                "some summary here",
                Set.of(authorId));
        final var book1 = new Book(
                id,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));
        final var book1Response = new BookResponse(
                id,
                "Learning Spring Boot 4.0",
                300,
                "some summary here",
                Set.of(john));

        final var updatedBook1 = new Book(
                id,
                "Learning Spring Boot 4.0",
                300,
                "some summary here",
                Set.of(john));
        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.of(book1));
        when(this.authorService.findAll(any())).thenReturn(Set.of(john));
        when(this.bookRepository.save(any(Book.class))).thenReturn(updatedBook1);
        when(this.mapper.fromBook(updatedBook1)).thenReturn(book1Response);
        // When
        final var response = this.underTest.update(id, updateRequest);
        // Then
        assertThat(response.title()).isEqualTo(updateRequest.title());
        assertThat(response.summary()).isEqualTo(updateRequest.summary());
        assertThat(response.pages()).isEqualTo(updateRequest.pages());
    }

    @Test
    void whenTryingToUpdateBookThatDoNotExists_thenThrowEntityElementNotFoundException() {
        // Given
        final var fakeBookId = -1L;
        final var updateRequest = new BookRequest(
                fakeBookId,
                "Neutron element",
                300,
                null,
                null);
        when(this.bookRepository.findById(fakeBookId)).thenReturn(Optional.empty());
        // When // Then
        assertThatThrownBy(() -> this.underTest.update(fakeBookId, updateRequest))
                .isInstanceOf(EntityElementNotFoundException.class)
                .hasMessage("Unable to find book with ID: %s", fakeBookId);
        verify(this.authorService, never()).findById(any());
        verify(this.bookRepository, never()).save(any());
        verify(this.mapper, never()).fromBook(any());
    }

    @Test
    void whenTryingToUpdateExistingBookWithNotExistingAuthor_thenThrowBookManagementInvalidException() {
        // Given
        final var id = 1L;
        final var fakeAuthorId = UUID.randomUUID();
        final var greg = Author.builder()
                .id(UUID.randomUUID())
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();
        final var book1 = new Book(
                id,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));

        final var updateRequest = new BookRequest(
                2L,
                "Neutron element",
                300,
                null,
                Set.of(fakeAuthorId));

        when(this.bookRepository.findById(anyLong())).thenReturn(Optional.of(book1));
        when(this.authorService.findAll(Set.of(fakeAuthorId))).thenReturn(Set.of());
        // When // Then
        assertThatThrownBy(() -> this.underTest.update(id, updateRequest))
                .isInstanceOf(BookManagementInvalidException.class)
                .hasMessage("One or more author(s) not found for update");
        verify(this.bookRepository, never()).save(any());
        verify(this.mapper, never()).fromBook(any());
    }
}