package com.gmail.ia.reader.domain.dtos.gmail;

public record EmailAttachmentRef
(
        String mimeType,
        String fileName,
        String attachmentId,
        Long sizeBytes
) {
}
