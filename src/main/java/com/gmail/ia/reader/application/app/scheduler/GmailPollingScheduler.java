package com.gmail.ia.reader.application.app.scheduler;

import com.gmail.ia.reader.application.app.gmail.PlannerProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GmailPollingScheduler {

    private final PlannerProcessingService plannerProcessingService;

    @Scheduled(fixedDelay = 30000)
    public void pollInbox() {
        plannerProcessingService.processPendingEmails();
    }
}
