package com.gmail.ia.reader.domain.dtos.gmail.pdf;

import com.gmail.ia.reader.domain.dtos.gmail.ValidationError;

import java.util.List;

public record PdfValidation(PdfDocument pdfDocument, List<ValidationError> validationError) {
}
