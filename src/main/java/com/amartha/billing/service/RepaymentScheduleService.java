package com.amartha.billing.service;

import com.amartha.billing.entity.Loan;
import com.amartha.billing.entity.RepaymentSchedule;

import java.math.BigDecimal;
import java.util.List;

public interface RepaymentScheduleService {
    List<RepaymentSchedule> populateRepaymentSchedules(Loan loan, BigDecimal principalAmount, BigDecimal annualInterestRate);
    List<RepaymentSchedule> saveAll(List<RepaymentSchedule> repaymentSchedules);
}
