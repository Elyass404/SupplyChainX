package com.supplychainx.production_service.mapper;

import com.supplychainx.dto.response.RawMaterialSimpleResponse;
import com.supplychainx.supply_service.model.RawMaterial;
import com.supplychainx.production_service.dto.request.BillOfMaterialRequest;
import com.supplychainx.production_service.dto.response.BillOfMaterialResponse;
import com.supplychainx.production_service.model.BillOfMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface BillOfMaterialMapper {

    // Helper method to map the nested RawMaterial entity to the simple DTO
    @Mapping(source = "rawMaterial.id", target = "id")
    @Mapping(source = "rawMaterial.name", target = "name")
    // Assuming RawMaterial has a field called 'unit'
    @Mapping(source = "rawMaterial.unit", target = "unit")
    RawMaterialSimpleResponse toRawMaterialSimpleResponse(RawMaterial rawMaterial);

    // Map Entity to Response DTO
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "rawMaterial", target = "rawMaterial")
    BillOfMaterialResponse toResponse(BillOfMaterial billOfMaterial);

    List<BillOfMaterialResponse> toResponseList(List<BillOfMaterial> billOfMaterials);

    // Note: Request to Entity mapping is handled in the Service because
    // the Service must fetch Product and RawMaterial entities by ID before mapping.
}