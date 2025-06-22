package com.amartha.billing.service.impl;

import com.amartha.billing.constant.ErrorConstant;
import com.amartha.billing.constant.PaymentStatus;
import com.amartha.billing.entity.Customer;
import com.amartha.billing.entity.Loan;
import com.amartha.billing.entity.RepaymentSchedule;
import com.amartha.billing.entity.SystemConfig;
import com.amartha.billing.exception.AppException;
import com.amartha.billing.repository.CustomerRepository;
import com.amartha.billing.repository.LoanRepository;
import com.amartha.billing.repository.RepaymentScheduleRepository;
import com.amartha.billing.repository.SystemConfigRepository;
import com.amartha.billing.service.LoanService;
import com.amartha.billing.service.model.LoanServiceRequest;
import com.amartha.billing.util.JSONHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static com.amartha.billing.constant.SystemConfigEnum.ANNUAL_INTEREST_RATE;
import static com.amartha.billing.constant.SystemConfigEnum.PRINCIPAL_AMOUNT;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanServiceImpl implements LoanService {

    private final SystemConfigRepository systemConfigRepository;

    private final CustomerRepository customerRepository;

    private final LoanRepository loanRepository;

    private final RepaymentScheduleRepository repaymentScheduleRepository;

    private final JSONHelper jsonHelper;

    @Transactional
    @Override
    public UUID createLoan(LoanServiceRequest request) {
        BigDecimal annualInterestRate = getAnnualInterestRate();
        BigDecimal principalAmount = Objects.nonNull(request.getAmount()) ? request.getAmount() : getPrincipalAmount();
        Customer customer = this.getCustomer(request);
        Loan loan = Loan.builder()
                .customer(customer)
                .principalAmount(principalAmount)
                .annualInterestRate(annualInterestRate)
                .repaymentSchedules(populateRepaymentSchedules(principalAmount, annualInterestRate))
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
                .filter(repaymentSchedule -> PaymentStatus.PENDING.equals(repaymentSchedule.getStatus()) && !repaymentSchedule.getDueDate().isAfter(LocalDate.now()))
                .map(RepaymentSchedule::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (amountToPaid.compareTo(BigDecimal.ZERO) == 0) {
            throw new AppException(ErrorConstant.NO_REPAYMENT_SCHEDULES_FOUND);
        }

        if (paidAmount.compareTo(amountToPaid) != 0) {
            throw new AppException(ErrorConstant.PAID_AMOUNT_NOT_EQUAL_WITH_CURRENT_REPAYMENT_AMOUNT, String.format(ErrorConstant.PAID_AMOUNT_NOT_EQUAL_WITH_CURRENT_REPAYMENT_AMOUNT.getMessage(), paidAmount, amountToPaid));
        }

        List<RepaymentSchedule> loanToPaid = loan.getRepaymentSchedules()
                .stream()
                .filter(repaymentSchedule -> PaymentStatus.PENDING.equals(repaymentSchedule.getStatus()) && !repaymentSchedule.getDueDate().isAfter(LocalDate.now()))
                .peek(repaymentSchedule -> repaymentSchedule.setStatus(PaymentStatus.PAID))
                .toList();

        repaymentScheduleRepository.saveAll(loanToPaid);
    }

    private List<RepaymentSchedule> populateRepaymentSchedules(BigDecimal principalAmount, BigDecimal annualInterestRate) {
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
                    .build();
            repaymentSchedules.add(schedule);
        }

        return repaymentSchedules;
    }

    private BigDecimal getAnnualInterestRate() {
        SystemConfig annualInterestRates = systemConfigRepository.findByCode(ANNUAL_INTEREST_RATE.name())
                .orElseThrow(() -> new AppException(ErrorConstant.ANNUAL_INTEREST_RATES_NOT_YET_AVAILABLE));
        return new BigDecimal(annualInterestRates.getValue());
    }

    private BigDecimal getPrincipalAmount() {
        SystemConfig annualInterestRates = systemConfigRepository.findByCode(PRINCIPAL_AMOUNT.name())
                .orElseThrow(() -> new AppException(ErrorConstant.ANNUAL_INTEREST_RATES_NOT_YET_AVAILABLE));
        return new BigDecimal(annualInterestRates.getValue());
    }

    private Customer getCustomer(LoanServiceRequest request) {
        if (Objects.isNull(request.getCustomer().getId())) {
            return customerRepository.save(Customer.builder().fullName(request.getCustomer().getFullName()).email(request.getCustomer().getEmail()).build());
        }
        return customerRepository.findById(request.getCustomer().getId()).orElseThrow(() -> new AppException(ErrorConstant.CUSTOMER_NOT_FOUND));
    }
}
