package com.gmail.ia.reader.infraestructure.models.records;

import com.gmail.ia.reader.infraestructure.models.aux.EmailContact;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_seq")
    @SequenceGenerator(
            name = "email_seq",
            sequenceName = "email_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "gmail_id")
    private String gmailId;

    @Column(name = "thread_id")
    private String threadId;

    private String subject;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "from_contact", columnDefinition = "jsonb")
    private EmailContact from;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "to_contacts", columnDefinition = "jsonb")
    private List<EmailContact> to;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cc_contacts", columnDefinition = "jsonb")
    private List<EmailContact> cc;

    private LocalDateTime date;

    @Column(columnDefinition = "TEXT")
    private String snippet;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "body_cleaned", columnDefinition = "TEXT")
    private String bodyCleaned;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> labels;
}
