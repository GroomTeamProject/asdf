package io.goorm.team02.dto.owner.menus.menucreate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "메뉴 수정 요청")
public class MenuUpdateRequest {

    @Size(max = 100, message = "메뉴명은 100자를 초과할 수 없습니다")
    @Schema(description = "메뉴명", example = "김치찌개")
    private String name;

    @Size(max = 1000, message = "메뉴 설명은 1000자를 초과할 수 없습니다")
    @Schema(description = "메뉴 설명", example = "집에서 직접 담근 김치로 끓인 얼큰한 김치찌개")
    private String description;

    @DecimalMin(value = "0.0", message = "가격은 0원 이상이어야 합니다")
    @DecimalMax(value = "1000000.0", message = "가격은 100만원을 초과할 수 없습니다")
    @Schema(description = "메뉴 가격", example = "8500.00")
    private BigDecimal price;

    @Schema(description = "메뉴 카테고리 ID", example = "2")
    private Long categoryId;

    @Size(max = 500, message = "이미지 URL은 500자를 초과할 수 없습니다")
    @Schema(description = "메뉴 이미지 URL", example = "https://example.com/images/new-kimchi-jjigae.jpg")
    private String imageUrl;

    @Schema(description = "인기 메뉴 여부", example = "true")
    private Boolean isPopular;

    @Schema(description = "추천 메뉴 여부", example = "false")
    private Boolean isRecommended;

    // Enum을 String으로 변경
    @Schema(description = "메뉴 판매 상태",
            allowableValues = {"AVAILABLE", "SOLD_OUT", "HIDDEN"},
            example = "AVAILABLE")
    private String status;

    @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
    @Schema(description = "메뉴 표시 순서", example = "2")
    private Integer displayOrder;

    @Schema(description = "이미지 삭제 여부 (true: 기존 이미지 삭제)", example = "false")
    private Boolean removeImage = false;
}