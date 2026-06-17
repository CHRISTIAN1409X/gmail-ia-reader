package com.gmail.ia.reader.domain.dtos.iaevaluation;

import java.time.LocalDateTime;
import java.util.UUID;

public record IaEvaluationListResponse(
        UUID uuid,
        Double score,
        String pdfName,
        String professor,
        String subjectName,
        String semester,
        String statusKey,
        String status,
        LocalDateTime submittedAt,
        LocalDateTime updatedAt
) {
}
