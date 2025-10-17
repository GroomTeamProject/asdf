package io.goorm.team02.core.reviews.controller;

import io.goorm.team02.core.reviews.controller.dto.OwnerReplyRequest;
import io.goorm.team02.core.reviews.controller.dto.ReviewRequest;
import io.goorm.team02.core.reviews.controller.dto.ReviewResponse;
import io.goorm.team02.core.auth.annotation.CurrentUser;
import io.goorm.team02.core.stores.domain.TempUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "리뷰 관리", description = "리뷰 관련 API")
public interface ReviewControllerDocs {

    @Operation(summary = "리뷰 생성", description = "새로운 리뷰를 생성합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 리뷰가 작성된 주문"),
            @ApiResponse(responseCode = "403", description = "본인의 주문에만 리뷰 작성 가능")
    })
    public ReviewResponse create(@RequestBody ReviewRequest reviewRequest, @CurrentUser TempUser user);

    @Operation(summary = "가게별 리뷰 목록 조회", description = "특정 가게의 리뷰 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    public List<ReviewResponse> getAllByStoreId(
            @Parameter(description = "가게 ID", required = true) @RequestParam("storeId") Long storeId);

    @Operation(summary = "사용자별 리뷰 목록 조회", description = "특정 사용자의 리뷰 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    public List<ReviewResponse> getAllByUserId(
            @Parameter(description = "사용자 ID", required = true) @RequestParam("userId") Long userId);

    @Operation(summary = "리뷰 상세 조회", description = "특정 리뷰의 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    public ReviewResponse getById(
            @Parameter(description = "리뷰 ID", required = true) @PathVariable Long reviewId, @CurrentUser TempUser user);

    @Operation(summary = "리뷰 수정", description = "기존 리뷰를 수정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    public ReviewResponse update(
            @Parameter(description = "리뷰 ID", required = true) @PathVariable Long reviewId,
            @RequestBody ReviewRequest reviewRequest, @CurrentUser TempUser user);

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    public void delete(
            @Parameter(description = "리뷰 ID", required = true) @PathVariable Long reviewId, @CurrentUser TempUser user);

    @Operation(summary = "사장님 답글 작성", description = "리뷰에 사장님 답글을 작성합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    public ReviewResponse addOwnerReply(
            @Parameter(description = "리뷰 ID", required = true) @PathVariable Long reviewId,
            @RequestBody OwnerReplyRequest request);

    @Operation(summary = "리뷰 신고", description = "부적절한 리뷰를 신고합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 신고 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    public ReviewResponse report(
            @Parameter(description = "리뷰 ID", required = true) @PathVariable Long reviewId);

    @Operation(summary = "가게별 평균 평점 조회", description = "특정 가게의 평균 평점을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "평균 평점 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    public Double getAverageRatingByStoreId(
            @Parameter(description = "가게 ID", required = true) @RequestParam("storeId") Long storeId);

    @Operation(summary = "가게별 리뷰 개수 조회", description = "특정 가게의 리뷰 개수를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 개수 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    public Long getReviewCountByStoreId(
            @Parameter(description = "가게 ID", required = true) @RequestParam("storeId") Long storeId);
}
