package com.gmail.ia.reader.infraestructure.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file_path")
public class FilePath {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_path_seq")
    @SequenceGenerator(
            name = "file_path_seq",
            sequenceName = "file_path_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id", referencedColumnName = "id", nullable = false)
    private Email email;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}