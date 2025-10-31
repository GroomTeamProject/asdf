package io.goorm.team02.order.entity;

import io.goorm.team02.dto.orders.OrderResponse;
import io.goorm.team02.order.service.dto.OrderItemOptionData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderItemOption 도메인 테스트")
class OrderItemOptionTest {

    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItem = new OrderItem();
    }

    @Test
    @DisplayName("create - OrderItemOptionData로 생성")
    void create_WithOrderItemOptionData() {
        // given
        OrderItemOptionData optionData = new OrderItemOptionData(
                1L, // optionId
                "사이즈", // optionName
                "대", // optionItemName
                2000 // additionalPrice
        );

        // when
        OrderItemOption option = OrderItemOption.create(orderItem, optionData);

        // then
        assertThat(option.getOrderItem()).isEqualTo(orderItem);
        assertThat(option.getOptionName()).isEqualTo("사이즈");
        assertThat(option.getOptionItemName()).isEqualTo("대");
        assertThat(option.getAdditionalPrice()).isEqualTo(2000);
    }

    @Test
    @DisplayName("create - 추가 가격이 0인 옵션")
    void create_ZeroAdditionalPrice() {
        // given
        OrderItemOptionData optionData = new OrderItemOptionData(
                2L, // optionId
                "맛", // optionName
                "보통맛", // optionItemName
                0 // additionalPrice
        );

        // when
        OrderItemOption option = OrderItemOption.create(orderItem, optionData);

        // then
        assertThat(option.getOrderItem()).isEqualTo(orderItem);
        assertThat(option.getOptionName()).isEqualTo("맛");
        assertThat(option.getOptionItemName()).isEqualTo("보통맛");
        assertThat(option.getAdditionalPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("getAdditionalPrice - 추가 가격 반환")
    void getAdditionalPrice() {
        // given
        OrderItemOptionData optionData = new OrderItemOptionData(
                1L, // optionId
                "사이즈", // optionName
                "대", // optionItemName
                3000 // additionalPrice
        );
        OrderItemOption option = OrderItemOption.create(orderItem, optionData);

        // when
        int additionalPrice = option.getAdditionalPrice();

        // then
        assertThat(additionalPrice).isEqualTo(3000);
    }

    @Test
    @DisplayName("toResponse - OrderItemOptionResponse 변환")
    void toResponse() {
        // given
        OrderItemOption option = new OrderItemOption();
        option.setId(1L);
        option.setOptionName("사이즈");
        option.setOptionItemName("대");
        option.setAdditionalPrice(2000);

        // when
        OrderResponse.OrderItemOptionResponse response = option.toResponse();

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.optionName()).isEqualTo("사이즈");
        assertThat(response.optionItemName()).isEqualTo("대");
        assertThat(response.additionalPrice()).isEqualTo(2000);
    }
}