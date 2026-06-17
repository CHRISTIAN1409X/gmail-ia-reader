package com.gmail.ia.reader.application.implementations.iaEvaluation;

import com.gmail.ia.reader.application.app.drive.DriveStorageService;
import com.gmail.ia.reader.application.app.gmail.sender.GmailSender;
import com.gmail.ia.reader.application.implementations.processedEmail.ProcessedEmailStatusServiceImpl;
import com.gmail.ia.reader.application.usecases.iaEvaluation.IaEvaluationService;
import com.gmail.ia.reader.domain.dtos.cloude.CriteriaResult;
import com.gmail.ia.reader.domain.dtos.drive.UploadDriveResponse;
import com.gmail.ia.reader.domain.dtos.iaevaluation.IaEvaluationDetailResponse;
import com.gmail.ia.reader.domain.dtos.iaevaluation.IaEvaluationListResponse;
import com.gmail.ia.reader.domain.enums.DriveFolderEnum;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.iaevaluation.IaEvaluationRepository;
import com.gmail.ia.reader.infraestructure.advicers.exceptions.BadRequestException;
import com.gmail.ia.reader.infraestructure.models.IaEvaluation;
import com.gmail.ia.reader.infraestructure.models.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class IaEvaluationServiceImpl implements IaEvaluationService {

    private final IaEvaluationRepository iaEvaluationRepository;
    private final ProcessedEmailStatusServiceImpl processedEmailStatusService;
    private final DriveStorageService driveStorageService;
    private final GmailSender gmailSender;

    @Override
    @Transactional
    public void aprovedPlanner(UUID uuidIa) {
        IaEvaluation iaEvaluation = iaEvaluationRepository.findByUUID(uuidIa).orElseThrow(()-> new BadRequestException("No existe el ID"));
        String urlDocument = driveStorageService.moveToApproved(iaEvaluation.getDriveFileId(),iaEvaluation.getPathPdf());
        if (urlDocument.isEmpty()){
            throw new BadRequestException("Error al crear la url del pdf");
        }
        String messageId = iaEvaluation.getEmail().getGmailId();
        processedEmailStatusService.markProcessed(messageId, Status.APPROVED);
        iaEvaluation.setDriveFolderEnum(DriveFolderEnum.APROBADOS);
        iaEvaluation.setUrlPdfDrive(urlDocument);
    }

    @Transactional
    public boolean startUpload(
            Long iaEvaluationId) {

        return iaEvaluationRepository
                .markUploading(
                        iaEvaluationId
                );
    }

    @Transactional
    public boolean finishUpload(
            Long iaEvaluationId,
            UploadDriveResponse uploadDriveResponse) {

        return iaEvaluationRepository
                .markUploaded(
                        iaEvaluationId,
                        uploadDriveResponse
                );
    }

    @Override
    public IaEvaluationDetailResponse getEvaluation(UUID uuidIa) {
        IaEvaluation evaluation = iaEvaluationRepository
                .findByUUID(uuidIa)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        String statusKey;
        String status;
        if (evaluation.getDriveFolderEnum() == DriveFolderEnum.APROBADOS) {
            statusKey = "approved";
            status = "Aprobado";
        } else {
            statusKey = "review";
            status = "Revisión coordinador";
        }

        return new IaEvaluationDetailResponse(
                evaluation.getUuid(),
                evaluation.getScore(),
                evaluation.getPdfName(),
                evaluation.getUrlPdfDrive(),
                statusKey,
                status,
                evaluation.getCriteriaResults()
        );
    }

    @Override
    public List<IaEvaluationListResponse> getAllEvaluations() {
        List<IaEvaluation> evaluations = iaEvaluationRepository.selectAll();
        List<IaEvaluationListResponse> response = new ArrayList<>();

        for (IaEvaluation eval : evaluations) {
            String emailFrom = eval.getEmail() != null ? eval.getEmail().getFrom() : "";
            String pathPdf = eval.getPathPdf() != null ? eval.getPathPdf() : "";
            String[] parts = pathPdf.split("/");

            String semester = parts.length > 0 ? parts[0] : "";
            String subjectName = parts.length > 1 ? parts[1] : "";
            String professor = parts.length > 2 ? parts[2] : extractDisplayName(emailFrom);

            String statusKey;
            String status;
            if (eval.getDriveFolderEnum() == DriveFolderEnum.APROBADOS) {
                statusKey = "approved";
                status = "Aprobado";
            } else {
                statusKey = "review";
                status = "Revisión coordinador";
            }

            response.add(new IaEvaluationListResponse(
                    eval.getUuid(),
                    eval.getScore(),
                    eval.getPdfName(),
                    professor,
                    subjectName,
                    semester,
                    statusKey,
                    status,
                    eval.getCreatedAt(),
                    eval.getUpdatedAt()
            ));
        }

        return response;
    }

    @Override
    public String sendObservations(UUID uuidIa) {
        IaEvaluation eval = iaEvaluationRepository.findByUUID(uuidIa)
                .orElseThrow(() -> new BadRequestException("No existe el ID"));

        String recipient = eval.getEmail() != null ? eval.getEmail().getFrom() : "";
        List<CriteriaResult> criteria = eval.getCriteriaResults();

        StringBuilder html = new StringBuilder();
        html.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>");
        html.append("<h2 style='color: #1e293b;'>Observaciones de revisión IA</h2>");
        html.append("<p style='color: #475569;'>Se han realizado las siguientes observaciones sobre el documento <strong>")
                .append(eval.getPdfName() != null ? eval.getPdfName() : "").append("</strong>:</p>");
        html.append("<hr style='border: none; border-top: 1px solid #e2e8f0; margin: 16px 0;'/>");

        if (criteria != null) {
            for (CriteriaResult cr : criteria) {
                String color = cr.passed() ? "#16a34a" : "#dc2626";
                String icon = cr.passed() ? "✓" : "✗";
                html.append("<div style='margin: 12px 0; padding: 12px; border-left: 4px solid ").append(color)
                        .append("; background: #f8fafc; border-radius: 4px;'>");
                html.append("<p style='margin: 0; font-weight: bold; color: #1e293b;'>").append(icon)
                        .append(" ").append(escapeHtml(cr.criterion())).append("</p>");
                html.append("<p style='margin: 4px 0 0 0; color: #475569;'>").append(escapeHtml(cr.observation()))
                        .append("</p>");
                html.append("<p style='margin: 4px 0 0 0; font-size: 12px; color: #64748b;'>Score: ")
                        .append(cr.score()).append("/10</p>");
                html.append("</div>");
            }
        }

        html.append("<hr style='border: none; border-top: 1px solid #e2e8f0; margin: 16px 0;'/>");
        html.append("<p style='color: #64748b; font-size: 12px;'>Este es un mensaje automático del sistema de validación documental.</p>");
        html.append("</div>");

        try {
            gmailSender.sendEmail(extractEmailAddress(recipient), "Observaciones de revisión - " + eval.getPdfName(), html.toString());
            return "Correo enviado exitosamente al profesor";
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el correo de observaciones", e);
        }
    }

    private String extractEmailAddress(String fromHeader) {
        if (fromHeader == null || fromHeader.isBlank()) return "";
        int start = fromHeader.indexOf('<');
        int end = fromHeader.indexOf('>');
        if (start != -1 && end != -1) return fromHeader.substring(start + 1, end);
        return fromHeader.trim();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String extractDisplayName(String fromHeader) {
        if (fromHeader == null || fromHeader.isBlank()) return "";
        int start = fromHeader.indexOf('"');
        if (start != -1) {
            int end = fromHeader.indexOf('"', start + 1);
            if (end != -1) return fromHeader.substring(start + 1, end);
        }
        int bracket = fromHeader.indexOf('<');
        if (bracket != -1) return fromHeader.substring(0, bracket).trim();
        int at = fromHeader.indexOf('@');
        if (at != -1) return fromHeader.substring(0, at);
        return fromHeader;
    }
}
