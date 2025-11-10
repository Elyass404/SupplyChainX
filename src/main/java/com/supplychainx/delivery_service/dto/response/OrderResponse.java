package com.supplychainx.delivery_service.dto.response;

import com.supplychainx.supply_service.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class OrderResponse {

    Long id;
    Integer quantity;
    OrderStatus status;
    LocalDate orderDate;


    Long customerId;
    Long productId;
}