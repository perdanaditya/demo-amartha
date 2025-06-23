package com.amartha.billing.service.impl;

import com.amartha.billing.constant.ErrorConstant;
import com.amartha.billing.entity.Loan;
import com.amartha.billing.exception.AppException;
import com.amartha.billing.repository.LoanRepository;
import com.amartha.billing.service.CustomerService;
import com.amartha.billing.service.RepaymentScheduleService;
import com.amartha.billing.service.SystemConfigService;
import com.amartha.billing.service.impl.variable.LoanServiceImplTestVariable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class LoanServiceImplTest extends LoanServiceImplTestVariable {

    @InjectMocks
    private LoanServiceImpl loanService;
    @Mock
    private SystemConfigService systemConfigService;
    @Mock
    private CustomerService customerService;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private RepaymentScheduleService repaymentScheduleService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        verifyNoMoreInteractions(systemConfigService);
        verifyNoMoreInteractions(customerService);
        verifyNoMoreInteractions(loanRepository);
        verifyNoMoreInteractions(repaymentScheduleService);
        closeable.close();
    }

    @Test
    void createLoan_success_existingCustomer() {
        when(systemConfigService.getAnnualInterestRate()).thenReturn(ANNUAL_INTEREST_RATE);
        when(customerService.saveOrGet(EXISTING_CUSTOMER_REQUEST)).thenReturn(EXISTING_CUSTOMER);
        when(repaymentScheduleService.populateRepaymentSchedules(any(), any(), any()))
                .thenReturn(REPAYMENT_SCHEDULES);
        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(inv -> {
                    Loan l = inv.getArgument(0);
                    l.setId(LOAN_ID);
                    return l;
                });

        UUID result = loanService.createLoan(LOAN_REQUEST_EXISTING_CUSTOMER);

        assertEquals(LOAN_ID, result);
        verify(systemConfigService).getAnnualInterestRate();
        verify(customerService).saveOrGet(EXISTING_CUSTOMER_REQUEST);
        verify(repaymentScheduleService).populateRepaymentSchedules(any(), any(), any());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void createLoan_success_newCustomer_withPrincipal() {
        when(systemConfigService.getAnnualInterestRate()).thenReturn(ANNUAL_INTEREST_RATE);
        when(customerService.saveOrGet(NEW_CUSTOMER_REQUEST)).thenReturn(EXISTING_CUSTOMER);
        when(repaymentScheduleService.populateRepaymentSchedules(any(), any(), any()))
                .thenReturn(REPAYMENT_SCHEDULES);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> {
            Loan l = inv.getArgument(0);
            l.setId(LOAN_ID);
            return l;
        });

        UUID result = loanService.createLoan(LOAN_REQUEST_NEW_CUSTOMER);

        assertEquals(LOAN_ID, result);
        verify(systemConfigService).getAnnualInterestRate();
        verify(customerService).saveOrGet(NEW_CUSTOMER_REQUEST);
        verify(repaymentScheduleService).populateRepaymentSchedules(any(), any(), any());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void createLoan_success_newCustomer_withoutPrincipal() {
        when(systemConfigService.getAnnualInterestRate()).thenReturn(ANNUAL_INTEREST_RATE);
        when(systemConfigService.getPrincipalAmount()).thenReturn(PRINCIPAL_AMOUNT);
        when(customerService.saveOrGet(NEW_CUSTOMER_REQUEST)).thenReturn(EXISTING_CUSTOMER);
        when(repaymentScheduleService.populateRepaymentSchedules(any(), any(), any()))
                .thenReturn(REPAYMENT_SCHEDULES);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> {
            Loan l = inv.getArgument(0);
            l.setId(LOAN_ID);
            return l;
        });

        UUID result = loanService.createLoan(LOAN_REQUEST_NEW_CUSTOMER_NO_PRINCIPAL);

        assertEquals(LOAN_ID, result);
        verify(systemConfigService).getAnnualInterestRate();
        verify(systemConfigService).getPrincipalAmount();
        verify(customerService).saveOrGet(NEW_CUSTOMER_REQUEST);
        verify(repaymentScheduleService).populateRepaymentSchedules(any(), any(), any());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void isDelinquent_true() {
        when(loanRepository.findByIdWithRepaymentSchedules(LOAN_ID))
                .thenReturn(Optional.of(LOAN_DELINQUENT));
        boolean result = loanService.isDelinquent(LOAN_ID);
        assertTrue(result);
        verify(loanRepository).findByIdWithRepaymentSchedules(LOAN_ID);
    }

    @Test
    void isDelinquent_false() {
        when(loanRepository.findByIdWithRepaymentSchedules(LOAN_ID))
                .thenReturn(Optional.of(LOAN_NOT_DELINQUENT));
        boolean result = loanService.isDelinquent(LOAN_ID);
        assertFalse(result);
        verify(loanRepository).findByIdWithRepaymentSchedules(LOAN_ID);
    }

    @Test
    void getOutstanding_success() {
        when(loanRepository.findOutstandingAmountByLoanId(LOAN_ID))
                .thenReturn(new BigDecimal("5500000"));
        BigDecimal result = loanService.getOutstanding(LOAN_ID);
        assertEquals(new BigDecimal("5500000"), result);
        verify(loanRepository).findOutstandingAmountByLoanId(LOAN_ID);
    }

    @Test
    void makePayment_success() {
        Loan loan = generateLoanWithPendingSchedule();
        when(loanRepository.findByIdWithRepaymentSchedules(LOAN_ID))
                .thenReturn(Optional.of(loan));
        when(repaymentScheduleService.saveAll(loan.getRepaymentSchedules()))
                .thenReturn(loan.getRepaymentSchedules());

        loanService.makePayment(LOAN_ID, new BigDecimal("110000"));

        verify(loanRepository).findByIdWithRepaymentSchedules(LOAN_ID);
        verify(repaymentScheduleService).saveAll(loan.getRepaymentSchedules());
    }

    @Test
    void makePayment_fail_noRepaymentSchedules() {
        when(loanRepository.findByIdWithRepaymentSchedules(LOAN_ID))
                .thenReturn(Optional.of(LOAN_WITH_NO_SCHEDULE));

        AppException ex = assertThrows(AppException.class, () ->
                loanService.makePayment(LOAN_ID, new BigDecimal("110000"))
        );
        assertEquals(ErrorConstant.NO_REPAYMENT_SCHEDULES_FOUND, ex.getError());
        verify(loanRepository).findByIdWithRepaymentSchedules(LOAN_ID);
    }

    @Test
    void makePayment_fail_wrongAmount() {
        Loan loan = generateLoanWithPendingSchedule();
        when(loanRepository.findByIdWithRepaymentSchedules(LOAN_ID))
                .thenReturn(Optional.of(loan));

        AppException ex = assertThrows(AppException.class, () ->
                loanService.makePayment(LOAN_ID, new BigDecimal("100000"))
        );
        assertEquals(ErrorConstant.PAID_AMOUNT_NOT_EQUAL_WITH_CURRENT_REPAYMENT_AMOUNT,
                ex.getError());
        verify(loanRepository).findByIdWithRepaymentSchedules(LOAN_ID);
    }
}