package com.supplychainx.mapper;

import com.supplychainx.dto.request.RawMaterialRequest;
import com.supplychainx.dto.response.RawMaterialResponse;
import com.supplychainx.model.RawMaterial;
import com.supplychainx.model.Supplier;
import com.supplychainx.model.SupplyOrderMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RawMaterialMapper {

    RawMaterialMapper INSTANCE = Mappers.getMapper(RawMaterialMapper.class);

    // --- FROM DTO TO ENTITY (Create/Update) ---
    // Ignore relationships that are set based on IDs in the Service layer
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "suppliers", ignore = true) // Ignored: set in service using IDs
    @Mapping(target = "supplyOrderMaterials", ignore = true)
    RawMaterial toEntity(RawMaterialRequest dto);

    // --- FROM ENTITY TO RESPONSE DTO ---
    // Map List<Supplier> to List<Long> (IDs)
    //@Mapping(target = "supplierIds", expression = "java(mapSuppliersToIds(entity.getSuppliers()))")

    // Map List<SupplyOrderMaterial> to List<Supplier> (suppliers )
    @Mapping (target= "suppliers", source = "suppliers")

    // Map List<SupplyOrderMaterial> to List<Long> (IDs)
    //@Mapping(target = "supplyOrderMaterialIds", expression = "java(mapSupplyOrderMaterialsToIds(entity.getSupplyOrderMaterials()))")
    RawMaterialResponse toResponse(RawMaterial entity);


    List<RawMaterialResponse> toResponseList(List<RawMaterial> entities);

    // --- Custom Mapping Functions ---

    default List<Long> mapSuppliersToIds(List<Supplier> suppliers) {
        if (suppliers == null) {
            return Collections.emptyList();
        }
        return suppliers.stream().map(Supplier::getId).collect(Collectors.toList());
    }

    default List<Long> mapSupplyOrderMaterialsToIds(List<SupplyOrderMaterial> orderMaterials) {
        if (orderMaterials == null) {
            return Collections.emptyList();
        }
        return orderMaterials.stream().map(SupplyOrderMaterial::getId).collect(Collectors.toList());
    }
}