package io.goorm.team02.core.menus.service;

import io.goorm.team02.core.menus.controller.dto.categorycreate.*;
import io.goorm.team02.core.menus.domain.Menu;
import io.goorm.team02.core.menus.domain.MenuCategory;
import io.goorm.team02.core.menus.domain.enums.MenuStatus;
import io.goorm.team02.core.menus.repository.MenuCategoryRepository;
import io.goorm.team02.core.menus.repository.MenuRepository;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.domain.TempUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MenuCategoryService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuRepository menuRepository;
    private final MenuValidationService menuValidationService;

    /**
     * 메뉴 카테고리 목록 조회
     */
    public List<MenuCategory> getMenuCategories(TempUser currentUser) {
        log.info("=== 메뉴 카테고리 목록 조회 시작 ===");

        Store store = menuValidationService.getMyStore(currentUser);

        List<MenuCategory> categories = menuCategoryRepository.findByStoreIdOrderByDisplayOrderAsc(store.getId());

        log.info("메뉴 카테고리 조회 완료 - 총 {}개 카테고리", categories.size());
        return categories;
    }

    /**
     * 메뉴 카테고리 등록
     */
    @Transactional
    public MenuCategory createCategory(TempUser currentUser, MenuCategoryCreateRequest request) {
        log.info("=== 메뉴 카테고리 등록 시작 ===");

        Store store = menuValidationService.getMyStore(currentUser);

        // 입력값 검증
        validateCategoryCreateRequest(request, store.getId());

        // 중복 카테고리명 체크
        if (menuCategoryRepository.existsByStoreIdAndName(store.getId(), request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "이미 존재하는 카테고리명입니다: " + request.getName());
        }

        // 표시 순서 자동 설정
        Integer displayOrder = request.getDisplayOrder();
        if (displayOrder == null || displayOrder == 0) {
            Integer maxOrder = getMaxCategoryOrder(store.getId());
            displayOrder = maxOrder + 1;
            log.info("표시 순서 자동 설정: {}", displayOrder);
        }

        MenuCategory category = MenuCategory.builder()
                .store(store)
                .name(request.getName().trim())
                .displayOrder(displayOrder)
                .isActive(request.getIsActive())
                .build();

        MenuCategory savedCategory = menuCategoryRepository.save(category);

        log.info("메뉴 카테고리 등록 완료! ID: {}, 이름: {}",
                savedCategory.getId(), savedCategory.getName());
        return savedCategory;
    }

    /**
     * 메뉴 카테고리 수정
     */
    @Transactional
    public MenuCategory updateCategory(TempUser currentUser, Long categoryId, MenuCategoryUpdateRequest request) {
        log.info("=== 메뉴 카테고리 수정 시작 - ID: {} ===", categoryId);

        Store store = menuValidationService.getMyStore(currentUser);

        MenuCategory category = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다"));

        // 권한 확인
        if (!category.getStore().getId().equals(store.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 가게의 카테고리입니다");
        }

        // 입력값 검증
        validateCategoryUpdateRequest(request, store.getId(), categoryId);

        boolean hasChanges = false;

        // 카테고리명 변경
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String newName = request.getName().trim();
            if (!newName.equals(category.getName())) {
                log.info("카테고리명 변경: {} -> {}", category.getName(), newName);
                category.updateName(newName);
                hasChanges = true;
            }
        }

        // 표시 순서 변경
        if (request.getDisplayOrder() != null && !request.getDisplayOrder().equals(category.getDisplayOrder())) {
            log.info("표시 순서 변경: {} -> {}", category.getDisplayOrder(), request.getDisplayOrder());
            category.updateDisplayOrder(request.getDisplayOrder());
            hasChanges = true;
        }

        // 활성화 상태 변경
        if (request.getIsActive() != null && !request.getIsActive().equals(category.getIsActive())) {
            log.info("활성화 상태 변경: {} -> {}", category.getIsActive(), request.getIsActive());

            if (!request.getIsActive()) {
                handleCategoryDeactivation(category, request.getForceDeactivate());
            }

            category.updateIsActive(request.getIsActive());
            hasChanges = true;
        }

        if (!hasChanges) {
            log.info("변경된 정보가 없습니다.");
            return category;
        }

        MenuCategory savedCategory = menuCategoryRepository.save(category);
        log.info("메뉴 카테고리 수정 완료!");
        return savedCategory;
    }

    /**
     * 메뉴 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(TempUser currentUser, Long categoryId) {
        log.info("=== 메뉴 카테고리 삭제 시작 - ID: {} ===", categoryId);

        Store store = menuValidationService.getMyStore(currentUser);

        MenuCategory category = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다"));

        // 권한 확인
        if (!category.getStore().getId().equals(store.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 가게의 카테고리입니다");
        }

        // 삭제 전 검증
        validateCategoryDeletion(category);

        menuCategoryRepository.delete(category);
        log.info("메뉴 카테고리 삭제 완료: {}", category.getName());
    }

    /**
     * 카테고리 순서 변경
     */
    @Transactional
    public List<MenuCategory> updateCategoryOrder(TempUser currentUser, CategoryMoveRequest request) {
        log.info("=== 카테고리 순서 변경 시작 ===");

        Store store = menuValidationService.getMyStore(currentUser);

        MenuCategory targetCategory = menuCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다"));

        // 권한 확인
        if (!targetCategory.getStore().getId().equals(store.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 가게의 카테고리입니다");
        }

        List<MenuCategory> allCategories = menuCategoryRepository.findByStoreIdAndIsActiveTrueOrderByDisplayOrderAsc(store.getId());

        if (allCategories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "활성 카테고리가 없습니다");
        }

        // 새로운 위치 유효성 검증
        if (request.getNewPosition() < 1 || request.getNewPosition() > allCategories.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("위치는 1부터 %d 사이여야 합니다", allCategories.size()));
        }

        // 현재 위치 찾기
        int currentPosition = -1;
        for (int i = 0; i < allCategories.size(); i++) {
            if (allCategories.get(i).getId().equals(request.getCategoryId())) {
                currentPosition = i + 1;
                break;
            }
        }

        if (currentPosition == -1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 목록에서 찾을 수 없습니다");
        }

        // 위치가 같으면 변경하지 않음
        if (currentPosition == request.getNewPosition()) {
            log.info("이미 같은 위치입니다. 변경하지 않습니다.");
            return allCategories;
        }

        // 순서 변경 로직
        MenuCategory movingCategory = allCategories.remove(currentPosition - 1);
        allCategories.add(request.getNewPosition() - 1, movingCategory);

        // 모든 카테고리의 displayOrder 재설정
        List<MenuCategory> updatedCategories = new ArrayList<>();
        for (int i = 0; i < allCategories.size(); i++) {
            MenuCategory category = allCategories.get(i);
            int newDisplayOrder = i + 1;

            if (!category.getDisplayOrder().equals(newDisplayOrder)) {
                category.updateDisplayOrder(newDisplayOrder);
                updatedCategories.add(category);
            }
        }

        if (!updatedCategories.isEmpty()) {
            menuCategoryRepository.saveAll(updatedCategories);
            log.info("총 {}개 카테고리 순서가 업데이트되었습니다", updatedCategories.size());
        }

        log.info("=== 카테고리 순서 변경 완료 ===");
        return menuCategoryRepository.findByStoreIdAndIsActiveTrueOrderByDisplayOrderAsc(store.getId());
    }

    // ================================
    // Private Helper Methods
    // ================================

    private void validateCategoryCreateRequest(MenuCategoryCreateRequest request, Long storeId) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리명은 필수입니다");
        }

        if (request.getName().trim().length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리명은 50자를 초과할 수 없습니다");
        }

        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "표시 순서는 0 이상이어야 합니다");
        }
    }

    private void validateCategoryUpdateRequest(MenuCategoryUpdateRequest request, Long storeId, Long categoryId) {
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String trimmedName = request.getName().trim();

            if (trimmedName.length() > 50) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리명은 50자를 초과할 수 없습니다");
            }

            // 중복 체크
            List<MenuCategory> existingCategories = menuCategoryRepository.findByStoreIdOrderByDisplayOrderAsc(storeId);
            boolean isDuplicate = existingCategories.stream()
                    .anyMatch(cat -> !cat.getId().equals(categoryId) &&
                            cat.getName().equals(trimmedName) &&
                            cat.getIsActive());

            if (isDuplicate) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 카테고리명입니다: " + trimmedName);
            }
        }

        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "표시 순서는 0 이상이어야 합니다");
        }
    }

    private void validateCategoryDeletion(MenuCategory category) {
        if (category.getMenus() != null && !category.getMenus().isEmpty()) {
            long activeMenus = category.getMenus().stream()
                    .filter(menu -> menu.getStatus() == MenuStatus.AVAILABLE || menu.getStatus() == MenuStatus.SOLD_OUT)
                    .count();

            if (activeMenus > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "카테고리에 활성 메뉴가 있습니다. 메뉴를 먼저 숨김 처리하거나 다른 카테고리로 이동해주세요.");
            }
        }

        // 마지막 카테고리 체크
        List<MenuCategory> activeCategories = menuCategoryRepository
                .findByStoreIdAndIsActiveTrueOrderByDisplayOrderAsc(category.getStore().getId());

        if (activeCategories.size() == 1 && activeCategories.get(0).getId().equals(category.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최소 1개 이상의 카테고리가 있어야 합니다");
        }
    }

    private void handleCategoryDeactivation(MenuCategory category, Boolean forceDeactivate) {
        if (category.getMenus() != null && !category.getMenus().isEmpty()) {
            long activeMenuCount = category.getMenus().stream()
                    .filter(menu -> menu.getStatus() == MenuStatus.AVAILABLE || menu.getStatus() == MenuStatus.SOLD_OUT)
                    .count();

            if (activeMenuCount > 0) {
                if (forceDeactivate != null && forceDeactivate) {
                    log.info("강제 비활성화 옵션으로 메뉴들을 숨김 처리합니다");

                    List<Menu> menusToUpdate = new ArrayList<>();
                    category.getMenus().forEach(menu -> {
                        if (menu.getStatus() == MenuStatus.AVAILABLE || menu.getStatus() == MenuStatus.SOLD_OUT) {
                            menu.updateStatus(MenuStatus.HIDDEN);
                            menusToUpdate.add(menu);
                        }
                    });

                    if (!menusToUpdate.isEmpty()) {
                        menuRepository.saveAll(menusToUpdate);
                        log.info("총 {}개 메뉴가 숨김 처리되었습니다", menusToUpdate.size());
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "카테고리에 활성 메뉴가 있습니다. 강제 비활성화 옵션(forceDeactivate=true)을 사용하거나 메뉴를 먼저 숨김 처리해주세요.");
                }
            }
        }
    }

    private Integer getMaxCategoryOrder(Long storeId) {
        List<MenuCategory> categories = menuCategoryRepository.findByStoreIdAndIsActiveTrueOrderByDisplayOrderAsc(storeId);

        if (categories.isEmpty()) {
            return 0;
        }

        return categories.stream()
                .mapToInt(MenuCategory::getDisplayOrder)
                .max()
                .orElse(0);
    }
}