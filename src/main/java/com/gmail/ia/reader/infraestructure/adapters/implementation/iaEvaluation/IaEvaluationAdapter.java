package com.gmail.ia.reader.infraestructure.adapters.implementation.iaEvaluation;

import com.gmail.ia.reader.global.domain.ports.DaoCrudPort;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.iaevaluation.IaEvaluationRepository;
import com.gmail.ia.reader.infraestructure.models.IaEvaluation;
import com.gmail.ia.reader.infraestructure.models.enums.DriveStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class IaEvaluationAdapter implements DaoCrudPort<IaEvaluation>, IaEvaluationRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<IaEvaluation> selectAll() {
        return List.of();
    }

    @Override
    public Optional<IaEvaluation> get(Long id) {
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean markUploading(Long iaEvaluationId) {

        int updatedRows =
                entityManager.createQuery("""
                    UPDATE IaEvaluation i
                       SET i.driveStatus = :uploading
                     WHERE i.id = :id
                       AND i.driveStatus = :pending
                    """)
                        .setParameter("uploading", DriveStatus.UPLOADING)
                        .setParameter("pending", DriveStatus.PENDING)
                        .setParameter("id", iaEvaluationId)
                        .executeUpdate();

        return updatedRows == 1;
    }

    @Override
    @Transactional
    public boolean markUploaded(
            Long iaEvaluationId,
            String fileId) {

        int updatedRows =
                entityManager.createQuery("""
                    UPDATE IaEvaluation i
                       SET i.driveFileId = :fileId,
                           i.driveStatus = :uploaded
                     WHERE i.id = :id
                       AND i.driveStatus = :uploading
                    """)
                        .setParameter("fileId", fileId)
                        .setParameter("uploaded", DriveStatus.UPLOADED)
                        .setParameter("uploading", DriveStatus.UPLOADING)
                        .setParameter("id", iaEvaluationId)
                        .executeUpdate();

        return updatedRows == 1;
    }

    @Override
    public boolean finishUpload(Long iaEvaluationId, String driveFileId) {
        return false;
    }

    @Override
    public Optional<IaEvaluation> create(IaEvaluation object) {
        if (object==null){
            throw new RuntimeException("Error Creating Report Ia");
        }
        entityManager.persist(object);
        return Optional.of(object);
    }

    @Override
    public Optional<IaEvaluation> update(IaEvaluation object) {
        return Optional.empty();
    }

    @Override
    public Optional<IaEvaluation> delete(Long id) {
        return Optional.empty();
    }
}
