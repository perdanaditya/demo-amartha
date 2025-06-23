package com.amartha.billing.service.impl;

import com.amartha.billing.constant.ErrorConstant;
import com.amartha.billing.constant.SystemConfigEnum;
import com.amartha.billing.entity.SystemConfig;
import com.amartha.billing.exception.AppException;
import com.amartha.billing.repository.SystemConfigRepository;
import com.amartha.billing.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    @Override
    public BigDecimal getAnnualInterestRate() {
        return getConfig(SystemConfigEnum.ANNUAL_INTEREST_RATE)
                .map(systemConfig ->
                        new BigDecimal(systemConfig.getValue()))
                .orElseThrow(() ->
                        new AppException(ErrorConstant.ANNUAL_INTEREST_RATES_NOT_YET_AVAILABLE));
    }

    @Override
    public BigDecimal getPrincipalAmount() {
        return getConfig(SystemConfigEnum.PRINCIPAL_AMOUNT)
                .map(systemConfig ->
                        new BigDecimal(systemConfig.getValue()))
                .orElseThrow(() ->
                        new AppException(ErrorConstant.PRINCIPAL_AMOUNT_NOT_AVAILABLE));
    }

    private Optional<SystemConfig> getConfig(SystemConfigEnum configEnum) {
        return systemConfigRepository.findByCode(configEnum.name());
    }
}
