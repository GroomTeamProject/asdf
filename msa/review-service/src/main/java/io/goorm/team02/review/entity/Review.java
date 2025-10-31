package io.goorm.team02.review.entity;

import io.goorm.team02.common.config.BaseEntity;
import io.goorm.team02.dto.reviews.ReviewResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@EqualsAndHashCode(callSuper = true)
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_id", nullable = false)
	private Long orderId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "store_id", nullable = false)
	private Long storeId;

	@Column(nullable = false)
	private Integer rating; // 1~5 점수

	@Column(columnDefinition = "TEXT")
	private String content;

	@Column(name = "owner_reply", columnDefinition = "TEXT")
	private String ownerReply;

	@Column(name = "owner_replied_at")
	private LocalDateTime ownerRepliedAt;

	// ================================
	// Domain Logic
	// ================================

	/**
	 * 리뷰 검증
	 */
	public void validate() {
		if (userId == null) {
			throw new IllegalStateException("사용자 정보가 필요합니다");
		}
		if (storeId == null) {
			throw new IllegalStateException("가게 정보가 필요합니다");
		}
		if (orderId == null) {
			throw new IllegalStateException("주문 정보가 필요합니다");
		}
		if (rating == null || rating < 1 || rating > 5) {
			throw new IllegalStateException("평점은 1~5 사이여야 합니다");
		}
	}

	/**
	 * 사장님 답글 작성
	 */
	public void addOwnerReply(String reply) {
		if (reply == null || reply.trim().isEmpty()) {
			throw new IllegalArgumentException("답글 내용이 필요합니다");
		}
		this.ownerReply = reply;
		this.ownerRepliedAt = LocalDateTime.now();
	}

	/**
	 * 리뷰 수정
	 */
	public void update(Integer rating, String content) {
		if (rating == null || rating < 1 || rating > 5) {
			throw new IllegalArgumentException("평점은 1~5 사이여야 합니다");
		}
		this.rating = rating;
		this.content = content;
	}

	// ================================
	// Factory Method
	// ================================
	public static Review create(Long orderId, Long userId, Long storeId, Integer rating, String content) {
		Review review = new Review();
		review.setOrderId(orderId);
		review.setUserId(userId);
		review.setStoreId(storeId);
		review.setRating(rating);
		review.setContent(content);

		// 리뷰 검증
		review.validate();

		return review;
	}

	/**
	 * ReviewResponse로 변환
	 */
	public ReviewResponse toReviewResponse() {
		return new ReviewResponse(
				this.id,
				this.orderId,
				this.userId,
				this.storeId,
				this.rating,
				this.content,
				this.ownerReply,
				this.ownerRepliedAt,
				this.createdAt,
				this.updatedAt);
	}
}
