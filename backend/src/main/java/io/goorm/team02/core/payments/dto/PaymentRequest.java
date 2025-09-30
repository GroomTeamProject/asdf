package io.goorm.team02.core.payments.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class PaymentRequest {
    private Long userId;
    private String paymentKey;
    private String pgProvider;
    private String pgTid;
    private BigDecimal amount;
}
