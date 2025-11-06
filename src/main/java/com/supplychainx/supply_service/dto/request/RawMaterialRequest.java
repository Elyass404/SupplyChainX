package com.supplychainx.supply_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

import java.util.List;

@Value
public class RawMaterialRequest {

    @NotBlank(message = "Name is required.")
    String name;

    @Min(value = 0, message = "Stock cannot be negative.")
    int stock;

    @Min(value = 0, message = "Minimum stock cannot be negative.")
    int stockMin;

    @NotBlank(message = "Unit of measure is required.")
    String unit;

    // List of existing Supplier IDs to associate with this RawMaterial
    @NotEmpty(message = "Raw material must be associated with at least one supplier.")
    List<Long> supplierIds;
}