package com.supplychainx.delivery_service.mapper;

import com.supplychainx.delivery_service.dto.request.OrderRequest;
import com.supplychainx.delivery_service.dto.response.OrderResponse;
import com.supplychainx.delivery_service.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {})
public interface OrderMapper {


    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "status", ignore = true)
    Order toEntity(OrderRequest request);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "product.id", target = "productId")
    OrderResponse toResponse(Order order);

    List<OrderResponse> toResponseList(List<Order> orders);
}