package io.goorm.team02.core.orders.service;

import org.springframework.transaction.annotation.Transactional;

import io.goorm.team02.core.orders.controller.dto.OrderAcceptRequest;
import io.goorm.team02.core.orders.controller.dto.OrderCancelRequest;
import io.goorm.team02.core.orders.controller.dto.OrderRejectRequest;
import io.goorm.team02.core.orders.controller.dto.OrderResponse;
import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.orders.event.*;
import io.goorm.team02.core.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 가게에서 주문 수락 (예상 조리 시간 포함)
     */
    @Transactional
    public OrderResponse acceptOrder(Long orderId, OrderAcceptRequest request) {
        Order order = orderService.getOrderById(orderId);
        
        // 주문 수락 처리
        order.accept(request.minCookingTime(), request.maxCookingTime());
        Order dbOrder = orderRepository.save(order);
        
        // 주문 상태 변경 이벤트 발행
        OrderAcceptedEvent event = new OrderAcceptedEvent(this, dbOrder);
        eventPublisher.publishEvent(event);
        
        return OrderResponse.from(dbOrder);
    }

    /**
     * 가게에서 조리 시작
     */
    @Transactional
    public OrderResponse startCooking(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        
        order.startCooking();
        Order dbOrder = orderRepository.save(order);
        
        // 주문 조리 시작 이벤트 발행
        eventPublisher.publishEvent(new OrderCookingEvent(this, dbOrder));
        
        return OrderResponse.from(dbOrder);
    }

    /**
     * 가게에서 조리 완료
     */
    @Transactional
    public OrderResponse completeCooking(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        
        order.completeCooking();
        Order dbOrder = orderRepository.save(order);
        
        // 주문 준비 완료 이벤트 발행
        eventPublisher.publishEvent(new OrderReadyEvent(this, dbOrder));
        
        return OrderResponse.from(dbOrder);
    }

    /**
     * 배달 시작
     */
    @Transactional
    public OrderResponse startDelivery(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        
        order.startDelivery();
        Order dbOrder = orderRepository.save(order);
        
        // 주문 배달 시작 이벤트 발행
        eventPublisher.publishEvent(new OrderDeliveringEvent(this, dbOrder));
        
        return OrderResponse.from(dbOrder);
    }

    /**
     * 배달 완료
     */
    @Transactional
    public OrderResponse deliverOrder(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        
        order.deliver();
        Order dbOrder = orderRepository.save(order);
        
        // 주문 배달 완료 이벤트 발행
        eventPublisher.publishEvent(new OrderDeliveredEvent(this, dbOrder));
        
        return OrderResponse.from(dbOrder);
    }

    /**
     * 주문 취소
     */
    @Transactional
    public OrderResponse cancelOrder(Long orderId, OrderCancelRequest request) {    
        Order order = orderService.getOrderById(orderId);
        
        order.cancel(request.cancelReason());
        Order dbOrder = orderRepository.save(order);
        
        // 주문 취소 이벤트 발행
        eventPublisher.publishEvent(new OrderCancelledEvent(this, dbOrder, request.cancelReason()));
        
        return OrderResponse.from(dbOrder);
    }

    /**
     * 가게에서 주문 거절
     */
    @Transactional
    public OrderResponse rejectOrder(Long orderId, OrderRejectRequest request) {
        Order order = orderService.getOrderById(orderId);
        
        order.reject(request.rejectReason());
        Order dbOrder = orderRepository.save(order);
        
        // 주문 거절 이벤트 발행
        eventPublisher.publishEvent(new OrderRejectedEvent(this, dbOrder, request.rejectReason()));
        
        return OrderResponse.from(dbOrder);
    }

}
