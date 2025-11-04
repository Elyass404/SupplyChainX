package com.supplychainx.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SupplyOrderMaterialResponse {

    Long id;

    Long supplyOrderId; // Parent order ID

    Long rawMaterialId; // Associated material ID

    int quantity;
}