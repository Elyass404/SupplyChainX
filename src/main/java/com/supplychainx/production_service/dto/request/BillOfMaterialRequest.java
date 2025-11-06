package com.supplychainx.production_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class BillOfMaterialRequest {

    @NotNull(message = "Product ID is required.")
    Long productId;

    @NotNull(message = "Raw Material ID is required.")
    Long rawMaterialId;

    @NotNull(message = "Required quantity must be specified.")
    @Positive(message = "Required quantity must be a positive value.")
    Double requiredQuantity;
}