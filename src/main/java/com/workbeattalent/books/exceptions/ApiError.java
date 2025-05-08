package com.workbeattalent.books.exceptions;

import java.util.Map;

public record ApiError(
        String path,
        String status,
        String message,
        Map<String, String> errors
) {
}
