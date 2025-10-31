// MenuOptionItemUpdateRequest.java
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
@Schema(description = "메뉴 옵션 아이템 수정 요청")
public class MenuOptionItemUpdateRequest {

    @Size(max = 50, message = "옵션 아이템명은 50자를 초과할 수 없습니다")
    @Schema(description = "옵션 아이템명", example = "콜라 (대)")
    private String name;

    @DecimalMin(value = "0.0", message = "추가 금액은 0원 이상이어야 합니다")
    @DecimalMax(value = "100000.0", message = "추가 금액은 10만원을 초과할 수 없습니다")
    @Schema(description = "추가 금액", example = "1500.00")
    private BigDecimal additionalPrice;

    @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
    @Schema(description = "옵션 아이템 표시 순서", example = "2")
    private Integer displayOrder;

    @Schema(description = "활성화 여부", example = "false")
    private Boolean isActive;
}