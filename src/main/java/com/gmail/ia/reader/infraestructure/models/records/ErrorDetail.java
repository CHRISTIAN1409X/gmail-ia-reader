package com.gmail.ia.reader.infraestructure.models.records;

public record ErrorDetail(
        String type,
        String message,
        String severity
) {}