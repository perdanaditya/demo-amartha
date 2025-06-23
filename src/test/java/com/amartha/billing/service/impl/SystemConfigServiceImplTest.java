package com.amartha.billing.service.impl;

import com.amartha.billing.constant.ErrorConstant;
import com.amartha.billing.constant.SystemConfigEnum;
import com.amartha.billing.exception.AppException;
import com.amartha.billing.repository.SystemConfigRepository;
import com.amartha.billing.service.impl.variable.SystemConfigServiceImplTestVariable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class SystemConfigServiceImplTest extends SystemConfigServiceImplTestVariable {

    @InjectMocks
    private SystemConfigServiceImpl systemConfigService;
    @Mock
    private SystemConfigRepository systemConfigRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        verifyNoMoreInteractions(systemConfigRepository);
        closeable.close();
    }

    @Test
    void getAnnualInterestRate_success() {
        when(systemConfigRepository.findByCode(SystemConfigEnum.ANNUAL_INTEREST_RATE.name()))
                .thenReturn(Optional.of(ANNUAL_INTEREST_RATE_CONFIG));

        assertEquals(ANNUAL_INTEREST_RATE, systemConfigService.getAnnualInterestRate());
        verify(systemConfigRepository).findByCode(SystemConfigEnum.ANNUAL_INTEREST_RATE.name());
    }

    @Test
    void getAnnualInterestRate_notAvailable_throwsException() {
        when(systemConfigRepository
                .findByCode(SystemConfigEnum.ANNUAL_INTEREST_RATE.name()))
                .thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () ->
                systemConfigService.getAnnualInterestRate());
        assertEquals(ErrorConstant.ANNUAL_INTEREST_RATES_NOT_YET_AVAILABLE, ex.getError());
        verify(systemConfigRepository)
                .findByCode(SystemConfigEnum.ANNUAL_INTEREST_RATE.name());
    }

    @Test
    void getPrincipalAmount_success() {
        when(systemConfigRepository
                .findByCode(SystemConfigEnum.PRINCIPAL_AMOUNT.name()))
                .thenReturn(Optional.of(PRINCIPAL_AMOUNT_CONFIG));

        assertEquals(PRINCIPAL_AMOUNT, systemConfigService.getPrincipalAmount());
        verify(systemConfigRepository)
                .findByCode(SystemConfigEnum.PRINCIPAL_AMOUNT.name());
    }

    @Test
    void getPrincipalAmount_notAvailable_throwsException() {
        when(systemConfigRepository.findByCode(SystemConfigEnum.PRINCIPAL_AMOUNT.name()))
                .thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () ->
                systemConfigService.getPrincipalAmount());
        assertEquals(ErrorConstant.PRINCIPAL_AMOUNT_NOT_AVAILABLE, ex.getError());
        verify(systemConfigRepository)
                .findByCode(SystemConfigEnum.PRINCIPAL_AMOUNT.name());
    }
}