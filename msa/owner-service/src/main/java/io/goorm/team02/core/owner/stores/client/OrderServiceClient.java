package io.goorm.team02.core.owner.stores.client;

import io.goorm.team02.dto.orders.OrderDashboardDto;
import io.goorm.team02.dto.orders.RecentOrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "order-service", url = "${order.service.url}")
public interface OrderServiceClient {

    @GetMapping("/api/orders/store/{storeId}/dashboard")
    OrderDashboardDto getDashboardData(@PathVariable("storeId") Long storeId);

    @GetMapping("/api/orders/store/{storeId}/today/count")
    Long getTodayOrderCount(@PathVariable("storeId") Long storeId);

    @GetMapping("/api/orders/store/{storeId}/today/revenue")
    BigDecimal getTodayRevenue(@PathVariable("storeId") Long storeId);

    @GetMapping("/api/orders/store/{storeId}/total/count")
    Long getTotalOrderCount(@PathVariable("storeId") Long storeId);

    @GetMapping("/api/orders/store/{storeId}/recent")
    List<RecentOrderDto> getRecentOrders(
            @PathVariable("storeId") Long storeId,
            @RequestParam("limit") int limit);
}
