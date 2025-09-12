package io.goorm.team02.core.reviews.controller.dto;

import io.goorm.team02.core.reviews.domain.Review;

import java.time.LocalDateTime;

/**
 * 리뷰 응답 DTO
 */
public record ReviewResponse(
        Long id,
        Long orderId,
        Long userId,
        String userName,
        Long storeId,
        String storeName,
        Integer rating,
        String content,
        String ownerReply,
        LocalDateTime ownerRepliedAt,
        Boolean isReported,
        LocalDateTime createdAt
) {

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getOrder() != null ? review.getOrder().getId() : null,
                review.getUser() != null ? review.getUser().getId() : null,
                review.getUser() != null ? review.getUser().getName() : null,
                review.getStore() != null ? review.getStore().getId() : null,
                review.getStore() != null ? review.getStore().getName() : null,
                review.getRating(),
                review.getContent(),
                review.getOwnerReply(),
                review.getOwnerRepliedAt(),
                review.getIsReported(),
                review.getCreatedAt()
);
    }
}
