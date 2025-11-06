package com.supplychainx.production_service.service;

import com.supplychainx.production_service.dto.request.ProductionOrderRequest;
import com.supplychainx.production_service.dto.response.ProductionOrderResponse;
import com.supplychainx.supply_service.model.enums.ProductionStatus;

import java.util.List;

public interface ProductionOrderService {

    // Create a new order, check material availability, and calculate estimated time.
    ProductionOrderResponse createOrder(ProductionOrderRequest request);

    // Consult the list of all orders.
    List<ProductionOrderResponse> getAllOrders();

    // Standard Get by ID
    ProductionOrderResponse getOrderById(Long id);

    // Consult the status of orders by filtering on status.
    List<ProductionOrderResponse> getOrdersByStatus(ProductionStatus status);

    //  Cancel an order if not started (PENDING status).
    ProductionOrderResponse cancelOrder(Long id);

    // Supervisor updates the status to IN_PROGRESS and sets the start date.
    ProductionOrderResponse startOrder(Long id);

    // Supervisor updates the status to COMPLETED and updates product stock.
    ProductionOrderResponse completeOrder(Long id);

    // Business Logic: Helper method to set status to BLOCKED if material check fails (part of US28).
    ProductionOrderResponse blockOrder(Long id);
}