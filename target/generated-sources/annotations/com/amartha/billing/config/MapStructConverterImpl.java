package com.amartha.billing.config;

import com.amartha.billing.inbound.model.CreateLoanRequest;
import com.amartha.billing.inbound.model.CustomerRequest;
import com.amartha.billing.service.model.CustomerServiceRequest;
import com.amartha.billing.service.model.LoanServiceRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-23T18:10:56+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class MapStructConverterImpl implements MapStructConverter {

    @Override
    public LoanServiceRequest toLoanServiceRequest(CreateLoanRequest request) {
        if ( request == null ) {
            return null;
        }

        LoanServiceRequest.LoanServiceRequestBuilder loanServiceRequest = LoanServiceRequest.builder();

        loanServiceRequest.customer( toCustomerServiceRequest( request.getCustomer() ) );
        loanServiceRequest.amount( request.getAmount() );

        return loanServiceRequest.build();
    }

    @Override
    public CustomerServiceRequest toCustomerServiceRequest(CustomerRequest request) {
        if ( request == null ) {
            return null;
        }

        CustomerServiceRequest.CustomerServiceRequestBuilder customerServiceRequest = CustomerServiceRequest.builder();

        customerServiceRequest.id( request.getId() );
        customerServiceRequest.email( request.getEmail() );
        customerServiceRequest.fullName( request.getFullName() );

        return customerServiceRequest.build();
    }
}
