package io.goorm.team02.order.service;

import org.springframework.transaction.annotation.Transactional;

import io.goorm.team02.dto.orders.OrderAcceptRequest;
import io.goorm.team02.dto.orders.OrderCancelRequest;
import io.goorm.team02.dto.orders.OrderRejectRequest;
import io.goorm.team02.dto.orders.OrderResponse;
import io.goorm.team02.order.entity.Order;
import io.goorm.team02.order.event.OrderAcceptedEvent;
import io.goorm.team02.order.event.OrderCookingEvent;
import io.goorm.team02.order.event.OrderReadyEvent;
import io.goorm.team02.order.event.OrderDeliveringEvent;
import io.goorm.team02.order.event.OrderDeliveredEvent;
import io.goorm.team02.order.event.OrderCancelledEvent;
import io.goorm.team02.order.event.OrderRejectedEvent;
import io.goorm.team02.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.goorm.team02.kafka.client.EventPublisher;

@Service
@RequiredArgsConstructor
public class OrderStatusService {
    public static final String ORDER_EVENTS_TOPIC = "order-events";

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final EventPublisher eventPublisher;

    /**
     * 가게에서 주문 수락 (예상 조리 시간 포함)
     */
    @Transactional
    public OrderResponse acceptOrder(Long orderId, OrderAcceptRequest request) {
        Order order = orderService.getOrderById(orderId);

        // 주문 수락 처리
        order.accept(request.minCookingTime(), request.maxCookingTime());
        Order dbOrder = orderRepository.save(order);

        // 주문 상태 변경 이벤트 발행 (Kafka)
        eventPublisher.publish(ORDER_EVENTS_TOPIC, new OrderAcceptedEvent(dbOrder));

        return dbOrder.toResponse();
    }

    /**
     * 가게에서 조리 시작
     */
    @Transactional
    public OrderResponse startCooking(Long orderId) {
        Order order = orderService.getOrderById(orderId);

        order.startCooking();
        Order dbOrder = orderRepository.save(order);

        // 주문 조리 시작 이벤트 발행 (Kafka)
        eventPublisher.publish(ORDER_EVENTS_TOPIC, new OrderCookingEvent(dbOrder));

        return dbOrder.toResponse();
    }

    /**
     * 가게에서 조리 완료
     */
    @Transactional
    public OrderResponse completeCooking(Long orderId) {
        Order order = orderService.getOrderById(orderId);

        order.completeCooking();
        Order dbOrder = orderRepository.save(order);

        // 주문 준비 완료 이벤트 발행 (Kafka)
        eventPublisher.publish(ORDER_EVENTS_TOPIC, new OrderReadyEvent(dbOrder));

        return dbOrder.toResponse();
    }

    /**
     * 배달 시작
     */
    @Transactional
    public OrderResponse startDelivery(Long orderId) {
        Order order = orderService.getOrderById(orderId);

        order.startDelivery();
        Order dbOrder = orderRepository.save(order);

        // 주문 배달 시작 이벤트 발행 (Kafka)
        eventPublisher.publish(ORDER_EVENTS_TOPIC, new OrderDeliveringEvent(dbOrder));

        return dbOrder.toResponse();
    }

    /**
     * 배달 완료
     */
    @Transactional
    public OrderResponse deliverOrder(Long orderId) {
        Order order = orderService.getOrderById(orderId);

        order.deliver();
        Order dbOrder = orderRepository.save(order);

        // 주문 배달 완료 이벤트 발행 (Kafka)
        eventPublisher.publish(ORDER_EVENTS_TOPIC, new OrderDeliveredEvent(dbOrder));

        return dbOrder.toResponse();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public OrderResponse cancelOrder(Long orderId, OrderCancelRequest request) {
        Order order = orderService.getOrderById(orderId);

        order.cancel(request.cancelReason());
        Order dbOrder = orderRepository.save(order);

        // 주문 취소 이벤트 발행 (Kafka)
        eventPublisher.publish(ORDER_EVENTS_TOPIC, new OrderCancelledEvent(dbOrder, request.cancelReason()));

        return dbOrder.toResponse();
    }

    /**
     * 가게에서 주문 거절
     */
    @Transactional
    public OrderResponse rejectOrder(Long orderId, OrderRejectRequest request) {
        Order order = orderService.getOrderById(orderId);

        order.reject(request.rejectReason());
        Order dbOrder = orderRepository.save(order);

        // 주문 거절 이벤트 발행 (Kafka)
        eventPublisher.publish(ORDER_EVENTS_TOPIC, new OrderRejectedEvent(dbOrder, request.rejectReason()));

        return dbOrder.toResponse();
    }

}
