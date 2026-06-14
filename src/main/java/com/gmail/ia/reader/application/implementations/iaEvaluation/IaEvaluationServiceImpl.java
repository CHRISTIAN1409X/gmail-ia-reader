package com.gmail.ia.reader.application.implementations.iaEvaluation;

import com.gmail.ia.reader.application.usecases.iaEvaluation.IaEvaluationService;
import com.gmail.ia.reader.global.domain.ports.DaoCrudPort;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.iaevaluation.IaEvaluationRepository;
import com.gmail.ia.reader.infraestructure.models.IaEvaluation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class IaEvaluationServiceImpl implements IaEvaluationService {

    private final IaEvaluationRepository iaEvaluationRepository;

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
            String driveFileId) {

        return iaEvaluationRepository
                .markUploaded(
                        iaEvaluationId,
                        driveFileId
                );
    }
}
