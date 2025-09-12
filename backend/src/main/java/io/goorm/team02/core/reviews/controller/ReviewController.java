package io.goorm.team02.core.reviews.controller;

import io.goorm.team02.core.reviews.controller.dto.OwnerReplyRequest;
import io.goorm.team02.core.reviews.controller.dto.ReviewRequest;
import io.goorm.team02.core.reviews.controller.dto.ReviewResponse;
import io.goorm.team02.core.reviews.domain.Review;
import io.goorm.team02.core.reviews.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController implements ReviewControllerDocs {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewResponse create(@RequestBody ReviewRequest reviewRequest) {
        Review review = reviewService.create(reviewRequest);
        return ReviewResponse.from(review);
    }

    @GetMapping("/store")
    public List<ReviewResponse> getAllByStoreId(@RequestParam("storeId") Long storeId) {
        List<Review> reviews = reviewService.getAllByStoreId(storeId);
        return reviews.stream()
                .map(ReviewResponse::from)
                .toList();
    }

    @GetMapping("/user")
    public List<ReviewResponse> getAllByUserId(@RequestParam("userId") Long userId) {
        List<Review> reviews = reviewService.getAllByUserId(userId);
        return reviews.stream()
                .map(ReviewResponse::from)
                .toList();
    }

    @GetMapping("/{reviewId}")
    public ReviewResponse getById(@PathVariable Long reviewId) {
        Review review = reviewService.getById(reviewId);
        return ReviewResponse.from(review);
    }

    @PutMapping("/{reviewId}")
    public ReviewResponse update(@PathVariable Long reviewId, @RequestBody ReviewRequest reviewRequest) {
        Review review = reviewService.update(reviewId, reviewRequest);
        return ReviewResponse.from(review);
    }

    @DeleteMapping("/{reviewId}")
    public void delete(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);
    }

    @PostMapping("/{reviewId}/reply")
    public ReviewResponse addOwnerReply(@PathVariable Long reviewId, @RequestBody OwnerReplyRequest request) {
        Review review = reviewService.addOwnerReply(reviewId, request.reply());
        return ReviewResponse.from(review);
    }

    @PostMapping("/{reviewId}/report")
    public ReviewResponse report(@PathVariable Long reviewId) {
        Review review = reviewService.report(reviewId);
        return ReviewResponse.from(review);
    }

    @GetMapping("/store/rating")
    public Double getAverageRatingByStoreId(@RequestParam("storeId") Long storeId) {
        return reviewService.getAverageRatingByStoreId(storeId);
    }

    @GetMapping("/store/count")
    public Long getReviewCountByStoreId(@RequestParam("storeId") Long storeId) {
        return reviewService.getReviewCountByStoreId(storeId);
    }
}
