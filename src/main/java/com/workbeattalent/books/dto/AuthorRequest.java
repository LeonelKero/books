package com.workbeattalent.books.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AuthorRequest(
        UUID id,

        @NotBlank(message = "Author firstname is required")
        @Size(min = 2, message = "Author firstname should be on at least 02 characters")
        String firstname,

        String lastname,

        @Email(message = "Invalid email", regexp = "")
        String email
) {
}
