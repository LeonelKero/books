package com.workbeattalent.books.dto;

import java.util.UUID;

public record AuthorResponse(
        UUID id,
        String firstname,
        String lastname,
        String email
) {
}
