package com.supplychainx.production_service.dto.response;

import com.supplychainx.production_service.dto.response.ProductResponse; // Dependency on previously created DTO
import com.supplychainx.supply_service.model.enums.ProductionStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ProductionOrderResponse {

    Long id;
    ProductResponse product;
    Integer quantity;
    ProductionStatus status;
    LocalDate startDate;
    LocalDate endDate;
}