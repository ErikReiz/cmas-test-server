package com.cmasproject.cmastestserver.entities;

import com.cmasproject.cmastestserver.entities.enums.FileType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
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
@Table(name = "files")
public class File {
    @Id
    @UuidGenerator
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Version
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "patient_id", columnDefinition = "varchar(36)", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", columnDefinition = "varchar(36)", nullable = false)
    private Doctor doctor;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", columnDefinition = "varchar(255)", nullable = false)
    private FileType fileType;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
