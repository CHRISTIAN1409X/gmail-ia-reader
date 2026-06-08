package com.gmail.ia.reader.infraestructure.models.records;

public record CriteriaResult(
        String criterion,
        Boolean passed,
        String observation
) {}