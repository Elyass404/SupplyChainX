package com.supplychainx.supply_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
public class SupplyOrderRequest {

    // The date the order is placed
    @NotNull(message = "Order date is required.")
    LocalDate orderDate;

    // The ID of the Supplier placing the order
    @NotNull(message = "Supplier ID is required.")
    Long supplierId;

    // The list of materials and quantities to be ordered
    @NotNull(message = "Materials list is required.")
    @Size(min = 1, message = "Order must contain at least one material.")
    List<SupplyOrderMaterialRequest> materials;

    // Status can be set on creation, or default to PENDING in the service layer
    // We'll exclude it from the request DTO to manage it internally for security/consistency.
}