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

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
	private Payment payment;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
	private Delivery delivery;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
	private Review review;

	// getter / setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getOrderMemo() {
        return orderMemo;
    }
    public void setOrderMemo(String orderMemo) {
        this.orderMemo = orderMemo;
    }

}
