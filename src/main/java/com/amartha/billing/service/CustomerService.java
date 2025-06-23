package com.amartha.billing.service;

import com.amartha.billing.entity.Customer;
import com.amartha.billing.service.model.CustomerServiceRequest;

public interface CustomerService {
    Customer saveOrGet(CustomerServiceRequest request);
}
