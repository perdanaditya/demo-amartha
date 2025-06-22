package com.amartha.billing.service.impl.variable;

import com.amartha.billing.constant.PaymentStatus;
import com.amartha.billing.entity.Customer;
import com.amartha.billing.entity.Loan;
import com.amartha.billing.entity.RepaymentSchedule;
import com.amartha.billing.entity.SystemConfig;
import com.amartha.billing.service.model.CustomerServiceRequest;
import com.amartha.billing.service.model.LoanServiceRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoanServiceImplTestVariable {

    protected static final UUID LOAN_ID = UUID.randomUUID();
    protected static final UUID CUSTOMER_ID = UUID.randomUUID();
    protected static final String PRINCIPAL_AMOUNT = "5000000";
    protected static final String ANNUAL_INTEREST = "0.10";
    protected static final BigDecimal PRINCIPAL_AMOUNT_BD = new BigDecimal(PRINCIPAL_AMOUNT);
    protected static final BigDecimal ANNUAL_INTEREST_BD = new BigDecimal(ANNUAL_INTEREST);

    protected static final SystemConfig PRINCIPAL_CONFIG = SystemConfig.builder().value(PRINCIPAL_AMOUNT).build();
    protected static final SystemConfig INTEREST_CONFIG = SystemConfig.builder().value(ANNUAL_INTEREST).build();

    protected static final Customer EXISTING_CUSTOMER = Customer.builder()
            .id(CUSTOMER_ID)
            .fullName("Test")
            .email("test@email.com")
            .build();

    protected static final CustomerServiceRequest NEW_CUSTOMER = CustomerServiceRequest.builder()
            .fullName("Test")
            .email("test@email.com")
            .build();

    protected static final LoanServiceRequest LOAN_REQUEST_EXISTING_CUSTOMER = LoanServiceRequest.builder()
            .customer(CustomerServiceRequest.builder().id(CUSTOMER_ID).build())
            .amount(PRINCIPAL_AMOUNT_BD)
            .build();

    protected static final LoanServiceRequest LOAN_REQUEST_NEW_CUSTOMER = LoanServiceRequest.builder()
            .customer(NEW_CUSTOMER)
            .amount(PRINCIPAL_AMOUNT_BD)
            .build();
    protected static final LoanServiceRequest LOAN_REQUEST_NEW_CUSTOMER_NO_PRINCIPAL = LoanServiceRequest.builder()
            .customer(NEW_CUSTOMER)
            .build();

    protected static final RepaymentSchedule PENDING_SCHEDULE = RepaymentSchedule.builder()
            .status(PaymentStatus.PENDING)
            .amount(new BigDecimal("110000"))
            .dueDate(LocalDate.now().minusDays(1))
            .build();

    protected static final RepaymentSchedule PAID_SCHEDULE = RepaymentSchedule.builder()
            .status(PaymentStatus.PAID)
            .amount(new BigDecimal("110000"))
            .dueDate(LocalDate.now().minusWeeks(2))
            .build();

    protected static final Loan LOAN_WITH_NO_SCHEDULE = Loan.builder()
            .id(LOAN_ID)
            .repaymentSchedules(new ArrayList<>())
            .build();

    protected static final Loan LOAN_DELINQUENT = Loan.builder()
            .id(LOAN_ID)
            .repaymentSchedules(new ArrayList<>(List.of(
                    RepaymentSchedule.builder()
                            .status(PaymentStatus.PENDING)
                            .dueDate(LocalDate.now().minusWeeks(2))
                            .build(),
                    RepaymentSchedule.builder()
                            .status(PaymentStatus.PENDING)
                            .dueDate(LocalDate.now().minusWeeks(1))
                            .build()
            )))
            .build();

    protected static final Loan LOAN_NOT_DELINQUENT = Loan.builder()
            .id(LOAN_ID)
            .repaymentSchedules(new ArrayList<>(List.of(
                    PAID_SCHEDULE,
                    RepaymentSchedule.builder()
                            .status(PaymentStatus.PENDING)
                            .dueDate(LocalDate.now().plusWeeks(1))
                            .build()
            )))
            .build();

    protected Loan generateLoanWithPendingSchedule() {
        return Loan.builder()
                .id(LOAN_ID)
                .repaymentSchedules(List.of(RepaymentSchedule.builder()
                        .status(PaymentStatus.PENDING)
                        .amount(new BigDecimal("110000"))
                        .dueDate(LocalDate.now().minusDays(1))
                        .build()))
                .build();
    }
}