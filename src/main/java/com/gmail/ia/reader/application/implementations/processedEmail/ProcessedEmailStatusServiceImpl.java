package com.gmail.ia.reader.application.implementations.processedEmail;

import com.gmail.ia.reader.global.domain.ports.DaoCrudPort;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.processedEmail.ProcessedEmailRepository;
import com.gmail.ia.reader.infraestructure.models.ProcessedEmail;
import com.gmail.ia.reader.infraestructure.models.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class ProcessedEmailStatusServiceImpl {
    private final ProcessedEmailRepository processedEmailRepository;
    private final DaoCrudPort<ProcessedEmail> daoCrudPort;


    @Transactional
    public boolean markProcessingAtomic(String messageId) {
        return processedEmailRepository.insertProcessingAtomic(messageId);
    }

    @Transactional
    public void markProcessing(String messageId) {
        ProcessedEmail email = new ProcessedEmail();
        email.setMessageId(messageId);
        email.setStatus(Status.PROCESSING);
        email.setCreatedAt(LocalDateTime.now());
        email.setUpdatedAt(LocalDateTime.now());
        email.setRetryCount(1);
        daoCrudPort.create(email);
    }

    @Transactional
    public void markProcessed(String messageId, Status status) {
        ProcessedEmail processedEmail = processedEmailRepository.findPemailByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("Email no encontrado"));
        processedEmail.setStatus(status);
    }
}
