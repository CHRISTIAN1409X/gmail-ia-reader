package com.gmail.ia.reader.application.app.gmail.validation;

import com.gmail.ia.reader.domain.dtos.gmail.EmailPart;
import com.gmail.ia.reader.domain.dtos.gmail.ValidationError;
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

    public List<ValidationError> validate(
            EmailPart pdfPart) {

        List<ValidationError> errors =
                new ArrayList<>();

        validateNotEmpty(pdfPart, errors);
        validateSize(pdfPart, errors);
        validatePages(pdfPart, errors);

        return errors;
    }

    private void validateNotEmpty(
            EmailPart pdfPart,
            List<ValidationError> errors) {

        if (pdfPart.attachment() == null ||
                pdfPart.attachment().length == 0) {

            errors.add(
                    new ValidationError(
                            "PDF_EMPTY",
                            "El PDF está vacío"
                    )
            );
        }
    }

    private void validateSize(
            EmailPart pdfPart,
            List<ValidationError> errors) {

        if (pdfPart.attachment() != null &&
                pdfPart.attachment().length > MAX_SIZE_BYTES) {

            errors.add(
                    new ValidationError(
                            "PDF_SIZE",
                            "El PDF supera los 5 MB"
                    )
            );
        }
    }

    private void validatePages(
            EmailPart pdfPart,
            List<ValidationError> errors) {

        if (pdfPart.attachment() == null) {
            return;
        }

        try (PDDocument document =
                     Loader.loadPDF(
                             pdfPart.attachment())) {

            if (document.getNumberOfPages() < 1) {

                errors.add(
                        new ValidationError(
                                "PDF_NO_PAGES",
                                "El PDF no contiene páginas"
                        )
                );
            }

        } catch (IOException e) {
            errors.add(
                    new ValidationError(
                            "PDF_INVALID",
                            "El archivo PDF es inválido"
                    )
            );
        }
    }
}
