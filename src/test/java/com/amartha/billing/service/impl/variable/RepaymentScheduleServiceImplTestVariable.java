package com.amartha.billing.service.impl.variable;

import com.amartha.billing.constant.PaymentStatus;
import com.amartha.billing.entity.RepaymentSchedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RepaymentScheduleServiceImplTestVariable {
    protected static final BigDecimal PRINCIPAL = new BigDecimal("1000000");
    protected static final BigDecimal ANNUAL_INTEREST = new BigDecimal("0.12");
    protected static final BigDecimal TOTAL_INTEREST = PRINCIPAL.multiply(ANNUAL_INTEREST);
    protected static final BigDecimal TOTAL_PAYABLE = PRINCIPAL.add(TOTAL_INTEREST);
    protected static final BigDecimal WEEKLY_INSTALLMENT = TOTAL_PAYABLE.divide(BigDecimal.valueOf(50), 0, java.math.RoundingMode.UP);

    protected static final List<RepaymentSchedule> ALL_PENDING_DUE = Arrays.asList(
            RepaymentSchedule.builder()
                    .week(1)
                    .amount(WEEKLY_INSTALLMENT)
                    .status(PaymentStatus.PENDING)
                    .dueDate(LocalDate.now().minusWeeks(1))
                    .build(),
            RepaymentSchedule.builder()
                    .week(2)
                    .amount(WEEKLY_INSTALLMENT)
                    .status(PaymentStatus.PENDING)
                    .dueDate(LocalDate.now())
                    .build()
    );

    protected static final List<RepaymentSchedule> ALL_PAID = Collections.singletonList(
            RepaymentSchedule.builder()
                    .week(1)
                    .amount(WEEKLY_INSTALLMENT)
                    .status(PaymentStatus.PAID)
                    .dueDate(LocalDate.now().minusWeeks(2))
                    .build()
    );

    protected static final List<RepaymentSchedule> MIXED = Arrays.asList(
            RepaymentSchedule.builder()
                    .week(1)
                    .amount(WEEKLY_INSTALLMENT)
                    .status(PaymentStatus.PAID)
                    .dueDate(LocalDate.now().minusWeeks(2))
                    .build(),
            RepaymentSchedule.builder()
                    .week(2)
                    .amount(WEEKLY_INSTALLMENT)
                    .status(PaymentStatus.PENDING)
                    .dueDate(LocalDate.now().minusWeeks(1))
                    .build(),
            RepaymentSchedule.builder()
                    .week(3)
                    .amount(WEEKLY_INSTALLMENT)
                    .status(PaymentStatus.PENDING)
                    .dueDate(LocalDate.now().plusWeeks(1))
                    .build()
    );

    protected static final List<RepaymentSchedule> EMPTY = Collections.emptyList();
}