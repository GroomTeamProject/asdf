package io.goorm.team02.order.service.dto;

/**
 * 주문 아이템 옵션 생성에 필요한 데이터를 조합하는 서비스 계층 DTO
 */
public record OrderItemOptionData(
        // 사용자 요청 정보
        Long optionId,
        
        // 옵션 정보
        String optionName,
        String optionItemName,
        Integer additionalPrice
) {
}
