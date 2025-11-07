package com.supplychainx.delivery_service.dto.response;

import com.supplychainx.production_service.dto.response.ProductResponse;
import com.supplychainx.supply_service.model.enums.OrderStatus;

import java.time.LocalDate;

public class OrderResponse {

    Long id;
    Integer quantity;
    OrderStatus status;
    String address;
    LocalDate orderDate;

    CustomerResponse customer;
    ProductResponse product;
}
