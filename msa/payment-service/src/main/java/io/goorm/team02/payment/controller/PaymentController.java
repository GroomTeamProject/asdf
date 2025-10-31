package io.goorm.team02.payment.controller;

import io.goorm.team02.payment.dto.PaymentConfirmRequest;
import io.goorm.team02.payment.dto.PaymentResponse;
import io.goorm.team02.payment.service.PaymentService;
import io.goorm.team02.common.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/callback")
    public ApiResponse<PaymentResponse> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        log.info("==== 결제 콜백 요청 데이터 확인 ====");
        log.info("paymentKey: {}, orderId: {}, amount: {}",
                request.getPaymentKey(), request.getOrderId(), request.getAmount());
        log.info("=================================");

        try {
            // 결제 승인 처리 (PaymentService 내부에서 이벤트 발행 포함)
            PaymentResponse response = paymentService.confirmPayment(request);

            log.info("==== 결제 API 응답 확인 ====");
            log.info(response.toString());
            log.info("=========================");

            return ApiResponse.ok(response);

        } catch (Exception e) {
            log.error("==== 결제 승인 실패 ====", e);

            // Controller에서 중복 이벤트 발행 제거
            return ApiResponse.fail("결제 승인 실패: " + e.getMessage());
        }
    }
}
