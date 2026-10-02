package com.smartdine.backend.controller;

import com.smartdine.backend.entity.Order;
import com.smartdine.backend.entity.OrderItem;
import com.smartdine.backend.repository.OrderItemRepository;
import com.smartdine.backend.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smartdine.backend.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;

    public OrderController(
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository,
        OrderService orderService
) {
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.orderService = orderService;
}

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestBody OrderRequest request
    ) {

        Order order = request.getOrder();

        order.setId(null);
        order.setStatus("PLACED");

        Order savedOrder = orderRepository.save(order);

        if (request.getItems() != null) {
            for (OrderItem item : request.getItems()) {
                item.setId(null);
                item.setOrderId(savedOrder.getId());
                orderItemRepository.save(item);
            }
        }

        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/items")
    public List<OrderItem> getOrderItems(@PathVariable Long id) {
        return orderItemRepository.findByOrderId(id);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(@PathVariable Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @PutMapping("/{id}/status")
public ResponseEntity<?> updateOrderStatus(
        @PathVariable Long id,
        @RequestParam String status
) {
    try {
        Order updatedOrder = orderService.updateStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    } catch (RuntimeException error) {
        return ResponseEntity.badRequest()
                .body(error.getMessage());
    }
}

    public static class OrderRequest {

        private Order order;
        private List<OrderItem> items;

        public OrderRequest() {
        }

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        public List<OrderItem> getItems() {
            return items;
        }

        public void setItems(List<OrderItem> items) {
            this.items = items;
        }
    }
}