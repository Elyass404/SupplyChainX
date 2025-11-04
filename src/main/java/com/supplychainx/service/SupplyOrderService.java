package com.supplychainx.service;

import com.supplychainx.dto.request.SupplyOrderRequest;
import com.supplychainx.dto.response.SupplyOrderResponse;
import com.supplychainx.model.enums.SupplyOrderStatus;

import java.time.LocalDate;
import java.util.List;

public interface SupplyOrderService {

    // --- CRUD Operations ---

    // Accepts DTO for creation, returns DTO response
    SupplyOrderResponse createSupplyOrder(SupplyOrderRequest request);

    // Accepts ID and DTO for update, returns DTO response
    SupplyOrderResponse updateSupplyOrder(Long id, SupplyOrderRequest request);

    void deleteSupplyOrder(Long id);

    // --- Retrieval ---

    // Now returns DTO response, throwing 404/Exception if not found
    SupplyOrderResponse getSupplyOrderById(Long id);

    List<SupplyOrderResponse> getAllSupplyOrders();

    // --- Business & Query Methods (Updated to return DTOs) ---

    List<SupplyOrderResponse> getOrdersBySupplier(Long supplierId);

    List<SupplyOrderResponse> getOrdersByStatus(SupplyOrderStatus status);

    List<SupplyOrderResponse> getOrdersByDateRange(LocalDate startDate, LocalDate endDate);

    boolean existsBySupplierAndDate(Long supplierId, LocalDate orderDate);
}