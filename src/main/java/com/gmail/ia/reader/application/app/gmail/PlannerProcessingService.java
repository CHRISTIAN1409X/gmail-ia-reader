package com.gmail.ia.reader.application.app.gmail;

import com.gmail.ia.reader.application.app.claude.ClaudeAnaliticService;
import com.gmail.ia.reader.application.app.gmail.sender.GmailSender;
import com.gmail.ia.reader.application.app.gmail.validation.EmailValidationService;
import com.gmail.ia.reader.application.app.gmail.validation.PdfValidationService;
import com.gmail.ia.reader.domain.dtos.gmail.EmailPart;
import com.gmail.ia.reader.domain.dtos.gmail.EmailValidationResult;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PlannerProcessingService {

    private final GmailExtractorService gmailExtractorService;
    private final ClaudeAnaliticService claudeAnaliticService;
    private final EmailValidationService emailValidationService;
    private final GmailSender gmailSender;
    private final String[] listEmailsError = new String[]{"corjuela1030@cue.edu.co","acastano2233@cue.edu.co","jquiceno2219@cue.edu.co","scardona1026@cue.edu.co"};
   // private final String[] listEmailsError = new String[]{"corjuela1030@cue.edu.co"};
    public void processPendingEmails(){
      List<ParsedEmail> parsedEmailList =  gmailExtractorService.extractEmails();
      parsedEmailList.forEach(this::processEmail);
    }

    public void processEmail(ParsedEmail email){
        EmailValidationResult validationResult = emailValidationService.validate(email);
        System.out.println(validationResult);
        if (!validationResult.valid()){
            System.out.println("Correo Rechazado enviado por "+validationResult.emailOwner());
            Arrays.asList(listEmailsError).forEach(recep-> {
                try {
                    gmailSender.sendEmail(recep,"Buenas tardes *****",validationResult);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            return;
        }
        List<EmailPart> pdfs =
                extractPDFs(email);

        pdfs.forEach(pdf ->
                claudeAnaliticService.analize(
                        email,
                        pdf
                ));

    }

    public List<EmailPart> extractPDFs(ParsedEmail parsedEmail){
        return parsedEmail
                .parts()
                .stream()
                .filter(part-> "application/pdf".equalsIgnoreCase(part.mimeType()))
                .toList();
    }
}
