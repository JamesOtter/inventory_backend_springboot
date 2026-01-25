package com.inventory.inventory_backend.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class ErrorResponse {

    private Map<String, String> errors;

    public ErrorResponse(Map<String, String> errors){
        this.errors = errors;
    }
}
