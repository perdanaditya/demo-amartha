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
import java.util.*;

public class LoanServiceImplTestVariable {
    protected static final BigDecimal ANNUAL_INTEREST_RATE = new BigDecimal("0.12");
    protected static final BigDecimal PRINCIPAL_AMOUNT = new BigDecimal("1000000");
    protected static final UUID LOAN_ID = UUID.randomUUID();

    protected static final Customer EXISTING_CUSTOMER = Customer.builder()
            .id(UUID.randomUUID())
            .fullName("Existing Customer")
            .build();

    protected static final Customer NEW_CUSTOMER = Customer.builder()
            .id(UUID.randomUUID())
            .fullName("New Customer")
            .build();

    protected static final CustomerServiceRequest EXISTING_CUSTOMER_REQUEST = CustomerServiceRequest.builder()
            .fullName("Existing Customer")
            .build();

    protected static final CustomerServiceRequest NEW_CUSTOMER_REQUEST = CustomerServiceRequest.builder()
            .fullName("New Customer")
            .build();

    protected static final LoanServiceRequest LOAN_REQUEST_EXISTING_CUSTOMER = LoanServiceRequest.builder()
            .customer(EXISTING_CUSTOMER_REQUEST)
            .amount(PRINCIPAL_AMOUNT)
            .build();

    protected static final LoanServiceRequest LOAN_REQUEST_NEW_CUSTOMER = LoanServiceRequest.builder()
            .customer(NEW_CUSTOMER_REQUEST)
            .amount(PRINCIPAL_AMOUNT)
            .build();

    protected static final LoanServiceRequest LOAN_REQUEST_NEW_CUSTOMER_NO_PRINCIPAL = LoanServiceRequest.builder()
            .customer(NEW_CUSTOMER_REQUEST)
            .amount(null)
            .build();

    protected static final List<RepaymentSchedule> REPAYMENT_SCHEDULES = Arrays.asList(
            RepaymentSchedule.builder()
                    .id(UUID.randomUUID())
                    .amount(new BigDecimal("110000"))
                    .dueDate(LocalDate.now().minusDays(1))
                    .status(PaymentStatus.PENDING)
                    .build()
    );

    protected static final Loan LOAN_DELINQUENT = Loan.builder()
            .id(LOAN_ID)
            .repaymentSchedules(Arrays.asList(
                    RepaymentSchedule.builder()
                            .dueDate(LocalDate.now().minusDays(10))
                            .status(PaymentStatus.PENDING)
                            .build(),
                    RepaymentSchedule.builder()
                            .dueDate(LocalDate.now().minusDays(5))
                            .status(PaymentStatus.PENDING)
                            .build()
            ))
            .build();

    protected static final Loan LOAN_NOT_DELINQUENT = Loan.builder()
            .id(LOAN_ID)
            .repaymentSchedules(Arrays.asList(
                    RepaymentSchedule.builder()
                            .dueDate(LocalDate.now().minusDays(10))
                            .status(PaymentStatus.PAID)
                            .build(),
                    RepaymentSchedule.builder()
                            .dueDate(LocalDate.now().minusDays(5))
                            .status(PaymentStatus.PENDING)
                            .build()
            ))
            .build();

    protected static final Loan LOAN_WITH_NO_SCHEDULE = Loan.builder()
            .id(LOAN_ID)
            .repaymentSchedules(Collections.emptyList())
            .build();

    protected Loan generateLoanWithPendingSchedule() {
        return Loan.builder()
                .id(LOAN_ID)
                .repaymentSchedules(new ArrayList<>(REPAYMENT_SCHEDULES))
                .build();
    }
}