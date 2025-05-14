package com.workbeattalent.books.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record BookRequest(
        Long id,

        @NotBlank(message = "Book title is required")
        @Size(min = 2, message = "Book title should be on at least 02 characters")
        String title,

        @NotNull(message = "Book page numbers is required")
//        @Size(min = 2, message = "Book must has as least 02 pages")
        Integer pages,

        String summary,

        @NotNull(message = "Please provide an authors id list")
        @Size(min = 1, message = "A book must have at least 01 author")
        Set<UUID> authorIds
) {
}
