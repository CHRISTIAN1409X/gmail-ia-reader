package com.gmail.ia.reader.domain.dtos.gmail;

import java.util.List;

public record EmailValidationResult(
        String emailOwner,
        boolean valid,
        List<ValidationError> errors
) {
}
