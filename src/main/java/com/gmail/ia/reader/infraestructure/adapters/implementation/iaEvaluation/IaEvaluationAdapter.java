package com.gmail.ia.reader.infraestructure.adapters.implementation.iaEvaluation;

import com.gmail.ia.reader.domain.dtos.drive.UploadDriveResponse;
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
import java.util.UUID;

@Repository
public class IaEvaluationAdapter implements DaoCrudPort<IaEvaluation>, IaEvaluationRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<IaEvaluation> selectAll() {
        return entityManager.createQuery(
                "SELECT i FROM IaEvaluation i JOIN FETCH i.email ORDER BY i.createdAt DESC",
                IaEvaluation.class
        ).getResultList();
    }

    @Override
    public Optional<IaEvaluation> get(Long id) {
        return Optional.ofNullable(entityManager.find(
                IaEvaluation.class,
                id
        ));
    }


    @Override
    public Optional<IaEvaluation> findByUUID(UUID uuid) {
        try {
            IaEvaluation entity = entityManager.createQuery(
                            "SELECT i FROM IaEvaluation i WHERE i.uuid = :uuid", IaEvaluation.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
            return Optional.of(entity);
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        }
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
            UploadDriveResponse uploadDriveResponse) {

        int updatedRows =
                entityManager.createQuery("""
                    UPDATE IaEvaluation i
                       SET i.driveFileId = :fileId,
                           i.urlPdfDrive =:url,
                           i.driveStatus = :uploaded
                     WHERE i.id = :id
                       AND i.driveStatus = :uploading
                    """)
                        .setParameter("fileId", uploadDriveResponse.fileId())
                        .setParameter("uploaded", DriveStatus.UPLOADED)
                        .setParameter("url",uploadDriveResponse.url())
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
