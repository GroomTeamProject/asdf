package io.goorm.team02.core.owner.menus.controller;

import io.goorm.team02.core.owner.auth.annotation.CurrentUser;
import io.goorm.team02.core.owner.menus.domain.Menu;
import io.goorm.team02.core.owner.menus.domain.MenuCategory;
import io.goorm.team02.core.owner.menus.domain.MenuOption;
import io.goorm.team02.core.owner.menus.domain.MenuOptionItem;
import io.goorm.team02.core.owner.menus.mapper.MenuCategoryMapper;
import io.goorm.team02.core.owner.menus.mapper.MenuMapper;
import io.goorm.team02.core.owner.menus.mapper.MenuOptionItemMapper;
import io.goorm.team02.core.owner.menus.mapper.MenuOptionMapper;
import io.goorm.team02.core.owner.menus.service.MenuService;
import io.goorm.team02.dto.owner.menus.categorycreate.CategoryMoveRequest;
import io.goorm.team02.dto.owner.menus.categorycreate.MenuCategoryCreateRequest;
import io.goorm.team02.dto.owner.menus.categorycreate.MenuCategoryResponse;
import io.goorm.team02.dto.owner.menus.categorycreate.MenuCategoryUpdateRequest;
import io.goorm.team02.dto.owner.menus.menucreate.*;
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
public class MenuController implements MenuControllerDocs {

    private final MenuService menuService;
    private final MenuCategoryMapper menuCategoryMapper;
    private final MenuMapper menuMapper;
    private final MenuOptionMapper menuOptionMapper;
    private final MenuOptionItemMapper menuOptionItemMapper;

    // ================================
    // 카테고리 관리
    // ================================

