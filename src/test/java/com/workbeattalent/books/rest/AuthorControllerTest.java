package com.workbeattalent.books.rest;

import com.workbeattalent.books.author.AuthorService;
import com.workbeattalent.books.dto.AuthorResponse;
import com.workbeattalent.books.exceptions.EntityElementNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = {AuthorController.class})
class AuthorControllerTest {

    private final static String API_PATH = "/api/v1/authors";

    @Autowired
    private MockMvc mvc;

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
    void author() {
    }
}