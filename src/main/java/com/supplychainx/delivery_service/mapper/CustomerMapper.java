package com.supplychainx.delivery_service.mapper;

import com.supplychainx.delivery_service.dto.request.CustomerRequest;
import com.supplychainx.delivery_service.dto.response.CustomerResponse;
import com.supplychainx.delivery_service.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    Customer toEntity(CustomerRequest request);

    //i want to ignore  this because i will handle the counting of the orders in the service layer
    @Mapping(target= "activeOrderCount" , ignore = true)
    CustomerResponse toResponse(Customer customer);

    List<CustomerResponse> toResponseList(List<Customer> customers);
}
