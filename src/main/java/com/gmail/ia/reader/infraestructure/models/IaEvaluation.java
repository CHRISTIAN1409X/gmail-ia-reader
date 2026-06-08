package com.gmail.ia.reader.infraestructure.models;

import com.gmail.ia.reader.infraestructure.models.records.CriteriaResult;
import com.gmail.ia.reader.infraestructure.models.records.ErrorDetail;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.databind.JsonNode;

@Entity
@Table(name = "ia_evaluation")
public class IaEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ia_evaluation_seq")
    @SequenceGenerator(
            name = "ia_evaluation_seq",
            sequenceName = "ia_evaluation_id_seq",
            allocationSize = 1
    )
    private Long id; // Cambiado de UUID a Long

    private String emailId;

    private Boolean processed;

    private LocalDateTime processingDate;

    private String model;

    private String promptVersion;

    private Double score;

    private Boolean approved;

    private Double confidence;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<ErrorDetail> errors;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> recommendations;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<CriteriaResult> criteriaResults;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode rawResponse;

    private String status;

    private Double processingTimeSeconds;

    private LocalDateTime createdAt;
}

