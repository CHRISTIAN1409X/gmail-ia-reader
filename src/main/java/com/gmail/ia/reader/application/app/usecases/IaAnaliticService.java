package com.gmail.ia.reader.application.app.usecases;

import com.gmail.ia.reader.domain.dtos.cloude.IaRespondeRecord;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;

public interface IaAnaliticService {
    IaRespondeRecord analize(ParsedEmail email, PdfDocument pdf);
}
