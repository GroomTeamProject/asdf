package io.goorm.team02.core.reviews.service;

import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.orders.service.OrderService;
import io.goorm.team02.core.reviews.controller.dto.ReviewRequest;
import io.goorm.team02.core.reviews.domain.Review;
import io.goorm.team02.core.reviews.repository.ReviewRepository;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.repository.UserinfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final OrderService orderService;
    private final ReviewRepository reviewRepository;
    private final UserinfoRepository userRepository;

    // ================================
    // API Methods
    // ================================

    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다: " + reviewId));
    }

    // ================================
    // Business Logic
    // ================================

    /**
     * 리뷰 생성
     */
    @Transactional
    public Review create(ReviewRequest reviewRequest) {
        // 1. 조회 & 검증
        User user = getUserById(reviewRequest.userId());
        Order order = getOrderById(reviewRequest.orderId());

        if (!order.getUser().getId().equals(reviewRequest.userId())) {
            throw new IllegalStateException("본인의 주문에만 리뷰를 작성할 수 있습니다");
        }
        if (reviewRepository.findByOrderId(reviewRequest.orderId()).isPresent()) {
            throw new IllegalStateException("이미 리뷰가 작성된 주문입니다");
        }

        // 2. 참조 엔티티 조회
        Store store = order.getStore();

        // 3. Review 도메인에 위임
        Review review = Review.create(order, user, store, reviewRequest.rating(), reviewRequest.content());

        // 4. 저장
        return reviewRepository.save(review);
    }

    /**
     * 리뷰 상세 조회
     */
    public Review getById(Long reviewId) {
        return getReviewById(reviewId);
    }

    /**
     * 가게별 리뷰 목록 조회
     */
    public List<Review> getAllByStoreId(Long storeId) {
        return reviewRepository.findAllByStoreIdWithDetails(storeId);
    }

    /**
     * 사용자별 리뷰 목록 조회
     */
    public List<Review> getAllByUserId(Long userId) {
        return reviewRepository.findAllByUserIdWithDetails(userId);
    }

    /**
     * 리뷰 수정
     */
    @Transactional
    public Review update(Long reviewId, ReviewRequest reviewRequest) {
        Review review = getReviewById(reviewId);

        review.update(reviewRequest.rating(), reviewRequest.content());

        return reviewRepository.save(review);
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public void delete(Long reviewId) {
        Review review = getReviewById(reviewId);

        reviewRepository.delete(review);
    }

    /**
     * 사장님 답글 작성
     */
    @Transactional
    public Review addOwnerReply(Long reviewId, String reply) {
        Review review = getReviewById(reviewId);
        review.addOwnerReply(reply);

        return reviewRepository.save(review);
    }

    /**
     * 리뷰 신고
     */
    @Transactional
    public Review report(Long reviewId) {
        Review review = getReviewById(reviewId);
        review.report();

        return reviewRepository.save(review);
    }

    /**
     * 가게별 평균 평점 조회
     */
    public Double getAverageRatingByStoreId(Long storeId) {
        BigDecimal averageRating = reviewRepository.findAverageRatingByStoreId(storeId);
        return averageRating != null ? averageRating.doubleValue() : 0.0;
    }

    /**
     * 가게별 리뷰 개수 조회
     */
    public Long getReviewCountByStoreId(Long storeId) {
        return reviewRepository.countByStoreId(storeId);
    }

    // ================================
    // Internal Methods
    // ================================

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
    }

    private Order getOrderById(Long orderId) {
        return orderService.getOrderById(orderId);
    }
}
