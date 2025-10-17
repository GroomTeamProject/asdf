package io.goorm.team02.order.entity;

import io.goorm.team02.common.config.BaseEntity;
import io.goorm.team02.order.controller.dto.OrderRequest;
import io.goorm.team02.order.entity.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_number", nullable = false, unique = true, length = 50)
	private String orderNumber;

	@JoinColumn(name = "user_id", nullable = false)
	private Long userId;

	@JoinColumn(name = "store_id", nullable = false)
	private Long storeId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String deliveryAddress;

	@Column(name = "delivery_detail_address", length = 100)
	private String deliveryDetailAddress;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String storeAddress;

	@Column(name = "store_detail_address", length = 100)
	private String storeDetailAddress;

	@Column(nullable = false, length = 20)
	private String phone;

	@Column(columnDefinition = "TEXT")
	private String orderMemo;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal menuTotalAmount = BigDecimal.ZERO;

	@Column(precision = 10, scale = 2)
	private BigDecimal discountAmount = BigDecimal.ZERO;

	@Column(precision = 10, scale = 2)
	private BigDecimal deliveryFee = BigDecimal.ZERO;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal totalAmount;

	@Enumerated(EnumType.STRING)
	private OrderStatus status = OrderStatus.PENDING;
	private LocalDateTime orderedAt = LocalDateTime.now();
	private LocalDateTime acceptedAt;
	private LocalDateTime cookingStartedAt;
	private LocalDateTime cookingCompletedAt;
	private LocalDateTime deliveredAt;
	private LocalDateTime pickedUpAt;
	private LocalDateTime cancelledAt;
	private LocalDateTime rejectedAt;

	@Column(name = "cancel_reason", length = 500)
	private String cancelReason;
	@Column(name = "reject_reason", length = 500)
	private String rejectReason;

	@Column(name = "min_cooking_time")
	private Integer minCookingTime;
	@Column(name = "max_cooking_time")
	private Integer maxCookingTime;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems;

	// 도메인 비즈니스 로직
	/**
	 * 주문 총액 계산 (메뉴 총액 + 배달비 - 할인)
	 */
	public void calculateTotalAmount() {
		BigDecimal menuTotal = calculateMenuTotalAmount();
		this.menuTotalAmount = menuTotal;

		// null 값 처리
		BigDecimal deliveryFee = this.deliveryFee != null ? this.deliveryFee : BigDecimal.ZERO;
		BigDecimal discountAmount = this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO;

		this.totalAmount = menuTotal.add(deliveryFee).subtract(discountAmount);
	}

	/**
	 * 메뉴 총액 계산
	 */
	private BigDecimal calculateMenuTotalAmount() {
		if (orderItems == null || orderItems.isEmpty()) {
			return BigDecimal.ZERO;
		}

		return orderItems.stream()
				.map(OrderItem::getTotalPrice)
				.filter(price -> price != null) // null 값 필터링
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * 주문 번호 생성
	 */
	public void generateOrderNumber() {
		this.orderNumber = "ORD-" + System.currentTimeMillis() + "-" +
				java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	/**
	 * 주문 상태 변경
	 */
	public void changeStatus(OrderStatus newStatus) {
		this.status = newStatus;
		updateStatusTimestamp(newStatus);
	}

	/**
	 * 주문 수락 (예상 조리 시간 포함)
	 */
	public void accept(Integer minCookingTime, Integer maxCookingTime) {
		if (this.status != OrderStatus.PENDING) {
			throw new IllegalStateException("수락할 수 없는 주문 상태입니다. 현재 상태: " + this.status);
		}

		this.minCookingTime = minCookingTime;
		this.maxCookingTime = maxCookingTime;
		changeStatus(OrderStatus.ACCEPTED);
	}

	/**
	 * 조리 시작
	 */
	public void startCooking() {
		if (this.status != OrderStatus.ACCEPTED) {
			throw new IllegalStateException("조리를 시작할 수 없는 주문 상태입니다. 현재 상태: " + this.status);
		}

		changeStatus(OrderStatus.COOKING);
	}

	/**
	 * 조리 완료
	 */
	public void completeCooking() {
		if (this.status != OrderStatus.COOKING) {
			throw new IllegalStateException("조리 완료할 수 없는 주문 상태입니다. 현재 상태: " + this.status);
		}

		changeStatus(OrderStatus.READY);
	}

	/**
	 * 배달 시작
	 */
	public void startDelivery() {
		if (this.status != OrderStatus.READY) {
			throw new IllegalStateException("배달을 시작할 수 없는 주문 상태입니다. 현재 상태: " + this.status);
		}

		changeStatus(OrderStatus.PICKED_UP);
	}

	/**
	 * 배달 완료
	 */
	public void deliver() {
		if (this.status != OrderStatus.PICKED_UP) {
			throw new IllegalStateException("배달 완료할 수 없는 주문 상태입니다. 현재 상태: " + this.status);
		}

		changeStatus(OrderStatus.DELIVERED);
	}

	/**
	 * 주문 취소
	 */
	public void cancel(String reason) {
		if (this.status != OrderStatus.PENDING && this.status != OrderStatus.ACCEPTED) {
			throw new IllegalStateException("취소할 수 없는 주문 상태입니다. 현재 상태: " + this.status);
		}

		this.cancelReason = reason;
		changeStatus(OrderStatus.CANCELLED);
	}

	/**
	 * 주문 거절
	 */
	public void reject(String reason) {
		if (this.status != OrderStatus.PENDING) {
			throw new IllegalStateException("거절할 수 없는 주문 상태입니다. 현재 상태: " + this.status);
		}

		this.rejectReason = reason;
		changeStatus(OrderStatus.REJECTED);
	}

	/**
	 * 상태별 타임스탬프 업데이트
	 */
	private void updateStatusTimestamp(OrderStatus status) {
		LocalDateTime now = LocalDateTime.now();

		switch (status) {
			case PENDING -> {
			} // PENDING은 타임스탬프 없음
			case ACCEPTED -> this.acceptedAt = now;
			case COOKING -> this.cookingStartedAt = now;
			case READY -> this.cookingCompletedAt = now;
			case PICKED_UP -> this.pickedUpAt = now;
			case DELIVERED -> this.deliveredAt = now;
			case CANCELLED -> this.cancelledAt = now;
			case REJECTED -> this.rejectedAt = now;
		}
	}

	public void validate() {
		if (userId == null) {
			throw new IllegalArgumentException("사용자 ID는 필수입니다.");
		}
		if (storeId == null) {
			throw new IllegalArgumentException("가게 ID는 필수입니다.");
		}
	}

	/**
	 * 주문과 주문 아이템들을 함께 생성하는 팩토리 메서드
	 */
	public static Order create(OrderRequest orderRequest, Long userId, Long storeId, BigDecimal deliveryFee) {
		Order order = new Order();
		order.setUserId(userId);
		order.setStoreId(storeId);
		order.setDeliveryAddress(orderRequest.deliveryAddress());
		order.setDeliveryDetailAddress(orderRequest.deliveryDetailAddress());
		order.setStoreAddress("");
		order.setStoreDetailAddress("");
		order.setPhone(orderRequest.phone());
		order.setOrderMemo(orderRequest.orderMemo());
		order.setDeliveryFee(deliveryFee);
		order.generateOrderNumber();
		order.setOrderedAt(LocalDateTime.now());
		order.setStatus(OrderStatus.PENDING);

		List<OrderItem> items = orderRequest.orderItems().stream()
			.map(req -> OrderItem.fromRequest(order, req))
			.toList();
		order.setOrderItems(items);

		order.calculateTotalAmount();
		return order;
	}

}
