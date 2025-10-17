package io.goorm.team02.core.orders.domain;

import io.goorm.team02.core.menus.domain.Menu;
import io.goorm.team02.core.TestEnv;
import io.goorm.team02.core.orders.controller.dto.OrderRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrderItem 도메인 테스트")
class OrderItemTest extends TestEnv {

    private OrderItem orderItem;
    private Order order;
    private Menu menu;

    @BeforeEach
    void setUp() {
        orderItem = new OrderItem();
        order = new Order();
        menu = new Menu();
        menu.setName("후라이드 치킨");
        menu.setPrice(new BigDecimal("18000.00"));
    }

    @Test
    @DisplayName("calculateTotalPrice - 옵션이 없는 경우")
    void calculateTotalPriceWithoutOptions() {
        // given
        orderItem.setMenuPrice(new BigDecimal("18000.00"));
        orderItem.setQuantity(2);
        orderItem.setOptions(new ArrayList<>());

        // when
        orderItem.calculateTotalPrice();

        // then
        assertThat(orderItem.getTotalPrice()).isEqualTo(new BigDecimal("36000.00"));
    }

    @Test
    @DisplayName("calculateTotalPrice - 옵션이 있는 경우")
    void calculateTotalPriceWithOptions() {
        // given
        orderItem.setMenuPrice(new BigDecimal("18000.00"));
        orderItem.setQuantity(2);
        
        List<OrderItemOption> options = new ArrayList<>();
        OrderItemOption option1 = new OrderItemOption();
        option1.setAdditionalPrice(new BigDecimal("1000.00"));
        options.add(option1);
        
        OrderItemOption option2 = new OrderItemOption();
        option2.setAdditionalPrice(new BigDecimal("500.00"));
        options.add(option2);
        
        orderItem.setOptions(options);

        // when
        orderItem.calculateTotalPrice();

        // then
        // 메뉴 가격: 18000 * 2 = 36000
        // 옵션 가격: (1000 + 500) * 2 = 3000
        // 총 가격: 36000 + 3000 = 39000
        assertThat(orderItem.getTotalPrice()).isEqualTo(new BigDecimal("39000.00"));
    }

