package io.goorm.team02.dto.owner.stores.storemanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "가게 정보 수정 요청")
public class StoreUpdateRequest {

    @Size(max = 100, message = "가게명은 100자를 초과할 수 없습니다")
    @Schema(description = "가게명", example = "맛있는 한식당")
    private String name;

    @Size(max = 500, message = "가게 설명은 500자를 초과할 수 없습니다")
    @Schema(description = "가게 설명", example = "정성스럽게 만든 전통 한식을 제공합니다")
    private String description;

    @Schema(description = "가게 카테고리",
            allowableValues = {"KOREAN", "CHINESE", "WESTERN", "JAPANESE", "FAST_FOOD", "CHICKEN", "PIZZA", "DESSERT"},
            example = "KOREAN")
    private String category; // Enum을 String으로 변경

    @Size(max = 500, message = "이미지 URL은 500자를 초과할 수 없습니다")
    @Schema(description = "가게 이미지 URL", example = "https://example.com/images/store.jpg")
    private String imageUrl;
}