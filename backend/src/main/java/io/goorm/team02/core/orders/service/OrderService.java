package io.goorm.team02.core.orders.service;

import io.goorm.team02.core.orders.controller.dto.OrderRequest;
import io.goorm.team02.core.orders.controller.dto.OrderResponse;
import io.goorm.team02.core.orders.controller.dto.OrderSearchRequest;
import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.orders.domain.enums.OrderStatus;
import io.goorm.team02.core.orders.repository.OrderRepository;
import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.repository.UserinfoRepository;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.repository.StoreRepository;
import io.goorm.team02.core.menus.domain.Menu;
import io.goorm.team02.core.menus.repository.MenuRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserinfoRepository userRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    // ================================
    // API Methods
    // ================================
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                              .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));
    }

    public List<Order> getAllOrdersByStoreId(Long storeId) {
        return orderRepository.findAllByStoreIdWithDetails(storeId);
    }

    public List<Order> getAllOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    // ================================
    // Business Logic
    // ================================
    @Transactional
    public Order create(OrderRequest orderRequest) {
        // 1. 엔티티 참조 조회 & 검증
        // TODO: MSA 전환 시 의존성 제거 필요
        User user = getUserById(orderRequest.userId());
        Store store = getStoreById(orderRequest.storeId());

        // 2. 메뉴 정보 조회 (주문 시점에서의 메뉴 정보 스냅샷)
        Map<Long, Menu> menuMap = getMenuMap(orderRequest.orderItems());

        // 3. Order 도메인에 생성 요청
        Order order = Order.create(user, store, orderRequest, menuMap);

        // 4. 저장
        Order savedOrder = orderRepository.save(order);

        // TODO: 5. 주문 생성 이벤트 발행 - OrderCreatedEvent

        return savedOrder;
    }

    /**
     * 메뉴 정보를 Map으로 조회 (서비스의 역할)
     */
    private Map<Long, Menu> getMenuMap(List<OrderRequest.OrderItemRequest> itemRequests) {
        List<Long> menuIds = itemRequests.stream()
                                         .map(OrderRequest.OrderItemRequest::menuId)
                                         .toList();

        List<Menu> menus = menuRepository.findAllById(menuIds);
        return menus.stream()
                    .collect(Collectors.toMap(Menu::getId, menu -> menu));
    }

    /**
     * 주문 목록 조회 (페이지네이션 지원)
     */
    public Page<Order> getAllByParams(OrderSearchRequest searchRequest) {
        Page<Order> orders;
        Pageable pageable = PageRequest.of(searchRequest.getPageOrDefault(), searchRequest.getSizeOrDefault());

        // 검색 조건에 따라 다른 메서드 호출
        if (searchRequest.hasStoreId()) {
            orders = orderRepository.findAllByStoreIdWithPagination(searchRequest.getStoreId(), pageable);
        } else if (searchRequest.hasUserId()) {
            orders = orderRepository.findAllByUserIdWithPagination(searchRequest.getUserId(), pageable);
        } else {
            // 모든 주문 조회
            orders = orderRepository.findAllWithPagination(pageable);
        }

        // JPA 지연 로딩으로 orderItems와 options를 가져옴
        orders.getContent().forEach(order -> {
            order.getOrderItems().forEach(orderItem -> {
                orderItem.getOptions().size(); // 지연 로딩 트리거
            });
        });

        return orders;
    }

    /**
     * 주문 상세 조회
     */
    public Order getOrderDetail(Long orderId) {
        Order order = getOrderById(orderId);

        order.getOrderItems().forEach(orderItem -> {
            orderItem.getOptions().size(); // 지연 로딩 트리거
        });

        return order;
    }

    /**
     * 픽업 가능한 주문 목록 조회 (READY 상태)
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAvailableOrders(Long storeId) {
        List<Order> orders;

        if (storeId != null) {
            // 특정 가게의 READY 상태 주문들
            orders = orderRepository.findByStoreIdAndStatus(storeId, OrderStatus.READY);
        } else {
            // 모든 가게의 READY 상태 주문들
            orders = orderRepository.findByStatus(OrderStatus.READY);
        }

        // 지연 로딩 처리
        orders.forEach(this::loadOrderDetails);

        return orders.stream()
                     .map(OrderResponse::from)
                     .toList();
    }

    /**
     * 주문 상세 정보 지연 로딩 처리 (중복 코드 제거)
     */
    private void loadOrderDetails(Order order) {
        order.getOrderItems().forEach(orderItem -> {
            orderItem.getOptions().size(); // 지연 로딩 트리거
        });
    }

    // ================================
    // Internal Methods
    // ================================
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
    }

    private Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                              .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다: " + storeId));
    }

	@Transactional
	public void cancelOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));
		if (order.getStatus() == OrderStatus.COOKING) {
			order.setStatus(OrderStatus.CANCELLED);
			orderRepository.save(order);
		} else {
			throw new RuntimeException("이미 완료된 주문은 취소할 수 없습니다.");
		}
	}

	@Transactional
	public void updateStatus(Long orderId, OrderStatus status) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));
		order.setStatus(status);
		orderRepository.save(order);
	}

}
