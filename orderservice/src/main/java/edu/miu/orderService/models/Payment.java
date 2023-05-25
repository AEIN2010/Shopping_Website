package edu.miu.orderService.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payment model class
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Payment {
    private Long id;
    private String userId;
    private String orderId;
    private Double amount;
    private String currency;
    private String cardNumber;
    private String cardType;
}
