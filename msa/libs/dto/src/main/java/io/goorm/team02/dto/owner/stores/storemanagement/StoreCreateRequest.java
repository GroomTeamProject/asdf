package io.goorm.team02.dto.owner.stores.storemanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "가게 생성 요청")
public class StoreCreateRequest {

    @NotBlank(message = "사업자등록번호는 필수입니다")
    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "사업자등록번호 형식이 올바르지 않습니다")
    @Schema(description = "사업자등록번호", required = true, example = "123-45-67890")
    private String businessNumber;

    @NotBlank(message = "가게명은 필수입니다")
    @Size(max = 100, message = "가게명은 100자를 초과할 수 없습니다")
    @Schema(description = "가게명", required = true, example = "맛있는 한식당")
    private String name;

    @Size(max = 500, message = "가게 설명은 500자를 초과할 수 없습니다")
    @Schema(description = "가게 설명", example = "정성스럽게 만든 전통 한식을 제공합니다")
    private String description;

    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "\\d{2,3}-\\d{3,4}-\\d{4}", message = "전화번호 형식이 올바르지 않습니다")
    @Schema(description = "가게 전화번호", required = true, example = "02-1234-5678")
    private String phone;

    @NotBlank(message = "주소는 필수입니다")
    @Size(max = 200, message = "주소는 200자를 초과할 수 없습니다")
    @Schema(description = "가게 주소", required = true, example = "서울시 강남구 테헤란로 123")
    private String address;

    @Size(max = 100, message = "상세주소는 100자를 초과할 수 없습니다")
    @Schema(description = "가게 상세주소", example = "1층 101호")
    private String detailAddress;

    @NotNull(message = "위도는 필수입니다")
    @DecimalMin(value = "33.0", message = "올바른 위도 범위가 아닙니다")
    @DecimalMax(value = "43.0", message = "올바른 위도 범위가 아닙니다")
    @Schema(description = "위도", required = true, example = "37.5665")
    private BigDecimal latitude;

    @NotNull(message = "경도는 필수입니다")
    @DecimalMin(value = "124.0", message = "올바른 경도 범위가 아닙니다")
    @DecimalMax(value = "132.0", message = "올바른 경도 범위가 아닙니다")
    @Schema(description = "경도", required = true, example = "126.9780")
    private BigDecimal longitude;

    @NotNull(message = "가게 카테고리는 필수입니다")
    @Schema(description = "가게 카테고리",
            required = true,
            allowableValues = {"KOREAN", "CHINESE", "WESTERN", "JAPANESE", "FAST_FOOD", "CHICKEN", "PIZZA", "DESSERT"},
            example = "KOREAN")
    private String category; // Enum을 String으로 변경

    @NotNull(message = "최소 주문금액은 필수입니다")
    @DecimalMin(value = "0.0", message = "최소 주문금액은 0원 이상이어야 합니다")
    @DecimalMax(value = "50000.0", message = "최소 주문금액은 50,000원을 초과할 수 없습니다")
    @Schema(description = "최소 주문금액", required = true, example = "15000.00")
    private BigDecimal minOrderAmount;

    @NotNull(message = "배달비는 필수입니다")
    @DecimalMin(value = "0.0", message = "배달비는 0원 이상이어야 합니다")
    @DecimalMax(value = "10000.0", message = "배달비는 10,000원을 초과할 수 없습니다")
    @Schema(description = "배달비", required = true, example = "2000.00")
    private BigDecimal deliveryFee;

    @NotNull(message = "최소 배달시간은 필수입니다")
    @Min(value = 10, message = "최소 배달시간은 10분 이상이어야 합니다")
    @Max(value = 120, message = "최소 배달시간은 120분을 초과할 수 없습니다")
    @Schema(description = "최소 배달시간(분)", required = true, example = "30")
    private Integer deliveryTimeMin;

    @NotNull(message = "최대 배달시간은 필수입니다")
    @Min(value = 15, message = "최대 배달시간은 15분 이상이어야 합니다")
    @Max(value = 180, message = "최대 배달시간은 180분을 초과할 수 없습니다")
    @Schema(description = "최대 배달시간(분)", required = true, example = "45")
    private Integer deliveryTimeMax;

    @Size(max = 500, message = "이미지 URL은 500자를 초과할 수 없습니다")
    @Schema(description = "가게 이미지 URL", example = "https://example.com/images/store.jpg")
    private String imageUrl;
}