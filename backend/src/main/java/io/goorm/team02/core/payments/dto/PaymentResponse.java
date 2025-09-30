package io.goorm.team02.core.payments.dto;

import java.math.BigDecimal;

public class PaymentResponse {
    private String paymentKey;
    private Long orderId;
    private BigDecimal amount;
    private String status;

    public PaymentResponse(String paymentKey, Long orderId, BigDecimal amount, String status) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}
