package com.gmail.ia.reader.application.app.rabbit;

import com.gmail.ia.reader.application.app.drive.DriveStorageService;
import com.gmail.ia.reader.application.usecases.iaEvaluation.IaEvaluationService;
import com.gmail.ia.reader.domain.dtos.drive.DriveUploadRecord;
import com.gmail.ia.reader.infraestructure.config.rabbit.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@Component
public class RabbitWorkerDrive {

    private final DriveStorageService driveStorageService;
    private final IaEvaluationService iaEvaluationService;
    private static final Logger log = LoggerFactory.getLogger(RabbitWorkerDrive.class);

    @RabbitListener(
            queues = RabbitConfig.DRIVE_QUEUE,
            containerFactory =
                    "driveRabbitListenerContainerFactory"
    )
    public void uploadListenerDrive(
            DriveUploadRecord event) {

        boolean acquired =
                iaEvaluationService.startUpload(
                        event.idIaEvaluation()
                );

        if (!acquired) {

            log.warn(
                    "La evaluación {} ya no está en estado PENDING",
                    event.idIaEvaluation()
            );

            return;
        }

        try {

            String driveFileId =
                    driveStorageService.uploadPdf(
                            event.driveFolderEnum(),
                            event.path(),
                            event.pdfDocument()
                    );

            boolean uploaded =
                    iaEvaluationService.finishUpload(
                            event.idIaEvaluation(),
                            driveFileId
                    );

            if (!uploaded) {

                log.error(
                        "No fue posible marcar UPLOADED para {}",
                        event.idIaEvaluation()
                );
            }

        } catch (Exception e) {

            throw new AmqpRejectAndDontRequeueException(
                    e.getMessage(),
                    e
            );
        } finally {
            if (event.pdfDocument() != null) {
                event.pdfDocument()
                        .deleteTempFile();
            }
        }

    }
}
