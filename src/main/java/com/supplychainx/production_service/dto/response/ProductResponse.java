package com.supplychainx.production_service.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductResponse {

    Long id;

    String name;
    Integer productionTime;
    Double cost;
    Integer stock;
}