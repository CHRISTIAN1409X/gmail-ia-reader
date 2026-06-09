package com.gmail.ia.reader.application.app.claude;

import com.gmail.ia.reader.domain.dtos.gmail.EmailPart;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import org.springframework.stereotype.Component;

@Component
public class ClaudeAnaliticService {

    public void analize(ParsedEmail email, EmailPart pdf){
        System.out.println("Analizando PDF "+pdf.fileName());
    }
}
