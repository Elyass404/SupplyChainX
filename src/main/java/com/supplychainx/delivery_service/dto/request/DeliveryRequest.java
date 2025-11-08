package com.supplychainx.delivery_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class DeliveryRequest {

    @NotNull(message = "Order ID is required to link the delivery.")
    Long orderId;

    @NotBlank(message = "Vehicle information is required.")
    String vehicle;

    @NotBlank(message = "Driver name is required.")
    String driver;

    @NotBlank(message = "Delivery address is required.")
    String deliveryAddress;

    @NotNull(message = "Planned delivery date is required.")
    LocalDate deliveryDate;

    @NotNull(message = "Cost is required for calculation (US40).")
    @DecimalMin(value = "0.0", inclusive = true, message = "Delivery cost cannot be negative.")
    Double cost;


}