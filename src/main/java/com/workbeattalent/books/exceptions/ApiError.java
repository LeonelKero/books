package com.workbeattalent.books.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Standard api error response structure")
public record ApiError(
        @Schema(description = "Error resource path")
        String path,
        @Schema(description = "Error status")
        String status,
        @Schema(description = "Error message")
        String message,
        @Schema(description = "Map of error field and corresponding error message")
        Map<String, String> errors
        // TODO: Add timestamp and refactor the controller advice
) {
}
