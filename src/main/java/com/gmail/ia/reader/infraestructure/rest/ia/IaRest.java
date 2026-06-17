package com.gmail.ia.reader.infraestructure.rest.ia;

import com.gmail.ia.reader.application.usecases.iaEvaluation.IaEvaluationService;
import com.gmail.ia.reader.domain.dtos.iaevaluation.IaEvaluationDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/ia")
@RestController()
public class IaRest {
    private final IaEvaluationService iaEvaluationService;
    @PostMapping("/{uuidIa}/approve")
    public ResponseEntity<Void> aprovedPlanner(@PathVariable UUID uuidIa){
        iaEvaluationService.aprovedPlanner(uuidIa);
        return ResponseEntity.noContent().build();
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
