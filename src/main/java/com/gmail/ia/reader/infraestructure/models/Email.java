package com.gmail.ia.reader.infraestructure.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_seq")
    @SequenceGenerator(
            name = "email_seq",
            sequenceName = "email_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "email")
    private List<FilePath> filePaths = new ArrayList<>();

    @Column(name = "gmail_id")
    private String gmailId;

    @Column(name = "thread_id")
    private String threadId;

    private String subject;

    @Column(name = "mail_from")
    private String from;

    @Column(name = "mail_to")
    private String to;

    @Builder.Default
    @OneToMany(mappedBy = "email")
    private List<IaEvaluation> iaEvaluations = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;



    /*

     @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "from_contact", columnDefinition = "jsonb")
    private EmailContact from;


    @Column(columnDefinition = "TEXT")
    private String snippet;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "body_cleaned", columnDefinition = "TEXT")
    private String bodyCleaned;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> labels;

     */
}
