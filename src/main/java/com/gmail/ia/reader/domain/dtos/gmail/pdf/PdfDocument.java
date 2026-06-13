package com.gmail.ia.reader.domain.dtos.gmail.pdf;

import com.gmail.ia.reader.domain.dtos.gmail.EmailValidationResult;

public record PdfDocument(
        String fileName,
        byte[] content
) {

    public PdfDocument clearContent(){
        return new PdfDocument(this.fileName,null);
    }
}