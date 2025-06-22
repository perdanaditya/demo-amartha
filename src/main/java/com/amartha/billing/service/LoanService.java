package com.amartha.billing.service;

import com.amartha.billing.service.model.LoanServiceRequest;

import java.math.BigDecimal;
import java.util.UUID;

public interface LoanService {

    UUID createLoan(LoanServiceRequest request);

    boolean isDelinquent(UUID id);

    BigDecimal getOutstanding(UUID id);

    void makePayment(UUID loanId, BigDecimal paidAmount);
}
