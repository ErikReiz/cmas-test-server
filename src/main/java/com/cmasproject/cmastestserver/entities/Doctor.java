package com.cmasproject.cmastestserver.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "doctors")
public class Doctor {

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

    @NotBlank
    @Size(max = 100)
    @Column(columnDefinition = "varchar(100)")
    private String specialty;

    @NotBlank
    @Size(max = 50)
    @Column(name = "license_number", columnDefinition = "char(50)", unique = true)
    private String licenseNumber;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "doctor_patient",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id")
    )
    Set<Patient> patients = new HashSet<>();
}