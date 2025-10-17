package io.goorm.team02.order.entity;

import io.goorm.team02.order.controller.dto.OrderRequest;
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
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

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
     * 주문 요청 기반 주문 아이템 생성 (임시 스냅샷)
     */
    public static OrderItem fromRequest(Order order, OrderRequest.OrderItemRequest itemRequest) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenuId(itemRequest.menuId());
        orderItem.setMenuName("");
        orderItem.setMenuPrice(BigDecimal.ZERO);
        orderItem.setQuantity(itemRequest.quantity());

        List<OrderItemOption> options = OrderItemOption.create(orderItem, itemRequest.options());
        orderItem.setOptions(options);

        orderItem.calculateTotalPrice();
        return orderItem;
    }
}
