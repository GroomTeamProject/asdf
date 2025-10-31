package io.goorm.team02.core.owner.menus.controller;

import io.goorm.team02.core.owner.menus.domain.Menu;
import io.goorm.team02.core.owner.menus.domain.MenuCategory;
import io.goorm.team02.core.owner.menus.mapper.MenuCategoryMapper;
import io.goorm.team02.core.owner.menus.mapper.MenuMapper;
import io.goorm.team02.core.owner.menus.service.CustomerMenuService;
import io.goorm.team02.dto.owner.menus.categorycreate.MenuCategoryResponse;
import io.goorm.team02.dto.owner.menus.menucreate.MenuDetailResponse;
import io.goorm.team02.dto.owner.menus.menucreate.MenuResponse;
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
    private final MenuCategoryMapper menuCategoryMapper;
    private final MenuMapper menuMapper;

    @GetMapping("/categories")
    public ResponseEntity<List<MenuCategoryResponse>> getMenuCategories(@PathVariable Long storeId) {
        List<MenuCategory> categories = customerMenuService.getMenuCategoriesByStoreId(storeId);
        // Mapper를 사용해서 변환
        List<MenuCategoryResponse> response = menuCategoryMapper.toResponseList(categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MenuResponse>> getMenus(
            @PathVariable Long storeId,
            @RequestParam(required = false) Long categoryId) {
        List<Menu> menus = customerMenuService.getMenusByStoreId(storeId, categoryId);
        // Mapper를 사용해서 변환
        List<MenuResponse> response = menuMapper.toResponseList(menus);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuDetailResponse> getMenu(
            @PathVariable Long storeId,
            @PathVariable Long menuId) {
        Menu menu = customerMenuService.getMenuById(menuId);
        // Mapper를 사용해서 변환
        MenuDetailResponse response = menuMapper.toDetailResponse(menu);
        return ResponseEntity.ok(response);
    }
}