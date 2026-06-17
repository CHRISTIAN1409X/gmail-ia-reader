package com.gmail.ia.reader.application.usecases.iaEvaluation;

import com.gmail.ia.reader.domain.dtos.drive.UploadDriveResponse;

import java.util.UUID;

public interface IaEvaluationService {

    void aprovedPlanner(UUID idIaEvaluation);

    boolean startUpload(
            Long iaEvaluationId);

    boolean finishUpload(
            Long iaEvaluationId,
            UploadDriveResponse uploadDriveResponse);

}
