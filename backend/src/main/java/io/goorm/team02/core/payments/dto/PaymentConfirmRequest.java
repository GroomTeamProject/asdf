package io.goorm.team02.core.payments.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class PaymentConfirmRequest {
    private String userId;
    private String paymentKey;
    private String orderId;
    private int amount;
    private String pgProvider;
    private String paymentMethod;
    private String pgTid;
}