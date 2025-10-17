package io.goorm.team02.core.menus.controller;

import io.goorm.team02.core.menus.controller.dto.categorycreate.MenuCategoryResponse;
import io.goorm.team02.core.menus.controller.dto.menucreate.MenuDetailResponse;
import io.goorm.team02.core.menus.controller.dto.menucreate.MenuResponse;
import io.goorm.team02.core.menus.domain.Menu;
import io.goorm.team02.core.menus.domain.MenuCategory;
import io.goorm.team02.core.menus.service.CustomerMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 고객용 메뉴 조회 API
 * 주문 도메인 구현을 위해 임시로 작성
 */
@RestController
@RequestMapping("/api/stores/{storeId}/menus")
@RequiredArgsConstructor
public class CustomerMenuController implements CustomerMenuControllerDocs {

    private final CustomerMenuService customerMenuService;

    @GetMapping("/categories")
    public ResponseEntity<List<MenuCategoryResponse>> getMenuCategories(@PathVariable Long storeId) {
        List<MenuCategory> categories = customerMenuService.getMenuCategoriesByStoreId(storeId);
        List<MenuCategoryResponse> response = categories.stream()
                .map(MenuCategoryResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MenuResponse>> getMenus(
            @PathVariable Long storeId,
            @RequestParam(required = false) Long categoryId) {
        List<Menu> menus = customerMenuService.getMenusByStoreId(storeId, categoryId);
        List<MenuResponse> response = menus.stream()
                .map(MenuResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuDetailResponse> getMenu(
            @PathVariable Long storeId,
            @PathVariable Long menuId) {
        Menu menu = customerMenuService.getMenuById(menuId);
        return ResponseEntity.ok(MenuDetailResponse.from(menu));
    }
}
