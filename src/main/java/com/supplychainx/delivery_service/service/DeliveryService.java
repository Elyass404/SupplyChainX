package com.supplychainx.delivery_service.service;

import com.supplychainx.delivery_service.dto.request.DeliveryRequest;
import com.supplychainx.delivery_service.dto.response.DeliveryResponse;
import com.supplychainx.supply_service.model.enums.DeliveryStatus;

import java.util.List;

public interface DeliveryService {

    DeliveryResponse createDelivery(DeliveryRequest request);

    DeliveryResponse getDeliveryById(Long id);

    List<DeliveryResponse> getAllDeliveries();

    DeliveryResponse updateDeliveryStatus(Long id, DeliveryStatus newStatus);

    DeliveryResponse getDeliveryByOrderId(Long orderId);
}