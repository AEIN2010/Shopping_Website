package edu.miu.orderService.controllers;

import java.util.List;

import edu.miu.orderService.JwtUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.miu.orderService.models.Order;
import edu.miu.orderService.models.Payment;
import edu.miu.orderService.services.OrderService;
import edu.miu.orderService.services.RabbitMQSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Import the Status enum
import edu.miu.orderService.models.Status;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RabbitMQSenderService rabbitMQSenderService;

    @GetMapping
    public List<Order> getOrders() {
        return orderService.getOrders();
    }
    @GetMapping("/byid/{userId}")
    public List<Order> getOrdersById(@PathVariable String userId){
        return orderService.findOrdersByUserId(userId);
    }

    @PostMapping("/{userId}")
    public Order createOrder(@PathVariable String userId, @RequestHeader("Authorization") String token) {
        Order order = orderService.createOrder(userId);

        // Send mail for created order
        // Remove the "Bearer " prefix from the token
        token = token.replace("Bearer ", "");

        String email = "dummy@email.com";//JwtUtil.extractEmailFromToken(token);

        rabbitMQSenderService.sendOrderConfirmation(
                "sender@example.com",
                List.of(email),
                "Order Confirmation",
                "Your order has been created",
                List.of()
        );

        return order;
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }

    @PutMapping
    public Order updateOrder(@RequestBody Order order, @RequestHeader("Authorization") String token) {
        Order updatedOrder = orderService.updateOrder(order);
        String email = "dummy@email.com";
//        String email = JwtUtil.extractEmailFromToken(token);
        // Check if the order status is updated to DELIVERED
        if(updatedOrder.getStatus() == Status.DELIVERED) {
            // Send mail for delivery order
            rabbitMQSenderService.sendDeliveryConfirmation(
                    "sender@example.com",
                    List.of(email),
                    "Delivery Confirmation",
                    "Your order has been delivered",
                    List.of()
            );
        }
        return updatedOrder;
    }

    @DeleteMapping("/{orderId}")
    public Order deleteOrder(@PathVariable Long orderId) {
        return orderService.deleteOrder(orderId);
    }
}
