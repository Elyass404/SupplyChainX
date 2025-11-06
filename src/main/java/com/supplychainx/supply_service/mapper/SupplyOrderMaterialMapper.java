package com.supplychainx.supply_service.mapper;

import com.supplychainx.supply_service.dto.response.SupplyOrderMaterialResponse;
import com.supplychainx.supply_service.model.SupplyOrderMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplyOrderMaterialMapper {

    // --- MAPPING FROM ENTITY TO RESPONSE DTO ---
    @Mapping(target = "supplyOrderId", source = "supplyOrder.id")
    @Mapping(target = "rawMaterialId", source = "rawMaterial.id")
    SupplyOrderMaterialResponse toResponse(SupplyOrderMaterial entity);

    // Note: We skip mapping from Request DTO to Entity here.
    // This is because the creation of SupplyOrderMaterial entities
    // happens inside the SupplyOrder service, which requires fetching
    // the SupplyOrder and RawMaterial entities first.
}