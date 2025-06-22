package com.amartha.billing.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loan")
public class Loan extends AuditSection implements Serializable {

    @Column(nullable = false)
    private BigDecimal principalAmount;

    @Column(nullable = false)
    private BigDecimal annualInterestRate;

    @Column(nullable = false)
    private int termInWeeks;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "loan")
    private List<RepaymentSchedule> repaymentSchedules;
}
