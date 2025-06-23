package com.amartha.billing.service.impl;

import com.amartha.billing.constant.ErrorConstant;
import com.amartha.billing.constant.PaymentStatus;
import com.amartha.billing.entity.Customer;
import com.amartha.billing.entity.Loan;
import com.amartha.billing.entity.RepaymentSchedule;
import com.amartha.billing.exception.AppException;
import com.amartha.billing.repository.LoanRepository;
import com.amartha.billing.service.CustomerService;
import com.amartha.billing.service.LoanService;
import com.amartha.billing.service.RepaymentScheduleService;
import com.amartha.billing.service.SystemConfigService;
import com.amartha.billing.service.model.LoanServiceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanServiceImpl implements LoanService {

    private final SystemConfigService systemConfigService;

    private final CustomerService customerService;

    private final RepaymentScheduleService repaymentScheduleService;

    private final LoanRepository loanRepository;

    @Transactional
    @Override
    public UUID createLoan(LoanServiceRequest request) {
        BigDecimal annualInterestRate = systemConfigService.getAnnualInterestRate();
        BigDecimal principalAmount = Objects.nonNull(
                request.getAmount()) ? request.getAmount() : systemConfigService.getPrincipalAmount();
        Customer customer = customerService.saveOrGet(request.getCustomer());
        Loan loan = Loan.builder()
                .customer(customer)
                .principalAmount(principalAmount)
                .annualInterestRate(annualInterestRate)
                .repaymentSchedules(repaymentScheduleService.populateRepaymentSchedules(principalAmount, annualInterestRate))
                .build();
        return loanRepository.save(loan).getId();
    }

    @Override
    public boolean isDelinquent(UUID id) {
        Loan loan = loanRepository.findByIdWithRepaymentSchedules(id)
                .orElseThrow(() -> new AppException(ErrorConstant.LOAN_NOT_FOUND));

        List<RepaymentSchedule> schedules = loan.getRepaymentSchedules();
        schedules.sort(Comparator.comparing(RepaymentSchedule::getDueDate));

        int unpaidStreak = 0;
        for (RepaymentSchedule schedule : schedules) {
            if (!PaymentStatus.PAID.equals(schedule.getStatus())
                    && schedule.getDueDate().isBefore(LocalDate.now())) {
                unpaidStreak++;
                if (unpaidStreak >= 2) {
                    return true;
                }
            } else {
                unpaidStreak = 0;
            }
        }
        return false;
    }

    @Override
    public BigDecimal getOutstanding(UUID id) {
        return loanRepository.findOutstandingAmountByLoanId(id);
    }

    @Transactional
    @Override
    public void makePayment(UUID loanId, BigDecimal paidAmount) {
        Loan loan = loanRepository.findByIdWithRepaymentSchedules(loanId)
                .orElseThrow(() -> new AppException(ErrorConstant.LOAN_NOT_FOUND));

        BigDecimal amountToPaid = loan.getRepaymentSchedules()
                .stream()
                .filter(repaymentSchedule ->
                        PaymentStatus.PENDING.equals(repaymentSchedule.getStatus())
                                && !repaymentSchedule.getDueDate().isAfter(LocalDate.now()))
                .map(RepaymentSchedule::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (amountToPaid.compareTo(BigDecimal.ZERO) == 0) {
            throw new AppException(ErrorConstant.NO_REPAYMENT_SCHEDULES_FOUND);
        }

        if (paidAmount.compareTo(amountToPaid) != 0) {
            throw new AppException(
                    ErrorConstant.PAID_AMOUNT_NOT_EQUAL_WITH_CURRENT_REPAYMENT_AMOUNT,
                    String.format(ErrorConstant.PAID_AMOUNT_NOT_EQUAL_WITH_CURRENT_REPAYMENT_AMOUNT.getMessage(), paidAmount, amountToPaid));
        }
        repaymentScheduleService.saveAll(loan.getRepaymentSchedules());
    }
}
