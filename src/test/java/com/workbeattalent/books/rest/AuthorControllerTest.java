package com.workbeattalent.books.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workbeattalent.books.author.AuthorService;
import com.workbeattalent.books.dto.AuthorRequest;
import com.workbeattalent.books.dto.AuthorResponse;
import com.workbeattalent.books.exceptions.EntityElementNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = {AuthorController.class})
class AuthorControllerTest {

    private final static String API_PATH = "/api/v1/authors";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void givenValidAuthorId_whenTryingToGet_thenReturnAuthorResponse() throws Exception {
        // Given
        final var authorId = UUID.randomUUID();
        final var greg = new AuthorResponse(
                authorId,
                "Greg L.",
                "Turnsquist",
                "turnquist@hotmail.com");
        when(this.authorService.findById(authorId)).thenReturn(greg);
        // When // Then
        this.mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/" + authorId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastname", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.notNullValue()));
    }

    @Test
    void givenAnInvalidAuthorId_whenTryingToGetAuthor_thenThrowEntityElementNotFoundException() throws Exception {
        // Given
        final var fakeAuthorId = UUID.randomUUID();
        when(this.authorService.findById(fakeAuthorId)).thenThrow(new EntityElementNotFoundException("Unable to fetch author with ID: " + fakeAuthorId));
        // When // Then
        mvc.perform(MockMvcRequestBuilders.get(API_PATH + "/" + fakeAuthorId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", Matchers.is("uri=" + API_PATH + "/" + fakeAuthorId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is("NOT_FOUND")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Unable to fetch author with ID: " + fakeAuthorId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", Matchers.is(Map.of())));
    }

    @Test
    void givenValidAuthor_whenRequestForCreation_thenCreateNewAuthor() throws Exception {
        // Given
        final var request = new AuthorRequest(null, "John", "Doe", "john.doe@mail.org");
        final var response = new AuthorResponse(UUID.randomUUID(), "John", "Doe", "john.doe@mail.org");
        when(this.authorService.create(request)).thenReturn(response);
        // When // Thens
        mvc.perform(MockMvcRequestBuilders.post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname", Matchers.is(request.firstname())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastname", Matchers.is(request.lastname())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(request.email())));
    }

    @Test
    void givenInvalidAuthorFirstname_whenRequestForCreation_thenReturnBadRequest() throws Exception {
        // Given
        final var request = new AuthorRequest(null, null, "Doe", "john.doe@mail.org");
        // When // Then
        mvc.perform(MockMvcRequestBuilders.post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['firstname']", Matchers.is("Author firstname is required")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())));
        verify(this.authorService, never()).create(any(AuthorRequest.class));
    }

    @Test
    void givenInvalidAuthorFirstnameLength_whenRequestForCreation_thenReturnBadRequest() throws Exception {
        // Given
        final var request = new AuthorRequest(null, "k", "Doe", "john.doe@mail.org");
        // When // Then
        mvc.perform(MockMvcRequestBuilders.post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['firstname']", Matchers.is("Author firstname should be on at least 02 characters")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())));
        verify(this.authorService, never()).create(any(AuthorRequest.class));
    }

    @Test
    void givenInvalidAuthorEmail_whenRequestForCreation_thenReturnBadRequest() throws Exception {
        // Given
        final var request = new AuthorRequest(null, "kye", "Doe", "john.doemail.org");
        // When // Then
        mvc.perform(MockMvcRequestBuilders.post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['email']", Matchers.is("Invalid email")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())));
        verify(this.authorService, never()).create(any(AuthorRequest.class));
    }

    @Test
    void givenInvalidAuthorEmailAndFirstname_whenRequestForCreation_thenReturnBadRequest() throws Exception {
        // Given
        final var request = new AuthorRequest(null, null, "Doe", "john.doemail.org");
        // When // Then
        mvc.perform(MockMvcRequestBuilders.post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['firstname']", Matchers.is("Author firstname is required")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors['email']", Matchers.is("Invalid email")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())));
        verify(this.authorService, never()).create(any(AuthorRequest.class));
    }
}