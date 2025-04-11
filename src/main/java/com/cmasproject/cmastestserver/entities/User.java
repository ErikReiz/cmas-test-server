package com.cmasproject.cmastestserver.entities;

import com.cmasproject.cmastestserver.entities.enums.Role;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="users")
public class User {
    @Id
    @UuidGenerator
    @Column(name="user_id", length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Version
    private Integer version;

    @NotBlank
    @Size(min=3, max=50)
    private String username;

    @NotBlank
    @Size(min=6, max=64)
    @Column(length = 64, name="password_hash")
    private String passwordHash;

    @NotBlank
    @Column(name="first_name")
    private String firstName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Column(name="last_name")
    private String lastName;

    @Pattern(regexp="^\\+[1-9]\\d{1,14}$")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255)", nullable = false)
    private Role role;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
}
