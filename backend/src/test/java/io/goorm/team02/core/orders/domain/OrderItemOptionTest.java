package io.goorm.team02.core.orders.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderItemOption 도메인 테스트")
class OrderItemOptionTest {

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
}
