package com.gmail.ia.reader.domain.dtos.iaevaluation;

import java.util.UUID;

public record IaEvaluationCreated(
        UUID correlationId,
        Long iaEvaluationId
) {
}
