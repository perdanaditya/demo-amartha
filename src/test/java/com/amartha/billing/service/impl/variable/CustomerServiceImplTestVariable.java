package com.amartha.billing.service.impl.variable;

import com.amartha.billing.entity.Customer;
import com.amartha.billing.service.model.CustomerServiceRequest;

import java.util.UUID;

public class CustomerServiceImplTestVariable {
    protected static final UUID CUSTOMER_ID = UUID.randomUUID();

    protected static final Customer EXISTING_CUSTOMER = Customer.builder()
            .id(CUSTOMER_ID)
            .fullName("John Doe")
            .build();

    protected static final CustomerServiceRequest EXISTING_CUSTOMER_REQUEST = CustomerServiceRequest.builder()
            .id(CUSTOMER_ID)
            .fullName("John Doe")
            .build();

    protected static final Customer NEW_CUSTOMER = Customer.builder()
            .id(UUID.randomUUID())
            .fullName("Jane Smith")
            .build();

    protected static final CustomerServiceRequest NEW_CUSTOMER_REQUEST = CustomerServiceRequest.builder()
            .fullName("Jane Smith")
            .build();
}