package io.goorm.team02.core.menus.controller;

import io.goorm.team02.core.auth.annotation.CurrentUser;
import io.goorm.team02.core.menus.controller.dto.categorycreate.*;
import io.goorm.team02.core.menus.controller.dto.menucreate.*;
import io.goorm.team02.core.stores.domain.TempUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "Menu Management", description = "메뉴 관리 API")
@SecurityRequirement(name = "JWT Token")
public interface MenuControllerDocs {

    // ================================
    // 카테고리 관리 문서화
    // ================================

    @Operation(summary = "메뉴 카테고리 목록 조회", description = "가게의 메뉴 카테고리를 조회합니다")
    @Tag(name = "Menu Category Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<List<MenuCategoryResponse>> getMenuCategories(@CurrentUser TempUser currentUser);

    @Operation(summary = "메뉴 카테고리 등록", description = "새로운 메뉴 카테고리를 등록합니다")
    @Tag(name = "Menu Category Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 카테고리명")
    })
    ResponseEntity<MenuCategoryResponse> createCategory(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "카테고리 생성 요청 정보", required = true)
            @Valid @RequestBody MenuCategoryCreateRequest request);

    @Operation(summary = "메뉴 카테고리 수정", description = "메뉴 카테고리 정보를 수정합니다")
    @Tag(name = "Menu Category Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    ResponseEntity<MenuCategoryResponse> updateCategory(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "카테고리 ID", required = true, example = "1")
            @PathVariable Long categoryId,
            @Parameter(description = "카테고리 수정 요청 정보", required = true)
            @Valid @RequestBody MenuCategoryUpdateRequest request);

    @Operation(summary = "메뉴 카테고리 삭제", description = "메뉴 카테고리를 삭제합니다")
    @Tag(name = "Menu Category Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "카테고리에 메뉴가 존재함"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    ResponseEntity<Void> deleteCategory(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "삭제할 카테고리 ID", required = true, example = "1")
            @PathVariable Long categoryId);

    @Operation(summary = "카테고리 순서 변경", description = "메뉴 카테고리를 다른 위치로 이동합니다")
    @Tag(name = "Menu Category Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "순서 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<List<MenuCategoryResponse>> updateCategoryOrder(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "카테고리 이동 요청", required = true)
            @Valid @RequestBody CategoryMoveRequest request);

    // ================================
    // 메뉴 관리 문서화
    // ================================

    @Operation(summary = "메뉴 목록 조회", description = "가게의 모든 메뉴 또는 특정 카테고리의 메뉴를 조회합니다")
    @Tag(name = "Menu Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<List<MenuResponse>> getMenus(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "카테고리 ID (선택사항)", example = "1")
            @RequestParam(required = false) Long categoryId);

    @Operation(summary = "메뉴 상세 조회", description = "특정 메뉴의 상세 정보를 조회합니다")
    @Tag(name = "Menu Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<MenuDetailResponse> getMenu(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId);

    @Operation(summary = "메뉴 등록", description = "새로운 메뉴를 등록합니다")
    @Tag(name = "Menu Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    ResponseEntity<MenuResponse> createMenu(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 생성 요청 정보", required = true)
            @Valid @RequestBody MenuCreateRequest request);

    @Operation(summary = "메뉴 수정", description = "메뉴 정보를 수정합니다")
    @Tag(name = "Menu Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<MenuResponse> updateMenu(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "메뉴 수정 요청 정보", required = true)
            @Valid @RequestBody MenuUpdateRequest request);

    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다")
    @Tag(name = "Menu Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<Void> deleteMenu(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "삭제할 메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId);

    @Operation(summary = "메뉴 판매 상태 변경", description = "메뉴의 판매 상태를 변경합니다")
    @Tag(name = "Menu Status Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 상태 값"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<MenuResponse> updateMenuStatus(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "메뉴 상태 변경 요청", required = true)
            @Valid @RequestBody MenuStatusRequest request);

    @Operation(summary = "메뉴 순서 변경", description = "메뉴의 표시 순서를 변경합니다")
    @Tag(name = "Menu Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "순서 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<List<MenuResponse>> updateMenuOrder(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 순서 변경 요청", required = true)
            @Valid @RequestBody MenuOrderUpdateRequest request);

    // ================================
    // 이미지 관리 문서화
    // ================================

    @Operation(summary = "메뉴 이미지 업로드", description = "메뉴 이미지를 업로드합니다")
    @Tag(name = "Menu Image Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "파일이 비어있거나 잘못된 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "413", description = "파일 크기가 너무 큼"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<String> uploadMenuImage(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file);

    @Operation(summary = "메뉴 이미지 정보 조회", description = "메뉴의 이미지 정보를 조회합니다")
    @Tag(name = "Menu Image Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<Map<String, Object>> getMenuImageInfo(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId);

    @Operation(summary = "메뉴 이미지 삭제", description = "업로드된 메뉴 이미지를 삭제합니다")
    @Tag(name = "Menu Image Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "삭제 권한이 없음"),
            @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    })
    ResponseEntity<Void> deleteMenuImage(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "삭제할 이미지 ID", required = true, example = "1")
            @PathVariable Long imageId);

    // ================================
    // 옵션 그룹 관리 문서화
    // ================================

    @Operation(summary = "메뉴 옵션 그룹 목록 조회", description = "특정 메뉴의 옵션 그룹 목록을 조회합니다")
    @Tag(name = "Menu Option Group Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 그룹 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<List<MenuOptionGroupResponse>> getMenuOptionGroups(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId);

    @Operation(summary = "메뉴 옵션 그룹 등록", description = "메뉴에 새로운 옵션 그룹을 등록합니다")
    @Tag(name = "Menu Option Group Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 그룹 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    ResponseEntity<MenuOptionGroupResponse> createOptionGroup(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 생성 요청 정보", required = true)
            @Valid @RequestBody MenuOptionGroupCreateRequest request);

    @Operation(summary = "메뉴 옵션 그룹 수정", description = "메뉴 옵션 그룹 정보를 수정합니다")
    @Tag(name = "Menu Option Group Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 그룹 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션 그룹을 찾을 수 없음")
    })
    ResponseEntity<MenuOptionGroupResponse> updateOptionGroup(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId,
            @Parameter(description = "옵션 그룹 수정 요청 정보", required = true)
            @Valid @RequestBody MenuOptionGroupUpdateRequest request);

    @Operation(summary = "메뉴 옵션 그룹 삭제", description = "메뉴 옵션 그룹을 삭제합니다")
    @Tag(name = "Menu Option Group Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 그룹 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션 그룹을 찾을 수 없음")
    })
    ResponseEntity<Void> deleteOptionGroup(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "삭제할 옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId);

    // ================================
    // 옵션 아이템 관리 문서화
    // ================================

    @Operation(summary = "메뉴 옵션 목록 조회", description = "특정 옵션 그룹의 옵션 아이템 목록을 조회합니다")
    @Tag(name = "Menu Option Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션 그룹을 찾을 수 없음")
    })
    ResponseEntity<List<MenuOptionItemResponse>> getMenuOptions(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId);

    @Operation(summary = "메뉴 옵션 등록", description = "옵션 그룹에 새로운 옵션 아이템을 등록합니다")
    @Tag(name = "Menu Option Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션 그룹을 찾을 수 없음")
    })
    ResponseEntity<MenuOptionItemResponse> createOption(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId,
            @Parameter(description = "옵션 아이템 생성 요청 정보", required = true)
            @Valid @RequestBody MenuOptionItemCreateRequest request);

    @Operation(summary = "메뉴 옵션 수정", description = "메뉴 옵션 아이템 정보를 수정합니다")
    @Tag(name = "Menu Option Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션을 찾을 수 없음")
    })
    ResponseEntity<MenuOptionItemResponse> updateOption(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId,
            @Parameter(description = "옵션 아이템 ID", required = true, example = "1")
            @PathVariable Long optionId,
            @Parameter(description = "옵션 아이템 수정 요청 정보", required = true)
            @Valid @RequestBody MenuOptionItemUpdateRequest request);

    @Operation(summary = "메뉴 옵션 삭제", description = "메뉴 옵션을 삭제합니다")
    @Tag(name = "Menu Option Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션을 찾을 수 없음")
    })
    ResponseEntity<Void> deleteOption(
            @CurrentUser TempUser currentUser,
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId,
            @Parameter(description = "삭제할 옵션 ID", required = true, example = "1")
            @PathVariable Long optionId);
}
