package io.goorm.team02.review.controller;

import io.goorm.team02.dto.reviews.ReviewRequest;
import io.goorm.team02.dto.reviews.ReviewResponse;
import io.goorm.team02.dto.reviews.OwnerReplyRequest;
import io.goorm.team02.review.entity.Review;
import io.goorm.team02.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController implements ReviewControllerDocs {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewResponse create(@Valid @RequestBody ReviewRequest reviewRequest, @RequestParam Long userId) {
        Review review = reviewService.create(reviewRequest, userId);
        return review.toReviewResponse();
    }

    @GetMapping("/store/{storeId}")
    public List<ReviewResponse> getAllByStoreId(@PathVariable Long storeId) {
        List<Review> reviews = reviewService.getAllByStoreId(storeId);
        return reviews.stream()
                .map(Review::toReviewResponse)
                .toList();
    }

    @GetMapping("/store/{storeId}/count")
    public Long getReviewCountByStoreId(@PathVariable Long storeId) {
        return reviewService.getReviewCountByStoreId(storeId);
    }

    @GetMapping("/store/{storeId}/rating")
    public Double getAverageRatingByStoreId(@PathVariable Long storeId) {
        return reviewService.getAverageRatingByStoreId(storeId);
    }

    @GetMapping("/user/{userId}")
    public List<ReviewResponse> getAllByUserId(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getAllByUserId(userId);
        return reviews.stream()
                .map(Review::toReviewResponse)
                .toList();
    }

    @GetMapping("/{reviewId}")
    public ReviewResponse getById(@PathVariable Long reviewId, @RequestParam Long userId) {
        Review review = reviewService.getById(reviewId, userId);
        return review.toReviewResponse();
    }

    @DeleteMapping("/{reviewId}")
    public void delete(@PathVariable Long reviewId, @RequestParam Long userId) {
        reviewService.delete(reviewId, userId);
    }

    @PostMapping("/{reviewId}/reply")
    public ReviewResponse addOwnerReply(@PathVariable Long reviewId, @RequestBody OwnerReplyRequest request) {
        Review review = reviewService.addOwnerReply(reviewId, request.reply());
        return review.toReviewResponse();
    }

}
