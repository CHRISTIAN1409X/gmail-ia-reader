package com.gmail.ia.reader.domain.dtos.pdf;

import com.gmail.ia.reader.domain.dtos.cloude.IaRespondeRecord;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;

import java.util.UUID;

public record PdfProcessingResult(
        UUID uuid,
        String localTempPath,
        IaRespondeRecord iaResponse,
        String path,
        String fileName
) {
}
