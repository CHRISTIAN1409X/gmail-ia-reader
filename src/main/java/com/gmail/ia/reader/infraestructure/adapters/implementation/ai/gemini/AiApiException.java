package com.gmail.ia.reader.infraestructure.adapters.implementation.ai.gemini;

public class AiApiException extends RuntimeException {
    public AiApiException(String message) {
        super(message);
    }

    public AiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
