package com.gmail.ia.reader.application.app.scheduler;

import com.gmail.ia.reader.application.app.gmail.GmailExtractorService;
import com.gmail.ia.reader.application.app.rabbit.RabbitWorker;
import com.gmail.ia.reader.domain.dtos.rabbit.GmailEvent;
import com.gmail.ia.reader.infraestructure.config.rabbit.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Component
@RequiredArgsConstructor
public class GmailPollingScheduler {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitWorker plannerProcessingService;
    private final GmailExtractorService gmailExtractorService;

    @Scheduled(fixedDelay = 30000)
    public void pollInbox() {
        gmailExtractorService.findUnreadMessageIds()
                .forEach(id->
                                rabbitTemplate.convertAndSend(
                                        RabbitConfig.EXCHANGE,
                                        RabbitConfig.ROUTING_KEY,
                                        new GmailEvent(id)
                                )
                        );
    }
}
