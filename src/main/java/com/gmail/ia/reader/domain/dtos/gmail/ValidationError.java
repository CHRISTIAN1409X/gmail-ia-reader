package com.gmail.ia.reader.domain.dtos.gmail;

public record ValidationError(
        String nameDocument,
        String code,
        String message
) {
}