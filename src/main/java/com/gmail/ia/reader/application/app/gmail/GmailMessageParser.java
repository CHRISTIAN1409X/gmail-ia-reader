package com.gmail.ia.reader.application.app.gmail;

import com.gmail.ia.reader.domain.dtos.gmail.EmailPart;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class GmailMessageParser {

    public ParsedEmail parse(
            Gmail gmail,
            Message message) {

        List<EmailPart> parts = new ArrayList<>();

        collectParts(
                gmail,
                message.getId(),
                message.getPayload(),
                parts
        );

        return new ParsedEmail(
                message.getId(),
                getHeader(message, "From"),
                getHeader(message, "To"),
                getHeader(message, "Subject"),
                parts
        );
    }

    private void collectParts(
            Gmail gmail,
            String messageId,
            MessagePart part,
            List<EmailPart> parts) {

        if (part == null) {
            return;
        }

        EmailPart emailPart =
                createEmailPart(
                        gmail,
                        messageId,
                        part
                );

        if (emailPart != null) {
            parts.add(emailPart);
        }

        if (part.getParts() != null) {

            for (MessagePart child : part.getParts()) {

                collectParts(
                        gmail,
                        messageId,
                        child,
                        parts
                );
            }
        }
    }

    private EmailPart createEmailPart(
            Gmail gmail,
            String messageId,
            MessagePart part) {

        String mimeType = part.getMimeType();
        String fileName = part.getFilename();

        String content = null;
        byte[] attachment = null;

        if (part.getBody() != null &&
                part.getBody().getData() != null) {

            content = decode(
                    part.getBody().getData()
            );
        }

        if (fileName != null &&
                !fileName.isBlank()) {

            String attachmentId =
                    part.getBody()
                            .getAttachmentId();

            if (attachmentId != null) {

                attachment = downloadAttachment(
                        gmail,
                        messageId,
                        attachmentId
                );
            }
        }

        return new EmailPart(
                mimeType,
                fileName,
                content,
                attachment
        );
    }

    private byte[] downloadAttachment(
            Gmail gmail,
            String messageId,
            String attachmentId) {

        try {

            MessagePartBody body =
                    gmail.users()
                            .messages()
                            .attachments()
                            .get(
                                    "me",
                                    messageId,
                                    attachmentId
                            )
                            .execute();

            return Base64.getUrlDecoder()
                    .decode(body.getData());

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error downloading attachment",
                    e
            );
        }
    }

    private String decode(String data) {

        byte[] bytes =
                Base64.getUrlDecoder()
                        .decode(data);

        return new String(
                bytes,
                StandardCharsets.UTF_8
        );
    }

    private String getHeader(
            Message message,
            String headerName) {

        if (message.getPayload() == null ||
                message.getPayload().getHeaders() == null) {

            return "";
        }

        return message.getPayload()
                .getHeaders()
                .stream()
                .filter(header ->
                        header.getName()
                                .equalsIgnoreCase(
                                        headerName
                                ))
                .map(MessagePartHeader::getValue)
                .findFirst()
                .orElse("");
    }
}
