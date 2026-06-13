package com.gmail.ia.reader.infraestructure.adapters.interfaces.processedEmail;

import com.gmail.ia.reader.infraestructure.models.ProcessedEmail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessedEmailRepository {
    Optional<ProcessedEmail> findPemailByMessageId(String id);

    boolean insertProcessingAtomic(String messageId);
}
