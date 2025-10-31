package io.goorm.team02.core.orders.domain;

import io.goorm.team02.core.TestEnv;
import io.goorm.team02.core.orders.controller.dto.OrderRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderItemOption 도메인 테스트")
class OrderItemOptionTest extends TestEnv {

    private OrderItemOption orderItemOption;

    @BeforeEach
    void setUp() {
        orderItemOption = new OrderItemOption();
    }

    @Test
    @DisplayName("additionalPrice가 null인 경우 BigDecimal.ZERO로 설정")
    void setAdditionalPriceWithNull() {
        // when
        orderItemOption.setAdditionalPrice(null);

        // then
        assertThat(orderItemOption.getAdditionalPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("additionalPrice가 유효한 값인 경우 정상 설정")
    void setAdditionalPriceWithValidValue() {
        // given
        BigDecimal validPrice = new BigDecimal("2000.50");

        // when
        orderItemOption.setAdditionalPrice(validPrice);

        // then
        assertThat(orderItemOption.getAdditionalPrice()).isEqualTo(validPrice);
    }

    @Test
    @DisplayName("additionalPrice가 0인 경우 정상 설정")
    void setAdditionalPriceWithZero() {
        // given
        BigDecimal zeroPrice = BigDecimal.ZERO;

        // when
        orderItemOption.setAdditionalPrice(zeroPrice);

        // then
        assertThat(orderItemOption.getAdditionalPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("음수 additionalPrice 설정 가능")
    void setAdditionalPriceWithNegativeValue() {
        // given
        BigDecimal negativePrice = new BigDecimal("-500.00");

        // when
        orderItemOption.setAdditionalPrice(negativePrice);

        // then
        assertThat(orderItemOption.getAdditionalPrice()).isEqualTo(negativePrice);
    }

    @Test
    @DisplayName("create - OrderRequest와 함께 생성하는 팩토리 메서드")
    void createWithOrderRequest() {
        // given
        OrderItem orderItem = new OrderItem();
        List<OrderRequest.OrderItemOptionRequest> optionRequests = List.of(
                new OrderRequest.OrderItemOptionRequest("사이즈", "대", new BigDecimal("2000.00")),
                new OrderRequest.OrderItemOptionRequest("맛", "매운맛", new BigDecimal("500.00"))
        );

        // when
        List<OrderItemOption> options = OrderItemOption.create(orderItem, optionRequests);

        // then
        assertThat(options).hasSize(2);
        assertThat(options.get(0).getOrderItem()).isEqualTo(orderItem);
        assertThat(options.get(0).getOptionName()).isEqualTo("사이즈");
        assertThat(options.get(0).getOptionItemName()).isEqualTo("대");
        assertThat(options.get(0).getAdditionalPrice()).isEqualTo(new BigDecimal("2000.00"));
        assertThat(options.get(1).getOptionName()).isEqualTo("맛");
        assertThat(options.get(1).getOptionItemName()).isEqualTo("매운맛");
        assertThat(options.get(1).getAdditionalPrice()).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("create - null 옵션 리스트")
    void createWithNullOptionRequests() {
        // given
        OrderItem orderItem = new OrderItem();

        // when
        List<OrderItemOption> options = OrderItemOption.create(orderItem, null);

        // then
        assertThat(options).isEmpty();
    }

    @Test
    @DisplayName("create - 빈 옵션 리스트")
    void createWithEmptyOptionRequests() {
        // given
        OrderItem orderItem = new OrderItem();

        // when
        List<OrderItemOption> options = OrderItemOption.create(orderItem, List.of());

        // then
        assertThat(options).isEmpty();
    }

    @Test
    @DisplayName("create - null 옵션명이 있는 요청")
    void createWithNullOptionName() {
        // given
        OrderItem orderItem = new OrderItem();
        List<OrderRequest.OrderItemOptionRequest> optionRequests = List.of(
                new OrderRequest.OrderItemOptionRequest(null, "대", new BigDecimal("2000.00")),
                new OrderRequest.OrderItemOptionRequest("맛", "매운맛", new BigDecimal("500.00"))
        );

        // when
        List<OrderItemOption> options = OrderItemOption.create(orderItem, optionRequests);

        // then
        assertThat(options).hasSize(1); // null 옵션명은 제외됨
        assertThat(options.get(0).getOptionName()).isEqualTo("맛");
    }

    @Test
    @DisplayName("create - null 옵션 아이템명이 있는 요청")
    void createWithNullOptionItemName() {
        // given
        OrderItem orderItem = new OrderItem();
        List<OrderRequest.OrderItemOptionRequest> optionRequests = List.of(
                new OrderRequest.OrderItemOptionRequest("사이즈", null, new BigDecimal("2000.00")),
                new OrderRequest.OrderItemOptionRequest("맛", "매운맛", new BigDecimal("500.00"))
        );

        // when
        List<OrderItemOption> options = OrderItemOption.create(orderItem, optionRequests);

        // then
        assertThat(options).hasSize(1); // null 옵션 아이템명은 제외됨
        assertThat(options.get(0).getOptionName()).isEqualTo("맛");
    }

    @Test
    @DisplayName("create - null additionalPrice가 있는 요청")
    void createWithNullAdditionalPrice() {
        // given
        OrderItem orderItem = new OrderItem();
        List<OrderRequest.OrderItemOptionRequest> optionRequests = List.of(
                new OrderRequest.OrderItemOptionRequest("사이즈", "대", null),
                new OrderRequest.OrderItemOptionRequest("맛", "매운맛", new BigDecimal("500.00"))
        );

        // when
        List<OrderItemOption> options = OrderItemOption.create(orderItem, optionRequests);

        // then
        assertThat(options).hasSize(2);
        assertThat(options.get(0).getAdditionalPrice()).isEqualTo(BigDecimal.ZERO); // null은 ZERO로 변환
        assertThat(options.get(1).getAdditionalPrice()).isEqualTo(new BigDecimal("500.00"));
    }
}
