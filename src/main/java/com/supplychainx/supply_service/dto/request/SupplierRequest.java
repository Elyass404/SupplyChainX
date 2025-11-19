package com.supplychainx.supply_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

// Using @Value for immutability and automatic getter generation
@Value
@Builder
public class SupplierRequest {

    @NotBlank(message = "Supplier name is required.")
    String name;

    @Email(message = "Must be a valid email format.")
    @NotBlank(message = "Email is required.")
    String email;

    // Optional fields, use Double wrapper for nullable database columns
    Double rating;

    @Min(value = 0, message = "Lead time cannot be negative.")
    int leadTime;
}