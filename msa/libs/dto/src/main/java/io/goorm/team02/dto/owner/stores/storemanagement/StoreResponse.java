package io.goorm.team02.dto.owner.stores.storemanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(description = "가게 정보 응답")
public class StoreResponse {

    @Schema(description = "가게 ID", example = "1")
    private Long id;

    @Schema(description = "사업자등록번호", example = "123-45-67890")
    private String businessNumber;

    @Schema(description = "가게명", example = "맛있는 한식당")
    private String name;

    @Schema(description = "가게 설명", example = "정성스럽게 만든 전통 한식을 제공합니다")
    private String description;

    @Schema(description = "가게 전화번호", example = "02-1234-5678")
    private String phone;

    @Schema(description = "가게 주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "가게 상세주소", example = "1층 101호")
    private String detailAddress;

    @Schema(description = "위도", example = "37.5665")
    private BigDecimal latitude;

    @Schema(description = "경도", example = "126.9780")
    private BigDecimal longitude;

    @Schema(description = "가게 카테고리",
            allowableValues = {"KOREAN", "CHINESE", "WESTERN", "JAPANESE", "FAST_FOOD", "CHICKEN", "PIZZA", "DESSERT"},
            example = "KOREAN")
    private String category;

    @Schema(description = "최소 주문금액", example = "15000.00")
    private BigDecimal minOrderAmount;

    @Schema(description = "배달비", example = "2000.00")
    private BigDecimal deliveryFee;

    @Schema(description = "최소 배달시간(분)", example = "30")
    private Integer deliveryTimeMin;

    @Schema(description = "최대 배달시간(분)", example = "45")
    private Integer deliveryTimeMax;

    @Schema(description = "가게 이미지 URL", example = "https://example.com/images/store.jpg")
    private String imageUrl;

    @Schema(description = "가게 상태",
            allowableValues = {"OPEN", "CLOSED", "TEMPORARILY_CLOSED", "BREAK"},
            example = "OPEN")
    private String status;

    @Schema(description = "평점", example = "4.5")
    private BigDecimal rating;

    @Schema(description = "리뷰 수", example = "128")
    private Integer reviewCount;

    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;

    // from 메서드 제거 - Entity 의존성 완전 제거!
}