    @Test
    @DisplayName("calculateTotalPrice - menuPrice가 null인 경우")
    void calculateTotalPriceWithNullMenuPrice() {
        // given
        orderItem.setMenuPrice(null);
        orderItem.setQuantity(2);
        orderItem.setOptions(new ArrayList<>());

        // when
        orderItem.calculateTotalPrice();

        // then
        assertThat(orderItem.getTotalPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("calculateTotalPrice - quantity가 null인 경우")
    void calculateTotalPriceWithNullQuantity() {
        // given
        orderItem.setMenuPrice(new BigDecimal("18000.00"));
        orderItem.setQuantity(null);
        orderItem.setOptions(new ArrayList<>());

        // when
        orderItem.calculateTotalPrice();

        // then
        // quantity가 null이면 0으로 처리되므로: 18000 * 0 = 0
        assertThat(orderItem.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("calculateTotalPrice - options가 null인 경우")
    void calculateTotalPriceWithNullOptions() {
        // given
        orderItem.setMenuPrice(new BigDecimal("18000.00"));
        orderItem.setQuantity(2);
        orderItem.setOptions(null);

        // when
        orderItem.calculateTotalPrice();

        // then
        assertThat(orderItem.getTotalPrice()).isEqualTo(new BigDecimal("36000.00"));
    }

    @Test
    @DisplayName("calculateTotalPrice - 옵션에 null additionalPrice가 있는 경우")
    void calculateTotalPriceWithNullAdditionalPrice() {
        // given
        orderItem.setMenuPrice(new BigDecimal("18000.00"));
        orderItem.setQuantity(2);
        
        List<OrderItemOption> options = new ArrayList<>();
        OrderItemOption option1 = new OrderItemOption();
        option1.setAdditionalPrice(new BigDecimal("1000.00"));
        options.add(option1);
        
        OrderItemOption option2 = new OrderItemOption();
        option2.setAdditionalPrice(null); // null 값
        options.add(option2);
        
        orderItem.setOptions(options);

        // when
        orderItem.calculateTotalPrice();

        // then
        // 메뉴 가격: 18000 * 2 = 36000
        // 옵션 가격: (1000 + 0) * 2 = 2000 (null은 0으로 처리)
        // 총 가격: 36000 + 2000 = 38000
        assertThat(orderItem.getTotalPrice()).isEqualTo(new BigDecimal("38000.00"));
    }

    @Test
    @DisplayName("create - 팩토리 메서드로 OrderItem 생성")
    void createOrderItem() {
        // given
        List<OrderItemOption> options = new ArrayList<>();
        OrderItemOption option = new OrderItemOption();
        option.setAdditionalPrice(new BigDecimal("1000.00"));
        options.add(option);

        // when
        OrderItem createdItem = OrderItem.create(order, menu, 3, options);

        // then
        assertThat(createdItem.getOrder()).isEqualTo(order);
        assertThat(createdItem.getMenu()).isEqualTo(menu);
        assertThat(createdItem.getMenuName()).isEqualTo("후라이드 치킨");
        assertThat(createdItem.getMenuPrice()).isEqualTo(new BigDecimal("18000.00"));
        assertThat(createdItem.getQuantity()).isEqualTo(3);
        assertThat(createdItem.getOptions()).hasSize(1);
        
        // 총 가격 계산 확인: (18000 * 3) + (1000 * 3) = 54000 + 3000 = 57000
        assertThat(createdItem.getTotalPrice()).isEqualTo(new BigDecimal("57000.00"));
    }

    @Test
    @DisplayName("create - options가 null인 경우 빈 리스트로 설정")
    void createOrderItemWithNullOptions() {
        // when
        OrderItem createdItem = OrderItem.create(order, menu, 2, null);

        // then
        assertThat(createdItem.getOptions()).isNotNull();
        assertThat(createdItem.getOptions()).isEmpty();
        assertThat(createdItem.getTotalPrice()).isEqualTo(new BigDecimal("36000.00"));
    }

    @Test
    @DisplayName("create - quantity가 0인 경우")
    void createOrderItemWithZeroQuantity() {
        // when
        OrderItem createdItem = OrderItem.create(order, menu, 0, new ArrayList<>());

        // then
        assertThat(createdItem.getQuantity()).isEqualTo(0);
        assertThat(createdItem.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("calculateOptionPrice - 빈 옵션 리스트")
    void calculateOptionPriceWithEmptyList() {
        // given
        orderItem.setMenuPrice(new BigDecimal("18000.00"));
        orderItem.setQuantity(2);
        orderItem.setOptions(new ArrayList<>());

        // when
        orderItem.calculateTotalPrice();

        // then
        assertThat(orderItem.getTotalPrice()).isEqualTo(new BigDecimal("36000.00"));
    }

    @Test
    @DisplayName("calculateOptionPrice - null 옵션 리스트")
    void calculateOptionPriceWithNullList() {
        // given
        orderItem.setMenuPrice(new BigDecimal("18000.00"));
        orderItem.setQuantity(2);
        orderItem.setOptions(null);

        // when
        orderItem.calculateTotalPrice();

        // then
        assertThat(orderItem.getTotalPrice()).isEqualTo(new BigDecimal("36000.00"));
    }

    @Test
    @DisplayName("create - OrderRequest와 함께 생성하는 팩토리 메서드")
    void createWithOrderRequest() {
        // given
        OrderRequest.OrderItemRequest itemRequest = new OrderRequest.OrderItemRequest(
                1L, 2, List.of(
                        new OrderRequest.OrderItemOptionRequest("사이즈", "대", new BigDecimal("2000.00"))
                )
        );
        Map<Long, Menu> menuMap = Map.of(1L, menu);

        // when
        List<OrderItem> orderItems = OrderItem.create(order, List.of(itemRequest), menuMap);

        // then
        assertThat(orderItems).hasSize(1);
        OrderItem createdItem = orderItems.get(0);
        assertThat(createdItem.getOrder()).isEqualTo(order);
        assertThat(createdItem.getMenu()).isEqualTo(menu);
        assertThat(createdItem.getMenuName()).isEqualTo("후라이드 치킨");
        assertThat(createdItem.getMenuPrice()).isEqualTo(new BigDecimal("18000.00"));
        assertThat(createdItem.getQuantity()).isEqualTo(2);
        assertThat(createdItem.getOptions()).hasSize(1);
        assertThat(createdItem.getTotalPrice()).isEqualTo(new BigDecimal("40000.00")); // (18000 * 2) + (2000 * 2)
    }

    @Test
    @DisplayName("create - OrderRequest에서 존재하지 않는 메뉴 ID")
    void createWithOrderRequest_NonExistentMenu() {
        // given
        OrderRequest.OrderItemRequest itemRequest = new OrderRequest.OrderItemRequest(
                999L, 2, List.of()
        );
        Map<Long, Menu> menuMap = Map.of(1L, menu);

        // when & then
        assertThatThrownBy(() -> OrderItem.create(order, List.of(itemRequest), menuMap))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴를 찾을 수 없습니다: 999");
    }

    @Test
    @DisplayName("create - OrderRequest에서 null 옵션 리스트")
    void createWithOrderRequest_NullOptions() {
        // given
        OrderRequest.OrderItemRequest itemRequest = new OrderRequest.OrderItemRequest(
                1L, 2, null
        );
        Map<Long, Menu> menuMap = Map.of(1L, menu);

        // when
        List<OrderItem> orderItems = OrderItem.create(order, List.of(itemRequest), menuMap);

        // then
        assertThat(orderItems).hasSize(1);
        assertThat(orderItems.get(0).getOptions()).isEmpty();
    }
}
