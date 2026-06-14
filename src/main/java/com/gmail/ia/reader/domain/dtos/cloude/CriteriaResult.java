package com.gmail.ia.reader.domain.dtos.cloude;

public record CriteriaResult(
        String criterion,
        Boolean passed,
        String observation,
        byte severity
) {}