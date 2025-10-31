// CategoryMoveRequest.java
package io.goorm.team02.dto.owner.menus.categorycreate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "카테고리 위치 이동 요청")
public class CategoryMoveRequest {

    @NotNull(message = "이동할 카테고리 ID는 필수입니다")
    @Schema(description = "이동할 카테고리 ID", required = true, example = "1")
    private Long categoryId;

    @NotNull(message = "새로운 위치는 필수입니다")
    @Schema(description = "새로운 위치 (1부터 시작)", required = true, example = "3")
    private Integer newPosition;
}