package io.goorm.team02.core.orders.domain;

import io.goorm.team02.core.common.domain.BaseEntity;
import io.goorm.team02.core.deliveries.domain.Delivery;
import io.goorm.team02.core.orders.domain.enums.OrderStatus;
import io.goorm.team02.core.payments.domain.Payment;
import io.goorm.team02.core.reviews.domain.Review;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.users.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String deliveryAddress;

	@Column(name = "delivery_detail_address", length = 100)
	private String deliveryDetailAddress;

	@Column(nullable = false, length = 20)
	private String phone;

	@Column(columnDefinition = "TEXT")
	private String orderMemo;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal menuTotalAmount;

	@Column(precision = 10, scale = 2)
	private BigDecimal deliveryFee = BigDecimal.ZERO;

	@Column(precision = 10, scale = 2)
	private BigDecimal discountAmount = BigDecimal.ZERO;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal totalAmount;

	@Enumerated(EnumType.STRING)
	private OrderStatus status = OrderStatus.PENDING;

	private LocalDateTime orderedAt = LocalDateTime.now();
	private LocalDateTime acceptedAt;
	private LocalDateTime cookingStartedAt;
	private LocalDateTime cookingCompletedAt;
	private LocalDateTime deliveredAt;
	private LocalDateTime cancelledAt;

	@Column(name = "reject_reason", length = 500)
	private String rejectReason;

	@Column(name = "cancel_reason", length = 500)
	private String cancelReason;

	@Column(name = "min_cooking_time")
	private Integer minCookingTime;

	@Column(name = "max_cooking_time")
	private Integer maxCookingTime;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
	private Payment payment;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
	private Delivery delivery;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
	private Review review;

	// 도메인 비즈니스 로직
	/**
	 * 주문 총액 계산 (메뉴 총액 + 배달비 - 할인)
	 */
	public void calculateTotalAmount() {
		BigDecimal menuTotal = calculateMenuTotalAmount();
		this.menuTotalAmount = menuTotal;
		this.totalAmount = menuTotal.add(this.deliveryFee).subtract(this.discountAmount);
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
	 * 상태별 타임스탬프 업데이트
	 */
	private void updateStatusTimestamp(OrderStatus status) {
		LocalDateTime now = LocalDateTime.now();
		switch (status) {
			case ACCEPTED -> this.acceptedAt = now;
			case COOKING -> this.cookingStartedAt = now;
			case READY -> this.cookingCompletedAt = now;
			case DELIVERED -> this.deliveredAt = now;
			case CANCELLED -> this.cancelledAt = now;
		}
	}
	
	/**
	 * 주문 검증
	 */
	public void validate() {
		if (user == null) {
			throw new IllegalStateException("사용자 정보가 필요합니다");
		}
		if (store == null) {
			throw new IllegalStateException("가게 정보가 필요합니다");
		}
		if (orderItems == null || orderItems.isEmpty()) {
			throw new IllegalStateException("주문 아이템이 필요합니다");
		}
		if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalStateException("주문 금액이 올바르지 않습니다");
		}
	}

}
