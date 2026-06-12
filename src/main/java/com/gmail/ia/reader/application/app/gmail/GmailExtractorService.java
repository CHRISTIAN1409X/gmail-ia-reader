package com.gmail.ia.reader.application.app.gmail;


import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Component
public class GmailExtractorService {

    private final Gmail gmail;

    public List<String> findUnreadMessageIds() {

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
                    .map(Message::getId)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Message loadMessage(String messageId) {

        try {
            return gmail.users()
                    .messages()
                    .get("me", messageId)
                    .setFormat("full")
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
