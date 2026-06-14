package com.gmail.ia.reader.application.app.gmail.validation;

import com.gmail.ia.reader.domain.dtos.gmail.ValidationError;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfValidation;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
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

    private void validateSize(
            PdfDocument pdf,
            List<ValidationError> errors) {

        try {

            long size =
                    Files.size(
                            pdf.tempFile()
                    );

            if (size > MAX_SIZE_BYTES) {

                errors.add(
                        new ValidationError(
                                pdf.fileName(),
                                "PDF_SIZE",
                                "El PDF supera los 5 MB"
                        )
                );
            }

        } catch (IOException e) {

            errors.add(
                    new ValidationError(
                            pdf.fileName(),
                            "PDF_SIZE_ERROR",
                            "No fue posible determinar el tamaño del PDF"
                    )
            );
        }
    }

    private void validatePages(
            PdfDocument pdf,
            List<ValidationError> errors) {

        try (PDDocument document =
                     Loader.loadPDF(
                             pdf.tempFile().toFile()
                     )) {

            if (document.getNumberOfPages() < 1) {
                errors.add(
                        new ValidationError(
                                pdf.fileName(),
                                "PDF_NO_PAGES",
                                "El PDF no contiene páginas"
                        )
                );
            }

        } catch (IOException e) {

            errors.add(
                    new ValidationError(
                            pdf.fileName(),
                            "PDF_INVALID",
                            "El archivo PDF es inválido o corrupto"
                    )
            );
        }
    }
}