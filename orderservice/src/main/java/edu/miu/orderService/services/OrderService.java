package edu.miu.orderService.services;

import edu.miu.orderService.models.Order;
import org.aspectj.weaver.ast.Or;

import java.util.List;

/**
 *
 * @author Daniel Tsegay Meresie
 */
public interface OrderService {
    List<Order> getOrders();

    Order createOrder(int order);

    Order getOrder(Long orderId);

    Order updateOrder(Order order);

    Order deleteOrder(Long OrderId);
    List<Order> findOrdersByUserId(int userId);
}
