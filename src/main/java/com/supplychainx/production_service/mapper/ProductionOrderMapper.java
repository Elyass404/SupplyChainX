package com.supplychainx.production_service.mapper;

import com.supplychainx.production_service.dto.response.ProductionOrderResponse;
import com.supplychainx.production_service.model.ProductionOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface ProductionOrderMapper {

    /**
     * Maps the ProductionOrder entity to the full response DTO.
     * The nested 'product' field is mapped using ProductMapper.
     */
    @Mapping(source = "product", target = "product")
    ProductionOrderResponse toResponse(ProductionOrder productionOrder);

    List<ProductionOrderResponse> toResponseList(List<ProductionOrder> productionOrders);

}