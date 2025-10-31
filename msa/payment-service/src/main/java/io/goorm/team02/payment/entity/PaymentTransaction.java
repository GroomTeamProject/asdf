package io.goorm.team02.payment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private String type; // ATTEMPT, APPROVAL, FAIL
    private String status; // PENDING, COMPLETED, FAILED
    private Integer amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
