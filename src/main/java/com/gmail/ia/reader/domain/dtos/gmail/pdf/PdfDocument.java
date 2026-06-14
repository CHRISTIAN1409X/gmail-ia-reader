package com.gmail.ia.reader.domain.dtos.gmail.pdf;

import com.gmail.ia.reader.domain.dtos.gmail.EmailValidationResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record PdfDocument(
        String fileName,
        Path tempFile
) {

    public PdfDocument clearContent(){
        return new PdfDocument(this.fileName,null);
    }

    public void deleteTempFile() {

        if (tempFile == null) {
            return;
        }

        try {

            Files.deleteIfExists(
                    tempFile
            );

        } catch (IOException ignored) {

        }
    }
}