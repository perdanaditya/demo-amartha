package com.amartha.billing.service.impl;

import com.amartha.billing.constant.PaymentStatus;
import com.amartha.billing.entity.Loan;
import com.amartha.billing.entity.RepaymentSchedule;
import com.amartha.billing.repository.RepaymentScheduleRepository;
import com.amartha.billing.service.RepaymentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepaymentScheduleServiceImpl implements RepaymentScheduleService {

    private final RepaymentScheduleRepository repaymentScheduleRepository;

    @Override
    public List<RepaymentSchedule> populateRepaymentSchedules(Loan loan, BigDecimal principalAmount, BigDecimal annualInterestRate) {
        List<RepaymentSchedule> repaymentSchedules = new ArrayList<>();

        BigDecimal totalInterest = principalAmount.multiply(annualInterestRate);
        BigDecimal totalPayable = principalAmount.add(totalInterest);

        BigDecimal weeklyInstallment = totalPayable.divide(BigDecimal.valueOf(50), 0, RoundingMode.UP);

        for (int week = 1; week <= 50; week++) {
            RepaymentSchedule schedule = RepaymentSchedule.builder()
                    .week(week)
                    .amount(weeklyInstallment)
                    .status(PaymentStatus.PENDING)
                    .dueDate(LocalDate.now().plusWeeks(week))
                    .loan(loan)
                    .build();
            repaymentSchedules.add(schedule);
        }

        return repaymentSchedules;
    }

    @Override
    public List<RepaymentSchedule> saveAll(List<RepaymentSchedule> repaymentSchedules) {
        List<RepaymentSchedule> loanToPaid = repaymentSchedules
                .stream()
                .filter(repaymentSchedule ->
                        PaymentStatus.PENDING.equals(repaymentSchedule.getStatus())
                                && !repaymentSchedule.getDueDate().isAfter(LocalDate.now()))
                .peek(repaymentSchedule ->
                        repaymentSchedule.setStatus(PaymentStatus.PAID))
                .toList();

        return repaymentScheduleRepository.saveAll(loanToPaid);
    }
}
