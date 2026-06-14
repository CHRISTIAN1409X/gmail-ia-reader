package com.gmail.ia.reader.domain.dtos.cloude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record IaRespondeRecord(
        String nameIa,
        String typeModel,
        long processingTimeMiliseconds,
        LocalDateTime processingDate,
        List<CriteriaResult> listCriteriaResult,
        List<PathPart> listPathPart
) {
    public IaRespondeRecord newObject(){
        return new IaRespondeRecord(this.nameIa,
                this.typeModel,
                this.processingTimeMiliseconds,
                this.processingDate,
                this.listCriteriaResult,
                this.listPathPart);
    }
}
