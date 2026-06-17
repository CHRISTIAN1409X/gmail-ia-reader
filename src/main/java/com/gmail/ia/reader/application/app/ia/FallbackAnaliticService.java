package com.gmail.ia.reader.application.app.ia;

import com.gmail.ia.reader.domain.dtos.cloude.CriteriaResult;
import com.gmail.ia.reader.domain.dtos.cloude.IaRespondeRecord;
import com.gmail.ia.reader.domain.dtos.cloude.PathPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FallbackAnaliticService {

    private static final Logger log = LoggerFactory.getLogger(FallbackAnaliticService.class);

    public IaRespondeRecord createFallbackResponse() {
        log.error("AI analysis has failed. Please check manually.");
        return new IaRespondeRecord(
                "FALLBACK",
                "ai-unavailable",
                0,
                LocalDateTime.now(),
                List.of(new CriteriaResult(
                        "AI Analysis",
                        false,
                        "AI analysis has failed. Please check manually",
                        (byte) 0
                )),
                List.of(new PathPart("PENDING_REVIEW"))
        );
    }
}
