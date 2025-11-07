package com.supplychainx.delivery_service.service;

import com.supplychainx.delivery_service.dto.request.OrderRequest;
import com.supplychainx.delivery_service.dto.response.OrderResponse;
import com.supplychainx.supply_service.model.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    OrderResponse updateOrder(Long id, OrderRequest request);

    void deleteOrder(Long id);

    List<OrderResponse> getAllOrders();

    OrderResponse  getOrderById(Long id);

    List<OrderResponse> getOrdersByStatus(OrderStatus status);

    OrderResponse shipOrder(Long id);

    OrderResponse completeOrder(Long id);

    OrderResponse cancelOrder(Long id);
}
