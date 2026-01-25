package com.inventory.inventory_backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Custom field validation exception
@Getter
@AllArgsConstructor
public class FieldValidationException extends RuntimeException{

    private final String field;
    private final String message;
}
