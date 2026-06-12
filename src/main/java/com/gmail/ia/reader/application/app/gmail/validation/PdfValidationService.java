package com.gmail.ia.reader.application.app.gmail.validation;

import com.gmail.ia.reader.domain.dtos.gmail.ValidationError;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfValidation;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfValidationService {

    private static final long MAX_SIZE_BYTES =
            5 * 1024 * 1024;

    public PdfValidation validate(PdfDocument pdf) {
        List<ValidationError> errors = new ArrayList<>();

        validateSize(pdf, errors);
        validatePages(pdf, errors);

        return new PdfValidation(pdf, errors);
    }

    private void validateSize(PdfDocument pdf, List<ValidationError> errors) {
        if (pdf.content() != null && pdf.content().length > MAX_SIZE_BYTES) {
            errors.add(new ValidationError("PDF_SIZE", "El PDF supera los 5 MB"));
        }
    }

    private void validatePages(PdfDocument pdf, List<ValidationError> errors) {
        if (pdf.content() == null || pdf.content().length == 0) {
            return;
        }

        try (PDDocument document = Loader.loadPDF(pdf.content())) {
            if (document.getNumberOfPages() < 1) {
                errors.add(new ValidationError("PDF_NO_PAGES", "El PDF no contiene páginas"));
            }
        } catch (IOException e) {
            errors.add(new ValidationError("PDF_INVALID", "El archivo PDF es inválido"));
        }
    }
}