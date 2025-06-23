package com.amartha.billing.service.impl;

import com.amartha.billing.constant.ErrorConstant;
import com.amartha.billing.entity.Customer;
import com.amartha.billing.exception.AppException;
import com.amartha.billing.repository.CustomerRepository;
import com.amartha.billing.service.CustomerService;
import com.amartha.billing.service.model.CustomerServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer saveOrGet(CustomerServiceRequest request) {
        if (Objects.isNull(request.getId())) {
            return customerRepository.save(Customer.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail()).build());
        }
        return customerRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorConstant.CUSTOMER_NOT_FOUND));
    }
}
