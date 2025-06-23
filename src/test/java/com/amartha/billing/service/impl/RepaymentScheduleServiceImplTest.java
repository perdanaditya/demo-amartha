package com.amartha.billing.service.impl;

import com.amartha.billing.constant.PaymentStatus;
import com.amartha.billing.entity.RepaymentSchedule;
import com.amartha.billing.repository.RepaymentScheduleRepository;
import com.amartha.billing.service.impl.variable.RepaymentScheduleServiceImplTestVariable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class RepaymentScheduleServiceImplTest extends RepaymentScheduleServiceImplTestVariable {

    @InjectMocks
    private RepaymentScheduleServiceImpl repaymentScheduleService;
    @Mock
    private RepaymentScheduleRepository repaymentScheduleRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        verifyNoMoreInteractions(repaymentScheduleRepository);
        closeable.close();
    }

    @Test
    void populateRepaymentSchedules_returnsCorrectList() {
        List<RepaymentSchedule> schedules = repaymentScheduleService
                .populateRepaymentSchedules(LOAN, PRINCIPAL, ANNUAL_INTEREST);
        assertEquals(50, schedules.size());
        assertTrue(schedules
                .stream()
                .allMatch(s -> s.getAmount().compareTo(WEEKLY_INSTALLMENT) == 0));
        assertTrue(schedules
                .stream()
                .allMatch(s -> s.getStatus() == PaymentStatus.PENDING));
        assertEquals(1, schedules.get(0).getWeek());
        assertEquals(50, schedules.get(49).getWeek());
    }

    @Test
    void saveAll_marksDuePendingAsPaid_andSaves() {
        when(repaymentScheduleRepository.saveAll(anyList()))
                .thenAnswer(inv -> inv.getArgument(0));

        List<RepaymentSchedule> result = repaymentScheduleService.saveAll(ALL_PENDING_DUE);

        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(s -> s.getStatus() == PaymentStatus.PAID));
        verify(repaymentScheduleRepository, times(1))
                .saveAll(anyList());
    }

    @Test
    void saveAll_ignoresAlreadyPaid_andFutureDueDates() {
        when(repaymentScheduleRepository.saveAll(anyList()))
                .thenAnswer(inv -> inv.getArgument(0));

        List<RepaymentSchedule> result = repaymentScheduleService.saveAll(MIXED);

        // Only the second schedule is pending and due
        assertEquals(1, result.size());
        assertEquals(PaymentStatus.PAID, result.get(0).getStatus());
        assertEquals(2, result.get(0).getWeek());
        verify(repaymentScheduleRepository).saveAll(anyList());
    }

    @Test
    void saveAll_emptyList_returnsEmpty() {
        List<RepaymentSchedule> result = repaymentScheduleService.saveAll(EMPTY);
        assertTrue(result.isEmpty());
        verify(repaymentScheduleRepository).saveAll(EMPTY);
    }

    @Test
    void saveAll_allPaid_returnsEmpty() {
        List<RepaymentSchedule> result = repaymentScheduleService.saveAll(ALL_PAID);
        assertTrue(result.isEmpty());
        verify(repaymentScheduleRepository).saveAll(List.of());
    }
}