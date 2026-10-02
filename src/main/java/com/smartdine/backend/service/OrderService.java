package com.smartdine.backend.service;

import com.smartdine.backend.entity.Order;
import com.smartdine.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order updateStatus(Long orderId, String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found")
                );

        String newStatus = status.toUpperCase();

        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException(
                    "Invalid order status: " + status
            );
        }

        order.setStatus(newStatus);

if ("READY".equals(newStatus)) {
    order.setReadyAt(LocalDateTime.now());
}

return orderRepository.save(order);
    }

    private boolean isValidStatus(String status) {

        return status.equals("PLACED")
                || status.equals("ACCEPTED")
                || status.equals("PREPARING")
                || status.equals("READY")
                || status.equals("SERVED")
                || status.equals("CANCELLED");
    }
}