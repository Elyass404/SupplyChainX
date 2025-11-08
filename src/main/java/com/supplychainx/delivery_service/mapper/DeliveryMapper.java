package com.supplychainx.delivery_service.mapper;

import com.supplychainx.delivery_service.dto.request.DeliveryRequest;
import com.supplychainx.delivery_service.dto.response.DeliveryResponse;
import com.supplychainx.delivery_service.model.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeliveryMapper {

    // Converts Request DTO to Entity for creation/update
    @Mapping(target = "order", ignore = true) // Order is set by the Service using orderId
    @Mapping(target = "status", ignore = true) // Status is set by the Service
    Delivery toEntity(DeliveryRequest request);

    // Converts Entity to Response DTO
    @Mapping(source = "order.id", target = "orderId")
    DeliveryResponse toResponse(Delivery delivery);

    List<DeliveryResponse> toResponseList(List<Delivery> deliveries);
}