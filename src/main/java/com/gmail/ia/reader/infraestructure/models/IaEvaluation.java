package com.gmail.ia.reader.infraestructure.models;

import com.gmail.ia.reader.domain.dtos.cloude.CriteriaResult;
import com.gmail.ia.reader.domain.enums.DriveFolderEnum;
import com.gmail.ia.reader.infraestructure.models.enums.DriveStatus;
import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private Long id;

    @Builder.Default
    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private UUID uuid = UUID.randomUUID();

    private Double score;

    @Column(name = "pdf_name")
    private String pdfName;

    @Column(name = "path_pdf")
    private String pathPdf;

    @Column(name = "drive_file_id")
    private String driveFileId;

    @Column(name = "url_pdf_drive")
    private String urlPdfDrive;

    @Column(name = "local_temp_path")
    private String localTempPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "drive_status")
    private DriveStatus driveStatus;

    @Enumerated(EnumType.STRING)
    private DriveFolderEnum driveFolderEnum;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<CriteriaResult> criteriaResults;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id", referencedColumnName = "id", nullable = false)
    private Email email;

    @Column(name = "processing_date")
    private LocalDateTime processingDate;

    @Column(name = "processing_miliseconds")
    private long processingMiliSeconds;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

