package io.goorm.team02.order.controller;

import io.goorm.team02.dto.orders.OrderDashboardDto;
import io.goorm.team02.dto.orders.OrderRequest;
import io.goorm.team02.dto.orders.OrderResponse;
import io.goorm.team02.dto.orders.OrderRejectRequest;
import io.goorm.team02.dto.orders.OrderAcceptRequest;
import io.goorm.team02.dto.orders.OrderCancelRequest;
import io.goorm.team02.dto.orders.OrderSearchRequest;
import io.goorm.team02.dto.orders.OrderResponseForDelivery;
import io.goorm.team02.dto.orders.RecentOrderDto;
import io.goorm.team02.security.annotation.CurrentUser;
import io.goorm.team02.order.entity.Order;
import io.goorm.team02.order.service.OrderStatusService;
import io.goorm.team02.order.service.OrderService;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController implements OrderControllerDocs {

    private final OrderService orderService;
    private final OrderStatusService orderStatusService;

    @PostMapping
    public OrderResponse create(@Valid @RequestBody OrderRequest orderRequest, @CurrentUser Long userId) {
        Order order = orderService.create(orderRequest, userId);
        return order.toResponse();
    }

    @GetMapping
    public Page<OrderResponse> getAllByParams(OrderSearchRequest searchRequest, @CurrentUser Long userId) {
        Page<Order> orders = orderService.getAllByParams(searchRequest, userId);
        return orders.map(Order::toResponse);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderDetail(@PathVariable Long orderId, @CurrentUser Long userId) {
        Order order = orderService.getOrderDetail(orderId, userId);
        return order.toResponse();
    }

    /**
     * 가게에서 주문 수락 (예상 조리 시간 포함)
     */
    @PutMapping("/{orderId}/accept")
    public OrderResponse acceptOrder(@PathVariable Long orderId, @RequestBody OrderAcceptRequest request) {
        return orderStatusService.acceptOrder(orderId, request);
    }

    /**
     * 가게에서 주문 거절
     */
    @PutMapping("/{orderId}/reject")
    public OrderResponse rejectOrder(@PathVariable Long orderId, @RequestBody OrderRejectRequest request) {
        return orderStatusService.rejectOrder(orderId, request);
    }

    /**
     * 가게에서 조리 시작
     */
    @PutMapping("/{orderId}/start-cooking")
    public OrderResponse startCooking(@PathVariable Long orderId) {
        return orderStatusService.startCooking(orderId);
    }

    /**
     * 가게에서 조리 완료
     */
    @PutMapping("/{orderId}/complete-cooking")
    public OrderResponse completeCooking(@PathVariable Long orderId) {
        return orderStatusService.completeCooking(orderId);
    }

    /**
     * 배달 시작
     */
    @PutMapping("/{orderId}/start-delivery")
    public OrderResponse startDelivery(@PathVariable Long orderId) {
        return orderStatusService.startDelivery(orderId);
    }

    /**
     * 배달 완료
     */
    @PutMapping("/{orderId}/deliver")
    public OrderResponse deliverOrder(@PathVariable Long orderId) {
        return orderStatusService.deliverOrder(orderId);
    }

    /**
     * 주문 취소
     */
    @PutMapping("/{orderId}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long orderId, @RequestBody OrderCancelRequest request) {
        return orderStatusService.cancelOrder(orderId, request);
    }

    /**
     * 라이더 - 주문 상세 조회
     */
    @GetMapping("/delivery/{orderId}")
    public OrderResponseForDelivery getOrderDetailForDelivery(@PathVariable Long orderId) {
        return orderService.getOrderDetailForDelivery(orderId);
    }

    /**
     * 픽업 가능한 주문 목록 조회 (배달 기사용)
     */
    @GetMapping("/delivery/available")
    public List<OrderResponse> getAvailableOrders(
            @RequestParam(value = "storeId", required = false) Long storeId) {
        return orderService.getAvailableOrders(storeId);
    }

    /**
     * 가게별 대시보드 데이터 조회 (Store 서비스용)
     */
    @GetMapping("/store/{storeId}/dashboard")
    public OrderDashboardDto getDashboardData(@PathVariable Long storeId) {
        return orderService.getDashboardData(storeId);
    }

    /**
     * 가게별 오늘 주문 개수 조회
     */
    @GetMapping("/store/{storeId}/today/count")
    public Long getTodayOrderCount(@PathVariable Long storeId) {
        return orderService.getTodayOrderCount(storeId);
    }

    /**
     * 가게별 오늘 매출 조회
     */
    @GetMapping("/store/{storeId}/today/revenue")
    public BigDecimal getTodayRevenue(@PathVariable Long storeId) {
        return orderService.getTodayRevenue(storeId);
    }

    /**
     * 가게별 총 주문 개수 조회
     */
    @GetMapping("/store/{storeId}/total/count")
    public Long getTotalOrderCount(@PathVariable Long storeId) {
        return orderService.getTotalOrderCount(storeId);
    }

    /**
     * 가게별 최근 주문 조회
     */
    @GetMapping("/store/{storeId}/recent")
    public List<RecentOrderDto> getRecentOrders(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "5") int limit) {
        return orderService.getRecentOrders(storeId, limit);
    }

}
