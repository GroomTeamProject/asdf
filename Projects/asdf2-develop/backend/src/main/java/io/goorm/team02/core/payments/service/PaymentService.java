package io.goorm.team02.core.payments.service;

import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.orders.repository.OrderRepository;
import io.goorm.team02.core.payments.domain.Payment;
import io.goorm.team02.core.payments.domain.enums.PaymentStatus;
import io.goorm.team02.core.payments.repository.PaymentRepository;
import io.goorm.team02.core.payments.dto.PaymentConfirmRequest;
import io.goorm.team02.core.payments.dto.PaymentResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;


@Service
public class PaymentService {

    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${toss.secret-key}")
    private String secretKey;

    public PaymentService(RestTemplateBuilder builder,
                          PaymentRepository paymentRepository,
                          OrderRepository orderRepository) {
        this.restTemplate = builder.build();
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        // 토스 결제 승인 확인
        String url = "https://api.tosspayments.com/v1/payments/confirm";
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedKey);

        Map<String, Object> body = Map.of(
                "paymentKey", request.getPaymentKey(),
                "orderId", request.getOrderId(),
                "amount", request.getAmount()
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        PaymentResponse paymentResponse;
        try {
            ResponseEntity<PaymentResponse> response =
                    restTemplate.exchange(url, org.springframework.http.HttpMethod.POST, entity, PaymentResponse.class);
            paymentResponse = response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("결제 승인 실패: " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            throw new RuntimeException("결제 API 호출 실패", e);
        }

        // 주문 객체 가져오기
        Order latestOrder = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다"));


        // Payment 생성
        Payment payment = new Payment();
        payment.setOrder(latestOrder); // Order 연결
        payment.setAmount(BigDecimal.valueOf(request.getAmount())); // int → BigDecimal 변환
        payment.setPaymentKey(request.getPaymentKey());
        payment.setPaymentMethod("Toss");
        payment.setPgProvider(request.getPgProvider());
        payment.setStatus(PaymentStatus.COMPLETED); // 상태 설정
        payment.setCreatedAt(LocalDateTime.now());

        // DB 저장
        paymentRepository.save(payment);

        return paymentResponse;
    }
}
