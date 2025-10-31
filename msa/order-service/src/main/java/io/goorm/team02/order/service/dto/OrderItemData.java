package io.goorm.team02.order.service.dto;

import java.util.List;

/**
 * 주문 아이템 생성에 필요한 데이터를 조합하는 서비스 계층 DTO
 */
public record OrderItemData(
        // 사용자 요청 정보
        Long menuId,
        Integer quantity,
        
        // 메뉴 정보
        String menuName,
        Integer menuPrice,
        
        // 옵션 목록
        List<OrderItemOptionData> options
) {
}
