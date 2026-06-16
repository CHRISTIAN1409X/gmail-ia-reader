package com.gmail.ia.reader.infraestructure.rest.ia;

import com.gmail.ia.reader.application.usecases.iaEvaluation.IaEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
