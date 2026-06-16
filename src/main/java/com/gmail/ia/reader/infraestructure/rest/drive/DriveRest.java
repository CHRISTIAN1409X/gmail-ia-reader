package com.gmail.ia.reader.infraestructure.rest.drive;

import com.gmail.ia.reader.application.app.drive.DriveStorageService;
import com.gmail.ia.reader.domain.enums.DriveFolderEnum;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/drive")
@RequiredArgsConstructor
public class DriveRest {

    private final DriveStorageService driveStorageService;

    @PostMapping("/upload-microcurriculum")
    public ResponseEntity<?> uploadMicrocurriculum(
            @RequestParam("file") MultipartFile file
    ) {

        try {

            Path tempFile =
                    Files.createTempFile(
                            "microcurriculum-",
                            ".pdf"
                    );

            file.transferTo(tempFile);

            PdfDocument pdfDocument =
                    new PdfDocument(
                            file.getOriginalFilename(),
                            tempFile
                    );

            String driveFileId =
                    driveStorageService.uploadPdf(
                            DriveFolderEnum.TEMPORAL,
                            "microcurriculos",
                            pdfDocument
                    );

            return ResponseEntity.ok(driveFileId);

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }
}
