package com.cmasproject.cmastestserver.entities;

import com.cmasproject.cmastestserver.entities.enums.TestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="tests")
public class TestRecord {
    @Id
    @UuidGenerator
    @Column(name="test_id", length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Version
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    private Integer totalCmasScore;

    @CreationTimestamp
    private LocalDateTime assignedDate;

    private LocalDateTime completedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestStatus status;
}
