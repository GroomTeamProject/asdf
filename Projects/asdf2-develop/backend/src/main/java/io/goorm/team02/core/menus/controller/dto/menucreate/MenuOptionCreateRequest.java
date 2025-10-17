// MenuOptionCreateRequest.java
package io.goorm.team02.core.menus.controller.dto.menucreate;

import io.goorm.team02.core.menus.domain.enums.OptionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.Valid;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "메뉴 옵션 생성 요청")
public class MenuOptionCreateRequest {

    @NotBlank(message = "옵션명은 필수입니다")
    @Size(max = 50, message = "옵션명은 50자를 초과할 수 없습니다")
    @Schema(description = "옵션명", required = true, example = "사이즈")
    private String name;

    @NotNull(message = "옵션 타입은 필수입니다")
    @Schema(description = "옵션 타입", required = true, allowableValues = {"SINGLE", "MULTIPLE"}, example = "SINGLE")
    private OptionType type;

    @Schema(description = "필수 옵션 여부", example = "true")
    private Boolean isRequired = false;

    @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
    @Schema(description = "옵션 표시 순서", example = "1")
    private Integer displayOrder = 0;

    @Valid
    @NotEmpty(message = "옵션 아이템은 최소 1개 이상이어야 합니다")
    @Schema(description = "옵션 아이템 목록")
    private List<MenuOptionItemCreateRequest> items;
}