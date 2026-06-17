package com.gmail.ia.reader.infraestructure.adapters.interfaces.iaevaluation;


import com.gmail.ia.reader.domain.dtos.drive.UploadDriveResponse;
import com.gmail.ia.reader.infraestructure.models.IaEvaluation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IaEvaluationRepository {


    Optional<IaEvaluation> findByUUID(UUID uuid);

    boolean markUploading(Long iaEvaluationId);

    boolean markUploaded(
            Long iaEvaluationId,
            UploadDriveResponse uploadDriveResponse);

    boolean finishUpload(
            Long iaEvaluationId,
            String driveFileId);

    List<IaEvaluation> selectAll();
}
