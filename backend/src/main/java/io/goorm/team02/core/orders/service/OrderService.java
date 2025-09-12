package io.goorm.team02.core.orders.service;

import io.goorm.team02.core.orders.controller.dto.OrderRequest;
import io.goorm.team02.core.orders.controller.dto.OrderResponse;
import io.goorm.team02.core.orders.controller.dto.OrderRejectRequest;
import io.goorm.team02.core.orders.controller.dto.OrderAcceptRequest;
import io.goorm.team02.core.orders.controller.dto.OrderCancelRequest;
import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.orders.domain.OrderItem;
import io.goorm.team02.core.orders.domain.OrderItemOption;
import io.goorm.team02.core.orders.domain.enums.OrderStatus;
import io.goorm.team02.core.orders.repository.OrderRepository;
import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.repository.UserinfoRepository;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.repository.StoreRepository;
import io.goorm.team02.core.menus.domain.Menu;
import io.goorm.team02.core.menus.repository.MenuRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserinfoRepository userRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public OrderResponse create(OrderRequest orderRequest) {
        // 1. 엔티티 조회 및 검증
        User user = userRepository.findById(orderRequest.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + orderRequest.userId()));

        Store store = storeRepository.findById(orderRequest.storeId())
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다: " + orderRequest.storeId()));

        // 2. Order 엔티티 생성
        Order order = orderRequest.toEntity();
        order.setUser(user);
        order.setStore(store);
        order.generateOrderNumber(); // 도메인에서 주문 번호 생성
        order.setOrderedAt(LocalDateTime.now());

        // 3. 주문 아이템 생성 (도메인에서 처리)
        List<OrderItem> orderItems = createOrderItems(order, orderRequest.orderItems());
        order.setOrderItems(orderItems);

        // 4. 배달비 및 할인 설정
        order.setDeliveryFee(store.getDeliveryFee() != null ? store.getDeliveryFee() : BigDecimal.ZERO);
        order.setDiscountAmount(calculateDiscount(user, order.getMenuTotalAmount()));

        // 5. 도메인에서 총액 계산
        order.calculateTotalAmount();

        // 6. 주문 검증
        order.validate();

        // 7. 저장
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    /**
     * 주문 아이템들 생성 (도메인 팩토리 메서드 사용)
     */
    private List<OrderItem> createOrderItems(Order order, List<OrderRequest.OrderItemRequest> itemRequests) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderRequest.OrderItemRequest itemRequest : itemRequests) {
            Menu menu = menuRepository.findById(itemRequest.menuId())
                    .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다: " + itemRequest.menuId()));

            // 먼저 OrderItem 생성
            OrderItem orderItem = OrderItem.create(order, menu, itemRequest.quantity(), new ArrayList<>());

            // 옵션 생성 (OrderItem이 먼저 생성되어야 함)
            List<OrderItemOption> options = createOrderItemOptions(orderItem, itemRequest.options());
            orderItem.setOptions(options);

            // 총액 다시 계산
            orderItem.calculateTotalPrice();

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    /**
     * 주문 아이템 옵션들 생성
     */
    private List<OrderItemOption> createOrderItemOptions(OrderItem orderItem,
            List<OrderRequest.OrderItemOptionRequest> optionRequests) {
        List<OrderItemOption> options = new ArrayList<>();

        for (OrderRequest.OrderItemOptionRequest optionRequest : optionRequests) {
            OrderItemOption option = new OrderItemOption();
            option.setOrderItem(orderItem); // 🔥 이 부분이 누락되었습니다!
            option.setOptionName(optionRequest.optionName());
            option.setOptionItemName(optionRequest.optionItemName());
            option.setAdditionalPrice(optionRequest.additionalPrice());
            options.add(option);
        }

        return options;
    }

    /**
     * 할인 금액 계산 (복잡한 비즈니스 로직)
     */
    private BigDecimal calculateDiscount(User user, BigDecimal amount) {
        // TODO: 등급별 할인, 쿠폰, 이벤트 등 복잡한 할인 로직 구현
        // 현재는 기본 할인 없음
        return BigDecimal.ZERO;
    }

    /**
     * 가게에서 주문 수락 (예상 조리 시간 포함)
     */
    @Transactional
    public OrderResponse acceptOrder(Long orderId, OrderAcceptRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

        // 주문 상태 검증
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("수락할 수 없는 주문 상태입니다. 현재 상태: " + order.getStatus());
        }

        // 도메인에서 상태 변경
        order.changeStatus(OrderStatus.ACCEPTED);
        
        // 예상 조리 시간 설정
        order.setMinCookingTime(request.minCookingTime());
        order.setMaxCookingTime(request.maxCookingTime());

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    /**
     * 가게에서 주문 거절
     */
    @Transactional
    public OrderResponse rejectOrder(Long orderId, OrderRejectRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

        // 주문 상태 검증
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("거절할 수 없는 주문 상태입니다. 현재 상태: " + order.getStatus());
        }

        // 거절 처리
        order.setRejectReason(request.reason());
        order.changeStatus(OrderStatus.CANCELLED);

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    /**
     * 가게에서 조리 시작
     */
    @Transactional
    public OrderResponse startCooking(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

        // 주문 상태 검증
        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new IllegalStateException("조리를 시작할 수 없는 주문 상태입니다. 현재 상태: " + order.getStatus());
        }

        // 도메인에서 상태 변경
        order.changeStatus(OrderStatus.COOKING);

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    /**
     * 가게에서 조리 완료
     */
    @Transactional
    public OrderResponse completeCooking(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

        // 주문 상태 검증
        if (order.getStatus() != OrderStatus.COOKING) {
            throw new IllegalStateException("조리 완료할 수 없는 주문 상태입니다. 현재 상태: " + order.getStatus());
        }

        // 도메인에서 상태 변경
        order.changeStatus(OrderStatus.READY);

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    /**
     * 배달 완료
     */
    @Transactional
    public OrderResponse deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

        // 주문 상태 검증 (READY 또는 PICKED_UP 상태에서 배달 완료 가능)
        if (order.getStatus() != OrderStatus.READY && order.getStatus() != OrderStatus.PICKED_UP) {
            throw new IllegalStateException("배달 완료할 수 없는 주문 상태입니다. 현재 상태: " + order.getStatus());
        }

        // 도메인에서 상태 변경
        order.changeStatus(OrderStatus.DELIVERED);

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    public List<OrderResponse> getAll(Long storeId) {
        List<Order> orders = orderRepository.findAllByStoreIdWithDetails(storeId);

        // JPA 지연 로딩으로 orderItems와 options를 가져옴
        orders.forEach(order -> {
            order.getOrderItems().forEach(orderItem -> {
                orderItem.getOptions().size(); // 지연 로딩 트리거
            });
        });

        return orders.stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

        // JPA 지연 로딩으로 orderItems와 options를 가져옴
        order.getOrderItems().forEach(orderItem -> {
            orderItem.getOptions().size(); // 지연 로딩 트리거
        });

		return OrderResponse.from(order);
	}

	/**
	 * 주문 취소
	 */
	@Transactional
	public OrderResponse cancelOrder(Long orderId, OrderCancelRequest request) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

		// 주문 상태 검증 (취소 가능한 상태: PENDING, ACCEPTED)
		if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.ACCEPTED) {
			throw new IllegalStateException("취소할 수 없는 주문 상태입니다. 현재 상태: " + order.getStatus());
		}

		// 취소 처리
		order.setCancelReason(request.cancelReason());
		order.changeStatus(OrderStatus.CANCELLED);

		Order savedOrder = orderRepository.save(order);
		return OrderResponse.from(savedOrder);
	}

}