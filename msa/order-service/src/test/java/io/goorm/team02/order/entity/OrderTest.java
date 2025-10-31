package io.goorm.team02.order.entity;

import io.goorm.team02.order.service.dto.OrderData;
import io.goorm.team02.order.service.dto.OrderItemData;
import io.goorm.team02.order.service.dto.OrderItemOptionData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Order 도메인 테스트")
class OrderTest {

    @Test
    @DisplayName("create - OrderData로 주문 생성")
    void create_WithOrderData() {
        // given
        OrderData orderData = new OrderData(
                1L, // userId
                100L, // storeId
                "서울시 강남구", // deliveryAddress
                "101호", // deliveryDetailAddress
                "010-1234-5678", // phone
                "문 앞에 놓아주세요", // orderMemo
                "맛있는 치킨집", // storeName
                "02-1234-5678", // storePhone
                "서울시 강남구 테헤란로", // storeAddress
                "1층", // storeDetailAddress
                3000, // deliveryFee
                List.of(createOrderItemData())
        );

        // when
        Order order = Order.create(orderData);

        // then
        assertThat(order.getUserId()).isEqualTo(1L);
        assertThat(order.getStoreId()).isEqualTo(100L);
        assertThat(order.getDeliveryAddress()).isEqualTo("서울시 강남구");
        assertThat(order.getDeliveryDetailAddress()).isEqualTo("101호");
        assertThat(order.getPhone()).isEqualTo("010-1234-5678");
        assertThat(order.getOrderMemo()).isEqualTo("문 앞에 놓아주세요");
        
        // 가게 정보
        assertThat(order.getStoreName()).isEqualTo("맛있는 치킨집");
        assertThat(order.getStorePhone()).isEqualTo("02-1234-5678");
        assertThat(order.getStoreAddress()).isEqualTo("서울시 강남구 테헤란로");
        assertThat(order.getStoreDetailAddress()).isEqualTo("1층");
        assertThat(order.getDeliveryFee()).isEqualTo(3000);
        
        // 주문 아이템
        assertThat(order.getOrderItems()).hasSize(1);
        OrderItem orderItem = order.getOrderItems().get(0);
        assertThat(orderItem.getMenuId()).isEqualTo(1L);
        assertThat(orderItem.getQuantity()).isEqualTo(2);
        assertThat(orderItem.getMenuName()).isEqualTo("치킨버거");
        assertThat(orderItem.getMenuPrice()).isEqualTo(15000);
        
        // 주문 번호 생성 확인
        assertThat(order.getOrderNumber()).isNotNull();
        assertThat(order.getOrderNumber()).isNotEmpty();
    }

    @Test
    @DisplayName("calculateTotalAmount - 주문 총액 계산")
    void calculateTotalAmount() {
        // given
        Order order = new Order();
        order.setDeliveryFee(3000);
        
        // OrderItem 1: 치킨버거 2개 (15000 * 2 = 30000)
        OrderItem item1 = new OrderItem();
        item1.setMenuPrice(15000);
        item1.setQuantity(2);
        item1.setTotalPrice(30000);
        
        // OrderItem 2: 콜라 1개 (2000 * 1 = 2000)
        OrderItem item2 = new OrderItem();
        item2.setMenuPrice(2000);
        item2.setQuantity(1);
        item2.setTotalPrice(2000);
        
        order.setOrderItems(List.of(item1, item2));

        // when
        order.calculateTotalAmount();

        // then
        // 메뉴 총액: 30000 + 2000 = 32000
        // 배달비: 3000
        // 총액: 32000 + 3000 = 35000
        assertThat(order.getMenuTotalAmount()).isEqualTo(32000);
        assertThat(order.getTotalAmount()).isEqualTo(35000);
    }

    private OrderItemData createOrderItemData() {
        return new OrderItemData(
                1L, // menuId
                2, // quantity
                "치킨버거", // menuName
                15000, // menuPrice
                List.of(createOrderItemOptionData())
        );
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