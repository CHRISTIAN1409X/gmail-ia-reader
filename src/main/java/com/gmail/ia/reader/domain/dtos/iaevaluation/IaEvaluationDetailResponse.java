package com.gmail.ia.reader.domain.dtos.iaevaluation;

import com.gmail.ia.reader.domain.dtos.cloude.CriteriaResult;

import java.util.List;
import java.util.UUID;

public record IaEvaluationDetailResponse(
        UUID uuid,
        Double score,
        String pdfName,
        String urlPdfDrive,
        String statusKey,
        String status,
        List<CriteriaResult> criteriaResults
) {
}