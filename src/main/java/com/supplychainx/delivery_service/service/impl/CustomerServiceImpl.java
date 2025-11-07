package com.supplychainx.delivery_service.service.impl;

import com.supplychainx.delivery_service.dto.request.CustomerRequest;
import com.supplychainx.delivery_service.dto.response.CustomerResponse;
import com.supplychainx.delivery_service.mapper.CustomerMapper;
import com.supplychainx.delivery_service.model.Customer;
import com.supplychainx.delivery_service.repository.CustomerRepository;
import com.supplychainx.delivery_service.repository.OrderRepository;
import com.supplychainx.delivery_service.service.CustomerService;
import com.supplychainx.exception.RessourceNotFoundException;
import com.supplychainx.supply_service.model.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository; // Needed for US32 check
    private final CustomerMapper customerMapper;

    /**
     * Helper method to retrieve a Customer entity or throw an exception.
     * This avoids repeating findById/orElseThrow logic.
     */
    private Customer findCustomerEntity(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Customer not found with ID: " + id));
    }

    /**
     * Helper method to map the entity and calculate the active order count for the DTO.
     */
    private CustomerResponse mapToResponse(Customer customer) {
        // Map the basic customer data
        CustomerResponse response = customerMapper.toResponse(customer);

        // Calculate and set active order count
        // Active orders are those that are NOT DELIVERED.
        long activeOrderCount = customer.getOrders().stream()
                .filter(order -> order.getStatus() != OrderStatus.DELIVERED)
                .count();

        // I used the DTO builder pattern to create a new response with the calculated count
        return CustomerResponse.builder()
                .id(response.getId())
                .name(response.getName())
                .address(response.getAddress())
                .city(response.getCity())
                .activeOrderCount((int) activeOrderCount)
                .build();
    }


    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer newCustomer = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(newCustomer);
        // Note: For a new customer, activeOrderCount will be 0.
        return mapToResponse(savedCustomer);
    }


    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customerToUpdate = findCustomerEntity(id);

        customerToUpdate.setName(request.getName());
        customerToUpdate.setAddress(request.getAddress());
        customerToUpdate.setCity(request.getCity());

        Customer updatedCustomer = customerRepository.save(customerToUpdate);
        return mapToResponse(updatedCustomer);
    }

    //Here iam deleting customers that dont have any active order
    @Override
    public void deleteCustomer(Long id) {
        Customer customer = findCustomerEntity(id);

        // Check for active orders using the OrderRepository for efficiency
        boolean hasActiveOrders = orderRepository.findAll().stream()
                .filter(order -> order.getCustomer().getId().equals(id))
                .anyMatch(order -> order.getStatus() != OrderStatus.DELIVERED);

        if (hasActiveOrders) {
            throw new IllegalStateException("Customer cannot be deleted because they have active orders (not DELIVERED).");
        }

        customerRepository.delete(customer);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomersByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}