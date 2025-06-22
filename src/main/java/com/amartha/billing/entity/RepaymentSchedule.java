package com.amartha.billing.entity;

import com.amartha.billing.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repayment_schedule")
public class RepaymentSchedule extends AuditSection implements Serializable {

    @Id
    private UUID id;

    @Column
    private int week;

    @Column
    private BigDecimal amount;

    @Column
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentStatus status;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;
}
