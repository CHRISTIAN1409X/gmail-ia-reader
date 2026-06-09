package com.gmail.ia.reader.application.app.gmail.validation;

import com.gmail.ia.reader.domain.dtos.gmail.EmailPart;
import com.gmail.ia.reader.domain.dtos.gmail.EmailValidationResult;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.gmail.ia.reader.domain.dtos.gmail.ValidationError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailValidationService {

    private final PdfValidationService pdfValidationService;

    public EmailValidationResult validate(
            ParsedEmail email) {

        List<ValidationError> errors =
                new ArrayList<>();

        List<EmailPart> pdfs =
                email.parts()
                        .stream()
                        .filter(part ->
                                "application/pdf"
                                        .equalsIgnoreCase(
                                                part.mimeType()))
                        .toList();

        if (pdfs.isEmpty()) {

            errors.add(
                    new ValidationError(
                            "NO_PDF",
                            "No se encontró ningún PDF"
                    )
            );

            return new EmailValidationResult(
                    email.from(),
                    false,
                    errors
            );
        }

        pdfs.forEach(pdf ->
                errors.addAll(
                        pdfValidationService.validate(
                                pdf
                        )
                ));

        return new EmailValidationResult(
                email.from(),
                errors.isEmpty(),
                errors
        );
    }
}
