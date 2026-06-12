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
import java.util.*;


@Service
@RequiredArgsConstructor

public class EmailValidationService {

    private final PdfValidationService pdfValidationService;
    private final Gmail gmail;

    public List<PdfValidation>  validate(ParsedEmail email) {


        List<EmailAttachmentRef> pdfs = email.attachments().stream()
                .filter(a -> "application/pdf".equalsIgnoreCase(a.mimeType()))
                .toList();

        if (pdfs.isEmpty()) {
            return List.of(new PdfValidation(null, List.of(new ValidationError("NO_PDF", "No se encontró ningún PDF"))));
        }


        return pdfs.stream()
                .map(pdf -> {
                    try {

                        PdfDocument doc = loadAttachment(email.messageId(), pdf.fileName(), pdf.attachmentId());
                        return pdfValidationService.validate(doc);
                    } catch (RuntimeException e) {
                        return new PdfValidation(
                                null,
                                List.of(new ValidationError("DOWNLOAD_ERROR", e.getMessage()))
                        );
                    }
                })
                .toList();


    }

    public PdfDocument loadAttachment(String messageId, String fileName, String attachmentId) {
        try {
            MessagePartBody attachment = gmail.users()
                    .messages()
                    .attachments()
                    .get("me", messageId, attachmentId)
                    .execute();

            String base64Data = attachment.getData();


            if (base64Data == null || base64Data.isEmpty()) {
                throw new RuntimeException("El contenido del adjunto " + fileName + " está vacío o no se pudo recuperar.");
            }

            byte[] data = Base64.getUrlDecoder().decode(base64Data);

            return new PdfDocument(fileName, data);
        } catch (IOException e) {
            throw new RuntimeException("Error al descargar el adjunto de Gmail: " + fileName, e);
        }
    }

}
