package io.goorm.team02.order.controller;

import io.goorm.team02.dto.orders.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "주문 관리", description = "주문 관련 API")
public interface OrderControllerDocs {

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 가게를 찾을 수 없음")
    })
    public OrderResponse create(OrderRequest order, Long userId);

    @Operation(summary = "주문 목록 조회", description = "다양한 조건으로 주문 목록을 조회합니다 (페이지네이션 지원)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    public Page<OrderResponse> getAllByParams(OrderSearchRequest searchRequest, Long userId);

    @Operation(summary = "주문 상세 조회", description = "특정 주문의 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    public OrderResponse getOrderDetail(
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId,
            Long userId);

    @Operation(summary = "주문 수락", description = "가게에서 주문을 수락하고 예상 조리 시간을 설정합니다 (PENDING → ACCEPTED)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 수락 성공"),
            @ApiResponse(responseCode = "400", description = "수락할 수 없는 주문 상태"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    public OrderResponse acceptOrder(
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId,
            OrderAcceptRequest request);

    @Operation(summary = "주문 거절", description = "가게에서 주문을 거절합니다 (PENDING → CANCELLED)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 거절 성공"),
            @ApiResponse(responseCode = "400", description = "거절할 수 없는 주문 상태"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    public OrderResponse rejectOrder(
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId,
            OrderRejectRequest request);

    @Operation(summary = "조리 시작", description = "가게에서 조리를 시작합니다 (ACCEPTED → COOKING)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조리 시작 성공"),
            @ApiResponse(responseCode = "400", description = "조리를 시작할 수 없는 주문 상태"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    public OrderResponse startCooking(
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId);

    @Operation(summary = "조리 완료", description = "가게에서 조리를 완료합니다 (COOKING → READY)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조리 완료 성공"),
            @ApiResponse(responseCode = "400", description = "조리를 완료할 수 없는 주문 상태"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    public OrderResponse completeCooking(
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId);

    @Operation(summary = "배달 완료", description = "배달을 완료합니다 (READY/PICKED_UP → DELIVERED)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배달 완료 성공"),
            @ApiResponse(responseCode = "400", description = "배달을 완료할 수 없는 주문 상태"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    public OrderResponse deliverOrder(
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId);

    @Operation(summary = "주문 취소", description = "주문을 취소합니다 (PENDING/ACCEPTED → CANCELLED)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
            @ApiResponse(responseCode = "400", description = "취소할 수 없는 주문 상태"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    public OrderResponse cancelOrder(
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId,
            OrderCancelRequest request);

    @Operation(summary = "[배달기사] 픽업 가능한 주문 목록 조회", description = "조리 완료된 주문들을 조회합니다 (READY 상태)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "픽업 가능한 주문 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    List<OrderResponse> getAvailableOrders(
            @Parameter(description = "가게 ID (선택사항, 특정 가게의 주문만 조회)") @RequestParam(required = false) Long storeId);

    @Operation(summary = "가게 대시보드 데이터 조회",
            description = "Store 서비스에서 사용하는 특정 가게의 대시보드 데이터를 조회합니다 (오늘 주문수, 매출, 최근 주문 등)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대시보드 데이터 조회 성공"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    OrderDashboardDto getDashboardData(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Long storeId);

    @Operation(summary = "오늘 주문 개수 조회",
            description = "특정 가게의 오늘 주문 개수를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "오늘 주문 개수 조회 성공"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    Long getTodayOrderCount(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Long storeId);

    @Operation(summary = "오늘 매출 조회",
            description = "특정 가게의 오늘 매출을 조회합니다 (배달 완료된 주문만 포함)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "오늘 매출 조회 성공"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    BigDecimal getTodayRevenue(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Long storeId);

    @Operation(summary = "총 주문 개수 조회",
            description = "특정 가게의 전체 주문 개수를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "총 주문 개수 조회 성공"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    Long getTotalOrderCount(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Long storeId);

    @Operation(summary = "최근 주문 목록 조회",
            description = "특정 가게의 최근 주문 목록을 조회합니다 (생성일 기준 내림차순)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최근 주문 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    List<RecentOrderDto> getRecentOrders(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Long storeId,
            @Parameter(description = "조회할 주문 개수", required = false, example = "5")
            @RequestParam(defaultValue = "5") int limit);
}