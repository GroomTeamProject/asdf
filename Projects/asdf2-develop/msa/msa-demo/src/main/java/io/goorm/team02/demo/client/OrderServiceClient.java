package io.goorm.team02.demo.client;

import io.goorm.team02.demo.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${msa.gateway.url:http://localhost:8080}")
public interface OrderServiceClient {

    @GetMapping("/api/orders/{id}")
    OrderResponse getOrderById(@PathVariable("id") Long id);
}