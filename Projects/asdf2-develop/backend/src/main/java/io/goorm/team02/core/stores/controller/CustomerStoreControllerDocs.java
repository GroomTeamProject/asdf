package io.goorm.team02.core.stores.controller;

import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 고객용 가게 조회 API 문서화 인터페이스
 */
@Tag(name = "Customer Store", description = "고객용 가게 조회 API")
public interface CustomerStoreControllerDocs {

    /**
     * 가게 목록 조회
     */
    @Operation(summary = "가게 목록 조회", description = "모든 가게 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<List<StoreResponse>> getAllStores();

    /**
     * 가게 상세 조회
     */
    @Operation(summary = "가게 상세 조회", description = "특정 가게의 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 조회 성공"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<StoreResponse> getStore(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Long storeId);
}
