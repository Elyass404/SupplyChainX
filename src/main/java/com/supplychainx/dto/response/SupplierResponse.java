package com.supplychainx.dto.response;

import lombok.Builder;
import lombok.Value;
import java.util.List;

@Value
@Builder // Useful for building the response in the mapper
public class SupplierResponse {

    Long id;

    String name;

    String email;

    Double rating;

    int leadTime;

    // Simplified relationship: just send the IDs of the related SupplyOrders
    List<Long> supplyOrderIds;
}