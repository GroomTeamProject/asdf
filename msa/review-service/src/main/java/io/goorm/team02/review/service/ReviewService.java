package io.goorm.team02.review.service;

import io.goorm.team02.review.repository.ReviewRepository;
import io.goorm.team02.dto.reviews.ReviewRequest;
import io.goorm.team02.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // ================================
    // Business Logic
    // ================================

    /**
     * 리뷰 생성
     */
    @Transactional
    public Review create(ReviewRequest reviewRequest, Long userId) {
        // 1. 조회 & 검증
        // TODO: 주문 조회 & 검증

        // 2. 참조 엔티티 조회
        // TODO: 가게 조회 & 검증

        // 3. Review 도메인에 위임
        Review review = Review.create(
                reviewRequest.orderId(),
                reviewRequest.storeId(),
                userId,
                reviewRequest.rating(),
                reviewRequest.content());

        // 4. 저장
        return reviewRepository.save(review);
    }

    /**
     * 리뷰 상세 조회 (권한 검증 포함)
     */
    public Review getById(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다: " + reviewId));

        // 권한 검증: 리뷰 작성자만 조회 가능
        if (!review.getUserId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 리뷰만 조회할 수 있습니다.");
        }

        return review;
    }

    /**
     * 사용자별 리뷰 목록 조회
     */
    public List<Review> getAllByUserId(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 가게별 리뷰 목록 조회
     */
    public List<Review> getAllByStoreId(Long storeId) {
        return reviewRepository.findByStoreIdOrderByCreatedAtDesc(storeId);
    }

    /**
     * 리뷰 수정 (권한 검증 포함)
     */
    @Transactional
    public Review update(Long reviewId, ReviewRequest reviewRequest, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다: " + reviewId));

        // 권한 검증: 리뷰 작성자만 수정 가능
        if (!review.getUserId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        review.update(reviewRequest.rating(), reviewRequest.content());

        return reviewRepository.save(review);
    }

    /**
     * 리뷰 삭제 (권한 검증 포함)
     */
    @Transactional
    public void delete(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다: " + reviewId));

        // 권한 검증: 리뷰 작성자만 삭제 가능
        if (!review.getUserId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    /**
     * 사장님 답글 작성
     */
    @Transactional
    public Review addOwnerReply(Long reviewId, String reply) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다: " + reviewId));
        review.addOwnerReply(reply);

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
}
