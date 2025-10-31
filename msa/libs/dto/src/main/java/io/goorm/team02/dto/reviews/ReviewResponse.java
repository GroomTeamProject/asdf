package io.goorm.team02.dto.reviews;

import java.time.LocalDateTime;

/**
 * 리뷰 응답 DTO
 */
public record ReviewResponse(
        Long id,
        Long orderId,
        Long userId,
        Long storeId,
        Integer rating,
        String content,
        String ownerReply,
        LocalDateTime ownerRepliedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
