package io.goorm.team02.payment.entity;

import io.goorm.team02.payment.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(columnNames = "paymentKey"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;      // PG사 고유 키
    private Long orderId;           // Order Service ID
    private Long userId;
    private BigDecimal amount;

    @Builder.Default
    private String currency = "KRW";

    private String paymentMethod;
    private String pgProvider;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<PaymentTransaction> transactions;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<Refund> refunds;
}
