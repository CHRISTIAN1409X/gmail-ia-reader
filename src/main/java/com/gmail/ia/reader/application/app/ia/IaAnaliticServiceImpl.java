package com.gmail.ia.reader.application.app.ia;

import com.gmail.ia.reader.application.app.usecases.IaAnaliticService;
import com.gmail.ia.reader.domain.dtos.cloude.IaRespondeRecord;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;
import com.gmail.ia.reader.infraestructure.adapters.implementation.ai.gemini.AiApiException;
import com.gmail.ia.reader.infraestructure.adapters.implementation.ai.gemini.AiApiRetryableException;
import com.gmail.ia.reader.infraestructure.adapters.implementation.ai.gemini.GeminiAiAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IaAnaliticServiceImpl implements IaAnaliticService {

    private static final Logger log = LoggerFactory.getLogger(IaAnaliticServiceImpl.class);
    private static final int MAX_RETRIES = 3;
    private static final long BASE_DELAY_MS = 1000;

    private final GeminiAiAdapter geminiAiAdapter;
    private final FallbackAnaliticService fallbackAnaliticService;

    public IaAnaliticServiceImpl(GeminiAiAdapter geminiAiAdapter, FallbackAnaliticService fallbackAnaliticService) {
        this.geminiAiAdapter = geminiAiAdapter;
        this.fallbackAnaliticService = fallbackAnaliticService;
    }

    public IaRespondeRecord analize(ParsedEmail email, PdfDocument planeador, PdfDocument microcurriculum) {
        log.info("Analyzing planeador {} via Gemini AI", planeador.fileName());

        byte[] planeadorBytes = loadPdfBytes(planeador);
        byte[] microcurriculumBytes = microcurriculum != null ? loadPdfBytes(microcurriculum) : null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return geminiAiAdapter.analyze(planeadorBytes, microcurriculumBytes, email);
            } catch (AiApiRetryableException e) {
                if (attempt == MAX_RETRIES) {
                    log.error("All {} retry attempts failed for PDF {}", attempt, planeador.fileName(), e);
                } else {
                    long delay = BASE_DELAY_MS * (long) Math.pow(2, attempt - 1);
                    log.warn("Gemini attempt {}/{} failed for PDF {}, retrying in {}ms: {}",
                            attempt, MAX_RETRIES, planeador.fileName(), delay, e.getMessage());
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } catch (AiApiException e) {
                log.error("Non-retryable Gemini API error for PDF {}: {}", planeador.fileName(), e.getMessage());
                break;
            }
        }

        log.warn("Falling back to local analysis for PDF {}", planeador.fileName());
        return fallbackAnaliticService.createFallbackResponse();
    }

    private byte[] loadPdfBytes(PdfDocument pdf) {
        try {
            return java.nio.file.Files.readAllBytes(pdf.tempFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read PDF file: " + pdf.fileName(), e);
        }
    }
}
