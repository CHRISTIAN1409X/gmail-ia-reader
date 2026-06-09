package com.gmail.ia.reader.application.app.claude;

import com.gmail.ia.reader.domain.dtos.gmail.EmailPart;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.gmail.ia.reader.infraestructure.models.IaEvaluation;
import com.gmail.ia.reader.infraestructure.models.records.CriteriaResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class ClaudeAnaliticService {

    public void analize(ParsedEmail email, EmailPart pdf){
        System.out.println("Analizando PDF "+pdf.fileName());
        CriteriaResult criteriaResult = new CriteriaResult(
                "Contiene Firmas",
                true,
                ""
        );
    }
}
