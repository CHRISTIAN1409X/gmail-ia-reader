package com.gmail.ia.reader.application.app.rabbit;

import com.gmail.ia.reader.application.app.drive.DriveStorageService;
import com.gmail.ia.reader.application.usecases.iaEvaluation.IaEvaluationService;
import com.gmail.ia.reader.domain.dtos.drive.ConsumeDriveRecord;
import com.gmail.ia.reader.domain.dtos.drive.UploadDriveResponse;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;
import com.gmail.ia.reader.global.domain.ports.DaoCrudPort;
import com.gmail.ia.reader.infraestructure.config.rabbit.RabbitConfig;
import com.gmail.ia.reader.infraestructure.models.IaEvaluation;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Component
public class RabbitWorkerDrive {

    private final DriveStorageService driveStorageService;
    private final DaoCrudPort<IaEvaluation> iaEvaluationDaoCrudPort;
    private final IaEvaluationService iaEvaluationService;
    private static final Logger log = LoggerFactory.getLogger(RabbitWorkerDrive.class);

    @RabbitListener(
            queues = RabbitConfig.DRIVE_QUEUE,
            containerFactory =
                    "driveRabbitListenerContainerFactory"
    )
    public void uploadListenerDrive(
            ConsumeDriveRecord event) {

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

        Path tempPath = null;
        boolean uploaded = false;

        try {

            IaEvaluation evaluation =
                    iaEvaluationDaoCrudPort.get(
                            event.idIaEvaluation()
                    ).orElseThrow();

            tempPath =
                    Paths.get(
                            evaluation.getLocalTempPath()
                    );

            PdfDocument pdf =
                    new PdfDocument(
                            evaluation.getPdfName(),
                            tempPath
                    );

            UploadDriveResponse uploadDriveResponse =
                    driveStorageService.uploadPdf(
                            evaluation.getDriveFolderEnum(),
                            evaluation.getPathPdf(),
                            pdf
                    );

            uploaded =
                    iaEvaluationService.finishUpload(
                            event.idIaEvaluation(),
                            uploadDriveResponse
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
            if (uploaded && tempPath != null) {
                PdfDocument.deleteTempFile(tempPath);
            }
        }
    }


}
