package io.goorm.team02.core.payments.controller;

import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.orders.domain.enums.OrderStatus;
import io.goorm.team02.core.orders.repository.OrderRepository;
import io.goorm.team02.core.payments.domain.Payment;
import io.goorm.team02.core.payments.dto.PaymentConfirmRequest;
import io.goorm.team02.core.payments.dto.PaymentResponse;
import io.goorm.team02.core.payments.service.PaymentService;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/callback")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        System.out.println("==== 결제 콜백 요청 데이터 확인 ====");
        System.out.println("paymentKey: " + request.getPaymentKey());
        System.out.println("orderId: " + request.getOrderId());
        System.out.println("amount: " + request.getAmount());
        System.out.println("=================================");

        try {
            PaymentResponse response = paymentService.confirmPayment(request);

            System.out.println("==== Toss API 응답 데이터 확인 ====");
            System.out.println(response);
            System.out.println("=================================");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("==== 결제 승인 실패 ====");
            e.printStackTrace();
            System.out.println("=======================");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "결제 승인 실패",
                            "detail", e.getMessage()
                    ));
        }
    }
}