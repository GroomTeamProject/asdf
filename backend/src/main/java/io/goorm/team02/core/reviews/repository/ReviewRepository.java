package io.goorm.team02.core.reviews.repository;

import io.goorm.team02.core.reviews.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 가게별 평균 평점 조회
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.store.id = :storeId")
    BigDecimal findAverageRatingByStoreId(@Param("storeId") Long storeId);

    /**
     * 가게별 리뷰 개수 조회
     */
    long countByStoreId(Long storeId);
}