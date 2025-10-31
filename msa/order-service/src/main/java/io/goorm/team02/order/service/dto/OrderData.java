package io.goorm.team02.order.service.dto;

import java.util.List;

/**
 * 주문 생성에 필요한 데이터를 조합하는 서비스 계층 DTO
 */
public record OrderData(
        // 사용자 요청 정보
        Long userId,
        Long storeId,
        String deliveryAddress,
        String deliveryDetailAddress,
        String phone,
        String orderMemo,
        
        // 가게 정보
        String storeName,
        String storePhone,
        String storeAddress,
        String storeDetailAddress,
        Integer deliveryFee,
        
        // 주문 아이템 목록
        List<OrderItemData> orderItems
) {
}