package com.gmail.ia.reader.application.usecases.iaEvaluation;

import com.gmail.ia.reader.domain.dtos.drive.UploadDriveResponse;
import com.gmail.ia.reader.domain.dtos.iaevaluation.IaEvaluationDetailResponse;
import com.gmail.ia.reader.domain.dtos.iaevaluation.IaEvaluationListResponse;

import java.util.List;
import java.util.UUID;

public interface IaEvaluationService {

    void aprovedPlanner(UUID idIaEvaluation);

    boolean startUpload(
            Long iaEvaluationId);

    boolean finishUpload(
            Long iaEvaluationId,
            UploadDriveResponse uploadDriveResponse);

    IaEvaluationDetailResponse getEvaluation(UUID uuidIa);

    List<IaEvaluationListResponse> getAllEvaluations();

    String sendObservations(UUID uuidIa);
}
