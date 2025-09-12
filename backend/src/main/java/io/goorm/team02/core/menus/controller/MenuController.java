package io.goorm.team02.core.menus.controller;

import io.goorm.team02.core.menus.controller.dto.categorycreate.*;
import io.goorm.team02.core.menus.controller.dto.menucreate.*;
import io.goorm.team02.core.menus.domain.Menu;
import io.goorm.team02.core.menus.domain.MenuCategory;
import io.goorm.team02.core.menus.domain.MenuOption;
import io.goorm.team02.core.menus.domain.MenuOptionItem;
import io.goorm.team02.core.menus.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner/menus")
@RequiredArgsConstructor
@Tag(name = "Menu Management", description = "메뉴 관리 API")
@SecurityRequirement(name = "JWT Token") // 전체 컨트롤러에 JWT 토큰 요구
public class MenuController {

    private final MenuService menuService;

    // ================================
    // 3.1 메뉴 카테고리 관리
    // ================================

    /**
     * 메뉴 카테고리 목록 조회
     */
    @GetMapping("/categories")
    @Operation(summary = "메뉴 카테고리 목록 조회", description = "가게의 메뉴 카테고리를 조회합니다")
    @Tag(name = "Menu Category Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    public ResponseEntity<List<MenuCategoryResponse>> getMenuCategories() {
        List<MenuCategory> categories = menuService.getMenuCategories();
        List<MenuCategoryResponse> response = categories.stream()
                .map(MenuCategoryResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * 메뉴 카테고리 등록
     */
    @PostMapping("/categories")
    @Operation(summary = "메뉴 카테고리 등록", description = "새로운 메뉴 카테고리를 등록합니다")
    @Tag(name = "Menu Category Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 카테고리명")
    })
    public ResponseEntity<MenuCategoryResponse> createCategory(
            @Parameter(description = "카테고리 생성 요청 정보", required = true)
            @Valid @RequestBody MenuCategoryCreateRequest request) {
        MenuCategory category = menuService.createCategory(request);
        return ResponseEntity.ok(MenuCategoryResponse.from(category));
    }

    /**
     * 메뉴 카테고리 수정
     */
    @PutMapping("/categories/{categoryId}")
    @Operation(summary = "메뉴 카테고리 수정", description = "메뉴 카테고리 정보를 수정합니다")
    @Tag(name = "Menu Category Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    public ResponseEntity<MenuCategoryResponse> updateCategory(
            @Parameter(description = "카테고리 ID", required = true, example = "1")
            @PathVariable Long categoryId,
            @Parameter(description = "카테고리 수정 요청 정보", required = true)
            @Valid @RequestBody MenuCategoryUpdateRequest request) {
        MenuCategory category = menuService.updateCategory(categoryId, request);
        return ResponseEntity.ok(MenuCategoryResponse.from(category));
    }

    /**
     * 메뉴 카테고리 삭제
     */
    @DeleteMapping("/categories/{categoryId}")
    @Operation(summary = "메뉴 카테고리 삭제", description = "메뉴 카테고리를 삭제합니다")
    @Tag(name = "Menu Category Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "카테고리에 메뉴가 존재함"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "삭제할 카테고리 ID", required = true, example = "1")
            @PathVariable Long categoryId) {
        menuService.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }

