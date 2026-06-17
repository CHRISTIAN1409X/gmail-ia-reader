package com.gmail.ia.reader.application.implementations.iaEvaluation;

import com.gmail.ia.reader.application.app.drive.DriveStorageService;
import com.gmail.ia.reader.application.implementations.processedEmail.ProcessedEmailStatusServiceImpl;
import com.gmail.ia.reader.application.usecases.iaEvaluation.IaEvaluationService;
import com.gmail.ia.reader.domain.dtos.drive.UploadDriveResponse;
import com.gmail.ia.reader.domain.enums.DriveFolderEnum;
import com.gmail.ia.reader.global.domain.ports.DaoCrudPort;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.iaevaluation.IaEvaluationRepository;
import com.gmail.ia.reader.infraestructure.advicers.exceptions.BadRequestException;
import com.gmail.ia.reader.infraestructure.models.IaEvaluation;
import com.gmail.ia.reader.infraestructure.models.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class IaEvaluationServiceImpl implements IaEvaluationService {

    private final IaEvaluationRepository iaEvaluationRepository;
    private final ProcessedEmailStatusServiceImpl processedEmailStatusService;
    private final DriveStorageService driveStorageService;

    @Override
    public void aprovedPlanner(UUID uuidIa) {
        IaEvaluation iaEvaluation = iaEvaluationRepository.findByUUID(uuidIa).orElseThrow(()-> new BadRequestException("No existe el ID"));
        String urlDocument = driveStorageService.moveToApproved(iaEvaluation.getDriveFileId(),iaEvaluation.getPathPdf());
        if (urlDocument.isEmpty()){
            throw new BadRequestException("Error al crear la url del pdf");
        }
        String messageId = iaEvaluation.getEmail().getGmailId();
        processedEmailStatusService.markProcessed(messageId, Status.APPROVED);
        iaEvaluation.setDriveFolderEnum(DriveFolderEnum.APROBADOS);
        iaEvaluation.setUrlPdfDrive(urlDocument);
    }

    @Transactional
    public boolean startUpload(
            Long iaEvaluationId) {

        return iaEvaluationRepository
                .markUploading(
                        iaEvaluationId
                );
    }

    @Transactional
    public boolean finishUpload(
            Long iaEvaluationId,
            UploadDriveResponse uploadDriveResponse) {

        return iaEvaluationRepository
                .markUploaded(
                        iaEvaluationId,
                        uploadDriveResponse
                );
    }
}
