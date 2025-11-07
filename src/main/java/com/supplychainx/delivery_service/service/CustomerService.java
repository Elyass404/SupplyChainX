package com.supplychainx.delivery_service.service;

import com.supplychainx.delivery_service.dto.request.CustomerRequest;
import com.supplychainx.delivery_service.dto.response.CustomerResponse;
import com.supplychainx.delivery_service.model.Customer;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse updateCustomer(Long id, CustomerRequest request);

    void  deleteCustomer(Long id);

    List<CustomerResponse> getAllCustomers();

    List<CustomerResponse> searchCustomersByName(String name);
}
