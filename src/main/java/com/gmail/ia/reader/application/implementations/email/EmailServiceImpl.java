package com.gmail.ia.reader.application.implementations.email;

import com.gmail.ia.reader.application.usecases.email.EmailService;
import com.gmail.ia.reader.domain.dtos.cloude.CriteriaResult;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.gmail.ia.reader.domain.dtos.iaevaluation.IaEvaluationCreated;
import com.gmail.ia.reader.domain.dtos.pdf.PdfProcessingResult;
import com.gmail.ia.reader.domain.enums.DriveFolderEnum;
import com.gmail.ia.reader.global.domain.ports.DaoCrudPort;
import com.gmail.ia.reader.infraestructure.models.Email;
import com.gmail.ia.reader.infraestructure.models.IaEvaluation;
import com.gmail.ia.reader.infraestructure.models.enums.DriveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class EmailServiceImpl implements EmailService {

    private final DaoCrudPort<Email> emailDaoCrudPort;
    private final DaoCrudPort<IaEvaluation> iaEvaluationDaoCrudPort;

    @Override
    @Transactional
    public List<IaEvaluationCreated> updloadFile(ParsedEmail parsedEmail, List<PdfProcessingResult> pdfProcessingResults) {
        List<PdfProcessingResult> newPdfProcessing = new ArrayList<>(pdfProcessingResults);
        List<IaEvaluationCreated> createdFile =
                new ArrayList<>();
        Email email = Email.builder()
                .id(null)
                .threadId("") // Recordar
                .gmailId(parsedEmail.messageId())
                .subject(parsedEmail.subject())
                .from(parsedEmail.from())
                .to(parsedEmail.to())
                .build();

        emailDaoCrudPort.create(email);
        for (PdfProcessingResult singleResult: newPdfProcessing){
            IaEvaluation iaEvaluation = IaEvaluation.builder()
                    .id(null)
                    .uuid(singleResult.uuid())
                    .score(calculateScoreCriteriaResult(singleResult.iaResponse().listCriteriaResult()))
                    .email(email)
                    .criteriaResults(singleResult.iaResponse().listCriteriaResult())
                    .processingDate(singleResult.iaResponse().processingDate())
                    .processingMiliSeconds(singleResult.iaResponse().processingTimeMiliseconds())
                    .driveFileId(null)
                    .driveStatus(DriveStatus.PENDING)
                    .driveFolderEnum(DriveFolderEnum.TEMPORAL)
                    .localTempPath(singleResult.localTempPath())
                    .pdfName(singleResult.fileName())
                    .pathPdf(singleResult.path())
                    .build();
            iaEvaluationDaoCrudPort.create(iaEvaluation);
            createdFile.add(
                    new IaEvaluationCreated(
                            singleResult.uuid(),
                            iaEvaluation.getId()
                    )
            );
        }

        return createdFile;
    }


    private Double calculateScoreCriteriaResult(List<CriteriaResult> criteriaResults){
        return criteriaResults.stream()
                .mapToInt(CriteriaResult::score)
                .average()
                .orElse(0.0);
    }
}
