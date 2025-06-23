package com.amartha.billing.service.impl.variable;

import com.amartha.billing.constant.SystemConfigEnum;
import com.amartha.billing.entity.SystemConfig;

import java.math.BigDecimal;

public class SystemConfigServiceImplTestVariable {
    protected static final String ANNUAL_INTEREST_RATE_VALUE = "0.12";
    protected static final String PRINCIPAL_AMOUNT_VALUE = "1000000";
    protected static final BigDecimal ANNUAL_INTEREST_RATE = new BigDecimal(ANNUAL_INTEREST_RATE_VALUE);
    protected static final BigDecimal PRINCIPAL_AMOUNT = new BigDecimal(PRINCIPAL_AMOUNT_VALUE);

    protected static final SystemConfig ANNUAL_INTEREST_RATE_CONFIG = SystemConfig.builder()
            .code(SystemConfigEnum.ANNUAL_INTEREST_RATE.name())
            .value(ANNUAL_INTEREST_RATE_VALUE)
            .build();

    protected static final SystemConfig PRINCIPAL_AMOUNT_CONFIG = SystemConfig.builder()
            .code(SystemConfigEnum.PRINCIPAL_AMOUNT.name())
            .value(PRINCIPAL_AMOUNT_VALUE)
            .build();
}