package io.goorm.team02.dto.owner.menus.menucreate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "메뉴 상세 응답")
public class MenuDetailResponse {

    @Schema(description = "메뉴 ID", example = "1")
    private Long id;

    @Schema(description = "가게 ID", example = "1")
    private Long storeId;

    @Schema(description = "가게명", example = "맛있는 한식당")
    private String storeName;

    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;

    @Schema(description = "카테고리명", example = "메인메뉴")
    private String categoryName;

    @Schema(description = "메뉴명", example = "김치찌개")
    private String name;

    @Schema(description = "메뉴 설명", example = "집에서 직접 담근 김치로 끓인 얼큰한 김치찌개")
    private String description;

    @Schema(description = "메뉴 가격", example = "8000.00")
    private BigDecimal price;

    @Schema(description = "메뉴 이미지 URL", example = "https://example.com/images/kimchi-jjigae.jpg")
    private String imageUrl;

    @Schema(description = "인기 메뉴 여부", example = "false")
    private Boolean isPopular;

    @Schema(description = "추천 메뉴 여부", example = "true")
    private Boolean isRecommended;

    @Schema(description = "메뉴 판매 상태",
            allowableValues = {"AVAILABLE", "SOLD_OUT", "HIDDEN"},
            example = "AVAILABLE")
    private String status; // Enum을 String으로 변경

    @Schema(description = "메뉴 표시 순서", example = "1")
    private Integer displayOrder;

    @Schema(description = "총 옵션 그룹 개수", example = "2")
    private Integer totalOptionGroups;

    @Schema(description = "필수 옵션 그룹 개수", example = "1")
    private Integer requiredOptionGroups;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "메뉴 옵션 그룹 목록")
    private List<MenuOptionResponse> options;

    @Schema(description = "카테고리 정보")
    private CategoryInfo categoryInfo;

    @Getter
    @Builder
    @Schema(description = "카테고리 정보")
    public static class CategoryInfo {
        @Schema(description = "카테고리 ID", example = "1")
        private Long id;

        @Schema(description = "카테고리명", example = "메인메뉴")
        private String name;

        @Schema(description = "카테고리 활성 상태", example = "true")
        private Boolean isActive;

        @Schema(description = "카테고리 표시 순서", example = "1")
        private Integer displayOrder;
    }

    // from 메서드 제거 - Entity 의존성 완전 제거!
}