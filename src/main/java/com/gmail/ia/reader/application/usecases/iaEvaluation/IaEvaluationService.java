package com.gmail.ia.reader.application.usecases.iaEvaluation;

public interface IaEvaluationService {
    boolean startUpload(
            Long iaEvaluationId);

    boolean finishUpload(
            Long iaEvaluationId,
            String driveFileId);

}
