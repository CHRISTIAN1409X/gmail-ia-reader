package com.gmail.ia.reader.infraestructure.adapters.implementation.ai.gemini;

public class AiApiRetryableException extends RuntimeException {
    public AiApiRetryableException(String message) {
        super(message);
    }

    public AiApiRetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
