package io.goorm.team02.payment.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private String paymentKey;
    private String orderId;      // orderId 를 String 으로 두면 다른 서비스와 호환성 쉬움
    private BigDecimal amount;
    private String method;
    private String status;
    private String pgProvider;
}
