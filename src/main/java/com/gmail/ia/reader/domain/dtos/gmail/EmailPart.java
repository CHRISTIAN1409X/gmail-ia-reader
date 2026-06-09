package com.gmail.ia.reader.domain.dtos.gmail;

public record EmailPart(
        String mimeType,
        String fileName,
        String content,
        byte[] attachment
) {
}
