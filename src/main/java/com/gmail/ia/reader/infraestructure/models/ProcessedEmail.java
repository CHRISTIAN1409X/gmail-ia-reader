package com.gmail.ia.reader.infraestructure.models;

import com.gmail.ia.reader.infraestructure.models.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Table(name = "processed_email")
@Data
public class ProcessedEmail {

    @Id
    @Column(name = "message_id", length = 255)
    private String messageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50) // <- Quita el columnDefinition
    private Status status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}