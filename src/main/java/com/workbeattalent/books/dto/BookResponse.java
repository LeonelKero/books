package com.workbeattalent.books.dto;

import com.workbeattalent.books.author.Author;

import java.util.Set;

public record BookResponse(
        Long id,
        String title,
        Integer pages,
        String summary,
        Set<Author> authors
) {
}
