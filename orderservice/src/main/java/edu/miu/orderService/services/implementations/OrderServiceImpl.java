package edu.miu.orderService.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.miu.orderService.models.*;
import edu.miu.orderService.repositories.OrderRepository;
import edu.miu.orderService.services.OrderService;
import edu.miu.orderService.services.RabbitMQSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Value("${cart.service.url}")
    private String cartServiceUrl;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitMQSenderService rabbitMQSenderService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order createOrder(String userId) {
        int userIdInt = Integer.parseInt(userId); // Convert String to int
        Cart c = retrieveCartForUser(userIdInt);
        ResponseDataFromProduct response = retrieveProductData();
        validateProductQuantities(c, response);
        double price = computePrice(c, response);
        updateProductQuantities(c);
        Order order = initializeOrder(userIdInt, price);
        processPayment(order, price, userId);
        return orderRepository.save(order);
    }

    private Cart retrieveCartForUser(int userId) {
        String url = cartServiceUrl + "/" + userId;
        ResponseEntity<Cart> response = restTemplate.exchange(url, HttpMethod.GET, null, Cart.class);
        return response.getBody();
    }

    private ResponseDataFromProduct retrieveProductData() {
        String url = productServiceUrl;
        ResponseEntity<ResponseDataFromProduct> response = restTemplate.exchange(url, HttpMethod.GET, null, ResponseDataFromProduct.class);
        return response.getBody();
    }

    private void validateProductQuantities(Cart c, ResponseDataFromProduct response) {
        for (Product p : c.getProds()) {
            for (Product2 p2 : response.getData()) {
                if (p.getProdId().equals(p2.get_id()) && p.getQuantity() > p2.getQuantity()) {
                    throw new RuntimeException("Not enough products for product with Id + " + p.getProdId());
                }
            }
        }
    }

    private double computePrice(Cart c, ResponseDataFromProduct response) {
        double price = 0;
        for (Product p : c.getProds()) {
            for (Product2 p2 : response.getData()) {
                if (p.getProdId().equals(p2.get_id())) {
                    price += p.getQuantity() * p2.getPrice();
                }
            }
        }
        return price;
    }

    private void updateProductQuantities(Cart c) {
        for (Product p : c.getProds()) {
            String url = productServiceUrl + "/" + p.getProdId();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String requestBody = "{\"order_quantity\": \"" + p.getQuantity() + "\"}";
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getStatusCode();
        }
    }

    private Order initializeOrder(int userId, double price) {
        Order order = new Order(null, userId, Status.UNPAID);
        return orderRepository.save(order);
    }

    private void processPayment(Order order, double price, String userId) {
        int userIdInt = Integer.parseInt(userId.trim());
        Payment p = new Payment();
        p.setAmount(price);
        p.setCardNumber("1234123412341234");
        p.setCardType("VISA");
        p.setOrderId(order.getOrderId().toString());
        p.setCurrency("USD");
        p.setUserId(userIdInt);
        p.setId(null);
        String apiUrl = paymentServiceUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Payment> request = new HttpEntity<>(p, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(apiUrl, request, Void.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            order.setStatus(Status.IN_TRANSIT);
            clearCart(String.valueOf(userIdInt)); // Convert int to String
        }
    }


    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow();
    }

    @Override
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        orderRepository.delete(order);
        return order;
    }

    @Override
    public List<Order> findOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public String clearCart(String userId) {
        int userIdInt = Integer.parseInt(userId.trim());
        String url = cartServiceUrl + "/" + userIdInt;

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);

        return response.getBody();
    }

}