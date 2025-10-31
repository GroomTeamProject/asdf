package io.goorm.team02.review.repository;

import io.goorm.team02.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 가게별 리뷰 목록 조회
     */
    List<Review> findByStoreIdOrderByCreatedAtDesc(Long storeId);

    /**
     * 사용자별 리뷰 목록 조회
     */
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 주문별 리뷰 조회
     */
    Optional<Review> findByOrderId(Long orderId);

    /**
     * 가게별 평균 평점 조회
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.storeId = :storeId")
    BigDecimal findAverageRatingByStoreId(@Param("storeId") Long storeId);

    /**
     * 가게별 리뷰 개수 조회
     */
    long countByStoreId(Long storeId);
}
