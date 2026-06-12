package com.gmail.ia.reader.application.app.rabbit;

import com.gmail.ia.reader.application.app.claude.ClaudeAnaliticService;
import com.gmail.ia.reader.application.app.drive.DriveStorageService;
import com.gmail.ia.reader.application.app.gmail.GmailExtractorService;
import com.gmail.ia.reader.application.app.gmail.GmailMessageParser;
import com.gmail.ia.reader.application.app.gmail.sender.GmailSender;
import com.gmail.ia.reader.application.app.gmail.validation.EmailValidationService;
import com.gmail.ia.reader.application.implementations.processedEmail.ProcessedEmailStatusService;
import com.gmail.ia.reader.domain.dtos.gmail.EmailAttachmentRef;
import com.gmail.ia.reader.domain.dtos.gmail.EmailValidationResult;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfValidation;
import com.gmail.ia.reader.domain.dtos.rabbit.GmailEvent;
import com.gmail.ia.reader.global.domain.ports.DaoCrudPort;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.processedEmail.ProcessedEmailRepository;
import com.gmail.ia.reader.infraestructure.advicers.exceptions.BusinessValidationException;
import com.gmail.ia.reader.infraestructure.config.rabbit.RabbitConfig;
import com.gmail.ia.reader.infraestructure.models.ProcessedEmail;
import com.gmail.ia.reader.infraestructure.models.enums.Status;
import com.google.api.services.gmail.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RequiredArgsConstructor
@Component
public class RabbitWorker {
    private static final Logger log = LoggerFactory.getLogger(RabbitWorker.class);
    private final ClaudeAnaliticService claudeAnaliticService;
    private final EmailValidationService emailValidationService;
    private final GmailSender gmailSender;
    private final GmailMessageParser parser;
    private final GmailExtractorService gmailExtractorService;
    private final DaoCrudPort<ProcessedEmail> processedEmailDaoCrudPort;
    private final ProcessedEmailRepository processedEmailRepository;
    private final ProcessedEmailStatusService processedEmailStatusService;
    private final DriveStorageService driveStorageService;


    private final String[] listEmailsError =
            new String[]{"corjuela1030@cue.edu.co"};



    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void processEmail(GmailEvent event) {
        String messageId = event.messageId();

        Optional<ProcessedEmail> existingEmail = processedEmailRepository.findPemailByMessageId(messageId);
        if (existingEmail.isPresent()) {
            return;
        }

        System.out.println("Correo " + messageId + " marcado como procesando");
        processedEmailStatusService.markProcessing(messageId);

        try {
            Message message = gmailExtractorService.loadMessage(messageId);
            ParsedEmail email = parser.parse(message);
            List<PdfValidation> pdfSanitized = emailValidationService.validate(email);

            pdfSanitized.forEach(pdfSanitizier-> {
                if (pdfSanitizier.pdfDocument()==null){
                    sendError(new EmailValidationResult(email.from(),false,pdfSanitizier.validationError()));
                    processedEmailStatusService.markProcessed(messageId, Status.REJECTED);
                   // throw new BusinessValidationException("Correo " + messageId + " marcado como rechazado");
                }else {
                    claudeAnaliticService.analize(email,pdfSanitizier.pdfDocument());
                    try {

                        driveStorageService.uploadPdf(
                                "/Ing-Soft-2026-1/Semestre-VII/gestion-proyectos/",
                                pdfSanitizier.pdfDocument()
                        );


                    } catch (Exception e) {

                        log.error(
                                "Error subiendo PDF {}",
                                pdfSanitizier.pdfDocument().fileName(),
                                e
                        );

                        throw new RuntimeException(
                                "Error al subir PDF "
                                        + pdfSanitizier.pdfDocument().fileName()
                                        + " a Drive",
                                e
                        );
                    }
                    processedEmailStatusService.markProcessed(messageId, Status.PROCESSED);
                }
            });

        }catch (BusinessValidationException bex){
            log.warn("LOG {}", bex.getMessage());
            throw new AmqpRejectAndDontRequeueException(
                    bex.getMessage(),
                    bex
            );
        }
        catch (Exception e) {
            log.error("Error procesando mensaje {}", messageId, e);

            try {
                processedEmailStatusService.markProcessed(
                        messageId,
                        Status.FAILED
                );
            } catch (Exception ex) {

                log.error(
                        "No se pudo actualizar estado FAILED para {}",
                        messageId,
                        ex
                );
            }

            throw new AmqpRejectAndDontRequeueException(
                    e.getMessage(),
                    e
            );
        }
    }

    private List<EmailAttachmentRef> extractPDFs(ParsedEmail email) {

        return email.attachments()
                .stream()
                .filter(a -> "application/pdf"
                        .equalsIgnoreCase(a.mimeType()))
                .toList();
    }

    private void sendError(EmailValidationResult result) {

        for (String recep : listEmailsError) {
            try {
                gmailSender.sendEmail(
                        recep,
                        "Correo rechazado",
                        result
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
