package io.goorm.team02.order.entity;

import io.goorm.team02.dto.orders.OrderResponse;
import io.goorm.team02.order.service.dto.OrderItemData;
import io.goorm.team02.order.service.dto.OrderItemOptionData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderItem 도메인 테스트")
class OrderItemTest {

    private OrderItem orderItem;
    private Order order;

    @BeforeEach
    void setUp() {
        orderItem = new OrderItem();
        order = new Order();
    }

    @Test
    @DisplayName("calculateTotalPrice - 옵션이 없는 경우")
    void calculateTotalPriceWithoutOptions() {
        // given
        orderItem.setMenuPrice(18000);
        orderItem.setQuantity(2);
        orderItem.setOptions(new ArrayList<>());

        // when
        orderItem.calculateTotalPrice();

        // then
        assertThat(orderItem.getTotalPrice()).isEqualTo(36000);
    }

    @Test
    @DisplayName("calculateTotalPrice - 옵션이 있는 경우")
    void calculateTotalPriceWithOptions() {
        // given
        orderItem.setMenuPrice(18000);
        orderItem.setQuantity(2);

        List<OrderItemOption> options = new ArrayList<>();
        OrderItemOption option1 = new OrderItemOption();
        option1.setAdditionalPrice(1000);
        options.add(option1);

        OrderItemOption option2 = new OrderItemOption();
        option2.setAdditionalPrice(500);
        options.add(option2);

        orderItem.setOptions(options);

        // when
        orderItem.calculateTotalPrice();

        // then
        // 메뉴 가격: 18000 * 2 = 36000
        // 옵션 가격: (1000 + 500) * 2 = 3000
        // 총 가격: 36000 + 3000 = 39000
        assertThat(orderItem.getTotalPrice()).isEqualTo(39000);
    }

    @Test
    @DisplayName("calculateTotalPrice - menuPrice가 null인 경우")
    void calculateTotalPriceWithNullMenuPrice() {
        // given
        orderItem.setMenuPrice(0);
        orderItem.setQuantity(2);
        orderItem.setOptions(new ArrayList<>());

        // when
        orderItem.calculateTotalPrice();

        // then
        assertThat(orderItem.getTotalPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("calculateTotalPrice - quantity가 0인 경우")
    void calculateTotalPriceWithZeroQuantity() {
        // given
        orderItem.setMenuPrice(18000);
        orderItem.setQuantity(0);
        orderItem.setOptions(new ArrayList<>());

        // when
        orderItem.calculateTotalPrice();

        // then
        // quantity가 0이면: 18000 * 0 = 0
        assertThat(orderItem.getTotalPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("calculateTotalPrice - options가 null인 경우")
    void calculateTotalPriceWithNullOptions() {
        // given
        orderItem.setMenuPrice(18000);
        orderItem.setQuantity(2);
        orderItem.setOptions(null);

        // when
        orderItem.calculateTotalPrice();

        // then
        assertThat(orderItem.getTotalPrice()).isEqualTo(36000);
    }

    @Test
    @DisplayName("calculateTotalPrice - 옵션에 0 additionalPrice가 있는 경우")
    void calculateTotalPriceWithZeroAdditionalPrice() {
        // given
        orderItem.setMenuPrice(18000);
        orderItem.setQuantity(2);

        List<OrderItemOption> options = new ArrayList<>();
        OrderItemOption option1 = new OrderItemOption();
        option1.setAdditionalPrice(1000);
        options.add(option1);

        OrderItemOption option2 = new OrderItemOption();
        option2.setAdditionalPrice(0); // 0 값
        options.add(option2);

        orderItem.setOptions(options);

        // when
        orderItem.calculateTotalPrice();

        // then
        // 메뉴 가격: 18000 * 2 = 36000
        // 옵션 가격: (1000 + 0) * 2 = 2000
        // 총 가격: 36000 + 2000 = 38000
        assertThat(orderItem.getTotalPrice()).isEqualTo(38000);
    }

    @Test
    @DisplayName("calculateOptionPrice - 빈 옵션 리스트")
    void calculateOptionPriceWithEmptyList() {
        // given
        orderItem.setMenuPrice(18000);
        orderItem.setQuantity(2);
        orderItem.setOptions(new ArrayList<>());

        // when
        orderItem.calculateTotalPrice();

        // then
        assertThat(orderItem.getTotalPrice()).isEqualTo(36000);
    }

    @Test
    @DisplayName("calculateOptionPrice - null 옵션 리스트")
    void calculateOptionPriceWithNullList() {
        // given
        orderItem.setMenuPrice(18000);
        orderItem.setQuantity(2);
        orderItem.setOptions(null);

        // when
        orderItem.calculateTotalPrice();

        // then
        assertThat(orderItem.getTotalPrice()).isEqualTo(36000);
    }

    @Test
    @DisplayName("create - OrderItemData로 생성하는 팩토리 메서드")
    void createWithOrderItemData() {
        // given
        OrderItemData itemData = new OrderItemData(
                1L, // menuId
                2, // quantity
                "치킨버거", // menuName
                15000, // menuPrice
                List.of(createOrderItemOptionData())
        );

        // when
        OrderItem createdItem = OrderItem.create(order, itemData);

        // then
        assertThat(createdItem.getOrder()).isEqualTo(order);
        assertThat(createdItem.getMenuId()).isEqualTo(1L);
        assertThat(createdItem.getQuantity()).isEqualTo(2);
        assertThat(createdItem.getMenuName()).isEqualTo("치킨버거");
        assertThat(createdItem.getMenuPrice()).isEqualTo(15000);
        assertThat(createdItem.getOptions()).hasSize(1);
    }

    @Test
    @DisplayName("create - null 옵션 리스트")
    void createWithNullOptions() {
        // given
        OrderItemData itemData = new OrderItemData(
                1L, // menuId
                2, // quantity
                "치킨버거", // menuName
                15000, // menuPrice
                null // options
        );

        // when
        OrderItem createdItem = OrderItem.create(order, itemData);

        // then
        assertThat(createdItem.getOrder()).isEqualTo(order);
        assertThat(createdItem.getMenuId()).isEqualTo(1L);
        assertThat(createdItem.getQuantity()).isEqualTo(2);
        assertThat(createdItem.getMenuName()).isEqualTo("치킨버거");
        assertThat(createdItem.getMenuPrice()).isEqualTo(15000);
        assertThat(createdItem.getOptions()).isNull();
    }

    private OrderItemOptionData createOrderItemOptionData() {
        return new OrderItemOptionData(
                1L, // optionId
                "사이즈", // optionName
                "대", // optionItemName
                2000 // additionalPrice
        );
    }
}
