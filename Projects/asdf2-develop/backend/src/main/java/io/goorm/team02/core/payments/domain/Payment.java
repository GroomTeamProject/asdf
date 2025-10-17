package io.goorm.team02.core.payments.domain;

import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.payments.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor

public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String paymentKey;

    private String pgProvider;

    private String pgTid;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    public Payment(BigDecimal amount, String paymentMethod, PaymentStatus status, String paymentKey,
                   String pgProvider, String pgTid, Order order) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentKey = paymentKey;
        this.pgProvider = pgProvider;
        this.pgTid = pgTid;
        this.order = order;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
