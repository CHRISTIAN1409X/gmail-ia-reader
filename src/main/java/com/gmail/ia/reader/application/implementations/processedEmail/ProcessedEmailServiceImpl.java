package com.gmail.ia.reader.application.implementations.processedEmail;

import com.gmail.ia.reader.application.usecases.processedEmail.ProcessedEmailService;
import com.gmail.ia.reader.infraestructure.models.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProcessedEmailServiceImpl implements ProcessedEmailService {

    private final ProcessedEmailStatusServiceImpl processedEmailStatusService;

    @Override
    @Transactional
    public void approvedPlanner(String messageId) {
        processedEmailStatusService.markProcessed(messageId, Status.APPROVED);

    }
}
