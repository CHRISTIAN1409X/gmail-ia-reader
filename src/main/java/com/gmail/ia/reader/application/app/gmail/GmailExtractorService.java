package com.gmail.ia.reader.application.app.gmail;

import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class GmailExtractorService {

    private final Gmail gmail;
    private final GmailMessageParser parser;

    public GmailExtractorService(
            Gmail gmail,
            GmailMessageParser parser) {

        this.gmail = gmail;
        this.parser = parser;
    }

    public List<ParsedEmail> extractEmails() {

        try {

            ListMessagesResponse response =
                    gmail.users()
                            .messages()
                            .list("me")
                            .setQ("subject:planeador is:unread")
                            .execute();

            if (response.getMessages() == null) {
                return List.of();
            }

            return response.getMessages()
                    .stream()
                    .map(this::loadMessage)
                    .map(message -> parser.parse(gmail, message))
                    .toList();

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    private Message loadMessage(Message summary) {

        try {

            return gmail.users()
                    .messages()
                    .get("me", summary.getId())
                    .setFormat("full")
                    .execute();

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}
