package io.goorm.team02.core.orders.controller;

import io.goorm.team02.core.orders.controller.dto.OrderRequest;
import io.goorm.team02.core.orders.controller.dto.OrderResponse;
import io.goorm.team02.core.orders.controller.dto.OrderRejectRequest;
import io.goorm.team02.core.orders.controller.dto.OrderAcceptRequest;
import io.goorm.team02.core.orders.controller.dto.OrderCancelRequest;
import io.goorm.team02.core.orders.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

	@PostMapping
	public OrderResponse create(@RequestBody OrderRequest order) {
		return orderService.create(order);
	}

	@GetMapping
	public List<OrderResponse> getAll(@RequestParam("storeId") Long storeId) {
		return orderService.getAll(storeId);
	}

	@GetMapping("/{orderId}")
	public OrderResponse getOrderDetail(@PathVariable Long orderId) {
		return orderService.getOrderDetail(orderId);
	}

	/**
	 * 가게에서 주문 수락 (예상 조리 시간 포함)
	 */
	@PutMapping("/{orderId}/accept")
	public OrderResponse acceptOrder(@PathVariable Long orderId, @RequestBody OrderAcceptRequest request) {
		return orderService.acceptOrder(orderId, request);
	}

	/**
	 * 가게에서 주문 거절
	 */
	@PutMapping("/{orderId}/reject")
	public OrderResponse rejectOrder(@PathVariable Long orderId, @RequestBody OrderRejectRequest request) {
		return orderService.rejectOrder(orderId, request);
	}

	/**
	 * 가게에서 조리 시작
	 */
	@PutMapping("/{orderId}/start-cooking")
	public OrderResponse startCooking(@PathVariable Long orderId) {
		return orderService.startCooking(orderId);
	}

	/**
	 * 가게에서 조리 완료
	 */
	@PutMapping("/{orderId}/complete-cooking")
	public OrderResponse completeCooking(@PathVariable Long orderId) {
		return orderService.completeCooking(orderId);
	}

	/**
	 * 배달 완료
	 */
	@PutMapping("/{orderId}/deliver")
	public OrderResponse deliverOrder(@PathVariable Long orderId) {
		return orderService.deliverOrder(orderId);
	}

	/**
	 * 주문 취소
	 */
	@PutMapping("/{orderId}/cancel")
	public OrderResponse cancelOrder(@PathVariable Long orderId, @RequestBody OrderCancelRequest request) {
		return orderService.cancelOrder(orderId, request);
	}

}