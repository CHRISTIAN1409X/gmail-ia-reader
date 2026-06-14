package com.gmail.ia.reader.application.app.gmail.validation;

import com.gmail.ia.reader.domain.dtos.gmail.EmailAttachmentRef;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.gmail.ia.reader.domain.dtos.gmail.ValidationError;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfValidation;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.MessagePartBody;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


@Service
@RequiredArgsConstructor

public class EmailValidationService {
    private final PdfValidationService pdfValidationService;
    private final Gmail gmail;

    public List<PdfValidation> validate(ParsedEmail email) {
        List<EmailAttachmentRef> pdfs = email.attachments().stream()
                .filter(a -> "application/pdf".equalsIgnoreCase(a.mimeType()))
                .toList();

        if (pdfs.isEmpty()) {
            return List.of(new PdfValidation(null, List.of(new ValidationError("","NO_PDF", "No se encontró ningún PDF"))));
        }

        List<PdfValidation> results = new ArrayList<>();

        for (EmailAttachmentRef pdf : pdfs) {
            try {
                PdfDocument doc = loadAttachment(email.messageId(), pdf.fileName(), pdf.attachmentId());
                results.add(pdfValidationService.validate(doc));
            } catch (Exception e) {
                results.add(new PdfValidation(
                        null,
                        List.of(new ValidationError(pdf.fileName(),"DOWNLOAD_ERROR", e.getMessage()))
                ));
            }
        }
        return results;
    }

    public PdfDocument loadAttachment(String messageId, String fileName, String attachmentId) {
        try {
            String base64Data = gmail.users()
                    .messages()
                    .attachments()
                    .get("me", messageId, attachmentId)
                    .execute()
                    .getData();

            if (base64Data == null || base64Data.isEmpty()) {
                throw new RuntimeException("El contenido del adjunto " + fileName + " está vacío o no se pudo recuperar.");
            }


            byte[] data = Base64.getUrlDecoder().decode(base64Data);

            Path temp =
                    Files.createTempFile(
                            "pdf-",
                            ".pdf"
                    );

            Files.write(
                    temp,
                    data
            );

            data = null;
            base64Data = null;

            return new PdfDocument(fileName, temp);
        } catch (IOException e) {
            throw new RuntimeException("Error al descargar el adjunto de Gmail: " + fileName, e);
        }
    }
}
