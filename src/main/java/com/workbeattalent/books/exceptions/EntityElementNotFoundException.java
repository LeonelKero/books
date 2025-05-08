package com.workbeattalent.books.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EntityElementNotFoundException extends RuntimeException {
    private final String message;
}
