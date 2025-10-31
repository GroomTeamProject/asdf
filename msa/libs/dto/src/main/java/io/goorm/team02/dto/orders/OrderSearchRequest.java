package io.goorm.team02.dto.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "주문 검색 요청 DTO")
public class OrderSearchRequest {

    @Schema(description = "가게 ID", example = "1")
    private Long storeId;
    
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    
    @Schema(description = "주문 상태", example = "PENDING")
    private String status;
    
    // 페이지네이션 파라미터
    @Schema(description = "페이지 번호 (0부터 시작)", example = "0", minimum = "0", maximum = "10000")
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    @Max(value = 10000, message = "페이지 번호는 10000 이하여야 합니다")
    private int page = 0;
    
    @Schema(description = "페이지 크기", example = "20", minimum = "1", maximum = "100")
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
    private int size = 20;

    // 검증 메서드
    public boolean hasStoreId() {
        return storeId != null;
    }

    public boolean hasUserId() {
        return userId != null;
    }

    public boolean hasStatus() {
        return status != null;
    }

    // 페이지네이션 관련 메서드
    public boolean hasPagination() {
        return true; // 기본값이 설정되어 있으므로 항상 true
    }

    public int getPageOrDefault() {
        return page;
    }

    public int getSizeOrDefault() {
        return size;
    }

}
