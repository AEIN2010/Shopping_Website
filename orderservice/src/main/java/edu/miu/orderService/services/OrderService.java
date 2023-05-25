package edu.miu.orderService.services;

import edu.miu.orderService.models.Order;

import java.util.List;

/**
 * OrderService interface
 */
public interface OrderService {
    List<Order> getOrders();

    Order createOrder(String userId);

    Order getOrder(Long orderId);

    Order updateOrder(Order order);

    Order deleteOrder(Long orderId);

    List<Order> findOrdersByUserId(String userId);

    String clearCart(String userId);
}
