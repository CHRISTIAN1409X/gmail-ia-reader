package com.gmail.ia.reader.domain.dtos.drive;

import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;
import com.gmail.ia.reader.domain.enums.DriveFolderEnum;

public record DriveUploadRecord(
        Long idIaEvaluation,
        DriveFolderEnum driveFolderEnum,
        String name,
        String path,
       PdfDocument pdfDocument) {

}
