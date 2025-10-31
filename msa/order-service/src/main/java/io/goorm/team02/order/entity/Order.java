package io.goorm.team02.order.entity;

import io.goorm.team02.common.config.BaseEntity;
import io.goorm.team02.dto.orders.OrderResponse;
import io.goorm.team02.order.service.dto.OrderData;
import io.goorm.team02.order.entity.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

	@Column(nullable = false, unique = true, length = 50)
	private String orderNumber;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long storeId;

	@Column(nullable = false)
	private String storeName;

	@Column(nullable = false)
	private String storePhone;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String storeAddress;

	@Column(length = 100)
	private String storeDetailAddress;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String deliveryAddress;

	@Column(length = 100)
	private String deliveryDetailAddress;

	@Column(nullable = false, length = 20)
	private String phone;

	@Column(columnDefinition = "TEXT")
	private String orderMemo;

	@Column(nullable = false)
	private int menuTotalAmount = 0;

	@Column(nullable = false)
	private int discountAmount = 0;

	@Column(nullable = false)
	private int deliveryFee = 0;

	@Column(nullable = false)
	private int totalAmount = 0;

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

	@Column(nullable = false)
	private int minCookingTime = 0;
	@Column(nullable = false)
	private int maxCookingTime = 0;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems;

	/**
	 * 주문 총액 계산 (메뉴 총액 + 배달비 - 할인)
	 */
	public void calculateTotalAmount() {
		int menuTotal = calculateMenuTotalAmount();
		this.menuTotalAmount = menuTotal;

		this.totalAmount = menuTotal + deliveryFee - discountAmount;
	}

	/**
	 * 메뉴 총액 계산
	 */
	private int calculateMenuTotalAmount() {
		if (orderItems == null || orderItems.isEmpty()) {
			return 0;
		}

		return orderItems.stream()
				.mapToInt(OrderItem::getTotalPrice)
				.sum();
	}

	/**
	 * 주문 번호 생성
	 */
	public void generateOrderNumber() {
		String generatedOrderNumber = String.format(
				"ORDER-%s",
				UUID.randomUUID().toString().substring(0, 8).toUpperCase());

		this.orderNumber = generatedOrderNumber;
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
	public void accept(int minCookingTime, int maxCookingTime) {
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

	/**
	 * 주문과 주문 아이템들을 함께 생성하는 팩토리 메서드
	 */
	public static Order create(OrderData orderData) {
		Order order = new Order();
		order.setUserId(orderData.userId());
		order.setStoreId(orderData.storeId());
		order.setDeliveryAddress(orderData.deliveryAddress());
		order.setDeliveryDetailAddress(orderData.deliveryDetailAddress());
		order.setPhone(orderData.phone());
		order.setOrderMemo(orderData.orderMemo());

		// 가게 정보 설정
		order.setStoreName(orderData.storeName());
		order.setStorePhone(orderData.storePhone());
		order.setStoreAddress(orderData.storeAddress());
		order.setStoreDetailAddress(orderData.storeDetailAddress());
		order.setDeliveryFee(orderData.deliveryFee());

		List<OrderItem> orderItems = orderData.orderItems().stream()
				.map(itemData -> OrderItem.create(order, itemData))
				.toList();
		order.setOrderItems(orderItems);
		order.calculateTotalAmount();
		order.generateOrderNumber();

		return order;
	}

	/**
	 * OrderResponse로 변환
	 */
	public OrderResponse toResponse() {
		return new OrderResponse(
				this.id,
				this.orderNumber,
				this.userId,
				this.storeId,
				this.deliveryAddress,
				this.deliveryDetailAddress,
				this.storeAddress,
				this.storeDetailAddress,
				this.phone,
				this.orderMemo,
				this.menuTotalAmount,
				this.deliveryFee,
				this.discountAmount,
				this.totalAmount,
				this.status.name(),
				this.orderedAt,
				this.acceptedAt,
				this.cookingStartedAt,
				this.cookingCompletedAt,
				this.pickedUpAt,
				this.deliveredAt,
				this.cancelledAt,
				this.rejectReason,
				this.cancelReason,
				this.minCookingTime,
				this.maxCookingTime,
				this.orderItems != null ? this.orderItems.stream()
						.map(OrderItem::toResponse)
						.toList() : List.of());
	}

}
