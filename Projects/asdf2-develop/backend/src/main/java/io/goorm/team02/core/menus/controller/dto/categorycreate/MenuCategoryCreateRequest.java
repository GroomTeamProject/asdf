package io.goorm.team02.core.menus.controller.dto.categorycreate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "메뉴 카테고리 생성 요청")
public class MenuCategoryCreateRequest {

    @NotBlank(message = "카테고리명은 필수입니다")
    @Size(max = 50, message = "카테고리명은 50자를 초과할 수 없습니다")
    @Schema(description = "카테고리명", required = true, example = "메인메뉴")
    private String name;

    @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
    @Schema(description = "카테고리 표시 순서", example = "1")
    private Integer displayOrder = 0;

    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive = true;
}
