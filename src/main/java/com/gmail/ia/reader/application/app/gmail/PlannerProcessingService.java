package com.gmail.ia.reader.application.app.gmail;

import com.gmail.ia.reader.application.app.claude.ClaudeAnaliticService;
import com.gmail.ia.reader.domain.dtos.gmail.EmailPart;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PlannerProcessingService {

    private final GmailExtractorService gmailExtractorService;
    private final ClaudeAnaliticService claudeAnaliticService;

    public void processPendingEmails(){
      List<ParsedEmail> parsedEmailList =  gmailExtractorService.extractEmails();
      for (ParsedEmail parsedEmail:parsedEmailList){
          List<EmailPart> emailPart = extractPDFs(parsedEmail);
          emailPart.forEach(pdf-> claudeAnaliticService.analize(parsedEmail,pdf));
      }
    }

    public List<EmailPart> extractPDFs(ParsedEmail parsedEmail){
        return parsedEmail
                .parts()
                .stream()
                .filter(part-> "application/pdf".equalsIgnoreCase(part.mimeType()))
                .toList();
    }
}
