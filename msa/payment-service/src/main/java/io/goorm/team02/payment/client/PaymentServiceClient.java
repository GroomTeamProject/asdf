package io.goorm.team02.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 실제 서비스 URL을 지정하거나, 테스트 환경에서는 MockBean으로 대체
@FeignClient(name = "order-service", url = "${msa.gateway.url:http://localhost:8080}")
public interface PaymentServiceClient {

    @GetMapping("/api/orders/events/{orderId}")
    OrderEventResponse getOrderEvent(@PathVariable("orderId") String orderId);

    // DTO는 내부 클래스나 별도 파일로 분리 가능
    class OrderEventResponse {
        private String orderId;
        private String status;

        public OrderEventResponse(String orderId, String status) {
            this.orderId = orderId;
            this.status = status;
        }

        public String getOrderId() { return orderId; }
        public String getStatus() { return status; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public void setStatus(String status) { this.status = status; }
    }
}
