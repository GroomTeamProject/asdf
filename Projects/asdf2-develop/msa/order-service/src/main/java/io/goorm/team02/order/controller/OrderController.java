package io.goorm.team02.order.controller;

import io.goorm.team02.order.controller.dto.OrderRequest;
import io.goorm.team02.order.controller.dto.OrderResponse;
import io.goorm.team02.order.controller.dto.OrderRejectRequest;
import io.goorm.team02.order.controller.dto.OrderAcceptRequest;
import io.goorm.team02.order.controller.dto.OrderCancelRequest;
import io.goorm.team02.order.controller.dto.OrderSearchRequest;
import io.goorm.team02.order.entity.Order;
import io.goorm.team02.order.service.OrderStatusService;
import io.goorm.team02.order.service.OrderService;
import io.goorm.team02.security.annotation.CurrentUser;
import jakarta.validation.Valid;
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
    public OrderResponse create(@Valid @RequestBody OrderRequest orderRequest, @RequestParam Long userId) {
        Order order = orderService.create(orderRequest, userId);
        return OrderResponse.from(order);
    }

    @GetMapping
    public Page<OrderResponse> getAllByParams(OrderSearchRequest searchRequest, @RequestParam Long userId) {
        Page<Order> orders = orderService.getAllByParams(searchRequest, userId);
        return orders.map(OrderResponse::from);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderDetail(@PathVariable Long orderId, @RequestParam Long userId) {
        Order order = orderService.getOrderDetail(orderId, userId);
        return OrderResponse.from(order);
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
     * 픽업 가능한 주문 목록 조회 (배달 기사용)
     */
    @GetMapping("/delivery/available")
    public List<OrderResponse> getAvailableOrders(
            @RequestParam(value = "storeId", required = false) Long storeId) {
        return orderService.getAvailableOrders(storeId);
    }

}
