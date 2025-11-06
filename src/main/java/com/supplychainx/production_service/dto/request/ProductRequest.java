package com.supplychainx.production_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class ProductRequest {
    @NotBlank (message = "The name of the product is required!")
    String name;

    @NotNull(message = "The time of production should be inserted!")
    @Positive(message = "The time should be positive")
    Integer productionTime;

    @NotNull(message = "Unit cost is required.")
    @Positive(message = "Unit cost must be positive.")
    Double cost;

    @NotNull(message = "Initial stock is required.")
    @Positive(message = "Stock must be non-negative.")
    Integer stock;

}
