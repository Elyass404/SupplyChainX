package com.supplychainx.supply_service.mapper;

import com.supplychainx.supply_service.dto.request.SupplyOrderRequest;
import com.supplychainx.supply_service.dto.response.SupplyOrderResponse;
import com.supplychainx.supply_service.model.SupplyOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

// Note: Requires the RawMaterialMapper to be fully functional for nested mapping,
// but we will manually handle the entity fetching in the service layer for relationship safety.
@Mapper(componentModel = "spring", uses = {SupplyOrderMaterialMapper.class})
public interface SupplyOrderMapper {

    // --- FROM DTO TO ENTITY (For Creation) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplier", ignore = true) // Ignored: Fetched and set in service using supplierId
    @Mapping(target = "materials", ignore = true) // Ignored: Created and set in service using nested DTOs
    @Mapping(target = "status", expression = "java(com.supplychainx.supply_service.model.enums.SupplyOrderStatus.PENDING)") // Default status
    SupplyOrder toEntity(SupplyOrderRequest dto);

    // --- FROM ENTITY TO RESPONSE DTO ---
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "materials", source = "materials") // MapStruct will use SupplyOrderMaterialMapper
    @Mapping(target = "totalOrderCost", ignore = true) // Calculate this in the service layer
    SupplyOrderResponse toResponse(SupplyOrder entity);

    List<SupplyOrderResponse> toResponseList(List<SupplyOrder> entities);

    // --- We still need a SupplyOrderMaterialMapper, but we will define that later ---
}