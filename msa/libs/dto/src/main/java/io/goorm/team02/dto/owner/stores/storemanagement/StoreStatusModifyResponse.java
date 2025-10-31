package io.goorm.team02.dto.owner.stores.storemanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가게 상태 변경 응답")
public class StoreStatusModifyResponse {

    @Schema(description = "가게 ID", example = "1")
    private Long storeId;

    @Schema(description = "가게명", example = "맛있는 한식당")
    private String storeName;

    @Schema(description = "변경된 가게 상태",
            allowableValues = {"OPEN", "CLOSED", "TEMPORARILY_CLOSED", "BREAK"},
            example = "OPEN")
    private String status; // Enum을 String으로 변경

    @Schema(description = "상태 변경 메시지", example = "가게 상태가 성공적으로 변경되었습니다")
    private String message;

    // of 메서드 제거 - Entity 의존성 완전 제거!
}