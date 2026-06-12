package com.gmail.ia.reader.application.app.gmail;

import com.gmail.ia.reader.domain.dtos.gmail.EmailAttachmentRef;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class GmailMessageParser {

    public ParsedEmail parse(Message message) {

        List<EmailAttachmentRef> attachments = new ArrayList<>();

        collectParts(
                message.getId(),
                message.getPayload(),
                attachments
        );

        return new ParsedEmail(
                message.getId(),
                getHeader(message, "From"),
                getHeader(message, "To"),
                getHeader(message, "Subject"),
                attachments
        );
    }

    private void collectParts(
            String messageId,
            MessagePart part,
            List<EmailAttachmentRef> attachments
    ) {
        if (part == null) return;

        if ("application/pdf".equalsIgnoreCase(part.getMimeType())) {

            String attachmentId =
                    part.getBody() != null
                            ? part.getBody().getAttachmentId()
                            : null;

            if (attachmentId != null) {

                attachments.add(new EmailAttachmentRef(
                        part.getMimeType(),
                        part.getFilename(),
                        attachmentId,
                        part.getBody().getSize() != null
                                ? part.getBody().getSize().longValue()
                                : 0L
                ));
            }
        }

        if (part.getParts() != null) {
            for (MessagePart child : part.getParts()) {
                collectParts(messageId, child, attachments);
            }
        }
    }

    private String getHeader(Message message, String headerName) {

        if (message.getPayload() == null ||
                message.getPayload().getHeaders() == null) {
            return "";
        }

        return message.getPayload()
                .getHeaders()
                .stream()
                .filter(h -> h.getName().equalsIgnoreCase(headerName))
                .map(MessagePartHeader::getValue)
                .findFirst()
                .orElse("");
    }
}
