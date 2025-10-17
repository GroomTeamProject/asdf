package io.goorm.team02.core.reviews.repository;

import io.goorm.team02.core.reviews.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 가게별 리뷰 목록 조회 (상세 정보 포함)
     */
    @Query("SELECT r FROM Review r "
            + "LEFT JOIN FETCH r.user u "
            + "LEFT JOIN FETCH r.store s "
            + "LEFT JOIN FETCH r.order o "
            + "WHERE r.store.id = :storeId "
            + "ORDER BY r.createdAt DESC")
    List<Review> findAllByStoreIdWithDetails(@Param("storeId") Long storeId);

    /**
     * 사용자별 리뷰 목록 조회
     */
    @Query("SELECT r FROM Review r "
            + "LEFT JOIN FETCH r.user u "
            + "LEFT JOIN FETCH r.store s "
            + "WHERE r.user.id = :userId "
            + "ORDER BY r.createdAt DESC")
    List<Review> findAllByUserIdWithDetails(@Param("userId") Long userId);

    /**
     * 주문별 리뷰 조회
     */
    Optional<Review> findByOrderId(Long orderId);

    /**
     * 가게별 평점별 리뷰 조회
     */
    @Query("SELECT r FROM Review r "
            + "LEFT JOIN FETCH r.user u "
            + "WHERE r.store.id = :storeId AND r.rating = :rating "
            + "ORDER BY r.createdAt DESC")
    List<Review> findByStoreIdAndRating(@Param("storeId") Long storeId,
            @Param("rating") Integer rating);

    /**
     * 가게별 평균 평점 조회
     */
    @Query("SELECT AVG(r.rating) FROM Review r "
            + "WHERE r.store.id = :storeId")
    BigDecimal findAverageRatingByStoreId(@Param("storeId") Long storeId);

    /**
     * 가게별 리뷰 개수 조회
     */
    long countByStoreId(Long storeId);

    /**
     * 신고된 리뷰 목록 조회
     */
    @Query("SELECT r FROM Review r "
            + "LEFT JOIN FETCH r.user u "
            + "LEFT JOIN FETCH r.store s "
            + "WHERE r.isReported = true "
            + "ORDER BY r.createdAt DESC")
    List<Review> findAllReportedReviews();

    /**
     * 가게별 리뷰 목록 조회 (간단한 정보만)
     */
    List<Review> findByStoreIdOrderByCreatedAtDesc(Long storeId);
}
