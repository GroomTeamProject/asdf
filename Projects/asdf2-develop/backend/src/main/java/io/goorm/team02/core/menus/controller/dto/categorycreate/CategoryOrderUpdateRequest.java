// CategoryOrderUpdateRequest.java
package io.goorm.team02.core.menus.controller.dto.categorycreate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "카테고리 순서 변경 요청")
public class CategoryOrderUpdateRequest {

    @NotEmpty(message = "카테고리 순서 목록은 필수입니다")
    @Schema(description = "카테고리 ID와 순서 목록", required = true)
    private List<CategoryOrderItem> categoryOrders;

    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "카테고리 순서 항목")
    public static class CategoryOrderItem {

        @NotNull(message = "카테고리 ID는 필수입니다")
        @Schema(description = "카테고리 ID", required = true, example = "1")
        private Long categoryId;

        @NotNull(message = "표시 순서는 필수입니다")
        @Schema(description = "새로운 표시 순서", required = true, example = "1")
        private Integer displayOrder;
    }
}