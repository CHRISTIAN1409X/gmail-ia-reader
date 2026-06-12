package com.gmail.ia.reader.infraestructure.adapters.implementation.processedEmail;

import com.gmail.ia.reader.global.domain.ports.DaoCrudPort;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.processedEmail.ProcessedEmailRepository;
import com.gmail.ia.reader.infraestructure.models.ProcessedEmail;
import com.gmail.ia.reader.infraestructure.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ProcessedEmailAdapter implements DaoCrudPort<ProcessedEmail>,ProcessedEmailRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Optional<ProcessedEmail> findPemailByMessageId(String id) {
        try {
            ProcessedEmail processedEmail = entityManager.createQuery(
                            "SELECT p FROM ProcessedEmail p WHERE p.messageId = :messageId", ProcessedEmail.class)
                    .setParameter("messageId", id)
                    .getSingleResult();
            return Optional.of(processedEmail);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ProcessedEmail> selectAll() {
        return List.of();
    }

    @Override
    public Optional<ProcessedEmail> get(Long id) {
        return null;
    }

    @Override
    @Transactional
    public Optional<ProcessedEmail> create(ProcessedEmail object) {
        entityManager.persist(object);
        return Optional.of(object);
    }

    @Override
    @Transactional
    public Optional<ProcessedEmail> update(ProcessedEmail object) {
        entityManager.merge(object);
        return Optional.of(object);
    }

    @Override
    public Optional<ProcessedEmail> delete(Long id) {
        return Optional.empty();
    }
}
