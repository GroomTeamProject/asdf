package io.goorm.team02.order.service;

import static io.goorm.team02.order.service.OrderStatusService.ORDER_EVENTS_TOPIC;

import io.goorm.team02.dto.orders.*;
import io.goorm.team02.dto.owner.stores.storemanagement.StoreResponse;
import io.goorm.team02.dto.owner.menus.menucreate.MenuResponse;
import io.goorm.team02.order.entity.Order;
import io.goorm.team02.order.entity.enums.OrderStatus;
import io.goorm.team02.order.repository.OrderRepository;
import io.goorm.team02.order.service.dto.OrderData;
import io.goorm.team02.order.service.dto.OrderItemData;
import io.goorm.team02.order.service.dto.OrderItemOptionData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.goorm.team02.kafka.client.EventPublisher;
import io.goorm.team02.order.client.StoreServiceClient;
import io.goorm.team02.order.event.OrderCreatedEvent;
import io.goorm.team02.dto.owner.menus.menucreate.MenuOptionItemResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
    private final StoreServiceClient storeServiceClient;
    private final OrderMapper orderMapper;

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
    public Order create(OrderRequest orderRequest, Long userId) {
        // 1. 가게 정보 조회
        var storeInfo = storeServiceClient.getStoreById(orderRequest.storeId()).getBody();
        if (storeInfo == null) {
            throw new IllegalArgumentException("가게 정보를 찾을 수 없습니다: " + orderRequest.storeId());
        }

        // 2. 메뉴 정보 조회 (주문 시점에서의 메뉴 정보 스냅샷)
        var menuList = storeServiceClient.getMenusByStoreId(orderRequest.storeId()).getBody();
        if (menuList == null) {
            throw new IllegalArgumentException("메뉴 정보를 찾을 수 없습니다: " + orderRequest.storeId());
        }

        // 3. OrderData 생성
        OrderData orderData = createOrderData(orderRequest, userId, storeInfo, menuList);

        // 4. Order 도메인에 생성 요청
        Order order = Order.create(orderData);

        // 5. 저장
        Order savedOrder = orderRepository.save(order);

        // 6. 주문 생성 이벤트 발행 - OrderCreatedEvent
        eventPublisher.publish(ORDER_EVENTS_TOPIC, new OrderCreatedEvent(savedOrder));

        return savedOrder;
    }

    /**
     * 주문 목록 조회 (페이지네이션 지원)
     */
    public Page<Order> getAllByParams(OrderSearchRequest searchRequest, Long userId) {
        Page<Order> orders;
        Pageable pageable = PageRequest.of(searchRequest.getPageOrDefault(), searchRequest.getSizeOrDefault());

        // 검색 조건에 따라 다른 메서드 호출
        if (searchRequest.hasStoreId()) {
            orders = orderRepository.findAllByStoreIdWithPagination(searchRequest.getStoreId(), pageable);
        } else if (searchRequest.hasUserId()) {
            orders = orderRepository.findAllByUserIdWithPagination(userId, pageable);
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
     * 주문 상세 조회 (권한 검증 포함)
     */
    public Order getOrderDetail(Long orderId, Long userId) {
        Order order = getOrderById(orderId);

        // 권한 검증: 주문 소유자만 조회 가능
        if (!order.getUserId().equals(userId)) {
            throw new IllegalStateException("본인의 주문만 조회할 수 있습니다.");
        }

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
                .map(Order::toResponse)
                .toList();
    }

    public OrderResponseForDelivery getOrderDetailForDelivery(Long orderId) {
        Order order = getOrderById(orderId);
        return orderMapper.toResponseForDelivery(order);
    }

    /**
     * 주문 상세 정보 지연 로딩 처리 (중복 코드 제거)
     */
    private void loadOrderDetails(Order order) {
        order.getOrderItems().forEach(orderItem -> {
            orderItem.getOptions().size(); // 지연 로딩 트리거
        });
    }

    /**
     * 가게별 대시보드 데이터 조회
     */
    public OrderDashboardDto getDashboardData(Long storeId) {

        Long todayOrderCount = getTodayOrderCount(storeId);
        BigDecimal todayRevenue = getTodayRevenue(storeId);
        Long totalOrderCount = getTotalOrderCount(storeId);
        List<RecentOrderDto> recentOrders = getRecentOrders(storeId, 5);

        // TODO: 리뷰 데이터는 Review 서비스에서 가져와야 함
        BigDecimal averageRating = BigDecimal.valueOf(4.5); // 임시값
        Long reviewCount = 100L; // 임시값

        OrderDashboardDto result = OrderDashboardDto.builder()
                .todayOrderCount(todayOrderCount)
                .todayRevenue(todayRevenue)
                .totalOrderCount(totalOrderCount)
                .averageRating(averageRating)
                .reviewCount(reviewCount)
                .recentOrders(recentOrders)
                .build();

        return result;
    }

    /**
     * OrderData 생성 (OrderRequest + StoreResponse + MenuResponse 조합)
     */
    private OrderData createOrderData(OrderRequest orderRequest, Long userId, StoreResponse storeInfo,
            List<MenuResponse> menuList) {
        // 메뉴 정보를 Map으로 변환하여 빠른 조회 가능하도록 함
        Map<Long, MenuResponse> menuMap = menuList.stream()
                .collect(Collectors.toMap(MenuResponse::getId, menu -> menu));

        // OrderItemData 목록 생성
        List<OrderItemData> orderItemDataList = orderRequest.orderItems().stream()
                .map(itemRequest -> createOrderItemData(itemRequest, menuMap))
                .toList();

        return new OrderData(
                // 사용자 요청 정보
                userId,
                orderRequest.storeId(),
                orderRequest.deliveryAddress(),
                orderRequest.deliveryDetailAddress(),
                orderRequest.phone(),
                orderRequest.orderMemo(),

                // 가게 정보
                storeInfo.getName(),
                storeInfo.getPhone(),
                storeInfo.getAddress(),
                storeInfo.getDetailAddress(),
                storeInfo.getDeliveryFee().intValue(),

                // 주문 아이템 목록
                orderItemDataList);
    }

    /**
     * OrderItemData 생성 (OrderItemRequest + MenuResponse 조합)
     */
    private OrderItemData createOrderItemData(OrderRequest.OrderItemRequest itemRequest,
            Map<Long, MenuResponse> menuMap) {
        MenuResponse menu = menuMap.get(itemRequest.menuId());

        // OrderItemOptionData 목록 생성
        List<OrderItemOptionData> optionDataList = List.of();
        if (itemRequest.options() != null && !itemRequest.options().isEmpty()) {
            optionDataList = itemRequest.options().stream()
                    .map(optionRequest -> createOrderItemOptionData(optionRequest, menu))
                    .toList();
        }

        return new OrderItemData(
                // 사용자 요청 정보
                itemRequest.menuId(),
                itemRequest.quantity(),

                // 메뉴 정보
                menu != null ? menu.getName() : "메뉴 정보 없음",
                menu != null ? menu.getPrice().intValue() : 0,

                // 옵션 목록
                optionDataList);
    }

    /**
     * OrderItemOptionData 생성 (OrderItemOptionRequest + MenuOptionItemResponse 조합)
     */
    private OrderItemOptionData createOrderItemOptionData(OrderRequest.OrderItemOptionRequest optionRequest,
            MenuResponse menu) {
        // 메뉴에서 해당 옵션 찾기
        MenuOptionItemResponse matchingOptionItem = null;
        String optionGroupName = "옵션 정보 없음";
        
        if (menu != null && menu.getOptions() != null) {
            for (var optionGroup : menu.getOptions()) {
                // optionId로 옵션 그룹 찾기
                if (optionGroup.getId().equals(optionRequest.optionId())) {
                    optionGroupName = optionGroup.getName(); // 옵션 그룹명 (예: "사이즈")
                    
                    // optionItemId로 옵션 아이템 찾기
                    matchingOptionItem = optionGroup.getItems().stream()
                            .filter(item -> item.getId().equals(optionRequest.optionItemId()))
                            .findFirst()
                            .orElse(null);
                    break;
                }
            }
        }

        return new OrderItemOptionData(
                // 사용자 요청 정보
                optionRequest.optionItemId(), // optionItemId를 ID로 사용

                // 옵션 정보
                optionGroupName, // 옵션 그룹명 (예: "사이즈")
                matchingOptionItem != null ? matchingOptionItem.getName() : "옵션 정보 없음", // 옵션 아이템명 (예: "소")
                matchingOptionItem != null ? matchingOptionItem.getAdditionalPrice().intValue() : 0);
    }

    /**
     * 가게별 오늘 주문 개수 조회
     */
    public Long getTodayOrderCount(Long storeId) {
        return orderRepository.countTodayOrdersByStoreId(storeId);
    }

    /**
     * 가게별 오늘 매출 조회 (배달 완료된 주문만)
     */
    public BigDecimal getTodayRevenue(Long storeId) {
        BigDecimal revenue = orderRepository.getTodayRevenueByStoreId(storeId);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    /**
     * 가게별 총 주문 개수 조회
     */
    public Long getTotalOrderCount(Long storeId) {
        return orderRepository.countTotalOrdersByStoreId(storeId);
    }

    /**
     * 가게별 최근 주문 조회
     */
    public List<RecentOrderDto> getRecentOrders(Long storeId, int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        List<Order> orders = orderRepository.findRecentOrdersByStoreId(storeId, pageable);

        return orders.stream()
                .map(this::convertToRecentOrderDto)
                .collect(Collectors.toList());
    }

    // ================================
    // Internal Methods
    // ================================
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

    /**
     * Order Entity를 RecentOrderDto로 변환
     */
    private RecentOrderDto convertToRecentOrderDto(Order order) {
        // 주문 아이템들 변환
        List<OrderItemDto> items = order.getOrderItems().stream()
                .map(orderItem -> OrderItemDto.builder()
                        .name(orderItem.getMenuName())
                        .quantity(orderItem.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return RecentOrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName("고객" + order.getUserId())
                .total(BigDecimal.valueOf(order.getTotalAmount()))
                .status(order.getStatus().name())
                .orderTime(order.getCreatedAt())
                .items(items)
                .build();
    }

}
