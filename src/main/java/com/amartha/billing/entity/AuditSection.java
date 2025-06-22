package com.amartha.billing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class AuditSection implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private Instant createdDate;

    @Column
    private Instant lastModifiedDate;

    @Version
    @Column
    private long version;
}
