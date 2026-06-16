package com.gmail.ia.reader.infraestructure.adapters.interfaces.iaevaluation;


public interface IaEvaluationRepository {


    boolean markUploading(Long iaEvaluationId);

    boolean markUploaded(
            Long iaEvaluationId,
            String fileId);

    boolean finishUpload(
            Long iaEvaluationId,
            String driveFileId);

}
