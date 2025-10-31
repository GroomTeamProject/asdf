package io.goorm.team02.order.entity;

import io.goorm.team02.dto.orders.OrderResponse;
import io.goorm.team02.order.service.dto.OrderItemData;
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

    @Column(nullable = false)
    private Long menuId;

    @Column(nullable = false, length = 100)
    private String menuName; // 주문 당시 메뉴명 스냅샷

    @Column(nullable = false)
    private int menuPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int totalPrice;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemOption> options;

    /**
     * 주문 아이템 총액 계산 (메뉴 가격 × 수량 + 옵션 추가 가격)
     */
    public void calculateTotalPrice() {
        int basePrice = menuPrice * quantity;
        int optionPrice = calculateOptionPrice();
        this.totalPrice = basePrice + optionPrice;
    }

    /**
     * 옵션 추가 가격 계산 (수량 반영)
     */
    private int calculateOptionPrice() {
        if (options == null || options.isEmpty()) {
            return 0;
        }

        // 옵션 가격의 합계에 수량을 곱함
        int totalOptionPrice = options.stream()
                .mapToInt(OrderItemOption::getAdditionalPrice)
                .sum();

        return totalOptionPrice * quantity;
    }

    /**
     * 주문 아이템 생성 (OrderItemData 기반)
     */
    public static OrderItem create(Order order, OrderItemData itemData) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenuId(itemData.menuId());
        orderItem.setQuantity(itemData.quantity());
        orderItem.setMenuName(itemData.menuName());
        orderItem.setMenuPrice(itemData.menuPrice());

        if (itemData.options() != null) {
            List<OrderItemOption> options = itemData.options().stream()
                    .map(optionData -> OrderItemOption.create(orderItem, optionData))
                    .toList();
            orderItem.setOptions(options);
        }
        orderItem.calculateTotalPrice();

        return orderItem;
    }

    /**
     * OrderItemResponse로 변환
     */
    public OrderResponse.OrderItemResponse toResponse() {
        return new OrderResponse.OrderItemResponse(
                this.id,
                this.menuId,
                this.menuName,
                this.menuPrice,
                this.quantity,
                this.totalPrice,
                this.options != null ? this.options.stream()
                        .map(OrderItemOption::toResponse)
                        .toList() : List.of());
    }
}
