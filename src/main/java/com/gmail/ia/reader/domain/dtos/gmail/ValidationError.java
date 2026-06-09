package com.gmail.ia.reader.domain.dtos.gmail;

public record ValidationError(
        String code,
        String message
) {
}