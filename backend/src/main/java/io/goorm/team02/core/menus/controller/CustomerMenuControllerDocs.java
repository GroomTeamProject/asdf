package io.goorm.team02.core.menus.controller;

import io.goorm.team02.core.menus.controller.dto.categorycreate.MenuCategoryResponse;
import io.goorm.team02.core.menus.controller.dto.menucreate.MenuDetailResponse;
import io.goorm.team02.core.menus.controller.dto.menucreate.MenuResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 고객용 메뉴 조회 API 문서화 인터페이스
 */
@Tag(name = "Customer Menu", description = "고객용 메뉴 조회 API")
public interface CustomerMenuControllerDocs {

    /**
     * 가게의 메뉴 카테고리 목록 조회
     */
    @Operation(summary = "메뉴 카테고리 목록 조회", description = "특정 가게의 메뉴 카테고리 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<List<MenuCategoryResponse>> getMenuCategories(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Long storeId);

    /**
     * 가게의 메뉴 목록 조회
     */
    @Operation(summary = "메뉴 목록 조회", description = "특정 가게의 메뉴 목록을 조회합니다 (카테고리별 필터링 가능)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "가게 또는 카테고리를 찾을 수 없음")
    })
    ResponseEntity<List<MenuResponse>> getMenus(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Long storeId,
            @Parameter(description = "카테고리 ID (선택사항)", example = "1")
            @RequestParam(required = false) Long categoryId);

    /**
     * 메뉴 상세 조회
     */
    @Operation(summary = "메뉴 상세 조회", description = "특정 메뉴의 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 조회 성공"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<MenuDetailResponse> getMenu(
            @Parameter(description = "가게 ID", required = true, example = "1")
            @PathVariable Long storeId,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId);
}
