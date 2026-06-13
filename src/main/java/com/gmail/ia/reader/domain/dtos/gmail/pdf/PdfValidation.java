package com.gmail.ia.reader.domain.dtos.gmail.pdf;

import com.gmail.ia.reader.domain.dtos.gmail.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfValidation{
    private PdfDocument pdfDocument;
    private List<ValidationError> validationError;
}

