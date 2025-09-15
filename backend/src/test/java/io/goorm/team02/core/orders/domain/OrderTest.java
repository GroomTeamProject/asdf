package io.goorm.team02.core.orders.domain;

import io.goorm.team02.core.orders.domain.enums.OrderStatus;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.TestEnv;
import io.goorm.team02.core.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Order 도메인 테스트")
class OrderTest extends TestEnv {

    private Order order;
    private User user;
    private Store store;
    private List<OrderItem> orderItems;

    @BeforeEach
    void setUp() {
        order = new Order();
        user = new User();
        store = new Store();

        // OrderItem 설정
        orderItems = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setMenuPrice(new BigDecimal("18000.00"));
        orderItem.setQuantity(2);
        orderItem.setTotalPrice(new BigDecimal("36000.00"));
        orderItems.add(orderItem);
    }

    @Test
    @DisplayName("calculateTotalAmount - 기본 계산")
    void calculateTotalAmount() {
        // given
        order.setOrderItems(orderItems);
        order.setDeliveryFee(new BigDecimal("3000.00"));
        order.setDiscountAmount(new BigDecimal("1000.00"));

        // when
        order.calculateTotalAmount();

        // then
        // 메뉴 총액: 36000, 배달비: 3000, 할인: 1000
        // 총액: 36000 + 3000 - 1000 = 38000
        assertThat(order.getMenuTotalAmount()).isEqualByComparingTo(new BigDecimal("36000.00"));
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("38000.00"));
    }

    @Test
    @DisplayName("calculateTotalAmount - deliveryFee가 null인 경우")
    void calculateTotalAmountWithNullDeliveryFee() {
        // given
        order.setOrderItems(orderItems);
        order.setDeliveryFee(null);
        order.setDiscountAmount(new BigDecimal("1000.00"));

        // when
        order.calculateTotalAmount();

        // then
        // 메뉴 총액: 36000, 배달비: 0, 할인: 1000
        // 총액: 36000 + 0 - 1000 = 35000
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("35000.00"));
    }

    @Test
    @DisplayName("calculateTotalAmount - discountAmount가 null인 경우")
    void calculateTotalAmountWithNullDiscountAmount() {
        // given
        order.setOrderItems(orderItems);
        order.setDeliveryFee(new BigDecimal("3000.00"));
        order.setDiscountAmount(null);

        // when
        order.calculateTotalAmount();

        // then
        // 메뉴 총액: 36000, 배달비: 3000, 할인: 0
        // 총액: 36000 + 3000 - 0 = 39000
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("39000.00"));
    }

    @Test
    @DisplayName("calculateTotalAmount - orderItems가 null인 경우")
    void calculateTotalAmountWithNullOrderItems() {
        // given
        order.setOrderItems(null);
        order.setDeliveryFee(new BigDecimal("3000.00"));
        order.setDiscountAmount(new BigDecimal("1000.00"));

        // when
        order.calculateTotalAmount();

        // then
        // 메뉴 총액: 0, 배달비: 3000, 할인: 1000
        // 총액: 0 + 3000 - 1000 = 2000
        assertThat(order.getMenuTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("generateOrderNumber - 주문 번호 생성")
    void generateOrderNumber() {
        // when
        order.generateOrderNumber();

        // then
        assertThat(order.getOrderNumber()).isNotNull();
        assertThat(order.getOrderNumber()).startsWith("ORD-");
        assertThat(order.getOrderNumber()).contains("-");

        // "ORD-" + timestamp + "-" + 8자리 UUID = 최소 25자리
        assertThat(order.getOrderNumber().length()).isGreaterThanOrEqualTo(25);
    }

    @Test
    @DisplayName("accept - 주문 수락")
    void accept() {
        // given
        order.setStatus(OrderStatus.PENDING);

        // when
        order.accept(15, 25);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        assertThat(order.getMinCookingTime()).isEqualTo(15);
        assertThat(order.getMaxCookingTime()).isEqualTo(25);
        assertThat(order.getAcceptedAt()).isNotNull();
    }

    @Test
    @DisplayName("accept - PENDING 상태가 아닌 경우 예외 발생")
    void acceptWithInvalidStatus() {
        // given
        order.setStatus(OrderStatus.ACCEPTED);

        // when & then
        assertThatThrownBy(() -> order.accept(15, 25))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("수락할 수 없는 주문 상태입니다");
    }

    @Test
    @DisplayName("startCooking - 조리 시작")
    void startCooking() {
        // given
        order.setStatus(OrderStatus.ACCEPTED);

        // when
        order.startCooking();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COOKING);
        assertThat(order.getCookingStartedAt()).isNotNull();
    }

    @Test
    @DisplayName("startCooking - ACCEPTED 상태가 아닌 경우 예외 발생")
    void startCookingWithInvalidStatus() {
        // given
        order.setStatus(OrderStatus.PENDING);

        // when & then
        assertThatThrownBy(() -> order.startCooking())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("조리를 시작할 수 없는 주문 상태입니다");
    }

    @Test
    @DisplayName("completeCooking - 조리 완료")
    void completeCooking() {
        // given
        order.setStatus(OrderStatus.COOKING);

        // when
        order.completeCooking();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.READY);
        assertThat(order.getCookingCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("completeCooking - COOKING 상태가 아닌 경우 예외 발생")
    void completeCookingWithInvalidStatus() {
        // given
        order.setStatus(OrderStatus.ACCEPTED);

        // when & then
        assertThatThrownBy(() -> order.completeCooking())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("조리 완료할 수 없는 주문 상태입니다");
    }

    @Test
    @DisplayName("startDelivery - 배달 시작 (READY 상태)")
    void startDeliveryFromReady() {
        // given
        order.setStatus(OrderStatus.READY);

        // when
        order.startDelivery();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PICKED_UP);
        assertThat(order.getPickedUpAt()).isNotNull();
    }

    @Test
    @DisplayName("startDelivery - 배달 시작 불가능한 상태에서 예외 발생")
    void startDeliveryWithInvalidStatus() {
        // given
        order.setStatus(OrderStatus.COOKING);

        // when & then
        assertThatThrownBy(() -> order.startDelivery())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("배달을 시작할 수 없는 주문 상태입니다");
    }

    @Test
    @DisplayName("deliver - 배달 완료 (PICKED_UP 상태)")
    void deliverFromPickedUp() {
        // given
        order.setStatus(OrderStatus.PICKED_UP);

        // when
        order.deliver();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(order.getDeliveredAt()).isNotNull();
    }

    @Test
    @DisplayName("deliver - 배달 불가능한 상태에서 예외 발생")
    void deliverWithInvalidStatus() {
        // given
        order.setStatus(OrderStatus.READY);

        // when & then
        assertThatThrownBy(() -> order.deliver())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("배달 완료할 수 없는 주문 상태입니다");
    }

    @Test
    @DisplayName("cancel - 주문 취소 (PENDING 상태)")
    void cancelFromPending() {
        // given
        order.setStatus(OrderStatus.PENDING);

        // when
        order.cancel("고객 요청");

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getCancelReason()).isEqualTo("고객 요청");
        assertThat(order.getCancelledAt()).isNotNull();
    }

    @Test
    @DisplayName("cancel - 주문 취소 (ACCEPTED 상태)")
    void cancelFromAccepted() {
        // given
        order.setStatus(OrderStatus.ACCEPTED);

        // when
        order.cancel("재료 부족");

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getCancelReason()).isEqualTo("재료 부족");
        assertThat(order.getCancelledAt()).isNotNull();
    }

    @Test
    @DisplayName("cancel - 취소 불가능한 상태에서 예외 발생")
    void cancelWithInvalidStatus() {
        // given
        order.setStatus(OrderStatus.COOKING);

        // when & then
        assertThatThrownBy(() -> order.cancel("취소 요청"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("취소할 수 없는 주문 상태입니다");
    }

    @Test
    @DisplayName("reject - 주문 거절")
    void reject() {
        // given
        order.setStatus(OrderStatus.PENDING);

        // when
        order.reject("재료 부족");

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.REJECTED);
        assertThat(order.getRejectReason()).isEqualTo("재료 부족");
        assertThat(order.getRejectedAt()).isNotNull();
    }

    @Test
    @DisplayName("reject - PENDING 상태가 아닌 경우 예외 발생")
    void rejectWithInvalidStatus() {
        // given
        order.setStatus(OrderStatus.ACCEPTED);

        // when & then
        assertThatThrownBy(() -> order.reject("거절 사유"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("거절할 수 없는 주문 상태입니다");
    }

    @Test
    @DisplayName("validate - 정상적인 주문 검증")
    void validate() {
        // given
        order.setUser(user);
        order.setStore(store);

        // when & then
        // 예외가 발생하지 않아야 함
        order.validate();
    }

    @Test
    @DisplayName("validate - user가 null인 경우 예외 발생")
    void validateWithNullUser() {
        // given
        order.setUser(null);
        order.setStore(store);

        // when & then
        assertThatThrownBy(() -> order.validate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("사용자 정보가 필요합니다");
    }

    @Test
    @DisplayName("validate - store가 null인 경우 예외 발생")
    void validateWithNullStore() {
        // given
        order.setUser(user);
        order.setStore(null);

        // when & then
        assertThatThrownBy(() -> order.validate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("가게 정보가 필요합니다");
    }


    @Test
    @DisplayName("create - 팩토리 메서드로 Order 생성")
    void createOrder() {
        // given
        String deliveryAddress = "서울시 강남구 테헤란로 123";
        String deliveryDetailAddress = "101호";
        String phone = "010-1234-5678";
        String orderMemo = "문 앞에 놓아주세요";
        BigDecimal deliveryFee = new BigDecimal("3000.00");

        // when
        Order createdOrder = Order.create(
                user, store, deliveryAddress, deliveryDetailAddress,
                phone, orderMemo, deliveryFee);

        // then
        assertThat(createdOrder.getUser()).isEqualTo(user);
        assertThat(createdOrder.getStore()).isEqualTo(store);
        assertThat(createdOrder.getDeliveryAddress()).isEqualTo(deliveryAddress);
        assertThat(createdOrder.getDeliveryDetailAddress()).isEqualTo(deliveryDetailAddress);
        assertThat(createdOrder.getPhone()).isEqualTo(phone);
        assertThat(createdOrder.getOrderMemo()).isEqualTo(orderMemo);
        assertThat(createdOrder.getDeliveryFee()).isEqualByComparingTo(deliveryFee);

        // 자동 설정된 값들
        assertThat(createdOrder.getOrderNumber()).isNotNull();
        assertThat(createdOrder.getOrderedAt()).isNotNull();
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("create - deliveryFee가 null인 경우")
    void createOrderWithNullDeliveryFee() {
        // when
        Order createdOrder = Order.create(
                user, store, "주소", "상세주소", "전화번호", "메모", null);

        // then
        assertThat(createdOrder.getDeliveryFee()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("create - discountAmount가 null인 경우")
    void createOrderWithNullDiscountAmount() {
        // when
        Order createdOrder = Order.create(
                user, store, "주소", "상세주소", "전화번호", "메모", BigDecimal.ZERO);

        // then
        assertThat(createdOrder.getDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
