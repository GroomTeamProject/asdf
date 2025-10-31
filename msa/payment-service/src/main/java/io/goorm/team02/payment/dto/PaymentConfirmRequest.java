package io.goorm.team02.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequest {
    private String userId;           // 결제 사용자 ID
    private String paymentKey;       // PG 결제 key
    private String orderId;          // 주문 서비스에서 발행한 주문 ID (문자열)
    private BigDecimal amount;       // 결제 금액
    private String currency;         // ex: "KRW"
    private String pgProvider;       // 결제사 (Toss, KakaoPay 등)
    private String paymentMethod;    // CARD, PAYCO 등
    private String pgTid;            // PG 결제 거래 번호
}
