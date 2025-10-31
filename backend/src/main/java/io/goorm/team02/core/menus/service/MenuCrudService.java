package io.goorm.team02.core.menus.service;

import io.goorm.team02.core.menus.logging.SecureLogger;
import io.goorm.team02.core.common.validation.SecureInputValidator;
import io.goorm.team02.core.menus.controller.dto.menucreate.*;
import io.goorm.team02.core.menus.domain.Menu;
import io.goorm.team02.core.menus.domain.MenuCategory;
import io.goorm.team02.core.menus.domain.MenuOption;
import io.goorm.team02.core.menus.domain.MenuOptionItem;
import io.goorm.team02.core.menus.domain.enums.MenuStatus;
import io.goorm.team02.core.menus.repository.MenuCategoryRepository;
import io.goorm.team02.core.menus.repository.MenuOptionItemRepository;
import io.goorm.team02.core.menus.repository.MenuOptionRepository;
import io.goorm.team02.core.menus.repository.MenuRepository;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.domain.TempUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MenuCrudService {

    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final MenuOptionItemRepository menuOptionItemRepository;
    private final MenuValidationService menuValidationService;
    private final SecureInputValidator inputValidator;

    /**
     * 메뉴 목록 조회
     */
    public List<Menu> getMenus(TempUser currentUser, Long categoryId) {
        SecureLogger.logSecurely(log, "info", "=== 메뉴 목록 조회 시작 - 사용자 ID: {} ===", currentUser.getId());

        Store store = menuValidationService.getMyStore(currentUser);

        List<Menu> menus;

        if (categoryId != null) {
            // 카테고리 권한 확인
            MenuCategory category = menuCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> {
                        SecureLogger.logSecurely(log, "warn", "존재하지 않는 카테고리 조회 시도 - 사용자 ID: {}, 카테고리 ID: {}",
                                currentUser.getId(), categoryId);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다");
                    });

            if (!category.getStore().getId().equals(store.getId())) {
                SecureLogger.logSecurely(log, "warn", "권한 없는 카테고리 접근 시도 - 사용자 ID: {}, 카테고리 ID: {}, 소유 가게 ID: {}",
                        currentUser.getId(), categoryId, store.getId());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 가게의 카테고리입니다");
            }

            menus = menuRepository.findByStoreIdAndCategoryIdOrderByDisplayOrderAsc(store.getId(), categoryId);
            SecureLogger.logSecurely(log, "info", "카테고리별 메뉴 조회 완료 - 사용자 ID: {}, 카테고리명: {}, 메뉴 수: {}개",
                    currentUser.getId(), category.getName(), menus.size());
        } else {
            menus = menuRepository.findByStoreIdOrderByDisplayOrderAsc(store.getId());
            SecureLogger.logSecurely(log, "info", "전체 메뉴 조회 완료 - 사용자 ID: {}, 총 메뉴 수: {}개",
                    currentUser.getId(), menus.size());
        }

        return menus;
    }

    /**
     * 메뉴 상세 조회
     */
    public Menu getMenu(TempUser currentUser, Long menuId) {
        SecureLogger.logSecurely(log, "info", "=== 메뉴 상세 조회 시작 - 사용자 ID: {}, 메뉴 ID: {} ===",
                currentUser.getId(), menuId);

        Store store = menuValidationService.getMyStore(currentUser);

        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    SecureLogger.logSecurely(log, "warn", "존재하지 않는 메뉴 조회 시도 - 사용자 ID: {}, 메뉴 ID: {}",
                            currentUser.getId(), menuId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다");
                });

        SecureLogger.logSecurely(log, "info", "메뉴 조회 완료 - 사용자 ID: {}, 메뉴 ID: {}, 상태: {}",
                currentUser.getId(), menu.getId(), menu.getStatus());
        return menu;
    }

    /**
     * 메뉴 등록
     */
    @Transactional
    public Menu createMenu(TempUser currentUser, MenuCreateRequest request) {
        SecureLogger.logSecurely(log, "info", "=== 메뉴 등록 시작 - 사용자 ID: {}, 메뉴명: {} ===",
                currentUser.getId(), request.getName());

        Store store = menuValidationService.getMyStore(currentUser);

        // 카테고리 검증
        MenuCategory category = validateAndGetCategory(request.getCategoryId(), store.getId());

        // 입력값 검증
        try {
            inputValidator.validateMenuName(request.getName());
            inputValidator.validateDescription(request.getDescription());
            inputValidator.validatePrice(request.getPrice());
            inputValidator.validateImageUrl(request.getImageUrl());
        } catch (SecurityException e) {
            SecureLogger.logSecurely(log, "warn", "메뉴 등록 입력값 검증 실패 - 사용자 ID: {}, 오류: {}",
                    currentUser.getId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        // 메뉴명 중복 체크
        if (menuRepository.existsByStoreIdAndName(store.getId(), request.getName())) {
            SecureLogger.logSecurely(log, "warn", "중복 메뉴명 등록 시도 - 사용자 ID: {}, 메뉴명: {}",
                    currentUser.getId(), request.getName());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 메뉴명입니다");
        }

        Menu menu = Menu.builder()
                .store(store)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .isPopular(request.getIsPopular() != null ? request.getIsPopular() : false)
                .isRecommended(request.getIsRecommended() != null ? request.getIsRecommended() : false)
                .status(request.getStatus())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        Menu savedMenu = menuRepository.save(menu);

        // 옵션 처리
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            SecureLogger.logSecurely(log, "info", "메뉴 옵션 생성 시작 - 메뉴 ID: {}, 옵션 그룹 수: {}개",
                    savedMenu.getId(), request.getOptions().size());
            createMenuOptions(savedMenu, request.getOptions());
        }

        SecureLogger.logSecurely(log, "info", "메뉴 등록 완료 - 사용자 ID: {}, 메뉴 ID: {}",
                currentUser.getId(), savedMenu.getId());
        return savedMenu;
    }

    /**
     * 메뉴 수정
     */
    @Transactional
    public Menu updateMenu(TempUser currentUser, Long menuId, MenuUpdateRequest request) {
        SecureLogger.logSecurely(log, "info", "=== 메뉴 수정 시작 - 사용자 ID: {}, 메뉴 ID: {} ===",
                currentUser.getId(), menuId);

        Store store = menuValidationService.getMyStore(currentUser);

        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    SecureLogger.logSecurely(log, "warn", "존재하지 않는 메뉴 수정 시도 - 사용자 ID: {}, 메뉴 ID: {}",
                            currentUser.getId(), menuId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다");
                });

        // 입력값 검증
        validateMenuUpdateRequest(request, store.getId(), menuId);

        boolean hasChanges = false;
        List<String> changes = new ArrayList<>();

        // 메뉴명 변경
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String newName = request.getName().trim();
            if (!newName.equals(menu.getName())) {
                changes.add("메뉴명: " + menu.getName() + " -> " + newName);
                menu.updateName(newName);
                hasChanges = true;
            }
        }

        // 설명 변경
        if (request.getDescription() != null) {
            String currentDescription = menu.getDescription() != null ? menu.getDescription() : "";
            if (!request.getDescription().equals(currentDescription)) {
                changes.add("설명 변경");
                menu.updateDescription(request.getDescription());
                hasChanges = true;
            }
        }

        // 가격 변경
        if (request.getPrice() != null && request.getPrice().compareTo(menu.getPrice()) != 0) {
            changes.add("가격: " + menu.getPrice() + " -> " + request.getPrice());
            menu.updatePrice(request.getPrice());
            hasChanges = true;
        }

        // 카테고리 변경
        if (request.getCategoryId() != null) {
            MenuCategory newCategory = validateAndGetCategory(request.getCategoryId(), store.getId());
            if (!newCategory.getId().equals(menu.getCategory().getId())) {
                changes.add("카테고리: " + menu.getCategory().getName() + " -> " + newCategory.getName());
                menu.updateCategory(newCategory);
                hasChanges = true;
            }
        }

        // 이미지 처리
        if (request.getRemoveImage() != null && request.getRemoveImage()) {
            if (menu.getImageUrl() != null) {
                changes.add("이미지 삭제");
                menu.removeImage();
                hasChanges = true;
            }
        } else if (request.getImageUrl() != null) {
            String currentImageUrl = menu.getImageUrl() != null ? menu.getImageUrl() : "";
            if (!request.getImageUrl().equals(currentImageUrl)) {
                changes.add("이미지 URL 변경");
                menu.updateImageUrl(request.getImageUrl());
                hasChanges = true;
            }
        }

        // 기타 속성 변경
        if (request.getIsPopular() != null && !request.getIsPopular().equals(menu.getIsPopular())) {
            changes.add("인기메뉴: " + menu.getIsPopular() + " -> " + request.getIsPopular());
            menu.updateIsPopular(request.getIsPopular());
            hasChanges = true;
        }

        if (request.getIsRecommended() != null && !request.getIsRecommended().equals(menu.getIsRecommended())) {
            changes.add("추천메뉴: " + menu.getIsRecommended() + " -> " + request.getIsRecommended());
            menu.updateIsRecommended(request.getIsRecommended());
            hasChanges = true;
        }

        if (request.getStatus() != null && !request.getStatus().equals(menu.getStatus())) {
            changes.add("상태: " + menu.getStatus() + " -> " + request.getStatus());
            menu.updateStatus(request.getStatus());
            hasChanges = true;
        }

        if (request.getDisplayOrder() != null && !request.getDisplayOrder().equals(menu.getDisplayOrder())) {
            changes.add("표시순서: " + menu.getDisplayOrder() + " -> " + request.getDisplayOrder());
            menu.updateDisplayOrder(request.getDisplayOrder());
            hasChanges = true;
        }

        if (!hasChanges) {
            SecureLogger.logSecurely(log, "info", "메뉴 수정 - 변경사항 없음 - 사용자 ID: {}, 메뉴 ID: {}",
                    currentUser.getId(), menuId);
            return menu;
        }

        Menu savedMenu = menuRepository.save(menu);

        // 변경사항 로깅 (민감정보 제외)
        SecureLogger.logSecurely(log, "info", "메뉴 수정 완료 - 사용자 ID: {}, 메뉴 ID: {}, 변경항목 수: {}개",
                currentUser.getId(), menuId, changes.size());

        return savedMenu;
    }

    /**
     * 메뉴 삭제
     */
    @Transactional
    public void deleteMenu(TempUser currentUser, Long menuId) {
        SecureLogger.logSecurely(log, "info", "=== 메뉴 삭제 시작 - 사용자 ID: {}, 메뉴 ID: {} ===",
                currentUser.getId(), menuId);

        Store store = menuValidationService.getMyStore(currentUser);

        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    SecureLogger.logSecurely(log, "warn", "존재하지 않는 메뉴 삭제 시도 - 사용자 ID: {}, 메뉴 ID: {}",
                            currentUser.getId(), menuId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다");
                });

        // 삭제 전 검증
        validateMenuDeletion(menu);

        String menuName = menu.getName(); // 삭제 전 이름 저장
        int optionGroupCount = 0;
        int optionItemCount = 0;

        // 옵션들 먼저 삭제
        if (menu.getOptions() != null && !menu.getOptions().isEmpty()) {
            optionGroupCount = menu.getOptions().size();

            for (MenuOption option : menu.getOptions()) {
                if (option.getItems() != null && !option.getItems().isEmpty()) {
                    optionItemCount += option.getItems().size();
                    menuOptionItemRepository.deleteByOptionId(option.getId());
                }
            }
            menuOptionRepository.deleteByMenuId(menuId);
        }

        menuRepository.delete(menu);

        SecureLogger.logSecurely(log, "info", "메뉴 삭제 완료 - 사용자 ID: {}, 메뉴 ID: {}, 옵션 그룹: {}개, 옵션 아이템: {}개",
                currentUser.getId(), menuId, optionGroupCount, optionItemCount);
    }

    /**
     * 메뉴 상태 변경
     */
    @Transactional
    public Menu updateMenuStatus(TempUser currentUser, Long menuId, MenuStatusRequest request) {
        SecureLogger.logSecurely(log, "info", "=== 메뉴 상태 변경 시작 - 사용자 ID: {}, 메뉴 ID: {}, 새 상태: {} ===",
                currentUser.getId(), menuId, request.getStatus());

        Store store = menuValidationService.getMyStore(currentUser);

        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    SecureLogger.logSecurely(log, "warn", "존재하지 않는 메뉴 상태 변경 시도 - 사용자 ID: {}, 메뉴 ID: {}",
                            currentUser.getId(), menuId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다");
                });

        // 입력값 검증
        validateMenuStatusRequest(request, menu);

        // 현재 상태와 동일한지 확인
        if (menu.getStatus() == request.getStatus()) {
            SecureLogger.logSecurely(log, "info", "메뉴 상태 변경 - 동일한 상태 - 사용자 ID: {}, 메뉴 ID: {}, 상태: {}",
                    currentUser.getId(), menuId, request.getStatus());
            return menu;
        }

        // 상태 변경 전 검증
        validateStatusTransition(menu.getStatus(), request.getStatus(), menu);

        MenuStatus previousStatus = menu.getStatus();
        menu.updateStatus(request.getStatus());

        // 상태 변경 로그 기록 (보안 로깅 적용)
        logStatusChangeSecurely(currentUser, menu, previousStatus, request);

        Menu savedMenu = menuRepository.save(menu);

        SecureLogger.logSecurely(log, "info", "메뉴 상태 변경 완료 - 사용자 ID: {}, 메뉴 ID: {}, 상태: {} -> {}",
                currentUser.getId(), menuId, previousStatus, savedMenu.getStatus());

        return savedMenu;
    }

    /**
     * 메뉴 순서 변경
     */
    @Transactional
    public List<Menu> updateMenuOrder(TempUser currentUser, MenuOrderUpdateRequest request) {
        SecureLogger.logSecurely(log, "info", "=== 메뉴 순서 변경 시작 - 사용자 ID: {}, 메뉴 ID: {}, 새 위치: {} ===",
                currentUser.getId(), request.getMenuId(), request.getNewPosition());

        Store store = menuValidationService.getMyStore(currentUser);

        Menu targetMenu = menuRepository.findByIdAndStoreId(request.getMenuId(), store.getId())
                .orElseThrow(() -> {
                    SecureLogger.logSecurely(log, "warn", "존재하지 않는 메뉴 순서 변경 시도 - 사용자 ID: {}, 메뉴 ID: {}",
                            currentUser.getId(), request.getMenuId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다");
                });

        List<Menu> menus;
        String orderScope;

        if (request.getGlobalOrder() != null && request.getGlobalOrder()) {
            menus = menuRepository.findByStoreIdOrderByDisplayOrderAsc(store.getId());
            orderScope = "전체 메뉴";
        } else {
            Long categoryIdForOrder = request.getCategoryId() != null ?
                    request.getCategoryId() :
                    (targetMenu.getCategory() != null ? targetMenu.getCategory().getId() : null);

            if (categoryIdForOrder == null) {
                SecureLogger.logSecurely(log, "warn", "카테고리 정보 없는 순서 변경 시도 - 사용자 ID: {}, 메뉴 ID: {}",
                        currentUser.getId(), request.getMenuId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리 정보가 필요합니다");
            }

            // 카테고리 권한 확인
            MenuCategory category = menuCategoryRepository.findById(categoryIdForOrder)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다"));

            if (!category.getStore().getId().equals(store.getId())) {
                SecureLogger.logSecurely(log, "warn", "권한 없는 카테고리 순서 변경 시도 - 사용자 ID: {}, 카테고리 ID: {}",
                        currentUser.getId(), categoryIdForOrder);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 가게의 카테고리입니다");
            }

            menus = menuRepository.findByStoreIdAndCategoryIdOrderByDisplayOrderAsc(store.getId(), categoryIdForOrder);
            orderScope = "카테고리 '" + category.getName() + "' 내";
        }

        if (menus.isEmpty()) {
            SecureLogger.logSecurely(log, "warn", "순서 변경할 메뉴 없음 - 사용자 ID: {}", currentUser.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "순서를 변경할 메뉴가 없습니다");
        }

        // 새로운 위치 유효성 검증
        if (request.getNewPosition() < 1 || request.getNewPosition() > menus.size()) {
            SecureLogger.logSecurely(log, "warn", "잘못된 순서 위치 - 사용자 ID: {}, 요청 위치: {}, 최대 위치: {}",
                    currentUser.getId(), request.getNewPosition(), menus.size());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("위치는 1부터 %d 사이여야 합니다", menus.size()));
        }

        // 현재 위치 찾기
        int currentPosition = -1;
        for (int i = 0; i < menus.size(); i++) {
            if (menus.get(i).getId().equals(request.getMenuId())) {
                currentPosition = i + 1;
                break;
            }
        }

        if (currentPosition == -1) {
            SecureLogger.logSecurely(log, "warn", "메뉴를 해당 범위에서 찾을 수 없음 - 사용자 ID: {}, 메뉴 ID: {}",
                    currentUser.getId(), request.getMenuId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 해당 범위에서 찾을 수 없습니다");
        }

        // 위치가 같으면 변경하지 않음
        if (currentPosition == request.getNewPosition()) {
            SecureLogger.logSecurely(log, "info", "메뉴 순서 변경 - 동일한 위치 - 사용자 ID: {}, 메뉴 ID: {}, 위치: {}",
                    currentUser.getId(), request.getMenuId(), currentPosition);
            return menus;
        }

        // 순서 변경 로직
        Menu movingMenu = menus.remove(currentPosition - 1);
        menus.add(request.getNewPosition() - 1, movingMenu);

        // 모든 메뉴의 displayOrder 재설정
        List<Menu> updatedMenus = new ArrayList<>();
        for (int i = 0; i < menus.size(); i++) {
            Menu menu = menus.get(i);
            int newDisplayOrder = i + 1;

            if (!menu.getDisplayOrder().equals(newDisplayOrder)) {
                menu.updateDisplayOrder(newDisplayOrder);
                updatedMenus.add(menu);
            }
        }

        if (!updatedMenus.isEmpty()) {
            menuRepository.saveAll(updatedMenus);
        }

        SecureLogger.logSecurely(log, "info", "메뉴 순서 변경 완료 - 사용자 ID: {}, 메뉴 ID: {}, 위치: {} -> {}, 업데이트된 메뉴 수: {}개",
                currentUser.getId(), request.getMenuId(), currentPosition, request.getNewPosition(), updatedMenus.size());

        return menus;
    }

    // ================================
    // Private Helper Methods
    // ================================

    private MenuCategory validateAndGetCategory(Long categoryId, Long storeId) {
        MenuCategory category = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다"));

        if (!category.getStore().getId().equals(storeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 가게의 카테고리입니다");
        }

        if (!category.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "비활성화된 카테고리로는 메뉴를 등록할 수 없습니다: " + category.getName());
        }

        return category;
    }

    private void validateMenuUpdateRequest(MenuUpdateRequest request, Long storeId, Long menuId) {
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String trimmedName = request.getName().trim();

            if (trimmedName.length() > 100) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "메뉴명은 100자를 초과할 수 없습니다");
            }

            // 중복 체크
            List<Menu> existingMenus = menuRepository.findByStoreIdOrderByDisplayOrderAsc(storeId);
            boolean isDuplicate = existingMenus.stream()
                    .anyMatch(menu -> !menu.getId().equals(menuId) &&
                            menu.getName().equals(trimmedName));

            if (isDuplicate) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 메뉴명입니다");
            }
        }

        if (request.getPrice() != null) {
            if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "가격은 0원 이상이어야 합니다");
            }
            if (request.getPrice().compareTo(new BigDecimal("1000000")) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "가격은 100만원을 초과할 수 없습니다");
            }
        }

        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "표시 순서는 0 이상이어야 합니다");
        }
    }

    private void validateMenuDeletion(Menu menu) {
        // 현재 주문 중인 메뉴인지 확인 (실제 주문 시스템이 있다면)
        // TODO: 주문 시스템과 연동하여 현재 주문 중인 메뉴인지 확인

        if (menu.getStatus() == MenuStatus.AVAILABLE) {
            SecureLogger.logSecurely(log, "warn", "판매 중인 메뉴 삭제 - 메뉴 ID: {}", menu.getId());
        }
    }

    private void validateMenuStatusRequest(MenuStatusRequest request, Menu menu) {
        if (request.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "메뉴 상태는 필수입니다");
        }

        if (request.getReason() != null && request.getReason().length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "변경 사유는 200자를 초과할 수 없습니다");
        }
    }

    private void validateStatusTransition(MenuStatus currentStatus, MenuStatus newStatus, Menu menu) {
        // 카테고리가 비활성화된 경우 AVAILABLE로 변경 불가
        if (newStatus == MenuStatus.AVAILABLE &&
                menu.getCategory() != null && !menu.getCategory().getIsActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "비활성화된 카테고리의 메뉴는 판매중으로 변경할 수 없습니다. 먼저 카테고리를 활성화해주세요.");
        }
    }

    /**
     * 보안이 강화된 상태 변경 로그
     */
    private void logStatusChangeSecurely(TempUser currentUser, Menu menu, MenuStatus previousStatus, MenuStatusRequest request) {
        SecureLogger.logSecurely(log, "info", "메뉴 상태 변경 상세:");
        SecureLogger.logSecurely(log, "info", "  - 사용자 ID: {}", currentUser.getId());
        SecureLogger.logSecurely(log, "info", "  - 메뉴 ID: {}", menu.getId());
        SecureLogger.logSecurely(log, "info", "  - 상태 변경: {} -> {}", previousStatus, request.getStatus());
        SecureLogger.logSecurely(log, "info", "  - 일시적 변경: {}", request.getIsTemporary());
        SecureLogger.logSecurely(log, "info", "  - 변경 시간: {}", LocalDateTime.now());

        // 변경 사유는 길이만 로깅 (내용은 민감정보일 수 있음)
        if (request.getReason() != null) {
            SecureLogger.logSecurely(log, "info", "  - 변경 사유 길이: {}자", request.getReason().length());
        }
    }

    private void createMenuOptions(Menu menu, List<MenuOptionCreateRequest> optionRequests) {
        SecureLogger.logSecurely(log, "debug", "메뉴 옵션 생성 시작 - 메뉴 ID: {}, 옵션 그룹 수: {}개",
                menu.getId(), optionRequests.size());

        for (int i = 0; i < optionRequests.size(); i++) {
            MenuOptionCreateRequest optionRequest = optionRequests.get(i);

            try {
                MenuOption menuOption = MenuOption.builder()
                        .menu(menu)
                        .name(optionRequest.getName())
                        .type(optionRequest.getType())
                        .isRequired(optionRequest.getIsRequired() != null ? optionRequest.getIsRequired() : false)
                        .displayOrder(optionRequest.getDisplayOrder() != null ? optionRequest.getDisplayOrder() : i)
                        .build();

                MenuOption savedOption = menuOptionRepository.save(menuOption);

                SecureLogger.logSecurely(log, "debug", "옵션 그룹 생성 완료 - 메뉴 ID: {}, 옵션 그룹 ID: {}, 순서: {}",
                        menu.getId(), savedOption.getId(), i + 1);

                if (optionRequest.getItems() != null && !optionRequest.getItems().isEmpty()) {
                    createMenuOptionItems(savedOption, optionRequest.getItems());
                }

            } catch (Exception e) {
                SecureLogger.logSecurely(log, "error", "옵션 그룹 생성 실패 - 메뉴 ID: {}, 순서: {}, 오류: {}",
                        menu.getId(), i + 1, e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "옵션 그룹 생성 중 오류가 발생했습니다");
            }
        }

        SecureLogger.logSecurely(log, "info", "메뉴 옵션 생성 완료 - 메뉴 ID: {}, 총 옵션 그룹: {}개",
                menu.getId(), optionRequests.size());
    }

    private void createMenuOptionItems(MenuOption option, List<MenuOptionItemCreateRequest> itemRequests) {
        SecureLogger.logSecurely(log, "debug", "옵션 아이템 생성 시작 - 옵션 그룹 ID: {}, 아이템 수: {}개",
                option.getId(), itemRequests.size());

        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < itemRequests.size(); i++) {
            MenuOptionItemCreateRequest itemRequest = itemRequests.get(i);

            try {
                // 옵션 아이템명 검증
                if (itemRequest.getName() == null || itemRequest.getName().trim().isEmpty()) {
                    SecureLogger.logSecurely(log, "warn", "빈 옵션 아이템명 - 옵션 그룹 ID: {}, 순서: {}",
                            option.getId(), i + 1);
                    failCount++;
                    continue;
                }

                // 추가 금액 검증
                BigDecimal additionalPrice = itemRequest.getAdditionalPrice() != null ?
                        itemRequest.getAdditionalPrice() : BigDecimal.ZERO;

                if (additionalPrice.compareTo(BigDecimal.ZERO) < 0) {
                    SecureLogger.logSecurely(log, "warn", "음수 추가 금액 - 옵션 그룹 ID: {}, 아이템: {}",
                            option.getId(), itemRequest.getName());
                    additionalPrice = BigDecimal.ZERO;
                }

                MenuOptionItem optionItem = MenuOptionItem.builder()
                        .option(option)
                        .name(itemRequest.getName().trim())
                        .additionalPrice(additionalPrice)
                        .displayOrder(itemRequest.getDisplayOrder() != null ? itemRequest.getDisplayOrder() : i)
                        .isActive(itemRequest.getIsActive() != null ? itemRequest.getIsActive() : true)
                        .build();

                menuOptionItemRepository.save(optionItem);
                successCount++;

                SecureLogger.logSecurely(log, "debug", "옵션 아이템 생성 완료 - 옵션 그룹 ID: {}, 순서: {}",
                        option.getId(), i + 1);

            } catch (Exception e) {
                SecureLogger.logSecurely(log, "error", "옵션 아이템 생성 실패 - 옵션 그룹 ID: {}, 순서: {}, 오류: {}",
                        option.getId(), i + 1, e.getMessage());
                failCount++;
            }
        }

        SecureLogger.logSecurely(log, "info", "옵션 아이템 생성 결과 - 옵션 그룹 ID: {}, 성공: {}개, 실패: {}개",
                option.getId(), successCount, failCount);

        if (successCount == 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "옵션 아이템 생성 중 오류가 발생했습니다");
        }
    }

    /**
     * 보안 감사를 위한 중요 작업 로깅
     */
    private void logSecurityAudit(TempUser currentUser, String action, String resource, String details) {
        SecureLogger.logSecurely(log, "info", "[SECURITY_AUDIT] 사용자 ID: {}, 작업: {}, 리소스: {}, 상세: {}",
                currentUser.getId(), action, resource, details);
    }

    /**
     * 사용자 활동 패턴 분석을 위한 로깅
     */
    private void logUserActivity(TempUser currentUser, String activity, Map<String, Object> metadata) {
        SecureLogger.logSecurely(log, "info", "[USER_ACTIVITY] 사용자 ID: {}, 활동: {}, 시간: {}",
                currentUser.getId(), activity, LocalDateTime.now());

        if (metadata != null && !metadata.isEmpty()) {
            // 민감정보 제외하고 메타데이터 로깅
            metadata.forEach((key, value) -> {
                if (!isSensitiveField(key)) {
                    SecureLogger.logSecurely(log, "debug", "[USER_ACTIVITY_META] {}: {}", key, value);
                }
            });
        }
    }

    /**
     * 민감한 필드인지 확인
     */
    private boolean isSensitiveField(String fieldName) {
        String[] sensitiveFields = {
                "password", "token", "secret", "key", "email", "phone",
                "address", "business", "card", "account"
        };

        String lowerFieldName = fieldName.toLowerCase();
        for (String sensitiveField : sensitiveFields) {
            if (lowerFieldName.contains(sensitiveField)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 예외 발생 시 보안 로깅
     */
    private void logSecurityException(TempUser currentUser, String operation, Exception e) {
        String errorId = java.util.UUID.randomUUID().toString();

        SecureLogger.logSecurely(log, "error", "[SECURITY_ERROR] 오류 ID: {}, 사용자 ID: {}, 작업: {}, 오류 타입: {}",
                errorId, currentUser.getId(), operation, e.getClass().getSimpleName());

        // 스택 트레이스는 DEBUG 레벨로 (운영에서는 출력되지 않음)
        SecureLogger.logSecurely(log, "debug", "[SECURITY_ERROR_DETAIL] 오류 ID: {}, 상세: {}",
                errorId, e.getMessage());
    }

    /**
     * 성능 모니터링을 위한 실행 시간 로깅
     */
    private void logPerformance(String operation, long startTime, int recordCount) {
        long executionTime = System.currentTimeMillis() - startTime;

        if (executionTime > 1000) { // 1초 이상 걸린 작업만 로깅
            SecureLogger.logSecurely(log, "warn", "[PERFORMANCE] 작업: {}, 실행시간: {}ms, 처리건수: {}건",
                    operation, executionTime, recordCount);
        } else {
            SecureLogger.logSecurely(log, "debug", "[PERFORMANCE] 작업: {}, 실행시간: {}ms, 처리건수: {}건",
                    operation, executionTime, recordCount);
        }
    }

    /**
     * 비즈니스 규칙 위반 시 로깅
     */
    private void logBusinessRuleViolation(TempUser currentUser, String rule, String violation) {
        SecureLogger.logSecurely(log, "warn", "[BUSINESS_RULE_VIOLATION] 사용자 ID: {}, 규칙: {}, 위반사항: {}",
                currentUser.getId(), rule, violation);
    }
}