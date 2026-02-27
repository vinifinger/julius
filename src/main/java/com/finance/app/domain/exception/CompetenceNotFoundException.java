package com.finance.app.domain.exception;

import java.util.UUID;

public class CompetenceNotFoundException extends RuntimeException {

    public CompetenceNotFoundException(UUID id) {
        super("Competence not found with id: " + id);
    }

}
