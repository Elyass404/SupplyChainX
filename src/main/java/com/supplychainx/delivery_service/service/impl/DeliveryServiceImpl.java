package com.supplychainx.delivery_service.service.impl;

import com.supplychainx.delivery_service.dto.request.DeliveryRequest;
import com.supplychainx.delivery_service.dto.response.DeliveryResponse;
import com.supplychainx.delivery_service.mapper.DeliveryMapper;
import com.supplychainx.delivery_service.model.Delivery;
import com.supplychainx.delivery_service.model.Order;
import com.supplychainx.delivery_service.repository.DeliveryRepository;
import com.supplychainx.delivery_service.repository.OrderRepository;
import com.supplychainx.delivery_service.service.DeliveryService;
import com.supplychainx.exception.RessourceNotFoundException;
import com.supplychainx.supply_service.model.enums.DeliveryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final DeliveryMapper deliveryMapper;

    private Order findOrderEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Order not found with ID: " + id));
    }

    private Delivery findDeliveryEntity(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Delivery not found with ID: " + id));
    }

    // Create a delivery for an order and calculate its total cost ---
    @Override
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        Order order = findOrderEntity(request.getOrderId());

        // Ensure only one delivery per order
        if (deliveryRepository.findByOrderId(order.getId()).isPresent()) {
            throw new IllegalStateException("A delivery already exists for order ID: " + order.getId());
        }

        Delivery newDelivery = deliveryMapper.toEntity(request);
        newDelivery.setOrder(order);

        newDelivery.setStatus(DeliveryStatus.SCHEDULED);

        Delivery savedDelivery = deliveryRepository.save(newDelivery);
        return deliveryMapper.toResponse(savedDelivery);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryById(Long id) {
        return deliveryMapper.toResponse(findDeliveryEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getAllDeliveries() {
        return deliveryRepository.findAll().stream()
                .map(deliveryMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public DeliveryResponse updateDeliveryStatus(Long id, DeliveryStatus newStatus) {
        Delivery delivery = findDeliveryEntity(id);

        //
        if (newStatus == DeliveryStatus.DELIVERED && delivery.getStatus() != DeliveryStatus.IN_PROGRESS) {
            throw new IllegalStateException("Delivery must be IN_PROGRESS before it can be marked as DELIVERED.");
        }

        delivery.setStatus(newStatus);
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return deliveryMapper.toResponse(updatedDelivery);
    }
}