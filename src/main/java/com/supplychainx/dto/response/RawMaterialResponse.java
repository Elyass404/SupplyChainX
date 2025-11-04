package com.supplychainx.dto.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RawMaterialResponse {

    Long id;

    String name;

    int stock;

    int stockMin;

    String unit;

    // List of Supplier IDs that provide this RawMaterial
    List<SupplierResponse> suppliers;

    // List of IDs related to SupplyOrderMaterial (optional, depending on need)
    List<Long> supplyOrderMaterialIds;
}