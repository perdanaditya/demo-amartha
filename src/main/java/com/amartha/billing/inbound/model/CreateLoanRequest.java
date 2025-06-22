package com.amartha.billing.inbound.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLoanRequest implements Serializable {

    @NotNull
    private CustomerRequest customer;

    @NotNull
    private BigDecimal amount;

}
