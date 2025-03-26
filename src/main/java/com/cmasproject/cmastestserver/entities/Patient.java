package com.cmasproject.cmastestserver.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @UuidGenerator
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(columnDefinition = "varchar(36)", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private User user;

    @Version
    private Integer version;

    @Past
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 5000)
    @Column(name = "medical_history", columnDefinition = "TEXT(5000)")
    private String medicalHistory;

    @ManyToMany(mappedBy = "patients")
    Set<Doctor> doctors = new HashSet<>();
}
