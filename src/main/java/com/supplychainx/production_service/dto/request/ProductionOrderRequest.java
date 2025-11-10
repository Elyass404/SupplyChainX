package com.supplychainx.production_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;
@Builder
@Value
public class ProductionOrderRequest {

    // Corresponds to 'product' in the entity: the ID of the product to be manufactured
    @NotNull(message = "Product ID is required.")
    Long productId;

    // Corresponds to 'quantity' in the entity: the amount to produce
    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must be a positive value.")
    Integer quantity;

    public ProductionOrderRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Note: 'status', 'startDate', and 'endDate' are calculated or set by the service layer.
}