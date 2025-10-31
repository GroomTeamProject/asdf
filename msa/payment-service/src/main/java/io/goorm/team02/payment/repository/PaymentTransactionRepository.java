package io.goorm.team02.payment.repository;

import io.goorm.team02.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);
    List<PaymentTransaction> findByPaymentIdAndStatus(Long paymentId, String status);
}
