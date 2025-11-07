package com.supplychainx.delivery_service.mapper;

import ch.qos.logback.core.model.ComponentModel;
import com.supplychainx.delivery_service.dto.request.OrderRequest;
import com.supplychainx.delivery_service.dto.response.OrderResponse;
import com.supplychainx.delivery_service.model.Order;
import com.supplychainx.production_service.mapper.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
        // Include dependent mappers to handle nested DTO conversion
        uses = {CustomerMapper.class, ProductMapper.class})
public interface OrderMapper {

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "status", ignore = true)
    Order toEntity(OrderRequest request);

    OrderResponse toResponse(Order order);

    List<OrderResponse> toResponseList(List<Order> orders);


}
