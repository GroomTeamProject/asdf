package io.goorm.team02.core.owner.menus.service;

import io.goorm.team02.core.owner.menus.domain.Menu;
import io.goorm.team02.core.owner.menus.domain.MenuCategory;
import io.goorm.team02.core.owner.menus.domain.MenuOption;
import io.goorm.team02.core.owner.menus.domain.MenuOptionItem;
import io.goorm.team02.dto.owner.menus.categorycreate.CategoryMoveRequest;
import io.goorm.team02.dto.owner.menus.categorycreate.MenuCategoryCreateRequest;
import io.goorm.team02.dto.owner.menus.categorycreate.MenuCategoryUpdateRequest;
import io.goorm.team02.dto.owner.menus.menucreate.*;
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

    public List<MenuCategory> getMenuCategories(Long currentUser) {
        return menuCategoryService.getMenuCategories(currentUser);
    }

    @Transactional
    public MenuCategory createCategory(Long currentUser, MenuCategoryCreateRequest request) {
        return menuCategoryService.createCategory(currentUser, request);
    }

    @Transactional
    public MenuCategory updateCategory(Long currentUser, Long categoryId, MenuCategoryUpdateRequest request) {
        return menuCategoryService.updateCategory(currentUser, categoryId, request);
    }

    @Transactional
    public void deleteCategory(Long currentUser, Long categoryId) {
        menuCategoryService.deleteCategory(currentUser, categoryId);
    }

    @Transactional
    public List<MenuCategory> updateCategoryOrder(Long currentUser, CategoryMoveRequest request) {
        return menuCategoryService.updateCategoryOrder(currentUser, request);
    }

    // ================================
    // 메뉴 CRUD 위임
    // ================================

    public List<Menu> getMenus(Long currentUser, Long categoryId) {
        return menuCrudService.getMenus(currentUser, categoryId);
    }

    public Menu getMenu(Long currentUser, Long menuId) {
        return menuCrudService.getMenu(currentUser, menuId);
    }

    @Transactional
    public Menu createMenu(Long currentUser, MenuCreateRequest request) {
        return menuCrudService.createMenu(currentUser, request);
    }

    @Transactional
    public Menu updateMenu(Long currentUser, Long menuId, MenuUpdateRequest request) {
        return menuCrudService.updateMenu(currentUser, menuId, request);
    }

    @Transactional
    public void deleteMenu(Long currentUser, Long menuId) {
        menuCrudService.deleteMenu(currentUser, menuId);
    }

    @Transactional
    public Menu updateMenuStatus(Long currentUser, Long menuId, MenuStatusRequest request) {
        return menuCrudService.updateMenuStatus(currentUser, menuId, request);
    }

    @Transactional
    public List<Menu> updateMenuOrder(Long currentUser, MenuOrderUpdateRequest request) {
        return menuCrudService.updateMenuOrder(currentUser, request);
    }

    // ================================
    // 이미지 관리 위임
    // ================================

    @Transactional
    public String uploadMenuImage(Long currentUser, Long menuId, MultipartFile file) {
        return menuImageService.uploadMenuImage(currentUser, menuId, file);
    }

    public Map<String, Object> getMenuImageInfo(Long currentUser, Long menuId) {
        return menuImageService.getMenuImageInfo(currentUser, menuId);
    }

    @Transactional
    public void deleteMenuImage(Long currentUser, Long menuId, Long imageId) {
        menuImageService.deleteMenuImage(currentUser, menuId, imageId);
    }

    // ================================
    // 옵션 관리 위임
    // ================================

    public List<MenuOption> getMenuOptionGroups(Long currentUser, Long menuId) {
        return menuOptionService.getMenuOptionGroups(currentUser, menuId);
    }

    @Transactional
    public MenuOption createOptionGroup(Long currentUser, Long menuId, MenuOptionGroupCreateRequest request) {
        return menuOptionService.createOptionGroup(currentUser, menuId, request);
    }

    @Transactional
    public MenuOption updateOptionGroup(Long currentUser, Long menuId, Long groupId, MenuOptionGroupUpdateRequest request) {
        return menuOptionService.updateOptionGroup(currentUser, menuId, groupId, request);
    }

    @Transactional
    public void deleteOptionGroup(Long currentUser, Long menuId, Long groupId) {
        menuOptionService.deleteOptionGroup(currentUser, menuId, groupId);
    }

    public List<MenuOptionItem> getMenuOptions(Long currentUser, Long menuId, Long groupId) {
        return menuOptionService.getMenuOptions(currentUser, menuId, groupId);
    }

    @Transactional
    public MenuOptionItem createOption(Long currentUser, Long menuId, Long groupId, MenuOptionItemCreateRequest request) {
        return menuOptionService.createOption(currentUser, menuId, groupId, request);
    }

    @Transactional
    public MenuOptionItem updateOption(Long currentUser, Long menuId, Long groupId, Long optionId, MenuOptionItemUpdateRequest request) {
        return menuOptionService.updateOption(currentUser, menuId, groupId, optionId, request);
    }

    @Transactional
    public void deleteOption(Long currentUser, Long menuId, Long groupId, Long optionId) {
        menuOptionService.deleteOption(currentUser, menuId, groupId, optionId);
    }
}