package io.goorm.team02.core.payments.service;

import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.orders.repository.OrderRepository;
import io.goorm.team02.core.payments.domain.Payment;
import io.goorm.team02.core.payments.domain.enums.PaymentStatus;
import io.goorm.team02.core.payments.repository.PaymentRepository;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    private static final String TOSS_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String TOSS_API_URL = "https://api.tosspayments.com/v1/payments/";

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
            RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    // 결제 완료
    @Transactional
    public Payment completePayment(Order order, String paymentKey, String pgProvider, String pgTid, BigDecimal amount) {
        // 기본 검증
        if (order == null || paymentKey == null || paymentKey.isEmpty() || amount == null) {
            throw new IllegalArgumentException("필수 정보가 누락되었습니다.");
        }

        // TODO: Toss API 호출 - 결제 정보 검증
        System.out.println("결제 키: " + paymentKey + ", 금액: " + amount);

        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseGet(() -> {
                    Payment newPayment = new Payment();
                    newPayment.setPaymentKey(paymentKey);
                    newPayment.setOrder(order);
                    return newPayment;
                });

        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPgProvider(pgProvider != null ? pgProvider : "toss");
        payment.setPgTid(pgTid != null ? pgTid : "");
        payment.setPaymentMethod("CARD");

        return paymentRepository.save(payment);
    }

    // 결제 상태 변경
    @Transactional
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus newStatus) {
        if (paymentId == null) {
            throw new IllegalArgumentException("결제 ID가 없습니다.");
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제를 찾을 수 없음"));

        if (payment.getStatus() == PaymentStatus.CANCELLED || payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalStateException("이미 종료된 결제는 상태 변경 불가");
        }

        payment.setStatus(newStatus != null ? newStatus : payment.getStatus());
        return paymentRepository.save(payment);
    }

    public void cancelPayment(String paymentKey, BigDecimal amount) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6", "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("cancelAmount", amount.longValue());
        body.put("cancelReason", "사용자 주문 취소");
        body.put("taxFreeAmount", 0L);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel",
                request,
                String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Toss 결제 취소 실패");
        }
    }

}
