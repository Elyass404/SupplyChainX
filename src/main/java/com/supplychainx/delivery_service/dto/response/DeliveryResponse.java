package com.supplychainx.delivery_service.dto.response;

import com.supplychainx.supply_service.model.enums.DeliveryStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class DeliveryResponse {

    Long id;
    Long orderId; // To simplify lookup without nesting the full order object
    String vehicle;
    String driver;
    DeliveryStatus status;
    String deliveryAddress;
    LocalDate deliveryDate;
    Double cost;
}