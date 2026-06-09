package com.finance.app.domain.exception;

import java.util.UUID;

public class SubcategoryNotFoundException extends RuntimeException {
    public SubcategoryNotFoundException(UUID id) {
        super("Subcategory with ID " + id + " not found");
    }
}
