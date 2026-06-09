package com.gmail.ia.reader.application.app.gmail.sender;

import com.gmail.ia.reader.domain.dtos.gmail.EmailValidationResult;
import com.gmail.ia.reader.domain.dtos.gmail.ValidationError;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

@AllArgsConstructor
@Component
public class GmailSender {

    private Gmail gmailService;

    public void sendEmail(String recipient, String subject, EmailValidationResult result) throws IOException {

        StringBuilder htmlBody = new StringBuilder();
        htmlBody.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
        htmlBody.append("<h2 style='color: #333;'>Email Validation Report</h2>");
        htmlBody.append("<p><strong>Owner:</strong> ").append(result.emailOwner()).append("</p>");


        String statusColor = result.valid() ? "#28a745" : "#dc3545";
        String statusText = result.valid() ? "VALID" : "INVALID";
        htmlBody.append("<p><strong>Status:</strong> ")
                .append("<span style='background-color: ").append(statusColor)
                .append("; color: white; padding: 4px 8px; border-radius: 4px; font-weight: bold;'>")
                .append(statusText).append("</span></p>");

        if (result.errors() != null && !result.errors().isEmpty()) {
            htmlBody.append("<h3 style='color: #555; margin-top: 20px;'>Validation Errors</h3>");
            htmlBody.append("<table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>");
            htmlBody.append("<thead><tr style='background-color: #f8f9fa;'>");
            htmlBody.append("<th style='border: 1px solid #dee2e6; padding: 8px; text-align: left;'>Code</th>");
            htmlBody.append("<th style='border: 1px solid #dee2e6; padding: 8px; text-align: left;'>Description</th>");
            htmlBody.append("</tr></thead><tbody>");

            for (ValidationError error : result.errors()) {
                htmlBody.append("<tr>");
                htmlBody.append("<td style='border: 1px solid #dee2e6; padding: 8px; font-family: monospace; color: #d63384;'>")
                        .append(error.code()).append("</td>");
                htmlBody.append("<td style='border: 1px solid #dee2e6; padding: 8px;'>")
                        .append(error.message()).append("</td>");
                htmlBody.append("</tr>");
            }

            htmlBody.append("</tbody></table>");
        } else {
            htmlBody.append("<p style='color: #28a745; font-weight: bold;'>No errors found during validation.</p>");
        }

        htmlBody.append("</div>");

        String mimeFormat = "To: " + recipient + "\n" +
                "Subject: " + subject + "\n" +
                "Content-Type: text/html; charset=utf-8\n\n" +
                htmlBody.toString();

        String encodedText = Base64.getUrlEncoder().withoutPadding().encodeToString(mimeFormat.getBytes("UTF-8"));

        Message message = new Message();
        message.setRaw(encodedText);
        gmailService.users().messages().send("me", message).execute();
    }
}
