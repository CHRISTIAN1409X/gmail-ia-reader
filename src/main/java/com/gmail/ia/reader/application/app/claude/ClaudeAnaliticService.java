package com.gmail.ia.reader.application.app.claude;

import com.gmail.ia.reader.domain.dtos.gmail.EmailAttachmentRef;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;
import com.gmail.ia.reader.infraestructure.models.records.CriteriaResult;
import org.springframework.stereotype.Component;


@Component
public class ClaudeAnaliticService {

    public void analize(ParsedEmail email, PdfDocument pdf){
        System.out.println("Analizando PDF "+pdf.fileName());
        CriteriaResult criteriaResult = new CriteriaResult(
                "Contiene Firmas",
                true,
                ""
        );
        System.out.println(criteriaResult);
    }
}
