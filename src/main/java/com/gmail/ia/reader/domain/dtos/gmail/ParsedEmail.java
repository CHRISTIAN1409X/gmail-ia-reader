package com.gmail.ia.reader.domain.dtos.gmail;

import java.util.List;

public record ParsedEmail(
        String messageId,
        String from,
        String to,
        String subject,
        List<EmailPart> parts
) {
}
