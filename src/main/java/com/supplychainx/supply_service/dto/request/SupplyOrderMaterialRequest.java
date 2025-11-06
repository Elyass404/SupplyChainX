package com.supplychainx.supply_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class SupplyOrderMaterialRequest {

    @NotNull(message = "Raw material ID is required.")
    Long rawMaterialId;

    @Min(value = 1, message = "Quantity must be at least 1.")
    int quantity;
}