    @Override
    @GetMapping("/categories")
    public ResponseEntity<List<MenuCategoryResponse>> getMenuCategories(@CurrentUser Long currentUser) {
        List<MenuCategory> categories = menuService.getMenuCategories(currentUser);
        List<MenuCategoryResponse> response = menuCategoryMapper.toResponseList(categories);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/categories")
    public ResponseEntity<MenuCategoryResponse> createCategory(
            @CurrentUser Long currentUser,
            @Valid @RequestBody MenuCategoryCreateRequest request) {
        MenuCategory category = menuService.createCategory(currentUser, request);
        MenuCategoryResponse response = menuCategoryMapper.toResponse(category);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<MenuCategoryResponse> updateCategory(
            @CurrentUser Long currentUser,
            @PathVariable Long categoryId,
            @Valid @RequestBody MenuCategoryUpdateRequest request) {
        MenuCategory category = menuService.updateCategory(currentUser, categoryId, request);
        MenuCategoryResponse response = menuCategoryMapper.toResponse(category);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @CurrentUser Long currentUser,
            @PathVariable Long categoryId) {
        menuService.deleteCategory(currentUser, categoryId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PutMapping("/categories/order")
    public ResponseEntity<List<MenuCategoryResponse>> updateCategoryOrder(
            @CurrentUser Long currentUser,
            @Valid @RequestBody CategoryMoveRequest request) {
        List<MenuCategory> categories = menuService.updateCategoryOrder(currentUser, request);
        List<MenuCategoryResponse> response = menuCategoryMapper.toResponseList(categories);
        return ResponseEntity.ok(response);
    }

    // ================================
    // 메뉴 관리
    // ================================

    @Override
    @GetMapping
    public ResponseEntity<List<MenuResponse>> getMenus(
            @CurrentUser Long currentUser,
            @RequestParam(required = false) Long categoryId) {
        List<Menu> menus = menuService.getMenus(currentUser, categoryId);
        List<MenuResponse> response = menuMapper.toResponseList(menus);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{menuId}")
    public ResponseEntity<MenuDetailResponse> getMenu(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId) {
        Menu menu = menuService.getMenu(currentUser, menuId);
        MenuDetailResponse response = menuMapper.toDetailResponse(menu);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping
    public ResponseEntity<MenuResponse> createMenu(
            @CurrentUser Long currentUser,
            @Valid @RequestBody MenuCreateRequest request) {
        Menu menu = menuService.createMenu(currentUser, request);
        MenuResponse response = menuMapper.toResponse(menu);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/{menuId}")
    public ResponseEntity<MenuResponse> updateMenu(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId,
            @Valid @RequestBody MenuUpdateRequest request) {
        Menu menu = menuService.updateMenu(currentUser, menuId, request);
        MenuResponse response = menuMapper.toResponse(menu);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteMenu(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId) {
        menuService.deleteMenu(currentUser, menuId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PutMapping("/{menuId}/status")
    public ResponseEntity<MenuResponse> updateMenuStatus(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId,
            @Valid @RequestBody MenuStatusRequest request) {
        Menu menu = menuService.updateMenuStatus(currentUser, menuId, request);
        MenuResponse response = menuMapper.toResponse(menu);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/order")
    public ResponseEntity<List<MenuResponse>> updateMenuOrder(
            @CurrentUser Long currentUser,
            @Valid @RequestBody MenuOrderUpdateRequest request) {
        List<Menu> menus = menuService.updateMenuOrder(currentUser, request);
        List<MenuResponse> response = menuMapper.toResponseList(menus);
        return ResponseEntity.ok(response);
    }

    // ================================
    // 이미지 관리
    // ================================

    @Override
    @PostMapping(value = "/{menuId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadMenuImage(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어있습니다");
        }

        String imageUrl = menuService.uploadMenuImage(currentUser, menuId, file);
        return ResponseEntity.ok(imageUrl);
    }

    @Override
    @GetMapping("/{menuId}/images/info")
    public ResponseEntity<Map<String, Object>> getMenuImageInfo(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId) {
        Map<String, Object> imageInfo = menuService.getMenuImageInfo(currentUser, menuId);
        return ResponseEntity.ok(imageInfo);
    }

    @Override
    @DeleteMapping("/{menuId}/images/{imageId}")
    public ResponseEntity<Void> deleteMenuImage(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId,
            @PathVariable Long imageId) {
        menuService.deleteMenuImage(currentUser, menuId, imageId);
        return ResponseEntity.ok().build();
    }

    // ================================
    // 옵션 그룹 관리
    // ================================

    @Override
    @GetMapping("/{menuId}/option-groups")
    public ResponseEntity<List<MenuOptionGroupResponse>> getMenuOptionGroups(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId) {
        List<MenuOption> optionGroups = menuService.getMenuOptionGroups(currentUser, menuId);
        List<MenuOptionGroupResponse> response = menuOptionMapper.toGroupResponseList(optionGroups);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/{menuId}/option-groups")
    public ResponseEntity<MenuOptionGroupResponse> createOptionGroup(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId,
            @Valid @RequestBody MenuOptionGroupCreateRequest request) {
        MenuOption optionGroup = menuService.createOptionGroup(currentUser, menuId, request);
        MenuOptionGroupResponse response = menuOptionMapper.toGroupResponse(optionGroup);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/{menuId}/option-groups/{groupId}")
    public ResponseEntity<MenuOptionGroupResponse> updateOptionGroup(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId,
            @PathVariable Long groupId,
            @Valid @RequestBody MenuOptionGroupUpdateRequest request) {
        MenuOption optionGroup = menuService.updateOptionGroup(currentUser, menuId, groupId, request);
        MenuOptionGroupResponse response = menuOptionMapper.toGroupResponse(optionGroup);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{menuId}/option-groups/{groupId}")
    public ResponseEntity<Void> deleteOptionGroup(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId,
            @PathVariable Long groupId) {
        menuService.deleteOptionGroup(currentUser, menuId, groupId);
        return ResponseEntity.ok().build();
    }

    // ================================
    // 옵션 아이템 관리
    // ================================

    @Override
    @GetMapping("/{menuId}/option-groups/{groupId}/options")
    public ResponseEntity<List<MenuOptionItemResponse>> getMenuOptions(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId,
            @PathVariable Long groupId) {
        List<MenuOptionItem> options = menuService.getMenuOptions(currentUser, menuId, groupId);
        List<MenuOptionItemResponse> response = menuOptionItemMapper.toResponseList(options);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/{menuId}/option-groups/{groupId}/options")
    public ResponseEntity<MenuOptionItemResponse> createOption(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId,
            @PathVariable Long groupId,
            @Valid @RequestBody MenuOptionItemCreateRequest request) {
        MenuOptionItem optionItem = menuService.createOption(currentUser, menuId, groupId, request);
        MenuOptionItemResponse response = menuOptionItemMapper.toResponse(optionItem);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/{menuId}/option-groups/{groupId}/options/{optionId}")
    public ResponseEntity<MenuOptionItemResponse> updateOption(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId,
            @PathVariable Long groupId,
            @PathVariable Long optionId,
            @Valid @RequestBody MenuOptionItemUpdateRequest request) {
        MenuOptionItem optionItem = menuService.updateOption(currentUser, menuId, groupId, optionId, request);
        MenuOptionItemResponse response = menuOptionItemMapper.toResponse(optionItem);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{menuId}/option-groups/{groupId}/options/{optionId}")
    public ResponseEntity<Void> deleteOption(
            @CurrentUser Long currentUser,
            @PathVariable Long menuId,
            @PathVariable Long groupId,
            @PathVariable Long optionId) {
        menuService.deleteOption(currentUser, menuId, groupId, optionId);
        return ResponseEntity.ok().build();
    }
}