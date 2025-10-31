package io.goorm.team02.core.payments.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentResponse {
    private String paymentKey;
    private String orderId;
    private int amount;
    private String method;
    private String tid;
    private String status;
}