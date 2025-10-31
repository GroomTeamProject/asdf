package io.goorm.team02.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentResponse {
    private String paymentKey;
    private String orderId;      // 주문 이벤트에서 받은 문자열 ID
    private BigDecimal amount;
    private String method;       // 결제 수단
    private String tid;          // PG 거래 번호
    private String status;       // PENDING, COMPLETED, FAILED
    private String pgProvider;   // 결제사
}
