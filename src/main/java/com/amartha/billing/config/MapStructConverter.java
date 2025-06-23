package com.amartha.billing.config;

import com.amartha.billing.inbound.model.CreateLoanRequest;
import com.amartha.billing.inbound.model.CustomerRequest;
import com.amartha.billing.service.model.CustomerServiceRequest;
import com.amartha.billing.service.model.LoanServiceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapStructConverter {

    MapStructConverter MAPPER = Mappers.getMapper(MapStructConverter.class);

    LoanServiceRequest toLoanServiceRequest(CreateLoanRequest request);
    CustomerServiceRequest toCustomerServiceRequest(CustomerRequest request);
}
