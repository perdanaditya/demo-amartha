package com.amartha.billing.service;

import com.amartha.billing.constant.SystemConfigEnum;

import java.math.BigDecimal;

public interface SystemConfigService {
    BigDecimal getAnnualInterestRate();
    BigDecimal getPrincipalAmount();
}
