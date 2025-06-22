package com.amartha.billing.inbound.variable;

import com.amartha.billing.inbound.model.CreateLoanRequest;
import com.amartha.billing.inbound.model.CustomerRequest;
import com.amartha.billing.inbound.model.MakePaymentRequest;

import java.math.BigDecimal;
import java.util.UUID;

public class LoanV1ControllerTestVariable {

    protected static final UUID MOCK_UUID = UUID.randomUUID();
    protected static final CreateLoanRequest LOAN_REQUEST = CreateLoanRequest.builder()
            .customer(CustomerRequest.builder()
                    .id(MOCK_UUID)
                    .email("mail@example.com")
                    .fullName("John Doe")
                    .build())
            .amount(new BigDecimal(5000000))
            .build();
    protected static final MakePaymentRequest PAYMENT_REQUEST = MakePaymentRequest.builder()
            .paidAmount(new BigDecimal(110000))
            .build();
}
