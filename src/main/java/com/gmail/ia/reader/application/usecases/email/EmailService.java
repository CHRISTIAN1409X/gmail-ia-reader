package com.gmail.ia.reader.application.usecases.email;

import com.gmail.ia.reader.domain.dtos.cloude.IaRespondeRecord;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.gmail.ia.reader.domain.dtos.iaevaluation.IaEvaluationCreated;
import com.gmail.ia.reader.domain.dtos.pdf.PdfProcessingResult;

import java.util.List;
import java.util.UUID;

public interface EmailService {

    List<IaEvaluationCreated> updloadFile(ParsedEmail parsedEmail, List<PdfProcessingResult> pdfProcessingResults);
}
