package io.goorm.team02.dto.owner.menus.menucreate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "메뉴 옵션 그룹 수정 요청")
public class MenuOptionGroupUpdateRequest {

    @Size(max = 50, message = "옵션 그룹명은 50자를 초과할 수 없습니다")
    @Schema(description = "옵션 그룹명", example = "사이즈 선택")
    private String name;

    @Schema(description = "옵션 타입",
            allowableValues = {"SINGLE", "MULTIPLE"},
            example = "SINGLE")
    private String type; // Enum을 String으로 변경

    @Schema(description = "필수 옵션 여부", example = "false")
    private Boolean isRequired;

    @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
    @Schema(description = "옵션 그룹 표시 순서", example = "2")
    private Integer displayOrder;
}