package com.gmail.ia.reader.application.app.ia;


import com.gmail.ia.reader.application.app.usecases.IaAnaliticService;
import com.gmail.ia.reader.domain.dtos.cloude.CriteriaResult;
import com.gmail.ia.reader.domain.dtos.cloude.IaRespondeRecord;
import com.gmail.ia.reader.domain.dtos.cloude.PathPart;
import com.gmail.ia.reader.domain.dtos.gmail.ParsedEmail;
import com.gmail.ia.reader.domain.dtos.gmail.pdf.PdfDocument;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class IaAnaliticServiceImpl implements IaAnaliticService {
    public IaRespondeRecord analize(ParsedEmail email, PdfDocument plannerPdf, PdfDocument microPdf){


        try (InputStream planner =
                     Files.newInputStream(
                             plannerPdf.tempFile()
                     );
             InputStream micro =
                     Files.newInputStream(
                             microPdf.tempFile()
                     )
        ) {
            System.out.println("Analizando PDF "+plannerPdf.fileName());
            List<CriteriaResult> listCriteriaResult = List.of(
                    new CriteriaResult("Contiene Firmas", true, "", (byte) 10),
                    new CriteriaResult("Fechas del planeador", true,"", (byte)10),
                    new CriteriaResult("Contenido de la unidad 1 corresponde con el micro-curriculum",true,"",(byte) 10),
                    new CriteriaResult("Las fechas de la unidad 4 estan entre los limites de las fechas del planeador",true,"",(byte) 8)
            );

            List<PathPart> pathPartList = List.of(new PathPart("VIII"),new PathPart("Etica profesional Ingeniero"),new PathPart("Profesor [x]"));
            return new IaRespondeRecord(
                    "Bot IA",
                    "LM Pattern 8",
                    4000,
                    LocalDateTime.now(),
                    listCriteriaResult,
                    pathPartList
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
}
