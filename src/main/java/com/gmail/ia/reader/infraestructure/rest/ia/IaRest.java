package com.gmail.ia.reader.infraestructure.rest.ia;

import com.gmail.ia.reader.application.app.drive.DriveStorageService;
import com.gmail.ia.reader.application.usecases.iaEvaluation.IaEvaluationService;
import com.gmail.ia.reader.domain.dtos.iaevaluation.IaEvaluationDetailResponse;
import com.gmail.ia.reader.domain.dtos.iaevaluation.IaEvaluationListResponse;
import com.gmail.ia.reader.infraestructure.advicers.exceptions.BadRequestException;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.iaevaluation.IaEvaluationRepository;
import com.gmail.ia.reader.infraestructure.models.IaEvaluation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/ia")
@RestController()
public class IaRest {
    private final IaEvaluationService iaEvaluationService;
    private final IaEvaluationRepository iaEvaluationRepository;
    private final DriveStorageService driveStorageService;

    @GetMapping
    public ResponseEntity<List<IaEvaluationListResponse>> getAllEvaluations() {
        return ResponseEntity.ok(iaEvaluationService.getAllEvaluations());
    }

    @PostMapping("/{uuidIa}/approve")
    public ResponseEntity<Void> aprovedPlanner(@PathVariable UUID uuidIa){
        iaEvaluationService.aprovedPlanner(uuidIa);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuidIa}/observations")
    public ResponseEntity<String> sendObservations(@PathVariable UUID uuidIa) {
        String message = iaEvaluationService.sendObservations(uuidIa);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{uuidIa}/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable UUID uuidIa) {
        IaEvaluation eval = iaEvaluationRepository.findByUUID(uuidIa)
                .orElseThrow(() -> new BadRequestException("No existe el ID"));
        if (eval.getDriveFileId() == null || eval.getDriveFileId().isBlank()) {
            throw new BadRequestException("El PDF aún no ha sido cargado a Drive");
        }
        byte[] pdfBytes = driveStorageService.downloadFile(eval.getDriveFileId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + eval.getPdfName() + "\"")
                .header("X-Frame-Options", "ALLOWALL")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/{uuidIa}")
    public ResponseEntity<IaEvaluationDetailResponse> getEvaluation(
            @PathVariable UUID uuidIa
    ) {

        return ResponseEntity.ok(
                iaEvaluationService.getEvaluation(uuidIa)
        );
    }
}
