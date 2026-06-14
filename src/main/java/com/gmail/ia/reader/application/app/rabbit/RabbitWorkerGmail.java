package com.gmail.ia.reader.application.app.rabbit;

import com.gmail.ia.reader.application.app.ia.IaAnaliticServiceImpl;
import com.gmail.ia.reader.application.app.gmail.GmailExtractorService;
import com.gmail.ia.reader.application.app.gmail.GmailMessageParser;
import com.gmail.ia.reader.application.app.gmail.sender.GmailSender;
import com.gmail.ia.reader.application.app.gmail.validation.EmailValidationService;
import com.gmail.ia.reader.application.implementations.processedEmail.ProcessedEmailStatusServiceImpl;
import com.gmail.ia.reader.application.usecases.email.EmailService;
import com.gmail.ia.reader.domain.dtos.drive.DriveUploadRecord;
import com.gmail.ia.reader.domain.dtos.iaevaluation.IaEvaluationCreated;
import com.gmail.ia.reader.domain.dtos.pdf.PdfProcessingResult;
import com.gmail.ia.reader.domain.dtos.pdf.PdfProcessingResultWithId;
import com.gmail.ia.reader.domain.enums.DriveFolderEnum;
import com.gmail.ia.reader.domain.dtos.cloude.IaRespondeRecord;
import com.gmail.ia.reader.domain.dtos.gmail.EmailValidationResult;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfValidation;
import com.gmail.ia.reader.domain.dtos.rabbit.GmailEvent;
import com.gmail.ia.reader.infraestructure.advicers.exceptions.BusinessValidationException;
import com.gmail.ia.reader.infraestructure.config.rabbit.RabbitConfig;
import com.gmail.ia.reader.infraestructure.models.FilePath;
import com.gmail.ia.reader.infraestructure.models.enums.Status;
import com.google.api.services.gmail.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gmail.ia.reader.domain.logic.EmailUtils.recreatePath;

@RequiredArgsConstructor
@Component
public class RabbitWorkerGmail {
    private static final Logger log = LoggerFactory.getLogger(RabbitWorkerGmail.class);
    private final IaAnaliticServiceImpl claudeAnaliticService;
    private final EmailValidationService emailValidationService;
    private final GmailSender gmailSender;
    private final GmailMessageParser parser;
    private final GmailExtractorService gmailExtractorService;
    private final ProcessedEmailStatusServiceImpl processedEmailStatusService;
    private final EmailService emailService;
    private final RabbitWorkerDrive rabbitWorkerDrive;


    private final String[] listEmailsError =
            new String[]{"corjuela1030@cue.edu.co"};

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void processEmail(GmailEvent event) {
        long startTime = System.currentTimeMillis();
        String messageId = event.messageId();

        boolean statusChanged = processedEmailStatusService.markProcessingAtomic(messageId);
        if (!statusChanged) {
            log.info("El correo {} ya está registrado o procesándose en el sistema. Descartando duplicado.", messageId);
            return;
        }

        List<PdfValidation> pdfSanitized = null;


        try {
            Message message = gmailExtractorService.loadMessage(messageId);
            ParsedEmail emailParsed = parser.parse(message);

            pdfSanitized = emailValidationService.validate(emailParsed);

            boolean hasErrors = false;
            String fileName = "";
            String path = "";
            List<PdfProcessingResult> pdfProcessingResults = new ArrayList<>();

            for (PdfValidation pdfSanitizier : pdfSanitized) {
                if (pdfSanitizier.getPdfDocument() == null) {
                    sendError(new EmailValidationResult(emailParsed.from(), false, pdfSanitizier.getValidationError()));
                    hasErrors = true;
                    continue;
                }

                fileName = pdfSanitizier.getPdfDocument().fileName();

                try {
                    IaRespondeRecord iaRespondeRecord = claudeAnaliticService.analize(emailParsed, pdfSanitizier.getPdfDocument());
                    path = recreatePath(iaRespondeRecord.listPathPart());
                    UUID correlationId = UUID.randomUUID();
                    pdfProcessingResults.add(new PdfProcessingResult(correlationId,pdfSanitizier.getPdfDocument(),iaRespondeRecord,path,fileName));
                } catch (Exception e) {
                    log.error("Error procesando o subiendo el PDF {}", fileName, e);
                    throw new RuntimeException("Error al procesar PDF " + fileName + " en el flujo", e);
                } finally {
                    log.info("Memoria del PDF {} liberada explícitamente.", fileName);
                    if (pdfSanitizier.getPdfDocument() != null) {
                        pdfSanitizier.setPdfDocument(
                                pdfSanitizier.getPdfDocument()
                                        .clearContent()
                        );
                    }
                }
            }

            if (hasErrors) {
                processedEmailStatusService.markProcessed(messageId, Status.REJECTED);
            } else {
                List<IaEvaluationCreated> idIaList = emailService.updloadFile(emailParsed,pdfProcessingResults);
                Map<UUID, Long> evaluationMap =
                        idIaList.stream()
                                .collect(
                                        Collectors.toMap(
                                                IaEvaluationCreated::correlationId,
                                                IaEvaluationCreated::iaEvaluationId
                                        )
                                );
                for (PdfProcessingResult result : pdfProcessingResults) {
                    Long iaEvaluationId =
                            evaluationMap.get(
                                    result.uuid()
                            );

                    rabbitWorkerDrive.uploadListenerDrive(
                            new DriveUploadRecord(
                                    iaEvaluationId,
                                    DriveFolderEnum.TEMPORAL,
                                    result.fileName(),
                                    result.path(),
                                    result.pdfDocument()
                            )
                    );
                }
                processedEmailStatusService.markProcessed(messageId, Status.PROCESSED);
            }

        } catch (BusinessValidationException bex) {
            log.warn("Validación de negocio fallida para el correo {}: {}", messageId, bex.getMessage());
            throw new AmqpRejectAndDontRequeueException(bex.getMessage(), bex);
        } catch (Exception e) {
            log.error("Error crítico procesando mensaje {}", messageId, e);
            try {
                processedEmailStatusService.markProcessed(messageId, Status.FAILED);
            } catch (Exception ex) {
                log.error("No se pudo actualizar estado FAILED para {}", messageId, ex);
            }
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        } finally {
            if (pdfSanitized != null) {
                for (PdfValidation pv : pdfSanitized) {
                    if (pv != null && pv.getPdfDocument() != null) {
                        pv.setPdfDocument(pv.getPdfDocument().clearContent());
                    }
                }
                pdfSanitized.clear();
                long execution = System.currentTimeMillis() - startTime;
                log.info("EL TIEMPO DE EJECUCIÓN FUE "+execution);
            }
        }
    }



    private <T,W> List<W> extractData(List<T> list, Function<T,W> mapper){
        return list.stream()
                .map(mapper)
                .toList();
    }

    private List<PdfProcessingResultWithId> enrichedResults(List<Long>idIaList, List<PdfProcessingResult> pdfProcessingResults){
       return IntStream.range(0, pdfProcessingResults.size())
                .mapToObj(i -> new PdfProcessingResultWithId(
                        idIaList.get(i),
                        pdfProcessingResults.get(i)
                ))
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