    /**
     * 카테고리 순서 변경
     */
    @PutMapping("/categories/order")
    @Operation(summary = "카테고리 순서 변경", description = "메뉴 카테고리를 다른 위치로 이동합니다 (드래그 앤 드롭)")
    @Tag(name = "Menu Category Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "순서 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<MenuCategoryResponse>> updateCategoryOrder(
            @Parameter(description = "카테고리 이동 요청", required = true)
            @Valid @RequestBody CategoryMoveRequest request) {
        List<MenuCategory> categories = menuService.updateCategoryOrder(request);
        List<MenuCategoryResponse> response = categories.stream()
                .map(MenuCategoryResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    // ================================
    // 3.2 메뉴 관리
    // ================================

    /**
     * 메뉴 목록 조회 (카테고리별)
     */
    @GetMapping
    @Operation(summary = "메뉴 목록 조회", description = "가게의 모든 메뉴 또는 특정 카테고리의 메뉴를 조회합니다")
    @Tag(name = "Menu Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    public ResponseEntity<List<MenuResponse>> getMenus(
            @Parameter(description = "카테고리 ID (선택사항)", example = "1")
            @RequestParam(required = false) Long categoryId) {
        List<Menu> menus = menuService.getMenus(categoryId);
        List<MenuResponse> response = menus.stream()
                .map(MenuResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * 메뉴 상세 조회
     */
    @GetMapping("/{menuId}")
    @Operation(summary = "메뉴 상세 조회", description = "특정 메뉴의 상세 정보를 조회합니다")
    @Tag(name = "Menu Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    public ResponseEntity<MenuDetailResponse> getMenu(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId) {
        Menu menu = menuService.getMenu(menuId);
        return ResponseEntity.ok(MenuDetailResponse.from(menu));
    }

    /**
     * 메뉴 등록
     */
    @PostMapping
    @Operation(summary = "메뉴 등록", description = "새로운 메뉴를 등록합니다")
    @Tag(name = "Menu Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    public ResponseEntity<MenuResponse> createMenu(
            @Parameter(description = "메뉴 생성 요청 정보", required = true)
            @RequestBody MenuCreateRequest request) {
        Menu menu = menuService.createMenu(request);
        return ResponseEntity.ok(MenuResponse.from(menu));
    }

    /**
     * 메뉴 수정
     */
    @PutMapping("/{menuId}")
    @Operation(summary = "메뉴 수정", description = "메뉴 정보를 수정합니다")
    @Tag(name = "Menu Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    public ResponseEntity<MenuResponse> updateMenu(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "메뉴 수정 요청 정보", required = true)
            @RequestBody MenuUpdateRequest request) {
        Menu menu = menuService.updateMenu(menuId, request);
        return ResponseEntity.ok(MenuResponse.from(menu));
    }

    /**
     * 메뉴 삭제
     */
    @DeleteMapping("/{menuId}")
    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다")
    @Tag(name = "Menu Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메뉴 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteMenu(
            @Parameter(description = "삭제할 메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.ok().build();
    }

    /**
     * 메뉴 판매 상태 변경
     */
    @PutMapping("/{menuId}/status")
    @Operation(summary = "메뉴 판매 상태 변경", description = "메뉴의 판매 상태를 변경합니다 (판매중/품절/숨김)")
    @Tag(name = "Menu Status Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 상태 값"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    public ResponseEntity<MenuResponse> updateMenuStatus(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "메뉴 상태 변경 요청", required = true)
            @RequestBody MenuStatusRequest request) {
        Menu menu = menuService.updateMenuStatus(menuId, request);
        return ResponseEntity.ok(MenuResponse.from(menu));
    }

    /**
     * 메뉴 순서 변경
     */
    @PutMapping("/order")
    @Operation(summary = "메뉴 순서 변경", description = "메뉴의 표시 순서를 변경합니다")
    @Tag(name = "Menu Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "순서 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<MenuResponse>> updateMenuOrder(
            @Parameter(description = "메뉴 순서 변경 요청", required = true)
            @RequestBody MenuOrderUpdateRequest request) {
        List<Menu> menus = menuService.updateMenuOrder(request);
        List<MenuResponse> response = menus.stream()
                .map(MenuResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    // ================================
    // 3.3 메뉴 이미지 관리
    // ================================

    /**
     * 메뉴 이미지 업로드
     */
    @PostMapping(value = "/{menuId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "메뉴 이미지 업로드", description = "메뉴 이미지를 업로드합니다")
    @Tag(name = "Menu Image Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "파일이 비어있거나 잘못된 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "413", description = "파일 크기가 너무 큼"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    public ResponseEntity<String> uploadMenuImage(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어있습니다");
        }

        String imageUrl = menuService.uploadMenuImage(menuId, file);
        return ResponseEntity.ok(imageUrl);
    }

    /**
     * 메뉴 이미지 정보 조회
     */
    @GetMapping("/{menuId}/images/info")
    @Operation(summary = "메뉴 이미지 정보 조회", description = "메뉴의 이미지 정보를 조회합니다")
    @Tag(name = "Menu Image Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    public ResponseEntity<Map<String, Object>> getMenuImageInfo(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId) {
        Map<String, Object> imageInfo = menuService.getMenuImageInfo(menuId);
        return ResponseEntity.ok(imageInfo);
    }

    /**
     * 메뉴 이미지 삭제
     */
    @DeleteMapping("/{menuId}/images/{imageId}")
    @Operation(summary = "메뉴 이미지 삭제", description = "업로드된 메뉴 이미지를 삭제합니다")
    @Tag(name = "Menu Image Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "삭제 권한이 없음"),
            @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteMenuImage(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "삭제할 이미지 ID", required = true, example = "1")
            @PathVariable Long imageId) {
        menuService.deleteMenuImage(menuId, imageId);
        return ResponseEntity.ok().build();
    }

    // ================================
    // 3.4 메뉴 옵션 그룹 관리
    // ================================

    /**
     * 메뉴 옵션 그룹 목록 조회
     */
    @GetMapping("/{menuId}/option-groups")
    @Operation(summary = "메뉴 옵션 그룹 목록 조회", description = "특정 메뉴의 옵션 그룹 목록을 조회합니다")
    @Tag(name = "Menu Option Group Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 그룹 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    public ResponseEntity<List<MenuOptionGroupResponse>> getMenuOptionGroups(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId) {
        List<MenuOption> optionGroups = menuService.getMenuOptionGroups(menuId);
        List<MenuOptionGroupResponse> response = optionGroups.stream()
                .map(MenuOptionGroupResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * 메뉴 옵션 그룹 등록
     */
    @PostMapping("/{menuId}/option-groups")
    @Operation(summary = "메뉴 옵션 그룹 등록", description = "메뉴에 새로운 옵션 그룹을 등록합니다")
    @Tag(name = "Menu Option Group Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 그룹 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    public ResponseEntity<MenuOptionGroupResponse> createOptionGroup(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 생성 요청 정보", required = true)
            @Valid @RequestBody MenuOptionGroupCreateRequest request) {
        MenuOption optionGroup = menuService.createOptionGroup(menuId, request);
        return ResponseEntity.ok(MenuOptionGroupResponse.from(optionGroup));
    }

    /**
     * 메뉴 옵션 그룹 수정
     */
    @PutMapping("/{menuId}/option-groups/{groupId}")
    @Operation(summary = "메뉴 옵션 그룹 수정", description = "메뉴 옵션 그룹 정보를 수정합니다")
    @Tag(name = "Menu Option Group Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 그룹 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션 그룹을 찾을 수 없음")
    })
    public ResponseEntity<MenuOptionGroupResponse> updateOptionGroup(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId,
            @Parameter(description = "옵션 그룹 수정 요청 정보", required = true)
            @Valid @RequestBody MenuOptionGroupUpdateRequest request) {
        MenuOption optionGroup = menuService.updateOptionGroup(menuId, groupId, request);
        return ResponseEntity.ok(MenuOptionGroupResponse.from(optionGroup));
    }

    /**
     * 메뉴 옵션 그룹 삭제
     */
    @DeleteMapping("/{menuId}/option-groups/{groupId}")
    @Operation(summary = "메뉴 옵션 그룹 삭제", description = "메뉴 옵션 그룹을 삭제합니다")
    @Tag(name = "Menu Option Group Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 그룹 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션 그룹을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteOptionGroup(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "삭제할 옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId) {
        menuService.deleteOptionGroup(menuId, groupId);
        return ResponseEntity.ok().build();
    }

    /**
     * 옵션 그룹 순서 변경
     */
    @PutMapping("/{menuId}/option-groups/{groupId}/order")
    @Operation(summary = "옵션 그룹 순서 변경", description = "옵션 그룹의 표시 순서를 변경합니다")
    public ResponseEntity<MenuOptionGroupResponse> updateOptionGroupOrder(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId,
            @Parameter(description = "옵션 그룹 순서 변경 요청", required = true)
            @Valid @RequestBody OptionGroupOrderUpdateRequest request) {

        // 요청 데이터 검증
        if (!request.getMenuId().equals(menuId)) {
            throw new IllegalArgumentException("요청의 메뉴 ID와 경로의 메뉴 ID가 일치하지 않습니다");
        }
        if (!request.getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("요청의 그룹 ID와 경로의 그룹 ID가 일치하지 않습니다");
        }

        MenuOption optionGroup = menuService.updateOptionGroupOrder(menuId, groupId, request);
        return ResponseEntity.ok(MenuOptionGroupResponse.from(optionGroup));
    }

    /**
     * 옵션 아이템 순서 변경
     */
    @PutMapping("/{menuId}/option-groups/{groupId}/options/{optionId}/order")
    @Operation(summary = "옵션 아이템 순서 변경", description = "옵션 아이템의 표시 순서를 변경합니다")
    public ResponseEntity<MenuOptionItemResponse> updateOptionItemOrder(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId,
            @Parameter(description = "옵션 아이템 ID", required = true, example = "1")
            @PathVariable Long optionId,
            @Parameter(description = "옵션 아이템 순서 변경 요청", required = true)
            @Valid @RequestBody OptionItemOrderUpdateRequest request) {

        // 요청 데이터 검증
        if (!request.getMenuId().equals(menuId)) {
            throw new IllegalArgumentException("요청의 메뉴 ID와 경로의 메뉴 ID가 일치하지 않습니다");
        }
        if (!request.getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("요청의 그룹 ID와 경로의 그룹 ID가 일치하지 않습니다");
        }
        if (!request.getOptionId().equals(optionId)) {
            throw new IllegalArgumentException("요청의 옵션 ID와 경로의 옵션 ID가 일치하지 않습니다");
        }

        MenuOptionItem optionItem = menuService.updateOptionItemOrder(menuId, groupId, optionId, request);
        return ResponseEntity.ok(MenuOptionItemResponse.from(optionItem));
    }

    /**
     * 옵션 그룹 순서 정규화 (유틸리티)
     */
    @PutMapping("/{menuId}/option-groups/normalize-order")
    @Operation(summary = "옵션 그룹 순서 정규화", description = "옵션 그룹의 표시 순서를 1부터 연속으로 정규화합니다")
    @Tag(name = "Menu Option Group Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "순서 정규화 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    public ResponseEntity<List<MenuOptionGroupResponse>> normalizeOptionGroupOrders(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId) {
        menuService.normalizeOptionGroupOrdersAfterDeletion(menuId);

        List<MenuOption> optionGroups = menuService.getMenuOptionGroups(menuId);
        List<MenuOptionGroupResponse> response = optionGroups.stream()
                .map(MenuOptionGroupResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    // ================================
    // 3.5 메뉴 옵션 관리
    // ================================

    /**
     * 메뉴 옵션 목록 조회
     */
    @GetMapping("/{menuId}/option-groups/{groupId}/options")
    @Operation(summary = "메뉴 옵션 목록 조회", description = "특정 옵션 그룹의 옵션 아이템 목록을 조회합니다")
    @Tag(name = "Menu Option Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션 그룹을 찾을 수 없음")
    })
    public ResponseEntity<List<MenuOptionItemResponse>> getMenuOptions(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId) {
        List<MenuOptionItem> options = menuService.getMenuOptions(menuId, groupId);
        List<MenuOptionItemResponse> response = options.stream()
                .map(MenuOptionItemResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * 메뉴 옵션 등록
     */
    @PostMapping("/{menuId}/option-groups/{groupId}/options")
    @Operation(summary = "메뉴 옵션 등록", description = "옵션 그룹에 새로운 옵션 아이템을 등록합니다")
    @Tag(name = "Menu Option Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션 그룹을 찾을 수 없음")
    })
    public ResponseEntity<MenuOptionItemResponse> createOption(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId,
            @Parameter(description = "옵션 아이템 생성 요청 정보", required = true)
            @Valid @RequestBody MenuOptionItemCreateRequest request) {
        MenuOptionItem optionItem = menuService.createOption(menuId, groupId, request);
        return ResponseEntity.ok(MenuOptionItemResponse.from(optionItem));
    }

    /**
     * 메뉴 옵션 수정
     */
    @PutMapping("/{menuId}/option-groups/{groupId}/options/{optionId}")
    @Operation(summary = "메뉴 옵션 수정", description = "메뉴 옵션 아이템 정보를 수정합니다")
    @Tag(name = "Menu Option Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션을 찾을 수 없음")
    })
    public ResponseEntity<MenuOptionItemResponse> updateOption(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId,
            @Parameter(description = "옵션 아이템 ID", required = true, example = "1")
            @PathVariable Long optionId,
            @Parameter(description = "옵션 아이템 수정 요청 정보", required = true)
            @Valid @RequestBody MenuOptionItemUpdateRequest request) {
        MenuOptionItem optionItem = menuService.updateOption(menuId, groupId, optionId, request);
        return ResponseEntity.ok(MenuOptionItemResponse.from(optionItem));
    }

    /**
     * 메뉴 옵션 삭제
     */
    @DeleteMapping("/{menuId}/option-groups/{groupId}/options/{optionId}")
    @Operation(summary = "메뉴 옵션 삭제", description = "메뉴 옵션을 삭제합니다")
    @Tag(name = "Menu Option Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteOption(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId,
            @Parameter(description = "삭제할 옵션 ID", required = true, example = "1")
            @PathVariable Long optionId) {
        menuService.deleteOption(menuId, groupId, optionId);
        return ResponseEntity.ok().build();
    }

    /**
     * 옵션 아이템 순서 정규화 (유틸리티)
     */
    @PutMapping("/{menuId}/option-groups/{groupId}/options/normalize-order")
    @Operation(summary = "옵션 아이템 순서 정규화", description = "옵션 아이템의 표시 순서를 1부터 연속으로 정규화합니다")
    @Tag(name = "Menu Option Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "순서 정규화 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션 그룹을 찾을 수 없음")
    })
    public ResponseEntity<List<MenuOptionItemResponse>> normalizeOptionItemOrders(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId) {
        menuService.normalizeOptionItemOrdersAfterDeletion(groupId);

        List<MenuOptionItem> optionItems = menuService.getMenuOptions(menuId, groupId);
        List<MenuOptionItemResponse> response = optionItems.stream()
                .map(MenuOptionItemResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 메뉴 옵션 상태 변경
     */
    @PutMapping("/{menuId}/option-groups/{groupId}/options/{optionId}/status")
    @Operation(summary = "메뉴 옵션 상태 변경", description = "메뉴 옵션 아이템의 활성화 상태를 변경합니다")
    @Tag(name = "Menu Option Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "옵션 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 상태 값"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션을 찾을 수 없음")
    })
    public ResponseEntity<MenuOptionItemResponse> updateOptionStatus(
            @Parameter(description = "메뉴 ID", required = true, example = "1")
            @PathVariable Long menuId,
            @Parameter(description = "옵션 그룹 ID", required = true, example = "1")
            @PathVariable Long groupId,
            @Parameter(description = "옵션 아이템 ID", required = true, example = "1")
            @PathVariable Long optionId,
            @Parameter(description = "옵션 아이템 상태 변경 요청", required = true)
            @Valid @RequestBody MenuOptionItemStatusRequest request) {
        MenuOptionItem optionItem = menuService.updateOptionStatus(menuId, groupId, optionId, request);
        return ResponseEntity.ok(MenuOptionItemResponse.from(optionItem));
    }
}