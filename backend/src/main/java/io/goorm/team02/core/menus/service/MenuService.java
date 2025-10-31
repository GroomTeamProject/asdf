package io.goorm.team02.core.menus.service;

import io.goorm.team02.core.menus.controller.dto.categorycreate.*;
import io.goorm.team02.core.menus.controller.dto.menucreate.*;
import io.goorm.team02.core.menus.domain.Menu;
import io.goorm.team02.core.menus.domain.MenuCategory;
import io.goorm.team02.core.menus.domain.MenuOption;
import io.goorm.team02.core.menus.domain.MenuOptionItem;
import io.goorm.team02.core.stores.domain.TempUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MenuService {

    private final MenuCategoryService menuCategoryService;
    private final MenuCrudService menuCrudService;
    private final MenuImageService menuImageService;
    private final MenuOptionService menuOptionService;
    private final MenuValidationService menuValidationService;

    // ================================
    // 카테고리 관리 위임
    // ================================

    public List<MenuCategory> getMenuCategories(TempUser currentUser) {
        return menuCategoryService.getMenuCategories(currentUser);
    }

    @Transactional
    public MenuCategory createCategory(TempUser currentUser, MenuCategoryCreateRequest request) {
        return menuCategoryService.createCategory(currentUser, request);
    }

    @Transactional
    public MenuCategory updateCategory(TempUser currentUser, Long categoryId, MenuCategoryUpdateRequest request) {
        return menuCategoryService.updateCategory(currentUser, categoryId, request);
    }

    @Transactional
    public void deleteCategory(TempUser currentUser, Long categoryId) {
        menuCategoryService.deleteCategory(currentUser, categoryId);
    }

    @Transactional
    public List<MenuCategory> updateCategoryOrder(TempUser currentUser, CategoryMoveRequest request) {
        return menuCategoryService.updateCategoryOrder(currentUser, request);
    }

    // ================================
    // 메뉴 CRUD 위임
    // ================================

    public List<Menu> getMenus(TempUser currentUser, Long categoryId) {
        return menuCrudService.getMenus(currentUser, categoryId);
    }

    public Menu getMenu(TempUser currentUser, Long menuId) {
        return menuCrudService.getMenu(currentUser, menuId);
    }

    @Transactional
    public Menu createMenu(TempUser currentUser, MenuCreateRequest request) {
        return menuCrudService.createMenu(currentUser, request);
    }

    @Transactional
    public Menu updateMenu(TempUser currentUser, Long menuId, MenuUpdateRequest request) {
        return menuCrudService.updateMenu(currentUser, menuId, request);
    }

    @Transactional
    public void deleteMenu(TempUser currentUser, Long menuId) {
        menuCrudService.deleteMenu(currentUser, menuId);
    }

    @Transactional
    public Menu updateMenuStatus(TempUser currentUser, Long menuId, MenuStatusRequest request) {
        return menuCrudService.updateMenuStatus(currentUser, menuId, request);
    }

    @Transactional
    public List<Menu> updateMenuOrder(TempUser currentUser, MenuOrderUpdateRequest request) {
        return menuCrudService.updateMenuOrder(currentUser, request);
    }

    // ================================
    // 이미지 관리 위임
    // ================================

    @Transactional
    public String uploadMenuImage(TempUser currentUser, Long menuId, MultipartFile file) {
        return menuImageService.uploadMenuImage(currentUser, menuId, file);
    }

    public Map<String, Object> getMenuImageInfo(TempUser currentUser, Long menuId) {
        return menuImageService.getMenuImageInfo(currentUser, menuId);
    }

    @Transactional
    public void deleteMenuImage(TempUser currentUser, Long menuId, Long imageId) {
        menuImageService.deleteMenuImage(currentUser, menuId, imageId);
    }

    // ================================
    // 옵션 관리 위임
    // ================================

    public List<MenuOption> getMenuOptionGroups(TempUser currentUser, Long menuId) {
        return menuOptionService.getMenuOptionGroups(currentUser, menuId);
    }

    @Transactional
    public MenuOption createOptionGroup(TempUser currentUser, Long menuId, MenuOptionGroupCreateRequest request) {
        return menuOptionService.createOptionGroup(currentUser, menuId, request);
    }

    @Transactional
    public MenuOption updateOptionGroup(TempUser currentUser, Long menuId, Long groupId, MenuOptionGroupUpdateRequest request) {
        return menuOptionService.updateOptionGroup(currentUser, menuId, groupId, request);
    }

    @Transactional
    public void deleteOptionGroup(TempUser currentUser, Long menuId, Long groupId) {
        menuOptionService.deleteOptionGroup(currentUser, menuId, groupId);
    }

    public List<MenuOptionItem> getMenuOptions(TempUser currentUser, Long menuId, Long groupId) {
        return menuOptionService.getMenuOptions(currentUser, menuId, groupId);
    }

    @Transactional
    public MenuOptionItem createOption(TempUser currentUser, Long menuId, Long groupId, MenuOptionItemCreateRequest request) {
        return menuOptionService.createOption(currentUser, menuId, groupId, request);
    }

    @Transactional
    public MenuOptionItem updateOption(TempUser currentUser, Long menuId, Long groupId, Long optionId, MenuOptionItemUpdateRequest request) {
        return menuOptionService.updateOption(currentUser, menuId, groupId, optionId, request);
    }

    @Transactional
    public void deleteOption(TempUser currentUser, Long menuId, Long groupId, Long optionId) {
        menuOptionService.deleteOption(currentUser, menuId, groupId, optionId);
    }
}