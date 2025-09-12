package io.goorm.team02.core.reviews.domain;

import io.goorm.team02.core.common.domain.BaseEntity;
import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.users.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

	@OneToOne
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(nullable = false)
	private Integer rating; // 1~5 점수

	@Column(columnDefinition = "TEXT")
	private String content;

	@Column(name = "owner_reply", columnDefinition = "TEXT")
	private String ownerReply;

	@Column(name = "owner_replied_at")
	private LocalDateTime ownerRepliedAt;

	@Column(name = "is_reported", nullable = false)
	private Boolean isReported = false;


	// ================================
	// Domain Logic
	// ================================

	/**
	 * 리뷰 검증
	 */
	public void validate() {
		if (user == null) {
			throw new IllegalStateException("사용자 정보가 필요합니다");
		}
		if (store == null) {
			throw new IllegalStateException("가게 정보가 필요합니다");
		}
		if (order == null) {
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
	 * 리뷰 신고
	 */
	public void report() {
		this.isReported = true;
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
	public static Review create(Order order, User user, Store store, Integer rating, String content) {
		Review review = new Review();
		review.setOrder(order);
		review.setUser(user);
		review.setStore(store);
		review.setRating(rating);
		review.setContent(content);
		review.setIsReported(false);

		// 리뷰 검증
		review.validate();

		return review;
	}
}
