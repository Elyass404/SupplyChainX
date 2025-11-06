package com.supplychainx.production_service.dto.response;

import com.supplychainx.dto.response.RawMaterialSimpleResponse;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BillOfMaterialResponse {

    Long id;
    Long productId; // Only include ID, as the request is usually filtered by product
    RawMaterialSimpleResponse rawMaterial;
    Double requiredQuantity;
}