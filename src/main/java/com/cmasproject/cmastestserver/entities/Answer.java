package com.cmasproject.cmastestserver.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "answers", uniqueConstraints = @UniqueConstraint(columnNames = {"test_id", "question_id"}))
public class Answer {
    @Id
    @UuidGenerator
    @Column(name="answer_id", length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;

    @Version
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private TestRecord testRecord;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "video_link")
    private String videoLink;
}
