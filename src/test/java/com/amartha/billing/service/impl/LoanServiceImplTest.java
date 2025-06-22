package com.amartha.billing.service.impl;

import com.amartha.billing.constant.ErrorConstant;
import com.amartha.billing.entity.Customer;
import com.amartha.billing.entity.Loan;
import com.amartha.billing.exception.AppException;
import com.amartha.billing.repository.CustomerRepository;
import com.amartha.billing.repository.LoanRepository;
import com.amartha.billing.repository.RepaymentScheduleRepository;
import com.amartha.billing.repository.SystemConfigRepository;
import com.amartha.billing.service.impl.variable.LoanServiceImplTestVariable;
import com.amartha.billing.util.JSONHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class LoanServiceImplTest extends LoanServiceImplTestVariable {

    @InjectMocks
    private LoanServiceImpl loanService;
    @Mock
    private SystemConfigRepository systemConfigRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private RepaymentScheduleRepository repaymentScheduleRepository;
    @Mock
    private JSONHelper jsonHelper;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        verifyNoMoreInteractions(systemConfigRepository);
        verifyNoMoreInteractions(customerRepository);
        verifyNoMoreInteractions(loanRepository);
        verifyNoMoreInteractions(repaymentScheduleRepository);
        closeable.close();
    }

    @Test
    void createLoan_success_existingCustomer() {
        when(systemConfigRepository.findByCode("ANNUAL_INTEREST_RATE"))
                .thenReturn(Optional.of(INTEREST_CONFIG));
        when(customerRepository.findById(CUSTOMER_ID))
                .thenReturn(Optional.of(EXISTING_CUSTOMER));
        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(inv -> {
                    Loan l = inv.getArgument(0);
                    l.setId(LOAN_ID);
                    return l;
                });

        UUID result = loanService.createLoan(LOAN_REQUEST_EXISTING_CUSTOMER);

        assertEquals(LOAN_ID, result);
        verify(systemConfigRepository).findByCode("ANNUAL_INTEREST_RATE");
        verify(customerRepository).findById(CUSTOMER_ID);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void createLoan_success_newCustomer_withPrincipal() {
        when(systemConfigRepository.findByCode("ANNUAL_INTEREST_RATE"))
                .thenReturn(Optional.of(INTEREST_CONFIG));
        when(customerRepository.save(any(Customer.class))).thenReturn(EXISTING_CUSTOMER);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> {
            Loan l = inv.getArgument(0);
            l.setId(LOAN_ID);
            return l;
        });

        UUID result = loanService.createLoan(LOAN_REQUEST_NEW_CUSTOMER);

        assertEquals(LOAN_ID, result);
        verify(systemConfigRepository).findByCode("ANNUAL_INTEREST_RATE");
        verify(customerRepository).save(any(Customer.class));
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void createLoan_success_newCustomer_withoutPrincipal() {
        when(systemConfigRepository.findByCode("ANNUAL_INTEREST_RATE"))
                .thenReturn(Optional.of(INTEREST_CONFIG));
        when(systemConfigRepository.findByCode("PRINCIPAL_AMOUNT"))
                .thenReturn(Optional.of(PRINCIPAL_CONFIG));
        when(customerRepository.save(any(Customer.class))).thenReturn(EXISTING_CUSTOMER);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> {
            Loan l = inv.getArgument(0);
            l.setId(LOAN_ID);
            return l;
        });

        UUID result = loanService.createLoan(LOAN_REQUEST_NEW_CUSTOMER_NO_PRINCIPAL);

        assertEquals(LOAN_ID, result);
        verify(systemConfigRepository).findByCode("ANNUAL_INTEREST_RATE");
        verify(systemConfigRepository).findByCode("PRINCIPAL_AMOUNT");
        verify(customerRepository).save(any(Customer.class));
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void createLoan_fail_missingInterestConfig() {
        when(systemConfigRepository.findByCode("ANNUAL_INTEREST_RATE"))
                .thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> loanService.createLoan(LOAN_REQUEST_EXISTING_CUSTOMER));
        assertEquals(ErrorConstant.ANNUAL_INTEREST_RATES_NOT_YET_AVAILABLE, ex.getError());
        verify(systemConfigRepository).findByCode("ANNUAL_INTEREST_RATE");
    }

    @Test
    void makePayment_success() {
        Loan loan = generateLoanWithPendingSchedule();
        when(loanRepository.findByIdWithRepaymentSchedules(LOAN_ID))
                .thenReturn(Optional.of(loan));
        when(repaymentScheduleRepository.saveAll(anyList()))
                .thenReturn(List.of(PENDING_SCHEDULE));

        loanService.makePayment(LOAN_ID, new BigDecimal("110000"));

        verify(loanRepository).findByIdWithRepaymentSchedules(LOAN_ID);
        verify(repaymentScheduleRepository).saveAll(anyList());
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

    @Test
    void getOutstanding_success() {
        when(loanRepository.findOutstandingAmountByLoanId(LOAN_ID))
                .thenReturn(new BigDecimal("5500000"));
        BigDecimal result = loanService.getOutstanding(LOAN_ID);
        assertEquals(new BigDecimal("5500000"), result);
        verify(loanRepository).findOutstandingAmountByLoanId(LOAN_ID);
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
}