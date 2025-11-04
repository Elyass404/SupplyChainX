package com.supplychainx.dto.response;

import com.supplychainx.model.enums.SupplyOrderStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class SupplyOrderResponse {

    Long id;

    LocalDate orderDate;

    SupplyOrderStatus status; // The current status (PENDING, RECEIVED, etc.)

    Long supplierId; // Only expose the supplier's ID

    Double totalOrderCost; // A field that might be calculated by the service

    // List of simplified material/quantity responses (often just the IDs)
    List<SupplyOrderMaterialResponse> materials;
}