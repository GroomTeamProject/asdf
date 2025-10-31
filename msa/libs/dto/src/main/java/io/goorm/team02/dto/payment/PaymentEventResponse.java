package io.goorm.team02.payment.event.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentEventResponse {
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
    private String method;
    private String status;
    private String pgProvider;   // 이벤트 수신자가 결제사 구분 가능하도록 추가
}
