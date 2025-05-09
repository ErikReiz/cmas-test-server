package com.cmasproject.cmastestserver.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="questions")
public class Question {
    @Id
    @UuidGenerator
    @Column(name="question_id", length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Version
    private Integer version;

    @Column(name="question_number", updatable = false, nullable = false)
    private Integer questionNumber;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private TestRecord testRecord;
}
