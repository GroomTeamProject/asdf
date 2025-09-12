package io.goorm.team02.core.orders.domain;

import io.goorm.team02.core.menus.domain.Menu;
import io.goorm.team02.core.orders.controller.dto.OrderRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@Column(name = "menu_name", nullable = false, length = 100)
	private String menuName; // 주문 당시 스냅샷

	@Column(name = "menu_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal menuPrice;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal totalPrice;

	@OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItemOption> options;

	// 도메인 비즈니스 로직
	/**
	 * 주문 아이템 총액 계산 (메뉴 가격 × 수량 + 옵션 추가 가격)
	 */
	public void calculateTotalPrice() {
		BigDecimal menuPrice = this.menuPrice != null ? this.menuPrice : BigDecimal.ZERO;
		Integer quantity = this.quantity != null ? this.quantity : 0;

		BigDecimal basePrice = menuPrice.multiply(BigDecimal.valueOf(quantity));
		BigDecimal optionPrice = calculateOptionPrice();

		// null 체크 추가
		if (basePrice == null)
			basePrice = BigDecimal.ZERO;
		if (optionPrice == null)
			optionPrice = BigDecimal.ZERO;

		this.totalPrice = basePrice.add(optionPrice);
	}

	/**
	 * 옵션 추가 가격 계산 (수량 반영)
	 */
	private BigDecimal calculateOptionPrice() {
		if (options == null || options.isEmpty()) {
			return BigDecimal.ZERO;
		}

		// 옵션 가격의 합계에 수량을 곱함
		BigDecimal totalOptionPrice = options.stream()
				.map(OrderItemOption::getAdditionalPrice)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Integer quantity = this.quantity != null ? this.quantity : 0;
		return totalOptionPrice.multiply(BigDecimal.valueOf(quantity));
	}

	/**
	 * 주문 아이템 생성 팩토리 메서드
	 */
	public static OrderItem create(Order order, Menu menu, Integer quantity, List<OrderItemOption> options) {
		OrderItem orderItem = new OrderItem();
		orderItem.setOrder(order);
		orderItem.setMenu(menu);
		orderItem.setMenuName(menu.getName());
		orderItem.setMenuPrice(menu.getPrice());
		orderItem.setQuantity(quantity);
		orderItem.setOptions(options != null ? options : new ArrayList<>());
		orderItem.calculateTotalPrice();
		return orderItem;
	}

	/**
	 * OrderRequest에서 OrderItem 리스트 생성 (도메인 로직)
	 */
	public static List<OrderItem> create(Order order, List<OrderRequest.OrderItemRequest> itemRequests,
			Map<Long, Menu> menuMap) {

		List<OrderItem> orderItems = new ArrayList<>();

		for (OrderRequest.OrderItemRequest itemRequest : itemRequests) {
			Menu menu = menuMap.get(itemRequest.menuId());
			if (menu == null) {
				throw new IllegalArgumentException("메뉴를 찾을 수 없습니다: " + itemRequest.menuId());
			}

			// OrderItem 생성
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setMenu(menu);
			orderItem.setMenuName(menu.getName());
			orderItem.setMenuPrice(menu.getPrice());
			orderItem.setQuantity(itemRequest.quantity());

			// 옵션 생성 (OrderItemOption 도메인에 위임)
			List<OrderItemOption> options = OrderItemOption.create(orderItem, itemRequest.options());
			orderItem.setOptions(options);

			// 총액 계산
			orderItem.calculateTotalPrice();

			orderItems.add(orderItem);
		}

		return orderItems;
	}

}
