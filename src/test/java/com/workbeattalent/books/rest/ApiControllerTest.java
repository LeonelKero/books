package com.workbeattalent.books.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workbeattalent.books.author.Author;
import com.workbeattalent.books.book.BookService;
import com.workbeattalent.books.dto.BookRequest;
import com.workbeattalent.books.dto.BookResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = {ApiController.class})
class ApiControllerTest {

    private final static String API_URI = "/api/v1/books";

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void givenValidBookRequest_whenBookIsStored_thenReturnSavedBookResponseWithStatusCREATED() throws Exception {
        // Given
        final var authorId = UUID.randomUUID();
        final var greg = Author.builder()
                .id(authorId)
                .firstname("Greg L.")
                .lastname("Turnsquist")
                .email("turnquist@hotmail.com")
                .build();
        final var request = new BookRequest(
                null,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(authorId));
        final var response = new BookResponse(
                1L,
                "Learning Spring Boot 3.0",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(greg));
        when(this.bookService.store(request)).thenReturn(response);
        // When // Then
        mvc.perform(MockMvcRequestBuilders.post(API_URI)
                        .content(this.mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(request.title())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pages", Matchers.is(request.pages())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.summary", Matchers.is(request.summary())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authors[0].firstname", Matchers.is(greg.getFirstname())));
    }

    @Test
    void givenNullBookTitle_whenBookIsStored_thenThrowAnExceptionAndReturnBAD_REQUEST() throws Exception {
        // Given
        final var authorId = UUID.randomUUID();
        final var request = new BookRequest(
                null,
                null,
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(authorId));
        // When // Then
        mvc.perform(MockMvcRequestBuilders.post(API_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['title']", Matchers.is("Book title is required")));
        verify(this.bookService, never()).store(any());
    }

    @Test
    void givenBookTooShortTitle_whenBookIsStored_thenThrowAnExceptionAndReturnBAD_REQUEST() throws Exception {
        // Given
        final var authorId = UUID.randomUUID();
        final var request = new BookRequest(
                null,
                "A",
                248,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(authorId));
        // When // Then
        mvc.perform(MockMvcRequestBuilders.post(API_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['title']", Matchers.is("Book title should be on at least 02 characters")));
        verify(this.bookService, never()).store(any());
    }

    @Test
    void givenBookWithNoPages_whenBookIsStored_thenThrowAnExceptionAndReturnBAD_REQUEST() throws Exception {
        // Given
        final var authorId = UUID.randomUUID();
        final var request = new BookRequest(
                null,
                "Learning Spring Boot 3.0",
                null,
                "Simplify the development of production grade application with java and spring boot",
                Set.of(authorId));
        // When // Then
        mvc.perform(MockMvcRequestBuilders.post(API_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['pages']", Matchers.is("Book page numbers is required")));
        verify(this.bookService, never()).store(any());
    }

    @Test
    void givenBookWithEmptyAuthorIdsSet_whenBookIsStored_thenThrowAnExceptionAndReturnBAD_REQUEST() throws Exception {
        // Given
        final var request = new BookRequest(
                null,
                "Learning Spring Boot 3.0",
                null,
                "Simplify the development of production grade application with java and spring boot",
                Set.of());
        // When // Then
        mvc.perform(MockMvcRequestBuilders.post(API_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['authorIds']", Matchers.is("A book must have at least 01 author")));
        verify(this.bookService, never()).store(any());
    }

    @Test
    void givenBookWithNullAuthorIdsSet_whenBookIsStored_thenThrowAnExceptionAndReturnBAD_REQUEST() throws Exception {
        // Given
        final var request = new BookRequest(
                null,
                "Learning Spring Boot 3.0",
                null,
                "Simplify the development of production grade application with java and spring boot",
                null);
        // When // Then
        mvc.perform(MockMvcRequestBuilders.post(API_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['authorIds']", Matchers.is("Please provide an authors id list")));
        verify(this.bookService, never()).store(any());
    }

    @Test
    void givenBookWithNoneOfTheRequiredFieldsSet_whenBookIsStored_thenThrowAnExceptionAndReturnBAD_REQUEST() throws Exception {
        // Given
        final var request = new BookRequest(
                null,
                null,
                null,
                "Some useless summary",
                null);
        // When // Then
        mvc.perform(MockMvcRequestBuilders.post(API_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['title']", Matchers.is("Book title is required")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['pages']", Matchers.is("Book page numbers is required")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['authorIds']", Matchers.is("Please provide an authors id list")));
        verify(this.bookService, never()).store(any());
    }

    @Test
    void givenTitleOfExistingBook__whenSearchingByTitleLikely_thenReturnOK() throws Exception {
        // Given
        final var title = "Spring";
        final var book1 = new BookResponse(1L, "Learning Spring Boot 3.0", 248, "", Set.of());
        final var book2 = new BookResponse(2L, "Spring Security", 300, "", Set.of());
        final var book3 = new BookResponse(3L, "Blockchain Fundamentals", 600, "", Set.of());
        when(this.bookService.findByTitleContaining(title)).thenReturn(List.of(book1, book2));
        // When // Then
        mvc.perform(MockMvcRequestBuilders.get(API_URI + "/search?title={title}", title))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(2)));
    }

    @Test
    void givenTitleOfNotExistingBook__whenSearchingByTitleLikely_thenReturnOK() throws Exception {
        // Given
        final var title = "Spring";
        when(this.bookService.findByTitleContaining(title)).thenReturn(Collections.emptyList());
        // When // Then
        mvc.perform(MockMvcRequestBuilders.get(API_URI + "/search?title={title}", title))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(0)));
    }


    @Test
    void searchAuthorBooks() {
    }

    @Test
    void whenRequestingAllBooks_thenReturnOK() throws Exception {
        // Given
        final var book1 = new BookResponse(1L, "Learning Spring Boot 3.0", 248, "", Set.of());
        final var book2 = new BookResponse(2L, "Spring Security", 300, "", Set.of());
        final var book3 = new BookResponse(3L, "Blockchain Fundamentals", 600, "", Set.of());
        when(this.bookService.findAll()).thenReturn(Arrays.asList(book1, book2, book3));
        // When // Then
        mvc.perform(MockMvcRequestBuilders.get(API_URI))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(3)));
    }

    @Test
    void getBook() {
    }

    @Test
    void delete() {
    }

    @Test
    void update() {
    }

}