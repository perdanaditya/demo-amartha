package com.amartha.billing.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanServiceRequest implements Serializable {

    private CustomerServiceRequest customer;

    /**
     * I added this amount in case the value can be dynamic
     */
    private BigDecimal amount;
}
