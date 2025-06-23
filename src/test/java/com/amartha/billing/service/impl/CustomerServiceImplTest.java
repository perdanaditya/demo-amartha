package com.amartha.billing.service.impl;

import com.amartha.billing.entity.Customer;
import com.amartha.billing.repository.CustomerRepository;
import com.amartha.billing.service.impl.variable.CustomerServiceImplTestVariable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CustomerServiceImplTest extends CustomerServiceImplTestVariable {

    @InjectMocks
    private CustomerServiceImpl customerService;
    @Mock
    private CustomerRepository customerRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        verifyNoMoreInteractions(customerRepository);
        closeable.close();
    }

    @Test
    void saveOrGet_existingCustomer() {
        when(customerRepository.findById(CUSTOMER_ID))
                .thenReturn(Optional.of(EXISTING_CUSTOMER));

        Customer result = customerService.saveOrGet(EXISTING_CUSTOMER_REQUEST);

        assertEquals(EXISTING_CUSTOMER, result);
        verify(customerRepository).findById(CUSTOMER_ID);
    }

    @Test
    void saveOrGet_newCustomer() {
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(NEW_CUSTOMER);

        Customer result = customerService.saveOrGet(NEW_CUSTOMER_REQUEST);

        assertEquals(NEW_CUSTOMER, result);
        verify(customerRepository).save(any(Customer.class));
    }
}