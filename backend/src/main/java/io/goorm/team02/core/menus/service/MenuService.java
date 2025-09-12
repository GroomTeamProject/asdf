package io.goorm.team02.core.menus.service;

import io.goorm.team02.core.auth.security.SecurityUtils;
import io.goorm.team02.core.menus.controller.dto.categorycreate.CategoryMoveRequest;
import io.goorm.team02.core.menus.controller.dto.categorycreate.CategoryOrderUpdateRequest;
import io.goorm.team02.core.menus.controller.dto.categorycreate.MenuCategoryCreateRequest;
import io.goorm.team02.core.menus.controller.dto.categorycreate.MenuCategoryUpdateRequest;
import io.goorm.team02.core.menus.controller.dto.menucreate.*;
import io.goorm.team02.core.menus.domain.Menu;
import io.goorm.team02.core.menus.domain.MenuCategory;
import io.goorm.team02.core.menus.domain.MenuOption;
import io.goorm.team02.core.menus.domain.MenuOptionItem;
import io.goorm.team02.core.menus.domain.enums.MenuStatus;
import io.goorm.team02.core.menus.domain.enums.OptionType;
import io.goorm.team02.core.menus.repository.MenuCategoryRepository;
import io.goorm.team02.core.menus.repository.MenuOptionItemRepository;
import io.goorm.team02.core.menus.repository.MenuOptionRepository;
import io.goorm.team02.core.menus.repository.MenuRepository;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.repository.StoreRepository;
import io.goorm.team02.core.stores.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuOptionRepository menuOptionRepository;      // 추가
    private final MenuOptionItemRepository menuOptionItemRepository; // 추가
    private final StoreRepository storeRepository;
    //private final UserRepository userRepository;

//    /**
//     * 현재 로그인한 사용자 ID 조회
//     */
//    private Long getCurrentUserId() {
//
//        // 임시로 1L 반환
//        return 1L;
//    }
    /**
     * 메뉴 카테고리 등록
     */
    @Transactional
    public MenuCategory createCategory(MenuCategoryCreateRequest request) {
        log.info("=== 메뉴 카테고리 등록 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);

        // 입력받은 값들 로그 출력
        log.info("입력받은 카테고리 정보:");
        log.info("  - 카테고리명: {}", request.getName());
        log.info("  - 표시순서: {}", request.getDisplayOrder());
        log.info("  - 활성화여부: {}", request.getIsActive());

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 입력값 검증
        validateCategoryCreateRequest(request, store.getId());

        // 중복 카테고리명 체크
        log.info("중복 카테고리명 확인 중...");
        if (menuCategoryRepository.existsByStoreIdAndName(store.getId(), request.getName())) {
            log.warn("이미 존재하는 카테고리명입니다. 가게 ID: {}, 카테고리명: {}", store.getId(), request.getName());
            throw new RuntimeException("이미 존재하는 카테고리명입니다: " + request.getName());
        }
        log.info("중복 카테고리명 확인 완료 - 사용 가능한 카테고리명");

        // 표시 순서 자동 설정 (요청에 없는 경우)
        Integer displayOrder = request.getDisplayOrder();
        if (displayOrder == null || displayOrder == 0) {
            // 가게의 마지막 카테고리 순서 + 1로 설정
            Integer maxOrder = getMaxCategoryOrder(store.getId());
            displayOrder = maxOrder + 1;
            log.info("표시 순서 자동 설정: {}", displayOrder);
        }

        // MenuCategory 엔티티 생성
        log.info("MenuCategory 엔티티 생성 중...");
        MenuCategory category = MenuCategory.builder()
                .store(store)
                .name(request.getName().trim())
                .displayOrder(displayOrder)
                .isActive(request.getIsActive())
                .build();

        log.info("MenuCategory 엔티티 생성 완료");

        // 카테고리 저장
        log.info("메뉴 카테고리 저장 중...");
        MenuCategory savedCategory = menuCategoryRepository.save(category);

        log.info("메뉴 카테고리 등록 완료! 생성된 카테고리 ID: {}, 카테고리명: {}",
                savedCategory.getId(), savedCategory.getName());
        log.info("=== 메뉴 카테고리 등록 종료 ===");

        return savedCategory;
    }

    /**
     * 카테고리 생성 요청 검증
     */
    private void validateCategoryCreateRequest(MenuCategoryCreateRequest request, Long storeId) {
        log.debug("카테고리 생성 요청 유효성 검증 시작");

        // 필수 필드 검증
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리명은 필수입니다");
        }

        if (request.getName().trim().length() > 50) {
            throw new IllegalArgumentException("카테고리명은 50자를 초과할 수 없습니다");
        }

        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new IllegalArgumentException("표시 순서는 0 이상이어야 합니다");
        }

        log.debug("카테고리 생성 요청 유효성 검증 완료");
    }

    /**
     * 가게의 카테고리 최대 순서 조회
     */
    private Integer getMaxCategoryOrder(Long storeId) {
        log.debug("가게의 카테고리 최대 순서 조회 - 가게 ID: {}", storeId);

        List<MenuCategory> categories = menuCategoryRepository.findByStoreIdAndIsActiveTrueOrderByDisplayOrderAsc(storeId);

        if (categories.isEmpty()) {
            log.debug("기존 카테고리가 없음 - 순서 0 반환");
            return 0;
        }

        Integer maxOrder = categories.stream()
                .mapToInt(MenuCategory::getDisplayOrder)
                .max()
                .orElse(0);

        log.debug("최대 순서: {}", maxOrder);
        return maxOrder;
    }



    /**
     * 메뉴 등록
     */
    @Transactional
    public Menu createMenu(MenuCreateRequest request) {
        log.info("=== 메뉴 등록 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);

        // 입력받은 값들 로그 출력
        log.info("입력받은 메뉴 정보:");
        log.info("  - 메뉴명: {}", request.getName());
        log.info("  - 설명: {}", request.getDescription());
        log.info("  - 가격: {}", request.getPrice());
        log.info("  - 카테고리 ID: {}", request.getCategoryId());
        log.info("  - 이미지URL: {}", request.getImageUrl());
        log.info("  - 인기메뉴: {}", request.getIsPopular());
        log.info("  - 추천메뉴: {}", request.getIsRecommended());
        log.info("  - 상태: {}", request.getStatus());
        log.info("  - 표시순서: {}", request.getDisplayOrder());
        if (request.getOptions() != null) {
            log.info("  - 옵션 개수: {}개", request.getOptions().size());
        }

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 카테고리 조회 및 검증
        log.info("카테고리 조회 중... 카테고리 ID: {}", request.getCategoryId());
        MenuCategory category = menuCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    log.error("카테고리를 찾을 수 없습니다. ID: {}", request.getCategoryId());
                    return new RuntimeException("카테고리를 찾을 수 없습니다");
                });

        // 카테고리가 해당 가게의 것인지 확인
        if (!category.getStore().getId().equals(store.getId())) {
            log.error("다른 가게의 카테고리입니다. 카테고리 소유 가게 ID: {}, 현재 가게 ID: {}",
                    category.getStore().getId(), store.getId());
            throw new RuntimeException("다른 가게의 카테고리입니다");
        }
        log.info("카테고리 조회 완료 - 카테고리명: {}", category.getName());

        // 입력값 검증
        validateMenuCreateRequest(request);

        // Menu 엔티티 생성
        log.info("Menu 엔티티 생성 중...");
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

        log.info("Menu 엔티티 생성 완료");

        // 메뉴 저장 (옵션은 아직 저장하지 않음)
        log.info("메뉴 기본 정보 저장 중...");
        Menu savedMenu = menuRepository.save(menu);
        log.info("메뉴 기본 정보 저장 완료 - 메뉴 ID: {}", savedMenu.getId());

        // 메뉴 옵션 처리
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            log.info("메뉴 옵션 처리 시작 - 옵션 그룹 수: {}", request.getOptions().size());
            List<MenuOption> menuOptions = createMenuOptions(savedMenu, request.getOptions());

            // 메뉴에 옵션 설정 (양방향 관계 설정)
            if (savedMenu.getOptions() == null) {
                savedMenu = Menu.builder()
                        .store(savedMenu.getStore())
                        .category(savedMenu.getCategory())
                        .name(savedMenu.getName())
                        .description(savedMenu.getDescription())
                        .price(savedMenu.getPrice())
                        .imageUrl(savedMenu.getImageUrl())
                        .isPopular(savedMenu.getIsPopular())
                        .isRecommended(savedMenu.getIsRecommended())
                        .status(savedMenu.getStatus())
                        .displayOrder(savedMenu.getDisplayOrder())
                        .build();
            }

            log.info("메뉴 옵션 처리 완료 - 생성된 옵션 그룹 수: {}", menuOptions.size());
        }

        log.info("메뉴 등록 완료! 생성된 메뉴 ID: {}, 메뉴명: {}", savedMenu.getId(), savedMenu.getName());
        log.info("=== 메뉴 등록 종료 ===");

        return savedMenu;
    }

    /**
     * 메뉴 옵션들 생성 및 저장
     */
    private List<MenuOption> createMenuOptions(Menu menu, List<MenuOptionCreateRequest> optionRequests) {
        List<MenuOption> menuOptions = new ArrayList<>();

        for (int i = 0; i < optionRequests.size(); i++) {
            MenuOptionCreateRequest optionRequest = optionRequests.get(i);
            log.info("옵션 그룹 {}번 생성 중... 옵션명: {}, 타입: {}",
                    i + 1, optionRequest.getName(), optionRequest.getType());

            // 옵션 그룹 생성
            MenuOption menuOption = MenuOption.builder()
                    .menu(menu)
                    .name(optionRequest.getName())
                    .type(optionRequest.getType())
                    .isRequired(optionRequest.getIsRequired() != null ? optionRequest.getIsRequired() : false)
                    .displayOrder(optionRequest.getDisplayOrder() != null ? optionRequest.getDisplayOrder() : i)
                    .build();

            // 옵션 그룹을 먼저 저장 (ID가 필요하기 때문)
            MenuOption savedOption = menuOptionRepository.save(menuOption);
            log.info("옵션 그룹 저장 완료 - 옵션 ID: {}", savedOption.getId());

            // 옵션 아이템들 생성 및 저장
            if (optionRequest.getItems() != null && !optionRequest.getItems().isEmpty()) {
                log.info("옵션 아이템 생성 중... 아이템 수: {}", optionRequest.getItems().size());
                List<MenuOptionItem> optionItems = createMenuOptionItems(savedOption, optionRequest.getItems());
                log.info("옵션 아이템 생성 완료 - {}개 생성됨", optionItems.size());
            }

            menuOptions.add(savedOption);
            log.info("옵션 그룹 {}번 생성 완료", i + 1);
        }

        return menuOptions;
    }

    /**
     * 메뉴 옵션 아이템들 생성 및 저장
     */
    private List<MenuOptionItem> createMenuOptionItems(MenuOption option, List<MenuOptionItemCreateRequest> itemRequests) {
        List<MenuOptionItem> optionItems = new ArrayList<>();

        for (int i = 0; i < itemRequests.size(); i++) {
            MenuOptionItemCreateRequest itemRequest = itemRequests.get(i);
            log.debug("옵션 아이템 {}번 생성: {}, 추가금액: {}",
                    i + 1, itemRequest.getName(), itemRequest.getAdditionalPrice());

            MenuOptionItem optionItem = MenuOptionItem.builder()
                    .option(option)
                    .name(itemRequest.getName())
                    .additionalPrice(itemRequest.getAdditionalPrice() != null ?
                            itemRequest.getAdditionalPrice() : BigDecimal.ZERO)
                    .displayOrder(itemRequest.getDisplayOrder() != null ? itemRequest.getDisplayOrder() : i)
                    .isActive(itemRequest.getIsActive() != null ? itemRequest.getIsActive() : true)
                    .build();

            // 옵션 아이템 저장
            MenuOptionItem savedItem = menuOptionItemRepository.save(optionItem);
            optionItems.add(savedItem);

            log.debug("옵션 아이템 저장 완료 - 아이템 ID: {}", savedItem.getId());
        }

        return optionItems;
    }
    /**
     * 메뉴 생성 요청 검증
     */
    private void validateMenuCreateRequest(MenuCreateRequest request) {
        log.debug("메뉴 생성 요청 유효성 검증 시작");

        // 필수 필드 검증
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴명은 필수입니다");
        }

        if (request.getPrice() == null) {
            throw new IllegalArgumentException("가격은 필수입니다");
        }

        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다");
        }

        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("카테고리는 필수입니다");
        }

        // 옵션 검증
        if (request.getOptions() != null) {
            for (MenuOptionCreateRequest option : request.getOptions()) {
                validateMenuOption(option);
            }
        }

        log.debug("메뉴 생성 요청 유효성 검증 완료");
    }

    /**
     * 메뉴 옵션 검증
     */
    private void validateMenuOption(MenuOptionCreateRequest option) {
        if (option.getName() == null || option.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("옵션명은 필수입니다");
        }

        if (option.getType() == null) {
            throw new IllegalArgumentException("옵션 타입은 필수입니다");
        }

        if (option.getItems() == null || option.getItems().isEmpty()) {
            throw new IllegalArgumentException("옵션 아이템은 최소 1개 이상이어야 합니다");
        }

        // 옵션 아이템 검증
        for (MenuOptionItemCreateRequest item : option.getItems()) {
            validateMenuOptionItem(item);
        }
    }

    /**
     * 메뉴 옵션 아이템 검증
     */
    private void validateMenuOptionItem(MenuOptionItemCreateRequest item) {
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("옵션 아이템명은 필수입니다");
        }

        if (item.getAdditionalPrice() != null &&
                item.getAdditionalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("추가 금액은 0원 이상이어야 합니다");
        }
    }

    /**
     * 내 가게 정보 조회
     */
    private Store getMyStore() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.debug("내 가게 정보 조회 - 사용자 ID: {}", currentUserId);

        return storeRepository.findByOwnerIdAndIsActiveTrue(currentUserId)
                .orElseThrow(() -> {
                    log.warn("등록된 가게가 없습니다. 사용자 ID: {}", currentUserId);
                    return new RuntimeException("등록된 가게가 없습니다");
                });
    }

    /**
     * 메뉴 카테고리 목록 조회
     */

    public List<MenuCategory> getMenuCategories() {
        log.info("=== 메뉴 카테고리 목록 조회 시작 ===");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Store store = getMyStore();

        log.info("메뉴 카테고리 전체 목록 조회 중... (비활성화 포함)");
        List<MenuCategory> categories = menuCategoryRepository.findByStoreIdOrderByDisplayOrderAsc(store.getId());

        log.info("메뉴 카테고리 전체 조회 완료 - 총 {}개 카테고리", categories.size());

        // 활성/비활성 상태별 통계
        long activeCount = categories.stream().filter(c -> c.getIsActive()).count();
        long inactiveCount = categories.size() - activeCount;
        log.info("  - 활성화: {}개, 비활성화: {}개", activeCount, inactiveCount);

        log.info("=== 메뉴 카테고리 전체 목록 조회 종료 ===");
        return categories;
    }





    /**
     * 메뉴 카테고리 수정
     */
    @Transactional
    public MenuCategory updateCategory(Long categoryId, MenuCategoryUpdateRequest request) {
        log.info("=== 메뉴 카테고리 수정 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("수정할 카테고리 ID: {}", categoryId);

        // 입력받은 값들 로그 출력
        log.info("수정 요청 정보:");
        log.info("  - 카테고리명: {}", request.getName());
        log.info("  - 표시순서: {}", request.getDisplayOrder());
        log.info("  - 활성화여부: {}", request.getIsActive());
        log.info("  - 강제비활성화: {}", request.getForceDeactivate());

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 카테고리 조회 및 권한 확인
        log.info("카테고리 조회 중... 카테고리 ID: {}", categoryId);
        MenuCategory category = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("카테고리를 찾을 수 없습니다. ID: {}", categoryId);
                    return new RuntimeException("카테고리를 찾을 수 없습니다");
                });

        // 카테고리가 해당 가게의 것인지 확인
        if (!category.getStore().getId().equals(store.getId())) {
            log.error("다른 가게의 카테고리입니다. 카테고리 소유 가게 ID: {}, 현재 가게 ID: {}",
                    category.getStore().getId(), store.getId());
            throw new RuntimeException("다른 가게의 카테고리입니다");
        }

        log.info("카테고리 조회 완료 - 기존 카테고리명: {}", category.getName());

        // 입력값 검증
        validateCategoryUpdateRequest(request, store.getId(), categoryId);

        // 변경사항 적용
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

            // 비활성화할 때 처리
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

        // 카테고리 저장
        log.info("메뉴 카테고리 저장 중...");
        MenuCategory savedCategory = menuCategoryRepository.save(category);

        log.info("메뉴 카테고리 수정 완료! 카테고리 ID: {}, 카테고리명: {}",
                savedCategory.getId(), savedCategory.getName());
        log.info("=== 메뉴 카테고리 수정 종료 ===");

        return savedCategory;
    }

    /**
     * 카테고리 수정 요청 검증
     */
    private void validateCategoryUpdateRequest(MenuCategoryUpdateRequest request, Long storeId, Long categoryId) {
        log.debug("카테고리 수정 요청 유효성 검증 시작");

        // 카테고리명 중복 체크 (다른 카테고리와)
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String trimmedName = request.getName().trim();

            if (trimmedName.length() > 50) {
                throw new IllegalArgumentException("카테고리명은 50자를 초과할 수 없습니다");
            }

            // 현재 카테고리가 아닌 다른 카테고리에서 같은 이름이 사용되는지 확인
            List<MenuCategory> existingCategories = menuCategoryRepository.findByStoreIdOrderByDisplayOrderAsc(storeId);
            boolean isDuplicate = existingCategories.stream()
                    .anyMatch(cat -> !cat.getId().equals(categoryId) &&
                            cat.getName().equals(trimmedName) &&
                            cat.getIsActive());

            if (isDuplicate) {
                throw new IllegalArgumentException("이미 존재하는 카테고리명입니다: " + trimmedName);
            }
        }

        // 표시 순서 검증
        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new IllegalArgumentException("표시 순서는 0 이상이어야 합니다");
        }

        log.debug("카테고리 수정 요청 유효성 검증 완료");
    }

    /**
     * 카테고리 비활성화 처리
     */
    private void handleCategoryDeactivation(MenuCategory category, Boolean forceDeactivate) {
        log.info("카테고리 비활성화 처리 시작...");

        if (category.getMenus() != null && !category.getMenus().isEmpty()) {
            long activeMenuCount = category.getMenus().stream()
                    .filter(menu -> menu.getStatus() == MenuStatus.AVAILABLE)
                    .count();

            long soldOutMenuCount = category.getMenus().stream()
                    .filter(menu -> menu.getStatus() == MenuStatus.SOLD_OUT)
                    .count();

            if (activeMenuCount > 0 || soldOutMenuCount > 0) {
                log.warn("카테고리에 활성/품절 메뉴가 {}개 있습니다 (판매중: {}개, 품절: {}개)",
                        activeMenuCount + soldOutMenuCount, activeMenuCount, soldOutMenuCount);

                // 강제 비활성화 옵션이 true인 경우 메뉴들을 자동으로 숨김 처리
                if (forceDeactivate != null && forceDeactivate) {
                    log.info("강제 비활성화 옵션으로 메뉴들을 숨김 처리합니다");

                    List<Menu> menusToUpdate = new ArrayList<>();
                    category.getMenus().forEach(menu -> {
                        if (menu.getStatus() == MenuStatus.AVAILABLE || menu.getStatus() == MenuStatus.SOLD_OUT) {
                            log.info("메뉴 숨김 처리: {} (기존 상태: {} -> HIDDEN)", menu.getName(), menu.getStatus());
                            menu.updateStatus(MenuStatus.HIDDEN);
                            menusToUpdate.add(menu);
                        }
                    });

                    // 변경된 메뉴들 저장
                    if (!menusToUpdate.isEmpty()) {
                        menuRepository.saveAll(menusToUpdate);
                        log.info("총 {}개 메뉴가 숨김 처리되었습니다", menusToUpdate.size());
                    }

                } else {
                    // 강제 옵션이 없으면 예외 발생
                    throw new IllegalArgumentException(
                            String.format("카테고리에 판매중인 메뉴가 %d개 있습니다 (판매중: %d개, 품절: %d개). " +
                                            "강제 비활성화를 원하면 forceDeactivate=true 옵션을 사용하거나 " +
                                            "메뉴를 먼저 숨김 처리해주세요.",
                                    activeMenuCount + soldOutMenuCount, activeMenuCount, soldOutMenuCount)
                    );
                }
            }
        }

        log.info("카테고리 비활성화 처리 완료");
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        log.info("=== 메뉴 카테고리 삭제 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("삭제할 카테고리 ID: {}", categoryId);

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 카테고리 조회 및 권한 확인
        log.info("카테고리 조회 중... 카테고리 ID: {}", categoryId);
        MenuCategory category = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("카테고리를 찾을 수 없습니다. ID: {}", categoryId);
                    return new RuntimeException("카테고리를 찾을 수 없습니다");
                });

        // 카테고리가 해당 가게의 것인지 확인
        if (!category.getStore().getId().equals(store.getId())) {
            log.error("다른 가게의 카테고리입니다. 카테고리 소유 가게 ID: {}, 현재 가게 ID: {}",
                    category.getStore().getId(), store.getId());
            throw new RuntimeException("다른 가게의 카테고리입니다");
        }

        log.info("카테고리 조회 완료 - 카테고리명: {}, 활성상태: {}", category.getName(), category.getIsActive());

        // 삭제 전 검증
        validateCategoryDeletion(category);

        // 카테고리에 속한 메뉴들 확인 및 처리
        handleMenusBeforeCategoryDeletion(category);

        // 카테고리 삭제
        log.info("카테고리 삭제 실행 중...");
        menuCategoryRepository.delete(category);

        log.info("메뉴 카테고리 삭제 완료! 삭제된 카테고리: {} (ID: {})", category.getName(), categoryId);
        log.info("=== 메뉴 카테고리 삭제 종료 ===");
    }

    /**
     * 카테고리 삭제 전 검증
     */
    private void validateCategoryDeletion(MenuCategory category) {
        log.info("카테고리 삭제 가능 여부 검증 중...");

        // 1. 카테고리에 메뉴가 있는지 확인
        if (category.getMenus() != null && !category.getMenus().isEmpty()) {
            int totalMenus = category.getMenus().size();
            long activeMenus = category.getMenus().stream()
                    .filter(menu -> menu.getStatus() == MenuStatus.AVAILABLE)
                    .count();
            long hiddenMenus = category.getMenus().stream()
                    .filter(menu -> menu.getStatus() == MenuStatus.HIDDEN)
                    .count();
            long soldOutMenus = category.getMenus().stream()
                    .filter(menu -> menu.getStatus() == MenuStatus.SOLD_OUT)
                    .count();

            log.warn("카테고리에 메뉴가 {}개 존재합니다", totalMenus);
            log.warn("  - 판매중: {}개, 품절: {}개, 숨김: {}개", activeMenus, soldOutMenus, hiddenMenus);

            // 활성 메뉴나 품절 메뉴가 있으면 삭제 불가
            if (activeMenus > 0 || soldOutMenus > 0) {
                throw new IllegalArgumentException(
                        String.format("카테고리에 메뉴가 %d개 있습니다 (판매중: %d개, 품절: %d개). " +
                                        "메뉴를 모두 숨김 처리하거나 다른 카테고리로 이동한 후 삭제해주세요.",
                                activeMenus + soldOutMenus, activeMenus, soldOutMenus)
                );
            }

            // 숨김 메뉴만 있는 경우 경고 로그만 출력하고 진행
            if (hiddenMenus > 0) {
                log.warn("숨김 처리된 메뉴 {}개가 함께 삭제됩니다", hiddenMenus);
            }
        }

        // 2. 마지막 남은 카테고리인지 확인
        List<MenuCategory> activeCategories = menuCategoryRepository
                .findByStoreIdAndIsActiveTrueOrderByDisplayOrderAsc(category.getStore().getId());

        if (activeCategories.size() == 1 && activeCategories.get(0).getId().equals(category.getId())) {
            log.warn("마지막 남은 활성 카테고리입니다");
            throw new IllegalArgumentException("최소 1개 이상의 카테고리가 있어야 합니다. 다른 카테고리를 먼저 생성해주세요.");
        }

        log.info("카테고리 삭제 가능 여부 검증 완료");
    }

    /**
     * 카테고리 삭제 전 메뉴들 처리
     */
    private void handleMenusBeforeCategoryDeletion(MenuCategory category) {
        if (category.getMenus() != null && !category.getMenus().isEmpty()) {
            log.info("카테고리에 속한 메뉴들 처리 중...");

            // 숨김 처리된 메뉴들과 관련 옵션들을 모두 삭제
            for (Menu menu : category.getMenus()) {
                log.info("메뉴 삭제 처리: {} (상태: {})", menu.getName(), menu.getStatus());

                // 메뉴 옵션들 삭제
                if (menu.getOptions() != null && !menu.getOptions().isEmpty()) {
                    log.info("메뉴 옵션 삭제 중... 옵션 그룹 수: {}", menu.getOptions().size());

                    for (MenuOption option : menu.getOptions()) {
                        // 옵션 아이템들 삭제
                        if (option.getItems() != null && !option.getItems().isEmpty()) {
                            log.debug("옵션 아이템 삭제: {} ({}개)", option.getName(), option.getItems().size());
                            menuOptionItemRepository.deleteByOptionId(option.getId());
                        }
                    }

                    // 옵션 그룹 삭제
                    menuOptionRepository.deleteByMenuId(menu.getId());
                }
            }

            log.info("메뉴들 처리 완료");
        }
    }

    /**
     * 카테고리 삭제 (소프트 삭제 버전) - 선택사항
     */
    @Transactional
    public void softDeleteCategory(Long categoryId) {
        log.info("=== 메뉴 카테고리 소프트 삭제 시작 ===");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Store store = getMyStore();

        MenuCategory category = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다"));

        if (!category.getStore().getId().equals(store.getId())) {
            throw new RuntimeException("다른 가게의 카테고리입니다");
        }

        log.info("카테고리 비활성화 처리: {}", category.getName());

        // 비활성화로 처리 (실제 삭제하지 않음)
        category.updateIsActive(false);
        menuCategoryRepository.save(category);

        log.info("메뉴 카테고리 소프트 삭제 완료! 카테고리: {} (ID: {})", category.getName(), categoryId);
        log.info("=== 메뉴 카테고리 소프트 삭제 종료 ===");
    }

    /**
     * 카테고리 순서 변경 (드래그 앤 드롭 방식)
     */
    @Transactional
    public List<MenuCategory> updateCategoryOrder(CategoryMoveRequest request) {
        log.info("=== 카테고리 순서 변경 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 이동할 카테고리 조회 및 권한 확인
        log.info("이동할 카테고리 조회 중... 카테고리 ID: {}", request.getCategoryId());
        MenuCategory targetCategory = menuCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    log.error("카테고리를 찾을 수 없습니다. ID: {}", request.getCategoryId());
                    return new IllegalArgumentException("카테고리를 찾을 수 없습니다");
                });

        // 카테고리가 해당 가게의 것인지 확인
        if (!targetCategory.getStore().getId().equals(store.getId())) {
            log.error("다른 가게의 카테고리입니다. 카테고리 소유 가게 ID: {}, 현재 가게 ID: {}",
                    targetCategory.getStore().getId(), store.getId());
            throw new IllegalArgumentException("다른 가게의 카테고리입니다");
        }

        // 현재 가게의 모든 활성 카테고리를 순서대로 조회
        List<MenuCategory> allCategories = menuCategoryRepository.findByStoreIdAndIsActiveTrueOrderByDisplayOrderAsc(store.getId());

        if (allCategories.isEmpty()) {
            log.warn("활성 카테고리가 없습니다");
            throw new IllegalArgumentException("활성 카테고리가 없습니다");
        }

        // 새로운 위치 유효성 검증
        if (request.getNewPosition() < 1 || request.getNewPosition() > allCategories.size()) {
            log.error("잘못된 위치입니다. 요청 위치: {}, 총 카테고리 수: {}", request.getNewPosition(), allCategories.size());
            throw new IllegalArgumentException(
                    String.format("위치는 1부터 %d 사이여야 합니다", allCategories.size())
            );
        }

        // 현재 위치 찾기
        int currentPosition = -1;
        for (int i = 0; i < allCategories.size(); i++) {
            if (allCategories.get(i).getId().equals(request.getCategoryId())) {
                currentPosition = i + 1; // 1부터 시작
                break;
            }
        }

        if (currentPosition == -1) {
            log.error("카테고리를 목록에서 찾을 수 없습니다. ID: {}", request.getCategoryId());
            throw new IllegalArgumentException("카테고리를 목록에서 찾을 수 없습니다");
        }

        log.info("카테고리 이동: {} (ID: {}) - 위치 {} -> {}",
                targetCategory.getName(), targetCategory.getId(),
                currentPosition, request.getNewPosition());

        // 위치가 같으면 변경하지 않음
        if (currentPosition == request.getNewPosition()) {
            log.info("이미 같은 위치입니다. 변경하지 않습니다.");
            return allCategories;
        }

        // 카테고리 리스트에서 이동할 카테고리 제거
        MenuCategory movingCategory = allCategories.remove(currentPosition - 1);

        // 새로운 위치에 삽입 (인덱스는 0부터 시작하므로 -1)
        allCategories.add(request.getNewPosition() - 1, movingCategory);

        // 모든 카테고리의 displayOrder를 새로 설정
        List<MenuCategory> updatedCategories = new ArrayList<>();
        for (int i = 0; i < allCategories.size(); i++) {
            MenuCategory category = allCategories.get(i);
            int newDisplayOrder = i + 1;

            if (!category.getDisplayOrder().equals(newDisplayOrder)) {
                log.info("카테고리 순서 업데이트: {} (ID: {}) - {} -> {}",
                        category.getName(), category.getId(),
                        category.getDisplayOrder(), newDisplayOrder);
                category.updateDisplayOrder(newDisplayOrder);
                updatedCategories.add(category);
            }
        }

        // 변경된 카테고리들만 저장
        if (!updatedCategories.isEmpty()) {
            menuCategoryRepository.saveAll(updatedCategories);
            log.info("총 {}개 카테고리 순서가 업데이트되었습니다", updatedCategories.size());
        }

        log.info("=== 카테고리 순서 변경 완료 ===");

        // 최종 결과를 순서대로 반환
        return menuCategoryRepository.findByStoreIdAndIsActiveTrueOrderByDisplayOrderAsc(store.getId());
    }

    /**
     * 카테고리 순서 변경 요청 검증
     */
    private void validateCategoryOrderRequest(CategoryOrderUpdateRequest request, Long storeId) {
        log.debug("카테고리 순서 변경 요청 유효성 검증 시작");

        if (request.getCategoryOrders() == null || request.getCategoryOrders().isEmpty()) {
            throw new IllegalArgumentException("카테고리 순서 목록은 필수입니다");
        }

        // 중복된 카테고리 ID 확인
        List<Long> categoryIds = request.getCategoryOrders().stream()
                .map(CategoryOrderUpdateRequest.CategoryOrderItem::getCategoryId)
                .toList();

        long uniqueCount = categoryIds.stream().distinct().count();
        if (uniqueCount != categoryIds.size()) {
            throw new IllegalArgumentException("중복된 카테고리 ID가 있습니다");
        }

        // 중복된 순서 확인
        List<Integer> orders = request.getCategoryOrders().stream()
                .map(CategoryOrderUpdateRequest.CategoryOrderItem::getDisplayOrder)
                .toList();

        long uniqueOrderCount = orders.stream().distinct().count();
        if (uniqueOrderCount != orders.size()) {
            throw new IllegalArgumentException("중복된 표시 순서가 있습니다");
        }

        // 순서 값 유효성 검증
        for (CategoryOrderUpdateRequest.CategoryOrderItem item : request.getCategoryOrders()) {
            if (item.getCategoryId() == null) {
                throw new IllegalArgumentException("카테고리 ID는 필수입니다");
            }
            if (item.getDisplayOrder() == null || item.getDisplayOrder() < 0) {
                throw new IllegalArgumentException("표시 순서는 0 이상이어야 합니다");
            }
        }

        log.debug("카테고리 순서 변경 요청 유효성 검증 완료");
    }

    /**
     * 메뉴 목록 조회 (카테고리별 필터링 가능)
     */
    public List<Menu> getMenus(Long categoryId) {
        log.info("=== 메뉴 목록 조회 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("카테고리 ID 필터: {}", categoryId);

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        List<Menu> menus;

        if (categoryId != null) {
            // 특정 카테고리의 메뉴 조회
            log.info("특정 카테고리의 메뉴 조회 중... 카테고리 ID: {}", categoryId);

            // 카테고리 존재 여부 및 권한 확인
            MenuCategory category = menuCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> {
                        log.error("카테고리를 찾을 수 없습니다. ID: {}", categoryId);
                        return new RuntimeException("카테고리를 찾을 수 없습니다");
                    });

            // 카테고리가 해당 가게의 것인지 확인
            if (!category.getStore().getId().equals(store.getId())) {
                log.error("다른 가게의 카테고리입니다. 카테고리 소유 가게 ID: {}, 현재 가게 ID: {}",
                        category.getStore().getId(), store.getId());
                throw new RuntimeException("다른 가게의 카테고리입니다");
            }

            log.info("카테고리 확인 완료 - 카테고리명: {}, 활성상태: {}", category.getName(), category.getIsActive());

            menus = menuRepository.findByStoreIdAndCategoryIdOrderByDisplayOrderAsc(store.getId(), categoryId);

            log.info("특정 카테고리 메뉴 조회 완료 - 카테고리: {}, 메뉴 수: {}개", category.getName(), menus.size());

        } else {
            // 전체 메뉴 조회
            log.info("전체 메뉴 조회 중...");
            menus = menuRepository.findByStoreIdOrderByDisplayOrderAsc(store.getId());
            log.info("전체 메뉴 조회 완료 - 총 메뉴 수: {}개", menus.size());
        }

        // 메뉴별 상태 통계
        if (!menus.isEmpty()) {
            long availableCount = menus.stream().filter(m -> m.getStatus() == MenuStatus.AVAILABLE).count();
            long soldOutCount = menus.stream().filter(m -> m.getStatus() == MenuStatus.SOLD_OUT).count();
            long hiddenCount = menus.stream().filter(m -> m.getStatus() == MenuStatus.HIDDEN).count();

            log.info("메뉴 상태별 통계 - 판매중: {}개, 품절: {}개, 숨김: {}개",
                    availableCount, soldOutCount, hiddenCount);
        }

        log.info("=== 메뉴 목록 조회 종료 ===");
        return menus;
    }

    /**
     * 메뉴 상세 조회
     */
    public Menu getMenu(Long menuId) {
        log.info("=== 메뉴 상세 조회 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("조회할 메뉴 ID: {}", menuId);

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}, 상태: {}", menu.getName(), menu.getStatus());

        // 메뉴 상세 정보 로깅
        logMenuDetails(menu);

        log.info("=== 메뉴 상세 조회 종료 ===");
        return menu;
    }

    /**
     * 메뉴 상세 정보 로깅
     */
    private void logMenuDetails(Menu menu) {
        log.info("메뉴 상세 정보:");
        log.info("  - ID: {}", menu.getId());
        log.info("  - 이름: {}", menu.getName());
        log.info("  - 설명: {}", menu.getDescription());
        log.info("  - 가격: {}원", menu.getPrice());
        log.info("  - 카테고리: {} (ID: {})",
                menu.getCategory() != null ? menu.getCategory().getName() : "없음",
                menu.getCategory() != null ? menu.getCategory().getId() : null);
        log.info("  - 상태: {}", menu.getStatus());
        log.info("  - 인기메뉴: {}", menu.getIsPopular());
        log.info("  - 추천메뉴: {}", menu.getIsRecommended());
        log.info("  - 표시순서: {}", menu.getDisplayOrder());
        log.info("  - 이미지: {}", menu.getImageUrl() != null ? "있음" : "없음");

        // 옵션 정보
        if (menu.getOptions() != null && !menu.getOptions().isEmpty()) {
            log.info("  - 옵션 그룹 수: {}개", menu.getOptions().size());

            long requiredOptions = menu.getOptions().stream()
                    .filter(option -> option.getIsRequired())
                    .count();
            log.info("  - 필수 옵션 그룹: {}개", requiredOptions);

            // 각 옵션 그룹의 상세 정보
            for (int i = 0; i < menu.getOptions().size(); i++) {
                var option = menu.getOptions().get(i);
                int itemCount = option.getItems() != null ? option.getItems().size() : 0;
                log.info("    {}. {} ({}) - 아이템 {}개, 필수: {}",
                        i + 1, option.getName(), option.getType(),
                        itemCount, option.getIsRequired());
            }
        } else {
            log.info("  - 옵션: 없음");
        }

        log.info("  - 생성일: {}", menu.getCreatedAt());
        log.info("  - 수정일: {}", menu.getUpdatedAt());
    }

    /**
     * 메뉴 수정
     */
    @Transactional
    public Menu updateMenu(Long menuId, MenuUpdateRequest request) {
        log.info("=== 메뉴 수정 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("수정할 메뉴 ID: {}", menuId);

        // 입력받은 값들 로그 출력
        log.info("수정 요청 정보:");
        log.info("  - 메뉴명: {}", request.getName());
        log.info("  - 설명: {}", request.getDescription());
        log.info("  - 가격: {}", request.getPrice());
        log.info("  - 카테고리 ID: {}", request.getCategoryId());
        log.info("  - 이미지URL: {}", request.getImageUrl());
        log.info("  - 인기메뉴: {}", request.getIsPopular());
        log.info("  - 추천메뉴: {}", request.getIsRecommended());
        log.info("  - 상태: {}", request.getStatus());
        log.info("  - 표시순서: {}", request.getDisplayOrder());
        log.info("  - 이미지삭제: {}", request.getRemoveImage());

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 기존 메뉴명: {}", menu.getName());

        // 입력값 검증
        validateMenuUpdateRequest(request, store.getId(), menuId);

        // 카테고리 변경 처리
        MenuCategory newCategory = null;
        if (request.getCategoryId() != null) {
            newCategory = validateAndGetCategory(request.getCategoryId(), store.getId());
        }

        // 변경사항 적용
        boolean hasChanges = false;

        // 메뉴명 변경
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String newName = request.getName().trim();
            if (!newName.equals(menu.getName())) {
                log.info("메뉴명 변경: {} -> {}", menu.getName(), newName);
                menu.updateName(newName);
                hasChanges = true;
            }
        }

        // 설명 변경
        if (request.getDescription() != null) {
            String currentDescription = menu.getDescription() != null ? menu.getDescription() : "";
            if (!request.getDescription().equals(currentDescription)) {
                log.info("설명 변경: {} -> {}", currentDescription, request.getDescription());
                menu.updateDescription(request.getDescription());
                hasChanges = true;
            }
        }

        // 가격 변경
        if (request.getPrice() != null && request.getPrice().compareTo(menu.getPrice()) != 0) {
            log.info("가격 변경: {} -> {}", menu.getPrice(), request.getPrice());
            menu.updatePrice(request.getPrice());
            hasChanges = true;
        }

        // 카테고리 변경
        if (newCategory != null && !newCategory.getId().equals(menu.getCategory().getId())) {
            log.info("카테고리 변경: {} (ID: {}) -> {} (ID: {})",
                    menu.getCategory().getName(), menu.getCategory().getId(),
                    newCategory.getName(), newCategory.getId());
            menu.updateCategory(newCategory);
            hasChanges = true;
        }

        // 이미지 처리
        if (request.getRemoveImage() != null && request.getRemoveImage()) {
            if (menu.getImageUrl() != null) {
                log.info("이미지 삭제: {}", menu.getImageUrl());
                menu.removeImage();
                hasChanges = true;
            }
        } else if (request.getImageUrl() != null) {
            String currentImageUrl = menu.getImageUrl() != null ? menu.getImageUrl() : "";
            if (!request.getImageUrl().equals(currentImageUrl)) {
                log.info("이미지 URL 변경: {} -> {}", currentImageUrl, request.getImageUrl());
                menu.updateImageUrl(request.getImageUrl());
                hasChanges = true;
            }
        }

        // 인기메뉴 여부 변경
        if (request.getIsPopular() != null && !request.getIsPopular().equals(menu.getIsPopular())) {
            log.info("인기메뉴 여부 변경: {} -> {}", menu.getIsPopular(), request.getIsPopular());
            menu.updateIsPopular(request.getIsPopular());
            hasChanges = true;
        }

        // 추천메뉴 여부 변경
        if (request.getIsRecommended() != null && !request.getIsRecommended().equals(menu.getIsRecommended())) {
            log.info("추천메뉴 여부 변경: {} -> {}", menu.getIsRecommended(), request.getIsRecommended());
            menu.updateIsRecommended(request.getIsRecommended());
            hasChanges = true;
        }

        // 상태 변경
        if (request.getStatus() != null && !request.getStatus().equals(menu.getStatus())) {
            log.info("상태 변경: {} -> {}", menu.getStatus(), request.getStatus());
            menu.updateStatus(request.getStatus());
            hasChanges = true;
        }

        // 표시 순서 변경
        if (request.getDisplayOrder() != null && !request.getDisplayOrder().equals(menu.getDisplayOrder())) {
            log.info("표시 순서 변경: {} -> {}", menu.getDisplayOrder(), request.getDisplayOrder());
            menu.updateDisplayOrder(request.getDisplayOrder());
            hasChanges = true;
        }

        if (!hasChanges) {
            log.info("변경된 정보가 없습니다.");
            return menu;
        }

        // 메뉴 저장
        log.info("메뉴 저장 중...");
        Menu savedMenu = menuRepository.save(menu);

        log.info("메뉴 수정 완료! 메뉴 ID: {}, 메뉴명: {}", savedMenu.getId(), savedMenu.getName());
        log.info("=== 메뉴 수정 종료 ===");

        return savedMenu;
    }

    /**
     * 메뉴 수정 요청 검증
     */
    private void validateMenuUpdateRequest(MenuUpdateRequest request, Long storeId, Long menuId) {
        log.debug("메뉴 수정 요청 유효성 검증 시작");

        // 메뉴명 중복 체크 (다른 메뉴와)
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String trimmedName = request.getName().trim();

            if (trimmedName.length() > 100) {
                throw new IllegalArgumentException("메뉴명은 100자를 초과할 수 없습니다");
            }

            // 현재 메뉴가 아닌 다른 메뉴에서 같은 이름이 사용되는지 확인
            List<Menu> existingMenus = menuRepository.findByStoreIdOrderByDisplayOrderAsc(storeId);
            boolean isDuplicate = existingMenus.stream()
                    .anyMatch(menu -> !menu.getId().equals(menuId) &&
                            menu.getName().equals(trimmedName));

            if (isDuplicate) {
                throw new IllegalArgumentException("이미 존재하는 메뉴명입니다: " + trimmedName);
            }
        }

        // 가격 검증
        if (request.getPrice() != null) {
            if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("가격은 0원 이상이어야 합니다");
            }
            if (request.getPrice().compareTo(new BigDecimal("1000000")) > 0) {
                throw new IllegalArgumentException("가격은 100만원을 초과할 수 없습니다");
            }
        }

        // 표시 순서 검증
        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new IllegalArgumentException("표시 순서는 0 이상이어야 합니다");
        }

        log.debug("메뉴 수정 요청 유효성 검증 완료");
    }

    /**
     * 카테고리 유효성 검증 및 조회
     */
    private MenuCategory validateAndGetCategory(Long categoryId, Long storeId) {
        log.info("카테고리 유효성 검증 중... 카테고리 ID: {}", categoryId);

        MenuCategory category = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("카테고리를 찾을 수 없습니다. ID: {}", categoryId);
                    return new IllegalArgumentException("카테고리를 찾을 수 없습니다");
                });

        // 카테고리가 해당 가게의 것인지 확인
        if (!category.getStore().getId().equals(storeId)) {
            log.error("다른 가게의 카테고리입니다. 카테고리 소유 가게 ID: {}, 현재 가게 ID: {}",
                    category.getStore().getId(), storeId);
            throw new IllegalArgumentException("다른 가게의 카테고리입니다");
        }

        // 카테고리가 활성 상태인지 확인
        if (!category.getIsActive()) {
            log.warn("비활성화된 카테고리입니다. 카테고리: {}", category.getName());
            throw new IllegalArgumentException("비활성화된 카테고리로는 이동할 수 없습니다: " + category.getName());
        }

        log.info("카테고리 검증 완료 - 카테고리명: {}", category.getName());
        return category;
    }

    /**
     * 메뉴 삭제
     */
    @Transactional
    public void deleteMenu(Long menuId) {
        log.info("=== 메뉴 삭제 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("삭제할 메뉴 ID: {}", menuId);

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}, 상태: {}, 카테고리: {}",
                menu.getName(), menu.getStatus(),
                menu.getCategory() != null ? menu.getCategory().getName() : "없음");

        // 삭제 전 검증
        validateMenuDeletion(menu);

        // 메뉴에 속한 옵션들 처리
        handleMenuOptionsBeforeDeletion(menu);

        // 메뉴 삭제
        log.info("메뉴 삭제 실행 중...");
        menuRepository.delete(menu);

        log.info("메뉴 삭제 완료! 삭제된 메뉴: {} (ID: {})", menu.getName(), menuId);
        log.info("=== 메뉴 삭제 종료 ===");
    }

    /**
     * 메뉴 삭제 전 검증
     */
    private void validateMenuDeletion(Menu menu) {
        log.info("메뉴 삭제 가능 여부 검증 중...");

        // 1. 현재 주문 중인 메뉴인지 확인 (실제 주문 시스템이 있다면)
        // TODO: 주문 시스템과 연동하여 현재 주문 중인 메뉴인지 확인
        // if (orderService.hasActiveOrders(menu.getId())) {
        //     throw new IllegalArgumentException("현재 주문 중인 메뉴는 삭제할 수 없습니다");
        // }

        // 2. 메뉴 상태에 따른 삭제 정책
        if (menu.getStatus() == MenuStatus.AVAILABLE) {
            log.warn("판매 중인 메뉴를 삭제합니다: {}", menu.getName());
            // 필요에 따라 경고만 하거나 삭제를 막을 수 있음
            // throw new IllegalArgumentException("판매 중인 메뉴는 먼저 숨김 처리 후 삭제해주세요");
        }

        // 3. 인기/추천 메뉴 삭제 시 경고
        if (menu.getIsPopular() || menu.getIsRecommended()) {
            log.warn("인기/추천 메뉴를 삭제합니다: {} (인기: {}, 추천: {})",
                    menu.getName(), menu.getIsPopular(), menu.getIsRecommended());
        }

        log.info("메뉴 삭제 가능 여부 검증 완료");
    }

    /**
     * 메뉴 삭제 전 옵션들 처리
     */
    private void handleMenuOptionsBeforeDeletion(Menu menu) {
        if (menu.getOptions() != null && !menu.getOptions().isEmpty()) {
            log.info("메뉴에 속한 옵션들 처리 중...");

            int totalOptionGroups = menu.getOptions().size();
            int totalOptionItems = 0;

            // 각 옵션 그룹의 아이템들 삭제
            for (MenuOption option : menu.getOptions()) {
                if (option.getItems() != null && !option.getItems().isEmpty()) {
                    int itemCount = option.getItems().size();
                    totalOptionItems += itemCount;

                    log.info("옵션 그룹 '{}' 의 아이템 {}개 삭제 중...", option.getName(), itemCount);

                    // 옵션 아이템들 삭제 (Cascade로 자동 삭제되지만 명시적으로 처리)
                    menuOptionItemRepository.deleteByOptionId(option.getId());

                    log.debug("옵션 그룹 '{}' 아이템들 삭제 완료", option.getName());
                }
            }

            // 옵션 그룹들 삭제 (Cascade로 자동 삭제되지만 명시적으로 처리)
            log.info("총 {}개 옵션 그룹 삭제 중...", totalOptionGroups);
            menuOptionRepository.deleteByMenuId(menu.getId());

            log.info("옵션 처리 완료 - 삭제된 옵션 그룹: {}개, 옵션 아이템: {}개",
                    totalOptionGroups, totalOptionItems);
        } else {
            log.info("삭제할 옵션이 없습니다");
        }
    }

    /**
     * 메뉴 소프트 삭제 (비활성화)
     */
    @Transactional
    public void softDeleteMenu(Long menuId) {
        log.info("=== 메뉴 소프트 삭제 시작 ===");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Store store = getMyStore();

        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다"));

        log.info("메뉴 숨김 처리: {} (기존 상태: {})", menu.getName(), menu.getStatus());

        // 상태를 HIDDEN으로 변경 (실제 삭제하지 않음)
        menu.updateStatus(MenuStatus.HIDDEN);
        menuRepository.save(menu);

        log.info("메뉴 소프트 삭제 완료! 메뉴: {} (ID: {})", menu.getName(), menuId);
        log.info("=== 메뉴 소프트 삭제 종료 ===");
    }

    /**
     * 메뉴 판매 상태 변경
     */
    @Transactional
    public Menu updateMenuStatus(Long menuId, MenuStatusRequest request) {
        log.info("=== 메뉴 판매 상태 변경 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("상태 변경할 메뉴 ID: {}", menuId);

        // 입력받은 값들 로그 출력
        log.info("상태 변경 요청 정보:");
        log.info("  - 새로운 상태: {}", request.getStatus());
        log.info("  - 변경 사유: {}", request.getReason());
        log.info("  - 일시적 변경: {}", request.getIsTemporary());

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}, 현재 상태: {}", menu.getName(), menu.getStatus());

        // 입력값 검증
        validateMenuStatusRequest(request, menu);

        // 현재 상태와 동일한지 확인
        if (menu.getStatus() == request.getStatus()) {
            log.info("이미 동일한 상태입니다: {}", request.getStatus());
            return menu;
        }

        // 상태 변경 전 추가 검증
        validateStatusTransition(menu.getStatus(), request.getStatus(), menu);

        // 상태 변경
        MenuStatus previousStatus = menu.getStatus();
        menu.updateStatus(request.getStatus());

        // 상태 변경 로그 기록
        logStatusChange(menu, previousStatus, request);

        // 상태별 추가 처리
        handleStatusSpecificActions(menu, previousStatus, request);

        // 메뉴 저장
        log.info("메뉴 상태 저장 중...");
        Menu savedMenu = menuRepository.save(menu);

        log.info("메뉴 판매 상태 변경 완료! 메뉴: {} - {} -> {}",
                savedMenu.getName(), previousStatus, savedMenu.getStatus());
        log.info("=== 메뉴 판매 상태 변경 종료 ===");

        return savedMenu;
    }

    /**
     * 메뉴 상태 변경 요청 검증
     */
    private void validateMenuStatusRequest(MenuStatusRequest request, Menu menu) {
        log.debug("메뉴 상태 변경 요청 유효성 검증 시작");

        // 필수 필드 검증
        if (request.getStatus() == null) {
            throw new IllegalArgumentException("메뉴 상태는 필수입니다");
        }

        // 변경 사유 길이 검증
        if (request.getReason() != null && request.getReason().length() > 200) {
            throw new IllegalArgumentException("변경 사유는 200자를 초과할 수 없습니다");
        }

        log.debug("메뉴 상태 변경 요청 유효성 검증 완료");
    }

    /**
     * 상태 전환 유효성 검증
     */
    private void validateStatusTransition(MenuStatus currentStatus, MenuStatus newStatus, Menu menu) {
        log.debug("상태 전환 유효성 검증: {} -> {}", currentStatus, newStatus);

        // 카테고리가 비활성화된 경우 AVAILABLE로 변경 불가
        if (newStatus == MenuStatus.AVAILABLE &&
                menu.getCategory() != null && !menu.getCategory().getIsActive()) {
            log.warn("비활성화된 카테고리의 메뉴를 판매중으로 변경할 수 없습니다. 카테고리: {}",
                    menu.getCategory().getName());
            throw new IllegalArgumentException(
                    "비활성화된 카테고리의 메뉴는 판매중으로 변경할 수 없습니다. 먼저 카테고리를 활성화해주세요."
            );
        }

        // 필수 옵션이 모두 비활성화된 경우 AVAILABLE로 변경 불가 (옵션이 있는 경우)
        if (newStatus == MenuStatus.AVAILABLE && menu.getOptions() != null && !menu.getOptions().isEmpty()) {
            boolean hasAllRequiredOptionsDisabled = menu.getOptions().stream()
                    .filter(option -> option.getIsRequired())
                    .anyMatch(option -> option.getItems() == null ||
                            option.getItems().stream().noneMatch(item -> item.getIsActive()));

            if (hasAllRequiredOptionsDisabled) {
                log.warn("필수 옵션이 모두 비활성화된 메뉴를 판매중으로 변경할 수 없습니다");
                throw new IllegalArgumentException(
                        "필수 옵션이 모두 비활성화되어 있습니다. 필수 옵션을 활성화한 후 판매중으로 변경해주세요."
                );
            }
        }

        log.debug("상태 전환 유효성 검증 완료");
    }

    /**
     * 상태 변경 로그 기록
     */
    private void logStatusChange(Menu menu, MenuStatus previousStatus, MenuStatusRequest request) {
        log.info("메뉴 상태 변경 상세:");
        log.info("  - 메뉴: {} (ID: {})", menu.getName(), menu.getId());
        log.info("  - 상태 변경: {} -> {}", previousStatus, request.getStatus());
        log.info("  - 변경 사유: {}", request.getReason() != null ? request.getReason() : "사유 없음");
        log.info("  - 일시적 변경: {}", request.getIsTemporary());
        log.info("  - 변경 시간: {}", LocalDateTime.now());

        // 상태별 의미 설명
        String statusDescription = getStatusDescription(request.getStatus());
        log.info("  - 상태 의미: {}", statusDescription);
    }

    /**
     * 상태별 설명 반환
     */
    private String getStatusDescription(MenuStatus status) {
        return switch (status) {
            case AVAILABLE -> "고객이 주문할 수 있는 상태";
            case SOLD_OUT -> "일시적으로 품절된 상태 (메뉴 목록에는 표시됨)";
            case HIDDEN -> "고객에게 보이지 않는 상태 (완전 숨김)";
        };
    }

    /**
     * 상태별 추가 처리
     */
    private void handleStatusSpecificActions(Menu menu, MenuStatus previousStatus, MenuStatusRequest request) {
        switch (request.getStatus()) {
            case AVAILABLE -> {
                log.info("메뉴를 판매 가능 상태로 변경했습니다");
                // TODO: 필요시 알림 발송, 캐시 갱신 등
            }
            case SOLD_OUT -> {
                log.info("메뉴를 품절 상태로 변경했습니다");
                if (request.getIsTemporary()) {
                    log.info("일시적 품절로 설정되었습니다");
                    // TODO: 자동 복구 스케줄링 등
                }
            }
            case HIDDEN -> {
                log.info("메뉴를 숨김 상태로 변경했습니다");
                // 인기/추천 메뉴 설정 해제 검토
                if (menu.getIsPopular() || menu.getIsRecommended()) {
                    log.warn("숨김 처리된 메뉴가 인기/추천 메뉴로 설정되어 있습니다. 검토가 필요합니다.");
                }
            }
        }
    }

    /**
     * 메뉴 상태 일괄 변경 (여러 메뉴 동시 처리) - 추가 기능
     */
    @Transactional
    public List<Menu> updateMultipleMenuStatus(List<Long> menuIds, MenuStatusRequest request) {
        log.info("=== 메뉴 상태 일괄 변경 시작 ===");
        log.info("대상 메뉴 수: {}개, 변경할 상태: {}", menuIds.size(), request.getStatus());

        Store store = getMyStore();
        List<Menu> updatedMenus = new ArrayList<>();

        for (Long menuId : menuIds) {
            try {
                Menu menu = updateMenuStatus(menuId, request);
                updatedMenus.add(menu);
            } catch (Exception e) {
                log.error("메뉴 상태 변경 실패 - 메뉴 ID: {}, 오류: {}", menuId, e.getMessage());
                // 개별 실패는 로그만 남기고 계속 진행 (필요에 따라 예외 발생 가능)
            }
        }

        log.info("메뉴 상태 일괄 변경 완료 - 성공: {}개, 실패: {}개",
                updatedMenus.size(), menuIds.size() - updatedMenus.size());
        log.info("=== 메뉴 상태 일괄 변경 종료 ===");

        return updatedMenus;
    }

    /**
     * 메뉴 순서 변경 (드래그 앤 드롭 방식)
     */
    @Transactional
    public List<Menu> updateMenuOrder(MenuOrderUpdateRequest request) {
        log.info("=== 메뉴 순서 변경 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);

        // 입력받은 값들 로그 출력
        log.info("순서 변경 요청 정보:");
        log.info("  - 이동할 메뉴 ID: {}", request.getMenuId());
        log.info("  - 새로운 위치: {}", request.getNewPosition());
        log.info("  - 카테고리 ID: {}", request.getCategoryId());
        log.info("  - 전체 순서: {}", request.getGlobalOrder());

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 이동할 메뉴 조회 및 권한 확인
        log.info("이동할 메뉴 조회 중... 메뉴 ID: {}", request.getMenuId());
        Menu targetMenu = menuRepository.findByIdAndStoreId(request.getMenuId(), store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", request.getMenuId(), store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("이동할 메뉴 확인 완료 - 메뉴명: {}, 현재 순서: {}, 카테고리: {}",
                targetMenu.getName(), targetMenu.getDisplayOrder(),
                targetMenu.getCategory() != null ? targetMenu.getCategory().getName() : "없음");

        List<Menu> menus;
        String orderScope;

        if (request.getGlobalOrder() != null && request.getGlobalOrder()) {
            // 전체 메뉴 기준 순서 변경
            menus = menuRepository.findByStoreIdOrderByDisplayOrderAsc(store.getId());
            orderScope = "전체 메뉴";
        } else {
            // 카테고리 내 순서 변경
            Long categoryIdForOrder = request.getCategoryId() != null ?
                    request.getCategoryId() :
                    (targetMenu.getCategory() != null ? targetMenu.getCategory().getId() : null);

            if (categoryIdForOrder == null) {
                log.error("카테고리 정보가 없습니다");
                throw new IllegalArgumentException("카테고리 정보가 필요합니다");
            }

            // 카테고리 권한 확인
            MenuCategory category = menuCategoryRepository.findById(categoryIdForOrder)
                    .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다"));

            if (!category.getStore().getId().equals(store.getId())) {
                throw new RuntimeException("다른 가게의 카테고리입니다");
            }

            menus = menuRepository.findByStoreIdAndCategoryIdOrderByDisplayOrderAsc(store.getId(), categoryIdForOrder);
            orderScope = "카테고리 '" + category.getName() + "' 내";
        }

        if (menus.isEmpty()) {
            log.warn("순서를 변경할 메뉴가 없습니다");
            throw new IllegalArgumentException("순서를 변경할 메뉴가 없습니다");
        }

        log.info("{} 메뉴 조회 완료 - 총 {}개", orderScope, menus.size());

        // 새로운 위치 유효성 검증
        if (request.getNewPosition() < 1 || request.getNewPosition() > menus.size()) {
            log.error("잘못된 위치입니다. 요청 위치: {}, 총 메뉴 수: {}", request.getNewPosition(), menus.size());
            throw new IllegalArgumentException(
                    String.format("위치는 1부터 %d 사이여야 합니다", menus.size())
            );
        }

        // 현재 위치 찾기
        int currentPosition = -1;
        for (int i = 0; i < menus.size(); i++) {
            if (menus.get(i).getId().equals(request.getMenuId())) {
                currentPosition = i + 1; // 1부터 시작
                break;
            }
        }

        if (currentPosition == -1) {
            log.error("메뉴를 목록에서 찾을 수 없습니다. 메뉴 ID: {}", request.getMenuId());
            throw new RuntimeException("메뉴를 해당 범위에서 찾을 수 없습니다");
        }

        log.info("메뉴 이동: {} (ID: {}) - {} 위치 {} -> {}",
                targetMenu.getName(), targetMenu.getId(), orderScope,
                currentPosition, request.getNewPosition());

        // 위치가 같으면 변경하지 않음
        if (currentPosition == request.getNewPosition()) {
            log.info("이미 같은 위치입니다. 변경하지 않습니다.");
            return menus;
        }

        // 메뉴 리스트에서 이동할 메뉴 제거
        Menu movingMenu = menus.remove(currentPosition - 1);

        // 새로운 위치에 삽입 (인덱스는 0부터 시작하므로 -1)
        menus.add(request.getNewPosition() - 1, movingMenu);

        // 모든 메뉴의 displayOrder를 새로 설정
        List<Menu> updatedMenus = new ArrayList<>();

        if (request.getGlobalOrder() != null && request.getGlobalOrder()) {
            // 전체 메뉴 순서 재설정
            for (int i = 0; i < menus.size(); i++) {
                Menu menu = menus.get(i);
                int newDisplayOrder = i + 1;

                if (!menu.getDisplayOrder().equals(newDisplayOrder)) {
                    log.info("메뉴 순서 업데이트: {} (ID: {}) - {} -> {}",
                            menu.getName(), menu.getId(),
                            menu.getDisplayOrder(), newDisplayOrder);
                    menu.updateDisplayOrder(newDisplayOrder);
                    updatedMenus.add(menu);
                }
            }
        } else {
            // 카테고리 내 순서만 재설정 (카테고리별로 1부터 시작)
            for (int i = 0; i < menus.size(); i++) {
                Menu menu = menus.get(i);
                int newDisplayOrder = i + 1;

                if (!menu.getDisplayOrder().equals(newDisplayOrder)) {
                    log.info("메뉴 순서 업데이트: {} (ID: {}) - {} -> {}",
                            menu.getName(), menu.getId(),
                            menu.getDisplayOrder(), newDisplayOrder);
                    menu.updateDisplayOrder(newDisplayOrder);
                    updatedMenus.add(menu);
                }
            }
        }

        // 변경된 메뉴들만 저장
        if (!updatedMenus.isEmpty()) {
            menuRepository.saveAll(updatedMenus);
            log.info("총 {}개 메뉴 순서가 업데이트되었습니다", updatedMenus.size());
        }

        log.info("=== 메뉴 순서 변경 완료 ===");

        // 최종 결과를 순서대로 반환
        if (request.getGlobalOrder() != null && request.getGlobalOrder()) {
            return menuRepository.findByStoreIdOrderByDisplayOrderAsc(store.getId());
        } else {
            Long categoryIdForResult = request.getCategoryId() != null ?
                    request.getCategoryId() :
                    targetMenu.getCategory().getId();
            return menuRepository.findByStoreIdAndCategoryIdOrderByDisplayOrderAsc(store.getId(), categoryIdForResult);
        }
    }

    /**
     * 카테고리별 메뉴 순서 정규화 (추가 유틸리티 메서드)
     */
    @Transactional
    public void normalizeMenuOrdersInCategory(Long categoryId) {
        log.info("카테고리 내 메뉴 순서 정규화 시작 - 카테고리 ID: {}", categoryId);

        Store store = getMyStore();
        List<Menu> menus = menuRepository.findByStoreIdAndCategoryIdOrderByDisplayOrderAsc(store.getId(), categoryId);

        List<Menu> updatedMenus = new ArrayList<>();
        for (int i = 0; i < menus.size(); i++) {
            Menu menu = menus.get(i);
            int expectedOrder = i + 1;

            if (!menu.getDisplayOrder().equals(expectedOrder)) {
                menu.updateDisplayOrder(expectedOrder);
                updatedMenus.add(menu);
            }
        }

        if (!updatedMenus.isEmpty()) {
            menuRepository.saveAll(updatedMenus);
            log.info("카테고리 내 {}개 메뉴 순서가 정규화되었습니다", updatedMenus.size());
        }

        log.info("카테고리 내 메뉴 순서 정규화 완료");
    }

    /**
     * 전체 메뉴 순서 정규화
     */
    @Transactional
    public void normalizeAllMenuOrders() {
        log.info("전체 메뉴 순서 정규화 시작");

        Store store = getMyStore();
        List<Menu> menus = menuRepository.findByStoreIdOrderByDisplayOrderAsc(store.getId());

        List<Menu> updatedMenus = new ArrayList<>();
        for (int i = 0; i < menus.size(); i++) {
            Menu menu = menus.get(i);
            int expectedOrder = i + 1;

            if (!menu.getDisplayOrder().equals(expectedOrder)) {
                menu.updateDisplayOrder(expectedOrder);
                updatedMenus.add(menu);
            }
        }

        if (!updatedMenus.isEmpty()) {
            menuRepository.saveAll(updatedMenus);
            log.info("전체 {}개 메뉴 순서가 정규화되었습니다", updatedMenus.size());
        }

        log.info("전체 메뉴 순서 정규화 완료");
    }

    /**
     * 메뉴 이미지 업로드
     */
    @Transactional
    public String uploadMenuImage(Long menuId, MultipartFile file) {
        log.info("=== 메뉴 이미지 업로드 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("이미지 업로드할 메뉴 ID: {}", menuId);

        // 파일 정보 로깅
        log.info("업로드 파일 정보:");
        log.info("  - 파일명: {}", file.getOriginalFilename());
        log.info("  - 파일 크기: {}바이트", file.getSize());
        log.info("  - 콘텐츠 타입: {}", file.getContentType());

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}, 기존 이미지: {}",
                menu.getName(), menu.getImageUrl() != null ? "있음" : "없음");

        // 파일 유효성 검증
        validateImageFile(file);

        try {
            // 이미지 파일 업로드 처리
            String imageUrl = processImageUpload(file, menu);

            // 기존 이미지가 있다면 삭제 처리 (선택사항)
            if (menu.getImageUrl() != null && !menu.getImageUrl().isEmpty()) {
                log.info("기존 이미지 삭제 처리: {}", menu.getImageUrl());
                // TODO: 실제 파일 삭제 로직 (S3, 로컬 파일시스템 등)
                deleteImageFile(menu.getImageUrl());
            }

            // 메뉴 이미지 URL 업데이트
            menu.updateImageUrl(imageUrl);
            menuRepository.save(menu);

            log.info("메뉴 이미지 업로드 완료! 메뉴: {}, 이미지 URL: {}", menu.getName(), imageUrl);
            log.info("=== 메뉴 이미지 업로드 종료 ===");

            return imageUrl;

        } catch (Exception e) {
            log.error("메뉴 이미지 업로드 실패", e);
            throw new RuntimeException("이미지 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 메뉴 이미지 삭제
     */
    @Transactional
    public void deleteMenuImage(Long menuId, Long imageId) {
        log.info("=== 메뉴 이미지 삭제 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("이미지 삭제할 메뉴 ID: {}, 이미지 ID: {}", menuId, imageId);

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}, 현재 이미지: {}",
                menu.getName(), menu.getImageUrl() != null ? "있음" : "없음");

        // 현재 구조에서는 메뉴당 하나의 이미지만 지원하므로 imageId는 검증용으로만 사용
        // 실제 다중 이미지를 지원하려면 별도의 MenuImage 엔티티가 필요
        if (menu.getImageUrl() == null || menu.getImageUrl().isEmpty()) {
            log.warn("삭제할 이미지가 없습니다. 메뉴: {}", menu.getName());
            throw new RuntimeException("삭제할 이미지가 없습니다");
        }

        // imageId 검증 (간단한 방식: URL의 해시값이나 파일명으로 검증)
        if (!isValidImageId(menu.getImageUrl(), imageId)) {
            log.error("잘못된 이미지 ID입니다. 메뉴 ID: {}, 이미지 ID: {}", menuId, imageId);
            throw new RuntimeException("잘못된 이미지 ID입니다");
        }

        try {
            // 실제 이미지 파일 삭제
            String imageUrl = menu.getImageUrl();
            log.info("이미지 파일 삭제 처리 중: {}", imageUrl);
            deleteImageFile(imageUrl);

            // 메뉴의 이미지 URL 제거
            menu.removeImage();
            menuRepository.save(menu);

            log.info("메뉴 이미지 삭제 완료! 메뉴: {}, 삭제된 이미지: {}", menu.getName(), imageUrl);
            log.info("=== 메뉴 이미지 삭제 종료 ===");

        } catch (Exception e) {
            log.error("메뉴 이미지 삭제 실패", e);
            throw new RuntimeException("이미지 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 이미지 파일 유효성 검증
     */
    private void validateImageFile(MultipartFile file) {
        log.debug("이미지 파일 유효성 검증 시작");

        // 파일 크기 검증 (10MB 제한)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            log.error("파일 크기가 너무 큽니다. 크기: {}바이트, 최대: {}바이트", file.getSize(), maxSize);
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다");
        }

        // 파일 형식 검증
        String contentType = file.getContentType();
        if (contentType == null || !isValidImageType(contentType)) {
            log.error("지원하지 않는 파일 형식입니다. 콘텐츠 타입: {}", contentType);
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. JPG, PNG, GIF만 업로드 가능합니다");
        }

        // 파일명 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            log.error("파일명이 없습니다");
            throw new IllegalArgumentException("올바른 파일명이 필요합니다");
        }

        // 파일 확장자 검증
        String extension = getFileExtension(originalFilename);
        if (!isValidImageExtension(extension)) {
            log.error("지원하지 않는 파일 확장자입니다. 확장자: {}", extension);
            throw new IllegalArgumentException("지원하지 않는 파일 확장자입니다");
        }

        log.debug("이미지 파일 유효성 검증 완료");
    }

    /**
     * 이미지 타입 유효성 검사
     */
    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp");
    }

    /**
     * 이미지 확장자 유효성 검사
     */
    private boolean isValidImageExtension(String extension) {
        return extension.matches("(?i)\\.(jpg|jpeg|png|gif|webp)$");
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }

    /**
     * 이미지 업로드 처리
     */
    private String processImageUpload(MultipartFile file, Menu menu) throws Exception {
        log.info("이미지 업로드 처리 중...");

        // 파일명 생성 (중복 방지)
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = generateUniqueFilename(menu, extension);

        log.info("생성된 파일명: {}", newFilename);

        // TODO: 실제 파일 저장 로직 구현
        // 예시: S3 업로드
        String imageUrl = uploadToS3(file, newFilename);

        // 또는 로컬 파일시스템에 저장
        // String imageUrl = uploadToLocalFileSystem(file, newFilename);

        log.info("이미지 업로드 완료 - URL: {}", imageUrl);
        return imageUrl;
    }

    /**
     * 고유 파일명 생성
     */
    private String generateUniqueFilename(Menu menu, String extension) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // 메뉴 ID + 타임스탬프 + 랜덤값으로 고유 파일명 생성
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        return String.format("menu_%d_%s_%s%s", menu.getId(), timestamp, randomSuffix, extension);
    }

    /**
     * S3에 이미지 업로드 (구현 필요)
     */
    private String uploadToS3(MultipartFile file, String filename) throws Exception {
        // TODO: AWS S3 SDK를 사용한 업로드 로직 구현
        // 임시로 더미 URL 반환
        log.warn("S3 업로드 로직이 구현되지 않았습니다. 더미 URL을 반환합니다.");
        return "https://example-bucket.s3.amazonaws.com/menu-images/" + filename;
    }

    /**
     * 로컬 파일시스템에 이미지 저장 (개발용)
     */
    private String uploadToLocalFileSystem(MultipartFile file, String filename) throws Exception {
        String uploadDir = "uploads/menu-images/";
        Path uploadPath = Paths.get(uploadDir);

        // 디렉토리 생성
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일 저장
        Path filePath = uploadPath.resolve(filename);
        file.transferTo(filePath.toFile());

        // URL 반환
        return "/images/menu/" + filename;
    }

    /**
     * 이미지 파일 삭제
     */
    private void deleteImageFile(String imageUrl) {
        try {
            log.info("이미지 파일 삭제 중: {}", imageUrl);

            // TODO: 실제 파일 삭제 로직 구현
            // S3에서 삭제
            // deleteFromS3(imageUrl);

            // 또는 로컬 파일시스템에서 삭제
            // deleteFromLocalFileSystem(imageUrl);

            log.info("이미지 파일 삭제 완료");
        } catch (Exception e) {
            log.error("이미지 파일 삭제 실패: {}", imageUrl, e);
            // 파일 삭제 실패는 치명적이지 않으므로 예외를 던지지 않음
        }
    }

    /**
     * 이미지 ID 유효성 검증
     */
    private boolean isValidImageId(String imageUrl, Long imageId) {
        // 간단한 검증: URL의 해시값과 imageId 비교
        // 실제 구현에서는 더 정교한 검증 로직 필요
        long urlHash = Math.abs(imageUrl.hashCode());
        return urlHash % 1000000 == imageId % 1000000;
    }

    /**
     * 메뉴 이미지 정보 조회
     */
    public Map<String, Object> getMenuImageInfo(Long menuId) {
        log.info("메뉴 이미지 정보 조회 - 메뉴 ID: {}", menuId);

        Store store = getMyStore();
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다"));

        Map<String, Object> imageInfo = new HashMap<>();
        imageInfo.put("menuId", menu.getId());
        imageInfo.put("menuName", menu.getName());
        imageInfo.put("hasImage", menu.getImageUrl() != null && !menu.getImageUrl().isEmpty());
        imageInfo.put("imageUrl", menu.getImageUrl());

        if (menu.getImageUrl() != null) {
            // 이미지 ID는 URL 해시값으로 생성
            long imageId = Math.abs(menu.getImageUrl().hashCode()) % 1000000;
            imageInfo.put("imageId", imageId);
        }

        return imageInfo;
    }

    /**
     * 메뉴 이미지 URL 직접 업데이트 (외부 이미지 URL 설정용)
     */
    @Transactional
    public Menu updateMenuImageUrl(Long menuId, String imageUrl) {
        log.info("메뉴 이미지 URL 직접 업데이트 - 메뉴 ID: {}, URL: {}", menuId, imageUrl);

        Store store = getMyStore();
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다"));

        // URL 유효성 간단 검증
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                throw new IllegalArgumentException("올바른 이미지 URL이 아닙니다");
            }
        }

        menu.updateImageUrl(imageUrl);
        Menu savedMenu = menuRepository.save(menu);

        log.info("메뉴 이미지 URL 업데이트 완료");
        return savedMenu;
    }

    /**
     * 메뉴 옵션 그룹 목록 조회
     */
    public List<MenuOption> getMenuOptionGroups(Long menuId) {
        log.info("=== 메뉴 옵션 그룹 목록 조회 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("옵션 그룹을 조회할 메뉴 ID: {}", menuId);

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}, 상태: {}", menu.getName(), menu.getStatus());

        // 메뉴의 옵션 그룹 목록 조회
        log.info("메뉴 옵션 그룹 조회 중...");
        List<MenuOption> optionGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);

        if (optionGroups.isEmpty()) {
            log.info("메뉴에 옵션 그룹이 없습니다");
        } else {
            log.info("메뉴 옵션 그룹 조회 완료 - 총 {}개 그룹", optionGroups.size());

            // 각 옵션 그룹별 상세 정보 로깅
            logOptionGroupDetails(optionGroups);
        }

        log.info("=== 메뉴 옵션 그룹 목록 조회 종료 ===");
        return optionGroups;
    }

    /**
     * 옵션 그룹 상세 정보 로깅
     */
    private void logOptionGroupDetails(List<MenuOption> optionGroups) {
        log.info("옵션 그룹 상세 정보:");

        int totalRequiredGroups = 0;
        int totalOptionalGroups = 0;
        int totalItems = 0;
        int totalActiveItems = 0;

        for (int i = 0; i < optionGroups.size(); i++) {
            MenuOption group = optionGroups.get(i);
            int itemCount = group.getItems() != null ? group.getItems().size() : 0;
            int activeItemCount = 0;

            if (group.getItems() != null) {
                activeItemCount = (int) group.getItems().stream()
                        .filter(item -> item.getIsActive())
                        .count();
            }

            totalItems += itemCount;
            totalActiveItems += activeItemCount;

            if (group.getIsRequired()) {
                totalRequiredGroups++;
            } else {
                totalOptionalGroups++;
            }

            String status = determineGroupStatus(group, activeItemCount, itemCount);

            log.info("  {}. {} ({}) - 아이템 {}개 (활성: {}개), 필수: {}, 순서: {}, 상태: {}",
                    i + 1, group.getName(), group.getType(),
                    itemCount, activeItemCount, group.getIsRequired(),
                    group.getDisplayOrder(), status);
        }

        log.info("옵션 그룹 통계:");
        log.info("  - 총 그룹 수: {}개 (필수: {}개, 선택: {}개)",
                optionGroups.size(), totalRequiredGroups, totalOptionalGroups);
        log.info("  - 총 아이템 수: {}개 (활성: {}개, 비활성: {}개)",
                totalItems, totalActiveItems, totalItems - totalActiveItems);

        // 문제가 있는 옵션 그룹 체크
        List<String> issues = checkOptionGroupIssues(optionGroups);
        if (!issues.isEmpty()) {
            log.warn("옵션 그룹 문제점:");
            issues.forEach(issue -> log.warn("  - {}", issue));
        }
    }

    /**
     * 개별 옵션 그룹 상태 판단
     */
    private String determineGroupStatus(MenuOption group, int activeItemCount, int totalItemCount) {
        if (totalItemCount == 0) {
            return "아이템 없음";
        } else if (group.getIsRequired() && activeItemCount == 0) {
            return "필수 그룹이지만 활성 아이템 없음";
        } else if (activeItemCount == 0) {
            return "활성 아이템 없음";
        } else if (activeItemCount < totalItemCount) {
            return "일부 아이템 비활성";
        } else {
            return "정상";
        }
    }

    /**
     * 옵션 그룹 문제점 체크
     */
    private List<String> checkOptionGroupIssues(List<MenuOption> optionGroups) {
        List<String> issues = new ArrayList<>();

        for (MenuOption group : optionGroups) {
            String groupName = group.getName();
            int itemCount = group.getItems() != null ? group.getItems().size() : 0;
            int activeItemCount = 0;

            if (group.getItems() != null) {
                activeItemCount = (int) group.getItems().stream()
                        .filter(item -> item.getIsActive())
                        .count();
            }

            // 아이템이 없는 그룹
            if (itemCount == 0) {
                issues.add(String.format("'%s' 그룹에 아이템이 없습니다", groupName));
            }

            // 필수 그룹인데 활성 아이템이 없는 경우
            else if (group.getIsRequired() && activeItemCount == 0) {
                issues.add(String.format("필수 그룹 '%s'에 활성화된 아이템이 없습니다", groupName));
            }

            // 단일 선택 그룹인데 아이템이 1개뿐인 경우
            else if (group.getType() == OptionType.SINGLE && activeItemCount == 1) {
                issues.add(String.format("단일 선택 그룹 '%s'에 선택지가 1개뿐입니다", groupName));
            }
        }

        // 순서 중복 체크
        Set<Integer> orders = optionGroups.stream()
                .map(MenuOption::getDisplayOrder)
                .collect(Collectors.toSet());

        if (orders.size() != optionGroups.size()) {
            issues.add("옵션 그룹 표시 순서에 중복이 있습니다");
        }

        return issues;
    }

    /**
     * 특정 옵션 그룹 상세 조회 (추가 메서드)
     */
    public MenuOption getMenuOptionGroup(Long menuId, Long optionGroupId) {
        log.info("특정 옵션 그룹 조회 - 메뉴 ID: {}, 옵션 그룹 ID: {}", menuId, optionGroupId);

        Store store = getMyStore();

        // 메뉴 권한 확인
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다"));

        // 옵션 그룹 조회
        MenuOption optionGroup = menuOptionRepository.findById(optionGroupId)
                .orElseThrow(() -> new RuntimeException("옵션 그룹을 찾을 수 없습니다"));

        // 옵션 그룹이 해당 메뉴의 것인지 확인
        if (!optionGroup.getMenu().getId().equals(menuId)) {
            throw new RuntimeException("다른 메뉴의 옵션 그룹입니다");
        }

        log.info("옵션 그룹 조회 완료 - 그룹명: {}", optionGroup.getName());
        return optionGroup;
    }

    /**
     * 메뉴 옵션 통계 정보 조회 (추가 메서드)
     */
    public Map<String, Object> getMenuOptionStatistics(Long menuId) {
        log.info("메뉴 옵션 통계 조회 - 메뉴 ID: {}", menuId);

        List<MenuOption> optionGroups = getMenuOptionGroups(menuId);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalGroups", optionGroups.size());
        statistics.put("requiredGroups", optionGroups.stream().filter(g -> g.getIsRequired()).count());
        statistics.put("optionalGroups", optionGroups.stream().filter(g -> !g.getIsRequired()).count());

        int totalItems = optionGroups.stream()
                .mapToInt(g -> g.getItems() != null ? g.getItems().size() : 0)
                .sum();

        int activeItems = optionGroups.stream()
                .mapToInt(g -> g.getItems() != null ?
                        (int) g.getItems().stream().filter(i -> i.getIsActive()).count() : 0)
                .sum();

        statistics.put("totalItems", totalItems);
        statistics.put("activeItems", activeItems);
        statistics.put("inactiveItems", totalItems - activeItems);

        List<String> issues = checkOptionGroupIssues(optionGroups);
        statistics.put("hasIssues", !issues.isEmpty());
        statistics.put("issues", issues);

        return statistics;
    }

    /**
     * 메뉴 옵션 그룹 등록
     */
    @Transactional
    public MenuOption createOptionGroup(Long menuId, MenuOptionGroupCreateRequest request) {
        log.info("=== 메뉴 옵션 그룹 등록 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("옵션 그룹을 추가할 메뉴 ID: {}", menuId);

        // 입력받은 값들 로그 출력
        log.info("옵션 그룹 생성 요청 정보:");
        log.info("  - 옵션 그룹명: {}", request.getName());
        log.info("  - 옵션 타입: {}", request.getType());
        log.info("  - 필수 옵션: {}", request.getIsRequired());
        log.info("  - 표시 순서: {}", request.getDisplayOrder());
        log.info("  - 아이템 개수: {}개", request.getItems() != null ? request.getItems().size() : 0);

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}, 상태: {}", menu.getName(), menu.getStatus());

        // 입력값 검증
        validateOptionGroupCreateRequest(request, menuId);

        // 표시 순서 자동 설정 (요청에 없는 경우)
        Integer displayOrder = request.getDisplayOrder();
        if (displayOrder == null) {
            Integer maxOrder = getMaxOptionGroupOrder(menuId);
            displayOrder = maxOrder + 1;
            log.info("표시 순서 자동 설정: {}", displayOrder);
        }

        // MenuOption 엔티티 생성
        log.info("MenuOption 엔티티 생성 중...");
        MenuOption optionGroup = MenuOption.builder()
                .menu(menu)
                .name(request.getName().trim())
                .type(request.getType())
                .isRequired(request.getIsRequired() != null ? request.getIsRequired() : false)
                .displayOrder(displayOrder)
                .build();

        log.info("MenuOption 엔티티 생성 완료");

        // 옵션 그룹 저장
        log.info("옵션 그룹 저장 중...");
        MenuOption savedOptionGroup = menuOptionRepository.save(optionGroup);
        log.info("옵션 그룹 저장 완료 - 옵션 그룹 ID: {}", savedOptionGroup.getId());

        // 옵션 아이템들 생성 및 저장
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            log.info("옵션 아이템들 생성 중... 아이템 수: {}", request.getItems().size());
            List<MenuOptionItem> optionItems = createOptionItemsForGroup(savedOptionGroup, request.getItems());
            log.info("옵션 아이템들 생성 완료 - {}개 생성됨", optionItems.size());
        }

        log.info("메뉴 옵션 그룹 등록 완료! 생성된 옵션 그룹 ID: {}, 옵션 그룹명: {}",
                savedOptionGroup.getId(), savedOptionGroup.getName());
        log.info("=== 메뉴 옵션 그룹 등록 종료 ===");

        return savedOptionGroup;
    }

    /**
     * 옵션 그룹 생성 요청 검증
     */
    private void validateOptionGroupCreateRequest(MenuOptionGroupCreateRequest request, Long menuId) {
        log.debug("옵션 그룹 생성 요청 유효성 검증 시작");

        // 필수 필드 검증
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("옵션 그룹명은 필수입니다");
        }

        if (request.getName().trim().length() > 50) {
            throw new IllegalArgumentException("옵션 그룹명은 50자를 초과할 수 없습니다");
        }

        if (request.getType() == null) {
            throw new IllegalArgumentException("옵션 타입은 필수입니다");
        }

        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new IllegalArgumentException("표시 순서는 0 이상이어야 합니다");
        }

        // 옵션 그룹명 중복 체크 (같은 메뉴 내에서)
        List<MenuOption> existingGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);
        boolean isDuplicate = existingGroups.stream()
                .anyMatch(group -> group.getName().equals(request.getName().trim()));

        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 옵션 그룹명입니다: " + request.getName().trim());
        }

        // 옵션 아이템 검증
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("옵션 아이템은 최소 1개 이상이어야 합니다");
        }

        // 각 옵션 아이템 검증
        for (int i = 0; i < request.getItems().size(); i++) {
            MenuOptionItemCreateRequest item = request.getItems().get(i);
            validateOptionItemForGroup(item, i + 1);
        }

        // 단일 선택인데 아이템이 1개뿐인 경우 경고
        if (request.getType() == OptionType.SINGLE && request.getItems().size() == 1) {
            log.warn("단일 선택 옵션 그룹에 아이템이 1개뿐입니다. 선택의 의미가 없을 수 있습니다.");
        }

        log.debug("옵션 그룹 생성 요청 유효성 검증 완료");
    }

    /**
     * 옵션 아이템 검증 (그룹 내)
     */
    private void validateOptionItemForGroup(MenuOptionItemCreateRequest item, int itemIndex) {
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("옵션 아이템 " + itemIndex + "번의 이름은 필수입니다");
        }

        if (item.getName().trim().length() > 50) {
            throw new IllegalArgumentException("옵션 아이템 " + itemIndex + "번의 이름은 50자를 초과할 수 없습니다");
        }

        if (item.getAdditionalPrice() != null &&
                item.getAdditionalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("옵션 아이템 " + itemIndex + "번의 추가 금액은 0원 이상이어야 합니다");
        }

        if (item.getDisplayOrder() != null && item.getDisplayOrder() < 0) {
            throw new IllegalArgumentException("옵션 아이템 " + itemIndex + "번의 표시 순서는 0 이상이어야 합니다");
        }
    }

    /**
     * 메뉴의 옵션 그룹 최대 순서 조회
     */
    private Integer getMaxOptionGroupOrder(Long menuId) {
        log.debug("메뉴의 옵션 그룹 최대 순서 조회 - 메뉴 ID: {}", menuId);

        List<MenuOption> optionGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);

        if (optionGroups.isEmpty()) {
            log.debug("기존 옵션 그룹이 없음 - 순서 0 반환");
            return 0;
        }

        Integer maxOrder = optionGroups.stream()
                .mapToInt(MenuOption::getDisplayOrder)
                .max()
                .orElse(0);

        log.debug("최대 순서: {}", maxOrder);
        return maxOrder;
    }

    /**
     * 옵션 그룹을 위한 옵션 아이템들 생성
     */
    private List<MenuOptionItem> createOptionItemsForGroup(MenuOption optionGroup, List<MenuOptionItemCreateRequest> itemRequests) {
        List<MenuOptionItem> optionItems = new ArrayList<>();

        for (int i = 0; i < itemRequests.size(); i++) {
            MenuOptionItemCreateRequest itemRequest = itemRequests.get(i);
            log.debug("옵션 아이템 {}번 생성: {}, 추가금액: {}",
                    i + 1, itemRequest.getName(), itemRequest.getAdditionalPrice());

            Integer itemDisplayOrder = itemRequest.getDisplayOrder();
            if (itemDisplayOrder == null) {
                itemDisplayOrder = i + 1; // 1부터 시작
            }

            MenuOptionItem optionItem = MenuOptionItem.builder()
                    .option(optionGroup)
                    .name(itemRequest.getName().trim())
                    .additionalPrice(itemRequest.getAdditionalPrice() != null ?
                            itemRequest.getAdditionalPrice() : BigDecimal.ZERO)
                    .displayOrder(itemDisplayOrder)
                    .isActive(itemRequest.getIsActive() != null ? itemRequest.getIsActive() : true)
                    .build();

            // 옵션 아이템 저장
            MenuOptionItem savedItem = menuOptionItemRepository.save(optionItem);
            optionItems.add(savedItem);

            log.debug("옵션 아이템 저장 완료 - 아이템 ID: {}", savedItem.getId());
        }

        return optionItems;
    }

    /**
     * 메뉴의 옵션 그룹 개수 제한 체크 (추가 검증)
     */
    private void validateOptionGroupLimit(Long menuId) {
        List<MenuOption> existingGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);

        // 옵션 그룹 개수 제한 (예: 최대 10개)
        int maxOptionGroups = 10;
        if (existingGroups.size() >= maxOptionGroups) {
            log.warn("옵션 그룹 개수 제한 초과 - 현재: {}개, 최대: {}개", existingGroups.size(), maxOptionGroups);
            throw new IllegalArgumentException("메뉴당 옵션 그룹은 최대 " + maxOptionGroups + "개까지 등록할 수 있습니다");
        }

        log.debug("옵션 그룹 개수 제한 체크 완료 - 현재: {}개", existingGroups.size());
    }

    /**
     * 메뉴 옵션 그룹 수정
     */
    @Transactional
    public MenuOption updateOptionGroup(Long menuId, Long groupId, MenuOptionGroupUpdateRequest request) {
        log.info("=== 메뉴 옵션 그룹 수정 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("수정할 메뉴 ID: {}, 옵션 그룹 ID: {}", menuId, groupId);

        // 입력받은 값들 로그 출력
        log.info("옵션 그룹 수정 요청 정보:");
        log.info("  - 옵션 그룹명: {}", request.getName());
        log.info("  - 옵션 타입: {}", request.getType());
        log.info("  - 필수 옵션: {}", request.getIsRequired());
        log.info("  - 표시 순서: {}", request.getDisplayOrder());

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}", menu.getName());

        // 옵션 그룹 조회 및 권한 확인
        log.info("옵션 그룹 조회 중... 옵션 그룹 ID: {}", groupId);
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("옵션 그룹을 찾을 수 없습니다. ID: {}", groupId);
                    return new RuntimeException("옵션 그룹을 찾을 수 없습니다");
                });

        // 옵션 그룹이 해당 메뉴의 것인지 확인
        if (!optionGroup.getMenu().getId().equals(menuId)) {
            log.error("다른 메뉴의 옵션 그룹입니다. 옵션 그룹의 메뉴 ID: {}, 요청된 메뉴 ID: {}",
                    optionGroup.getMenu().getId(), menuId);
            throw new RuntimeException("다른 메뉴의 옵션 그룹입니다");
        }

        log.info("옵션 그룹 조회 완료 - 기존 그룹명: {}, 타입: {}, 필수: {}",
                optionGroup.getName(), optionGroup.getType(), optionGroup.getIsRequired());

        // 입력값 검증
        validateOptionGroupUpdateRequest(request, menuId, groupId);

        // 변경사항 적용
        boolean hasChanges = false;

        // 옵션 그룹명 변경
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String newName = request.getName().trim();
            if (!newName.equals(optionGroup.getName())) {
                log.info("옵션 그룹명 변경: {} -> {}", optionGroup.getName(), newName);
                optionGroup.updateName(newName);
                hasChanges = true;
            }
        }

        // 옵션 타입 변경
        if (request.getType() != null && !request.getType().equals(optionGroup.getType())) {
            log.info("옵션 타입 변경: {} -> {}", optionGroup.getType(), request.getType());

            // 타입 변경 시 추가 검증
            validateOptionTypeChange(optionGroup, request.getType());

            optionGroup.updateType(request.getType());
            hasChanges = true;
        }

        // 필수 옵션 여부 변경
        if (request.getIsRequired() != null && !request.getIsRequired().equals(optionGroup.getIsRequired())) {
            log.info("필수 옵션 여부 변경: {} -> {}", optionGroup.getIsRequired(), request.getIsRequired());

            // 필수로 변경할 때 추가 검증
            if (request.getIsRequired()) {
                validateRequiredOptionChange(optionGroup);
            }

            optionGroup.updateIsRequired(request.getIsRequired());
            hasChanges = true;
        }

        // 표시 순서 변경
        if (request.getDisplayOrder() != null && !request.getDisplayOrder().equals(optionGroup.getDisplayOrder())) {
            log.info("표시 순서 변경: {} -> {}", optionGroup.getDisplayOrder(), request.getDisplayOrder());
            optionGroup.updateDisplayOrder(request.getDisplayOrder());
            hasChanges = true;
        }

        if (!hasChanges) {
            log.info("변경된 정보가 없습니다.");
            return optionGroup;
        }

        // 옵션 그룹 저장
        log.info("옵션 그룹 저장 중...");
        MenuOption savedOptionGroup = menuOptionRepository.save(optionGroup);

        log.info("메뉴 옵션 그룹 수정 완료! 옵션 그룹 ID: {}, 그룹명: {}",
                savedOptionGroup.getId(), savedOptionGroup.getName());
        log.info("=== 메뉴 옵션 그룹 수정 종료 ===");

        return savedOptionGroup;
    }

    /**
     * 옵션 그룹 수정 요청 검증
     */
    private void validateOptionGroupUpdateRequest(MenuOptionGroupUpdateRequest request, Long menuId, Long groupId) {
        log.debug("옵션 그룹 수정 요청 유효성 검증 시작");

        // 옵션 그룹명 중복 체크 (다른 옵션 그룹과)
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String trimmedName = request.getName().trim();

            if (trimmedName.length() > 50) {
                throw new IllegalArgumentException("옵션 그룹명은 50자를 초과할 수 없습니다");
            }

            // 현재 옵션 그룹이 아닌 다른 옵션 그룹에서 같은 이름이 사용되는지 확인
            List<MenuOption> existingGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);
            boolean isDuplicate = existingGroups.stream()
                    .anyMatch(group -> !group.getId().equals(groupId) &&
                            group.getName().equals(trimmedName));

            if (isDuplicate) {
                throw new IllegalArgumentException("이미 존재하는 옵션 그룹명입니다: " + trimmedName);
            }
        }

        // 표시 순서 검증
        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new IllegalArgumentException("표시 순서는 0 이상이어야 합니다");
        }

        log.debug("옵션 그룹 수정 요청 유효성 검증 완료");
    }

    /**
     * 옵션 타입 변경 검증
     */
    private void validateOptionTypeChange(MenuOption optionGroup, OptionType newType) {
        log.debug("옵션 타입 변경 검증 - {} -> {}", optionGroup.getType(), newType);

        // MULTIPLE에서 SINGLE로 변경하는 경우
        if (optionGroup.getType() == OptionType.MULTIPLE && newType == OptionType.SINGLE) {
            log.warn("다중 선택에서 단일 선택으로 변경합니다. 고객의 선택 방식이 제한됩니다.");
            // 필요에 따라 추가 검증이나 경고 처리
        }

        // SINGLE에서 MULTIPLE로 변경하는 경우
        else if (optionGroup.getType() == OptionType.SINGLE && newType == OptionType.MULTIPLE) {
            log.info("단일 선택에서 다중 선택으로 변경합니다. 고객이 여러 옵션을 선택할 수 있게 됩니다.");
        }

        log.debug("옵션 타입 변경 검증 완료");
    }

    /**
     * 필수 옵션 변경 검증
     */
    private void validateRequiredOptionChange(MenuOption optionGroup) {
        log.debug("필수 옵션으로 변경 검증");

        // 활성화된 옵션 아이템이 있는지 확인
        if (optionGroup.getItems() != null) {
            long activeItemCount = optionGroup.getItems().stream()
                    .filter(item -> item.getIsActive())
                    .count();

            if (activeItemCount == 0) {
                log.error("필수 옵션으로 변경할 수 없습니다. 활성화된 아이템이 없습니다.");
                throw new IllegalArgumentException(
                        "필수 옵션으로 변경하려면 최소 1개 이상의 활성화된 아이템이 있어야 합니다"
                );
            }

            log.debug("필수 옵션 변경 가능 - 활성화된 아이템: {}개", activeItemCount);
        } else {
            log.error("필수 옵션으로 변경할 수 없습니다. 아이템이 없습니다.");
            throw new IllegalArgumentException(
                    "필수 옵션으로 변경하려면 옵션 아이템이 있어야 합니다"
            );
        }

        log.debug("필수 옵션 변경 검증 완료");
    }

    /**
     * 메뉴 옵션 그룹 삭제
     */
    @Transactional
    public void deleteOptionGroup(Long menuId, Long groupId) {
        log.info("=== 메뉴 옵션 그룹 삭제 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("삭제할 메뉴 ID: {}, 옵션 그룹 ID: {}", menuId, groupId);

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}", menu.getName());

        // 옵션 그룹 조회 및 권한 확인
        log.info("옵션 그룹 조회 중... 옵션 그룹 ID: {}", groupId);
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("옵션 그룹을 찾을 수 없습니다. ID: {}", groupId);
                    return new RuntimeException("옵션 그룹을 찾을 수 없습니다");
                });

        // 옵션 그룹이 해당 메뉴의 것인지 확인
        if (!optionGroup.getMenu().getId().equals(menuId)) {
            log.error("다른 메뉴의 옵션 그룹입니다. 옵션 그룹의 메뉴 ID: {}, 요청된 메뉴 ID: {}",
                    optionGroup.getMenu().getId(), menuId);
            throw new RuntimeException("다른 메뉴의 옵션 그룹입니다");
        }

        log.info("옵션 그룹 조회 완료 - 그룹명: {}, 타입: {}, 필수: {}, 아이템 수: {}개",
                optionGroup.getName(), optionGroup.getType(), optionGroup.getIsRequired(),
                optionGroup.getItems() != null ? optionGroup.getItems().size() : 0);

        // 삭제 전 검증
        validateOptionGroupDeletion(optionGroup, menuId);

        // 옵션 그룹에 속한 아이템들 처리
        handleOptionItemsBeforeDeletion(optionGroup);

        // 옵션 그룹 삭제
        log.info("옵션 그룹 삭제 실행 중...");
        menuOptionRepository.delete(optionGroup);

        log.info("메뉴 옵션 그룹 삭제 완료! 삭제된 그룹: {} (ID: {})", optionGroup.getName(), groupId);
        log.info("=== 메뉴 옵션 그룹 삭제 종료 ===");
    }

    /**
     * 옵션 그룹 삭제 전 검증
     */
    private void validateOptionGroupDeletion(MenuOption optionGroup, Long menuId) {
        log.info("옵션 그룹 삭제 가능 여부 검증 중...");

        // 1. 필수 옵션 그룹 삭제 시 경고
        if (optionGroup.getIsRequired()) {
            log.warn("필수 옵션 그룹을 삭제합니다: {}", optionGroup.getName());

            // 필수 옵션 그룹이 남아있는지 확인
            List<MenuOption> remainingRequiredGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId)
                    .stream()
                    .filter(group -> !group.getId().equals(optionGroup.getId()) && group.getIsRequired())
                    .toList();

            if (remainingRequiredGroups.isEmpty()) {
                log.warn("이 메뉴의 마지막 필수 옵션 그룹을 삭제합니다");
                // 필요에 따라 삭제를 막거나 경고만 할 수 있음
                // throw new IllegalArgumentException("메뉴에는 최소 1개의 필수 옵션 그룹이 있어야 합니다");
            }
        }

        // 2. 현재 주문 중인 메뉴의 옵션 그룹인지 확인 (실제 주문 시스템이 있다면)
        // TODO: 주문 시스템과 연동하여 현재 주문 중인 옵션 그룹인지 확인
        // if (orderService.hasActiveOrdersWithOption(optionGroup.getId())) {
        //     throw new IllegalArgumentException("현재 주문 중인 옵션 그룹은 삭제할 수 없습니다");
        // }

        // 3. 옵션 아이템 개수 확인
        int itemCount = optionGroup.getItems() != null ? optionGroup.getItems().size() : 0;
        if (itemCount > 0) {
            log.info("옵션 그룹에 {}개의 아이템이 있습니다. 함께 삭제됩니다", itemCount);
        }

        log.info("옵션 그룹 삭제 가능 여부 검증 완료");
    }

    /**
     * 옵션 그룹 삭제 전 아이템들 처리
     */
    private void handleOptionItemsBeforeDeletion(MenuOption optionGroup) {
        if (optionGroup.getItems() != null && !optionGroup.getItems().isEmpty()) {
            log.info("옵션 그룹에 속한 아이템들 처리 중...");

            int totalItems = optionGroup.getItems().size();
            int activeItems = (int) optionGroup.getItems().stream()
                    .filter(item -> item.getIsActive())
                    .count();
            int inactiveItems = totalItems - activeItems;

            log.info("삭제될 아이템 통계:");
            log.info("  - 총 아이템: {}개", totalItems);
            log.info("  - 활성 아이템: {}개", activeItems);
            log.info("  - 비활성 아이템: {}개", inactiveItems);

            // 각 아이템 정보 로깅
            for (int i = 0; i < optionGroup.getItems().size(); i++) {
                MenuOptionItem item = optionGroup.getItems().get(i);
                log.info("  {}. {} (추가금액: {}원, 활성: {})",
                        i + 1, item.getName(), item.getAdditionalPrice(), item.getIsActive());
            }

            // 옵션 아이템들 삭제 (Cascade로 자동 삭제되지만 명시적으로 처리)
            log.info("옵션 아이템들 삭제 중...");
            menuOptionItemRepository.deleteByOptionId(optionGroup.getId());

            log.info("옵션 아이템들 처리 완료 - {}개 아이템 삭제됨", totalItems);
        } else {
            log.info("삭제할 옵션 아이템이 없습니다");
        }
    }

    /**
     * 메뉴의 모든 옵션 그룹 삭제 (메뉴 삭제 시 사용)
     */
    @Transactional
    public void deleteAllOptionGroupsByMenuId(Long menuId) {
        log.info("메뉴의 모든 옵션 그룹 삭제 - 메뉴 ID: {}", menuId);

        // 모든 옵션 그룹 조회
        List<MenuOption> optionGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);

        if (optionGroups.isEmpty()) {
            log.info("삭제할 옵션 그룹이 없습니다");
            return;
        }

        log.info("총 {}개 옵션 그룹 삭제 시작", optionGroups.size());

        // 각 옵션 그룹의 아이템들 먼저 삭제
        for (MenuOption optionGroup : optionGroups) {
            if (optionGroup.getItems() != null && !optionGroup.getItems().isEmpty()) {
                log.info("옵션 그룹 '{}' 의 아이템 {}개 삭제",
                        optionGroup.getName(), optionGroup.getItems().size());
                menuOptionItemRepository.deleteByOptionId(optionGroup.getId());
            }
        }

        // 모든 옵션 그룹 삭제
        menuOptionRepository.deleteByMenuId(menuId);

        log.info("메뉴의 모든 옵션 그룹 삭제 완료 - {}개 그룹 삭제됨", optionGroups.size());
    }

    /**
     * 옵션 그룹 삭제 후 순서 정규화
     */
    @Transactional
    public void normalizeOptionGroupOrdersAfterDeletion(Long menuId) {
        log.info("옵션 그룹 삭제 후 순서 정규화 - 메뉴 ID: {}", menuId);

        List<MenuOption> remainingGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);

        List<MenuOption> updatedGroups = new ArrayList<>();
        for (int i = 0; i < remainingGroups.size(); i++) {
            MenuOption group = remainingGroups.get(i);
            int expectedOrder = i + 1;

            if (!group.getDisplayOrder().equals(expectedOrder)) {
                group.updateDisplayOrder(expectedOrder);
                updatedGroups.add(group);
            }
        }

        if (!updatedGroups.isEmpty()) {
            menuOptionRepository.saveAll(updatedGroups);
            log.info("{}개 옵션 그룹 순서가 정규화되었습니다", updatedGroups.size());
        }

        log.info("옵션 그룹 순서 정규화 완료");
    }

    /**
     * 메뉴 옵션 아이템 목록 조회
     */
    public List<MenuOptionItem> getMenuOptions(Long menuId, Long groupId) {
        log.info("=== 메뉴 옵션 아이템 목록 조회 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("옵션 아이템을 조회할 메뉴 ID: {}, 옵션 그룹 ID: {}", menuId, groupId);

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}", menu.getName());

        // 옵션 그룹 조회 및 권한 확인
        log.info("옵션 그룹 조회 중... 옵션 그룹 ID: {}", groupId);
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("옵션 그룹을 찾을 수 없습니다. ID: {}", groupId);
                    return new RuntimeException("옵션 그룹을 찾을 수 없습니다");
                });

        // 옵션 그룹이 해당 메뉴의 것인지 확인
        if (!optionGroup.getMenu().getId().equals(menuId)) {
            log.error("다른 메뉴의 옵션 그룹입니다. 옵션 그룹의 메뉴 ID: {}, 요청된 메뉴 ID: {}",
                    optionGroup.getMenu().getId(), menuId);
            throw new RuntimeException("다른 메뉴의 옵션 그룹입니다");
        }

        log.info("옵션 그룹 조회 완료 - 그룹명: {}, 타입: {}, 필수: {}",
                optionGroup.getName(), optionGroup.getType(), optionGroup.getIsRequired());

        // 옵션 아이템 목록 조회
        log.info("옵션 아이템 목록 조회 중...");
        List<MenuOptionItem> optionItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);

        if (optionItems.isEmpty()) {
            log.info("옵션 그룹에 아이템이 없습니다");
        } else {
            log.info("옵션 아이템 조회 완료 - 총 {}개 아이템", optionItems.size());

            // 각 옵션 아이템별 상세 정보 로깅
            logOptionItemDetails(optionItems, optionGroup);
        }

        log.info("=== 메뉴 옵션 아이템 목록 조회 종료 ===");
        return optionItems;
    }

    /**
     * 옵션 아이템 상세 정보 로깅
     */
    private void logOptionItemDetails(List<MenuOptionItem> optionItems, MenuOption optionGroup) {
        log.info("옵션 아이템 상세 정보:");
        log.info("  옵션 그룹: {} ({})", optionGroup.getName(), optionGroup.getType());

        int totalItems = optionItems.size();
        int activeItems = 0;
        int inactiveItems = 0;
        BigDecimal totalAdditionalPrice = BigDecimal.ZERO;
        BigDecimal maxPrice = BigDecimal.ZERO;
        BigDecimal minPrice = null;

        for (int i = 0; i < optionItems.size(); i++) {
            MenuOptionItem item = optionItems.get(i);

            if (item.getIsActive()) {
                activeItems++;
            } else {
                inactiveItems++;
            }

            // 가격 통계
            BigDecimal itemPrice = item.getAdditionalPrice();
            totalAdditionalPrice = totalAdditionalPrice.add(itemPrice);

            if (itemPrice.compareTo(maxPrice) > 0) {
                maxPrice = itemPrice;
            }

            if (minPrice == null || itemPrice.compareTo(minPrice) < 0) {
                minPrice = itemPrice;
            }

            log.info("    {}. {} - 추가금액: {}원, 순서: {}, 활성: {}",
                    i + 1, item.getName(), item.getAdditionalPrice(),
                    item.getDisplayOrder(), item.getIsActive());
        }

        // 통계 정보 로깅
        log.info("옵션 아이템 통계:");
        log.info("  - 총 아이템 수: {}개 (활성: {}개, 비활성: {}개)", totalItems, activeItems, inactiveItems);
        log.info("  - 가격 범위: {}원 ~ {}원", minPrice != null ? minPrice : BigDecimal.ZERO, maxPrice);

        if (totalItems > 0) {
            BigDecimal avgPrice = totalAdditionalPrice.divide(new BigDecimal(totalItems), 2, RoundingMode.HALF_UP);
            log.info("  - 평균 추가금액: {}원", avgPrice);
        }

        // 문제점 체크
        List<String> issues = checkOptionItemIssues(optionItems, optionGroup);
        if (!issues.isEmpty()) {
            log.warn("옵션 아이템 문제점:");
            issues.forEach(issue -> log.warn("  - {}", issue));
        }
    }

    /**
     * 옵션 아이템 문제점 체크
     */
    private List<String> checkOptionItemIssues(List<MenuOptionItem> optionItems, MenuOption optionGroup) {
        List<String> issues = new ArrayList<>();

        // 활성 아이템 수 체크
        long activeCount = optionItems.stream().filter(MenuOptionItem::getIsActive).count();

        if (activeCount == 0) {
            issues.add("활성화된 아이템이 없습니다");
        } else if (optionGroup.getType() == OptionType.SINGLE && activeCount == 1) {
            issues.add("단일 선택 옵션에 활성 아이템이 1개뿐입니다 (선택의 의미가 없음)");
        }

        // 필수 옵션 그룹인데 활성 아이템이 없는 경우
        if (optionGroup.getIsRequired() && activeCount == 0) {
            issues.add("필수 옵션 그룹에 활성 아이템이 없습니다");
        }

        // 순서 중복 체크
        Set<Integer> orders = optionItems.stream()
                .map(MenuOptionItem::getDisplayOrder)
                .collect(Collectors.toSet());

        if (orders.size() != optionItems.size()) {
            issues.add("옵션 아이템 표시 순서에 중복이 있습니다");
        }

        // 이름 중복 체크
        Set<String> names = optionItems.stream()
                .map(MenuOptionItem::getName)
                .collect(Collectors.toSet());

        if (names.size() != optionItems.size()) {
            issues.add("옵션 아이템 이름에 중복이 있습니다");
        }

        // 가격이 음수인 아이템 체크
        boolean hasNegativePrice = optionItems.stream()
                .anyMatch(item -> item.getAdditionalPrice().compareTo(BigDecimal.ZERO) < 0);

        if (hasNegativePrice) {
            issues.add("추가금액이 음수인 아이템이 있습니다");
        }

        return issues;
    }

    /**
     * 메뉴 옵션 아이템 등록
     */
    @Transactional
    public MenuOptionItem createOption(Long menuId, Long groupId, MenuOptionItemCreateRequest request) {
        log.info("=== 메뉴 옵션 아이템 등록 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("옵션 아이템을 추가할 메뉴 ID: {}, 옵션 그룹 ID: {}", menuId, groupId);

        // 입력받은 값들 로그 출력
        log.info("옵션 아이템 생성 요청 정보:");
        log.info("  - 옵션 아이템명: {}", request.getName());
        log.info("  - 추가 금액: {}", request.getAdditionalPrice());
        log.info("  - 표시 순서: {}", request.getDisplayOrder());
        log.info("  - 활성화 상태: {}", request.getIsActive());

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}", menu.getName());

        // 옵션 그룹 조회 및 권한 확인
        log.info("옵션 그룹 조회 중... 옵션 그룹 ID: {}", groupId);
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("옵션 그룹을 찾을 수 없습니다. ID: {}", groupId);
                    return new RuntimeException("옵션 그룹을 찾을 수 없습니다");
                });

        // 옵션 그룹이 해당 메뉴의 것인지 확인
        if (!optionGroup.getMenu().getId().equals(menuId)) {
            log.error("다른 메뉴의 옵션 그룹입니다. 옵션 그룹의 메뉴 ID: {}, 요청된 메뉴 ID: {}",
                    optionGroup.getMenu().getId(), menuId);
            throw new RuntimeException("다른 메뉴의 옵션 그룹입니다");
        }

        log.info("옵션 그룹 조회 완료 - 그룹명: {}, 타입: {}, 기존 아이템 수: {}개",
                optionGroup.getName(), optionGroup.getType(),
                optionGroup.getItems() != null ? optionGroup.getItems().size() : 0);

        // 입력값 검증
        validateOptionItemCreateRequest(request, groupId);

        // 표시 순서 자동 설정 (요청에 없는 경우)
        Integer displayOrder = request.getDisplayOrder();
        if (displayOrder == null) {
            Integer maxOrder = getMaxOptionItemOrder(groupId);
            displayOrder = maxOrder + 1;
            log.info("표시 순서 자동 설정: {}", displayOrder);
        }

        // MenuOptionItem 엔티티 생성
        log.info("MenuOptionItem 엔티티 생성 중...");
        MenuOptionItem optionItem = MenuOptionItem.builder()
                .option(optionGroup)
                .name(request.getName().trim())
                .additionalPrice(request.getAdditionalPrice() != null ?
                        request.getAdditionalPrice() : BigDecimal.ZERO)
                .displayOrder(displayOrder)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        log.info("MenuOptionItem 엔티티 생성 완료");

        // 옵션 아이템 저장
        log.info("옵션 아이템 저장 중...");
        MenuOptionItem savedOptionItem = menuOptionItemRepository.save(optionItem);
        log.info("옵션 아이템 저장 완료 - 옵션 아이템 ID: {}", savedOptionItem.getId());

        log.info("메뉴 옵션 아이템 등록 완료! 생성된 옵션 아이템 ID: {}, 아이템명: {}",
                savedOptionItem.getId(), savedOptionItem.getName());
        log.info("=== 메뉴 옵션 아이템 등록 종료 ===");

        return savedOptionItem;
    }

    /**
     * 옵션 아이템 생성 요청 검증
     */
    private void validateOptionItemCreateRequest(MenuOptionItemCreateRequest request, Long groupId) {
        log.debug("옵션 아이템 생성 요청 유효성 검증 시작");

        // 필수 필드 검증
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("옵션 아이템명은 필수입니다");
        }

        if (request.getName().trim().length() > 50) {
            throw new IllegalArgumentException("옵션 아이템명은 50자를 초과할 수 없습니다");
        }

        // 추가 금액 검증
        if (request.getAdditionalPrice() != null &&
                request.getAdditionalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("추가 금액은 0원 이상이어야 합니다");
        }

        if (request.getAdditionalPrice() != null &&
                request.getAdditionalPrice().compareTo(new BigDecimal("100000")) > 0) {
            throw new IllegalArgumentException("추가 금액은 10만원을 초과할 수 없습니다");
        }

        // 표시 순서 검증
        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new IllegalArgumentException("표시 순서는 0 이상이어야 합니다");
        }

        // 옵션 아이템명 중복 체크 (같은 옵션 그룹 내에서)
        List<MenuOptionItem> existingItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);
        boolean isDuplicate = existingItems.stream()
                .anyMatch(item -> item.getName().equals(request.getName().trim()));

        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 옵션 아이템명입니다: " + request.getName().trim());
        }

        // 옵션 아이템 개수 제한 체크
        validateOptionItemLimit(groupId);

        log.debug("옵션 아이템 생성 요청 유효성 검증 완료");
    }

    /**
     * 옵션 그룹의 아이템 최대 순서 조회
     */
    private Integer getMaxOptionItemOrder(Long groupId) {
        log.debug("옵션 그룹의 아이템 최대 순서 조회 - 그룹 ID: {}", groupId);

        List<MenuOptionItem> optionItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);

        if (optionItems.isEmpty()) {
            log.debug("기존 옵션 아이템이 없음 - 순서 0 반환");
            return 0;
        }

        Integer maxOrder = optionItems.stream()
                .mapToInt(MenuOptionItem::getDisplayOrder)
                .max()
                .orElse(0);

        log.debug("최대 순서: {}", maxOrder);
        return maxOrder;
    }

    /**
     * 옵션 아이템 개수 제한 체크
     */
    private void validateOptionItemLimit(Long groupId) {
        List<MenuOptionItem> existingItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);

        // 옵션 아이템 개수 제한 (예: 최대 20개)
        int maxOptionItems = 20;
        if (existingItems.size() >= maxOptionItems) {
            log.warn("옵션 아이템 개수 제한 초과 - 현재: {}개, 최대: {}개", existingItems.size(), maxOptionItems);
            throw new IllegalArgumentException("옵션 그룹당 아이템은 최대 " + maxOptionItems + "개까지 등록할 수 있습니다");
        }

        log.debug("옵션 아이템 개수 제한 체크 완료 - 현재: {}개", existingItems.size());
    }

    /**
     * 메뉴 옵션 아이템 수정
     */
    @Transactional
    public MenuOptionItem updateOption(Long menuId, Long groupId, Long optionId, MenuOptionItemUpdateRequest request) {
        log.info("=== 메뉴 옵션 아이템 수정 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("수정할 메뉴 ID: {}, 옵션 그룹 ID: {}, 옵션 아이템 ID: {}", menuId, groupId, optionId);

        // 입력받은 값들 로그 출력
        log.info("옵션 아이템 수정 요청 정보:");
        log.info("  - 옵션 아이템명: {}", request.getName());
        log.info("  - 추가 금액: {}", request.getAdditionalPrice());
        log.info("  - 표시 순서: {}", request.getDisplayOrder());
        log.info("  - 활성화 상태: {}", request.getIsActive());

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}", menu.getName());

        // 옵션 그룹 조회 및 권한 확인
        log.info("옵션 그룹 조회 중... 옵션 그룹 ID: {}", groupId);
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("옵션 그룹을 찾을 수 없습니다. ID: {}", groupId);
                    return new RuntimeException("옵션 그룹을 찾을 수 없습니다");
                });

        // 옵션 그룹이 해당 메뉴의 것인지 확인
        if (!optionGroup.getMenu().getId().equals(menuId)) {
            log.error("다른 메뉴의 옵션 그룹입니다. 옵션 그룹의 메뉴 ID: {}, 요청된 메뉴 ID: {}",
                    optionGroup.getMenu().getId(), menuId);
            throw new RuntimeException("다른 메뉴의 옵션 그룹입니다");
        }

        log.info("옵션 그룹 조회 완료 - 그룹명: {}", optionGroup.getName());

        // 옵션 아이템 조회 및 권한 확인
        log.info("옵션 아이템 조회 중... 옵션 아이템 ID: {}", optionId);
        MenuOptionItem optionItem = menuOptionItemRepository.findById(optionId)
                .orElseThrow(() -> {
                    log.error("옵션 아이템을 찾을 수 없습니다. ID: {}", optionId);
                    return new RuntimeException("옵션 아이템을 찾을 수 없습니다");
                });

        // 옵션 아이템이 해당 옵션 그룹의 것인지 확인
        if (!optionItem.getOption().getId().equals(groupId)) {
            log.error("다른 옵션 그룹의 아이템입니다. 아이템의 그룹 ID: {}, 요청된 그룹 ID: {}",
                    optionItem.getOption().getId(), groupId);
            throw new RuntimeException("다른 옵션 그룹의 아이템입니다");
        }

        log.info("옵션 아이템 조회 완료 - 기존 아이템명: {}, 추가금액: {}원, 활성: {}",
                optionItem.getName(), optionItem.getAdditionalPrice(), optionItem.getIsActive());

        // 입력값 검증
        validateOptionItemUpdateRequest(request, groupId, optionId);

        // 변경사항 적용
        boolean hasChanges = false;

        // 옵션 아이템명 변경
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String newName = request.getName().trim();
            if (!newName.equals(optionItem.getName())) {
                log.info("옵션 아이템명 변경: {} -> {}", optionItem.getName(), newName);
                optionItem.updateName(newName);
                hasChanges = true;
            }
        }

        // 추가 금액 변경
        if (request.getAdditionalPrice() != null &&
                request.getAdditionalPrice().compareTo(optionItem.getAdditionalPrice()) != 0) {
            log.info("추가 금액 변경: {}원 -> {}원", optionItem.getAdditionalPrice(), request.getAdditionalPrice());
            optionItem.updateAdditionalPrice(request.getAdditionalPrice());
            hasChanges = true;
        }

        // 표시 순서 변경
        if (request.getDisplayOrder() != null &&
                !request.getDisplayOrder().equals(optionItem.getDisplayOrder())) {
            log.info("표시 순서 변경: {} -> {}", optionItem.getDisplayOrder(), request.getDisplayOrder());
            optionItem.updateDisplayOrder(request.getDisplayOrder());
            hasChanges = true;
        }

        // 활성화 상태 변경
        if (request.getIsActive() != null &&
                !request.getIsActive().equals(optionItem.getIsActive())) {
            log.info("활성화 상태 변경: {} -> {}", optionItem.getIsActive(), request.getIsActive());

            // 비활성화할 때 추가 검증
            if (!request.getIsActive()) {
                validateOptionItemDeactivation(optionItem, optionGroup);
            }

            optionItem.updateIsActive(request.getIsActive());
            hasChanges = true;
        }

        if (!hasChanges) {
            log.info("변경된 정보가 없습니다.");
            return optionItem;
        }

        // 옵션 아이템 저장
        log.info("옵션 아이템 저장 중...");
        MenuOptionItem savedOptionItem = menuOptionItemRepository.save(optionItem);

        log.info("메뉴 옵션 아이템 수정 완료! 아이템 ID: {}, 아이템명: {}",
                savedOptionItem.getId(), savedOptionItem.getName());
        log.info("=== 메뉴 옵션 아이템 수정 종료 ===");

        return savedOptionItem;
    }

    /**
     * 옵션 아이템 수정 요청 검증
     */
    private void validateOptionItemUpdateRequest(MenuOptionItemUpdateRequest request, Long groupId, Long optionId) {
        log.debug("옵션 아이템 수정 요청 유효성 검증 시작");

        // 옵션 아이템명 중복 체크 (다른 아이템과)
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String trimmedName = request.getName().trim();

            if (trimmedName.length() > 50) {
                throw new IllegalArgumentException("옵션 아이템명은 50자를 초과할 수 없습니다");
            }

            // 현재 아이템이 아닌 다른 아이템에서 같은 이름이 사용되는지 확인
            List<MenuOptionItem> existingItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);
            boolean isDuplicate = existingItems.stream()
                    .anyMatch(item -> !item.getId().equals(optionId) &&
                            item.getName().equals(trimmedName));

            if (isDuplicate) {
                throw new IllegalArgumentException("이미 존재하는 옵션 아이템명입니다: " + trimmedName);
            }
        }

        // 추가 금액 검증
        if (request.getAdditionalPrice() != null) {
            if (request.getAdditionalPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("추가 금액은 0원 이상이어야 합니다");
            }
            if (request.getAdditionalPrice().compareTo(new BigDecimal("100000")) > 0) {
                throw new IllegalArgumentException("추가 금액은 10만원을 초과할 수 없습니다");
            }
        }

        // 표시 순서 검증
        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new IllegalArgumentException("표시 순서는 0 이상이어야 합니다");
        }

        log.debug("옵션 아이템 수정 요청 유효성 검증 완료");
    }

    /**
     * 옵션 아이템 비활성화 검증
     */
    private void validateOptionItemDeactivation(MenuOptionItem optionItem, MenuOption optionGroup) {
        log.debug("옵션 아이템 비활성화 검증");

        // 필수 옵션 그룹인 경우, 최소 1개의 활성 아이템이 남아있는지 확인
        if (optionGroup.getIsRequired()) {
            List<MenuOptionItem> activeItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(optionGroup.getId())
                    .stream()
                    .filter(item -> item.getIsActive() && !item.getId().equals(optionItem.getId()))
                    .toList();

            if (activeItems.isEmpty()) {
                log.error("필수 옵션 그룹의 마지막 활성 아이템을 비활성화할 수 없습니다");
                throw new IllegalArgumentException(
                        "필수 옵션 그룹에는 최소 1개의 활성 아이템이 있어야 합니다"
                );
            }

            log.debug("필수 옵션 그룹 비활성화 검증 완료 - 남은 활성 아이템: {}개", activeItems.size());
        }

        // 단일 선택 옵션에서 활성 아이템이 1개만 남는 경우 경고
        if (optionGroup.getType() == OptionType.SINGLE) {
            List<MenuOptionItem> activeItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(optionGroup.getId())
                    .stream()
                    .filter(item -> item.getIsActive() && !item.getId().equals(optionItem.getId()))
                    .toList();

            if (activeItems.size() == 1) {
                log.warn("단일 선택 옵션에서 활성 아이템이 1개만 남게 됩니다. 선택의 의미가 없을 수 있습니다.");
            }
        }

        log.debug("옵션 아이템 비활성화 검증 완료");
    }

    /**
     * 메뉴 옵션 아이템 삭제
     */
    @Transactional
    public void deleteOption(Long menuId, Long groupId, Long optionId) {
        log.info("=== 메뉴 옵션 아이템 삭제 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("삭제할 메뉴 ID: {}, 옵션 그룹 ID: {}, 옵션 아이템 ID: {}", menuId, groupId, optionId);

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}", menu.getName());

        // 옵션 그룹 조회 및 권한 확인
        log.info("옵션 그룹 조회 중... 옵션 그룹 ID: {}", groupId);
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("옵션 그룹을 찾을 수 없습니다. ID: {}", groupId);
                    return new RuntimeException("옵션 그룹을 찾을 수 없습니다");
                });

        // 옵션 그룹이 해당 메뉴의 것인지 확인
        if (!optionGroup.getMenu().getId().equals(menuId)) {
            log.error("다른 메뉴의 옵션 그룹입니다. 옵션 그룹의 메뉴 ID: {}, 요청된 메뉴 ID: {}",
                    optionGroup.getMenu().getId(), menuId);
            throw new RuntimeException("다른 메뉴의 옵션 그룹입니다");
        }

        log.info("옵션 그룹 조회 완료 - 그룹명: {}, 타입: {}, 필수: {}",
                optionGroup.getName(), optionGroup.getType(), optionGroup.getIsRequired());

        // 옵션 아이템 조회 및 권한 확인
        log.info("옵션 아이템 조회 중... 옵션 아이템 ID: {}", optionId);
        MenuOptionItem optionItem = menuOptionItemRepository.findById(optionId)
                .orElseThrow(() -> {
                    log.error("옵션 아이템을 찾을 수 없습니다. ID: {}", optionId);
                    return new RuntimeException("옵션 아이템을 찾을 수 없습니다");
                });

        // 옵션 아이템이 해당 옵션 그룹의 것인지 확인
        if (!optionItem.getOption().getId().equals(groupId)) {
            log.error("다른 옵션 그룹의 아이템입니다. 아이템의 그룹 ID: {}, 요청된 그룹 ID: {}",
                    optionItem.getOption().getId(), groupId);
            throw new RuntimeException("다른 옵션 그룹의 아이템입니다");
        }

        log.info("옵션 아이템 조회 완료 - 아이템명: {}, 추가금액: {}원, 활성: {}, 순서: {}",
                optionItem.getName(), optionItem.getAdditionalPrice(),
                optionItem.getIsActive(), optionItem.getDisplayOrder());

        // 삭제 전 검증
        validateOptionItemDeletion(optionItem, optionGroup);

        // 옵션 아이템 삭제
        log.info("옵션 아이템 삭제 실행 중...");
        menuOptionItemRepository.delete(optionItem);

        log.info("메뉴 옵션 아이템 삭제 완료! 삭제된 아이템: {} (ID: {})",
                optionItem.getName(), optionId);
        log.info("=== 메뉴 옵션 아이템 삭제 종료 ===");
    }

    /**
     * 옵션 아이템 삭제 전 검증
     */
    private void validateOptionItemDeletion(MenuOptionItem optionItem, MenuOption optionGroup) {
        log.info("옵션 아이템 삭제 가능 여부 검증 중...");

        // 1. 필수 옵션 그룹에서의 삭제 검증
        if (optionGroup.getIsRequired()) {
            List<MenuOptionItem> activeItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(optionGroup.getId())
                    .stream()
                    .filter(MenuOptionItem::getIsActive)
                    .toList();

            // 현재 아이템이 활성화된 상태이고, 유일한 활성 아이템인 경우
            if (optionItem.getIsActive() && activeItems.size() <= 1) {
                log.error("필수 옵션 그룹의 마지막 활성 아이템을 삭제할 수 없습니다");
                throw new IllegalArgumentException(
                        "필수 옵션 그룹에는 최소 1개의 활성 아이템이 있어야 합니다. " +
                                "먼저 다른 아이템을 활성화하거나 옵션 그룹을 선택사항으로 변경해주세요."
                );
            }

            log.info("필수 옵션 그룹 삭제 검증 완료 - 남은 활성 아이템: {}개",
                    optionItem.getIsActive() ? activeItems.size() - 1 : activeItems.size());
        }

        // 2. 단일 선택 옵션에서 아이템이 1개만 남는 경우 경고
        if (optionGroup.getType() == OptionType.SINGLE) {
            List<MenuOptionItem> allItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(optionGroup.getId());

            if (allItems.size() <= 2) { // 삭제 후 1개 이하가 되는 경우
                log.warn("단일 선택 옵션에서 아이템을 삭제하면 선택지가 {}개가 됩니다", allItems.size() - 1);

                if (allItems.size() == 1) {
                    throw new IllegalArgumentException(
                            "옵션 그룹에는 최소 1개의 아이템이 있어야 합니다"
                    );
                }
            }
        }

        // 3. 현재 주문 중인 옵션 아이템인지 확인 (실제 주문 시스템이 있다면)
        // TODO: 주문 시스템과 연동하여 현재 주문 중인 옵션 아이템인지 확인
        // if (orderService.hasActiveOrdersWithOptionItem(optionItem.getId())) {
        //     throw new IllegalArgumentException("현재 주문 중인 옵션 아이템은 삭제할 수 없습니다");
        // }

        // 4. 삭제될 아이템 정보 로깅
        log.info("삭제될 아이템 정보:");
        log.info("  - 아이템명: {}", optionItem.getName());
        log.info("  - 추가금액: {}원", optionItem.getAdditionalPrice());
        log.info("  - 활성상태: {}", optionItem.getIsActive());
        log.info("  - 표시순서: {}", optionItem.getDisplayOrder());
        log.info("  - 소속그룹: {} ({})", optionGroup.getName(), optionGroup.getType());

        log.info("옵션 아이템 삭제 가능 여부 검증 완료");
    }

    /**
     * 옵션 아이템 소프트 삭제 (비활성화) - 선택사항
     */
    @Transactional
    public void softDeleteOption(Long menuId, Long groupId, Long optionId) {
        log.info("=== 메뉴 옵션 아이템 소프트 삭제 시작 ===");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Store store = getMyStore();

        // 메뉴 권한 확인
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다"));

        // 옵션 그룹 권한 확인
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("옵션 그룹을 찾을 수 없습니다"));

        if (!optionGroup.getMenu().getId().equals(menuId)) {
            throw new RuntimeException("다른 메뉴의 옵션 그룹입니다");
        }

        // 옵션 아이템 조회 및 권한 확인
        MenuOptionItem optionItem = menuOptionItemRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("옵션 아이템을 찾을 수 없습니다"));

        if (!optionItem.getOption().getId().equals(groupId)) {
            throw new RuntimeException("다른 옵션 그룹의 아이템입니다");
        }

        log.info("옵션 아이템 비활성화 처리: {} (기존 상태: {})",
                optionItem.getName(), optionItem.getIsActive());

        // 비활성화 전 검증 (필수 옵션 그룹에서 마지막 활성 아이템인지 확인)
        if (optionGroup.getIsRequired() && optionItem.getIsActive()) {
            List<MenuOptionItem> activeItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId)
                    .stream()
                    .filter(item -> item.getIsActive() && !item.getId().equals(optionId))
                    .toList();

            if (activeItems.isEmpty()) {
                throw new IllegalArgumentException(
                        "필수 옵션 그룹의 마지막 활성 아이템은 비활성화할 수 없습니다"
                );
            }
        }

        // 비활성화 처리
        optionItem.updateIsActive(false);
        menuOptionItemRepository.save(optionItem);

        log.info("메뉴 옵션 아이템 소프트 삭제 완료! 아이템: {} (ID: {})",
                optionItem.getName(), optionId);
        log.info("=== 메뉴 옵션 아이템 소프트 삭제 종료 ===");
    }

    /**
     * 옵션 그룹의 모든 옵션 아이템 삭제 (옵션 그룹 삭제 시 사용)
     */
    @Transactional
    public void deleteAllOptionItemsByGroupId(Long groupId) {
        log.info("옵션 그룹의 모든 옵션 아이템 삭제 - 그룹 ID: {}", groupId);

        List<MenuOptionItem> optionItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);

        if (optionItems.isEmpty()) {
            log.info("삭제할 옵션 아이템이 없습니다");
            return;
        }

        log.info("총 {}개 옵션 아이템 삭제 시작", optionItems.size());

        // 각 옵션 아이템 정보 로깅
        for (int i = 0; i < optionItems.size(); i++) {
            MenuOptionItem item = optionItems.get(i);
            log.info("  {}. {} (추가금액: {}원, 활성: {})",
                    i + 1, item.getName(), item.getAdditionalPrice(), item.getIsActive());
        }

        // 모든 옵션 아이템 삭제
        menuOptionItemRepository.deleteByOptionId(groupId);

        log.info("옵션 그룹의 모든 옵션 아이템 삭제 완료 - {}개 아이템 삭제됨", optionItems.size());
    }

    /**
     * 옵션 아이템 삭제 후 순서 정규화
     */
    @Transactional
    public void normalizeOptionItemOrdersAfterDeletion(Long groupId) {
        log.info("옵션 아이템 삭제 후 순서 정규화 - 그룹 ID: {}", groupId);

        List<MenuOptionItem> remainingItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);

        List<MenuOptionItem> updatedItems = new ArrayList<>();
        for (int i = 0; i < remainingItems.size(); i++) {
            MenuOptionItem item = remainingItems.get(i);
            int expectedOrder = i + 1;

            if (!item.getDisplayOrder().equals(expectedOrder)) {
                item.updateDisplayOrder(expectedOrder);
                updatedItems.add(item);
            }
        }

        if (!updatedItems.isEmpty()) {
            menuOptionItemRepository.saveAll(updatedItems);
            log.info("{}개 옵션 아이템 순서가 정규화되었습니다", updatedItems.size());
        }

        log.info("옵션 아이템 순서 정규화 완료");
    }

    /**
     * 메뉴 옵션 아이템 상태 변경
     */
    @Transactional
    public MenuOptionItem updateOptionStatus(Long menuId, Long groupId, Long optionId, MenuOptionItemStatusRequest request) {
        log.info("=== 메뉴 옵션 아이템 상태 변경 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);
        log.info("상태를 변경할 메뉴 ID: {}, 옵션 그룹 ID: {}, 옵션 아이템 ID: {}", menuId, groupId, optionId);

        // 입력받은 값들 로그 출력
        log.info("상태 변경 요청 정보:");
        log.info("  - 새로운 활성화 상태: {}", request.getIsActive());
        log.info("  - 변경 사유: {}", request.getReason());
        log.info("  - 일시적 변경: {}", request.getIsTemporary());

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 조회 완료 - 메뉴명: {}", menu.getName());

        // 옵션 그룹 조회 및 권한 확인
        log.info("옵션 그룹 조회 중... 옵션 그룹 ID: {}", groupId);
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("옵션 그룹을 찾을 수 없습니다. ID: {}", groupId);
                    return new RuntimeException("옵션 그룹을 찾을 수 없습니다");
                });

        // 옵션 그룹이 해당 메뉴의 것인지 확인
        if (!optionGroup.getMenu().getId().equals(menuId)) {
            log.error("다른 메뉴의 옵션 그룹입니다. 옵션 그룹의 메뉴 ID: {}, 요청된 메뉴 ID: {}",
                    optionGroup.getMenu().getId(), menuId);
            throw new RuntimeException("다른 메뉴의 옵션 그룹입니다");
        }

        log.info("옵션 그룹 조회 완료 - 그룹명: {}, 타입: {}, 필수: {}",
                optionGroup.getName(), optionGroup.getType(), optionGroup.getIsRequired());

        // 옵션 아이템 조회 및 권한 확인
        log.info("옵션 아이템 조회 중... 옵션 아이템 ID: {}", optionId);
        MenuOptionItem optionItem = menuOptionItemRepository.findById(optionId)
                .orElseThrow(() -> {
                    log.error("옵션 아이템을 찾을 수 없습니다. ID: {}", optionId);
                    return new RuntimeException("옵션 아이템을 찾을 수 없습니다");
                });

        // 옵션 아이템이 해당 옵션 그룹의 것인지 확인
        if (!optionItem.getOption().getId().equals(groupId)) {
            log.error("다른 옵션 그룹의 아이템입니다. 아이템의 그룹 ID: {}, 요청된 그룹 ID: {}",
                    optionItem.getOption().getId(), groupId);
            throw new RuntimeException("다른 옵션 그룹의 아이템입니다");
        }

        log.info("옵션 아이템 조회 완료 - 아이템명: {}, 현재 활성 상태: {}",
                optionItem.getName(), optionItem.getIsActive());

        // 입력값 검증
        validateOptionStatusRequest(request);

        // 현재 상태와 동일한지 확인
        if (optionItem.getIsActive().equals(request.getIsActive())) {
            log.info("이미 동일한 상태입니다: {}", request.getIsActive());
            return optionItem;
        }

        // 상태 변경 전 검증
        validateOptionStatusChange(optionItem, optionGroup, request.getIsActive());

        // 상태 변경
        Boolean previousStatus = optionItem.getIsActive();
        optionItem.updateIsActive(request.getIsActive());

        // 상태 변경 로그 기록
        logOptionStatusChange(optionItem, optionGroup, previousStatus, request);

        // 옵션 아이템 저장
        log.info("옵션 아이템 상태 저장 중...");
        MenuOptionItem savedOptionItem = menuOptionItemRepository.save(optionItem);

        log.info("메뉴 옵션 아이템 상태 변경 완료! 아이템: {} - {} -> {}",
                savedOptionItem.getName(), previousStatus, savedOptionItem.getIsActive());
        log.info("=== 메뉴 옵션 아이템 상태 변경 종료 ===");

        return savedOptionItem;
    }

    /**
     * 옵션 상태 변경 요청 검증
     */
    private void validateOptionStatusRequest(MenuOptionItemStatusRequest request) {
        log.debug("옵션 상태 변경 요청 유효성 검증 시작");

        // 필수 필드 검증
        if (request.getIsActive() == null) {
            throw new IllegalArgumentException("활성화 상태는 필수입니다");
        }

        // 변경 사유 길이 검증
        if (request.getReason() != null && request.getReason().length() > 200) {
            throw new IllegalArgumentException("변경 사유는 200자를 초과할 수 없습니다");
        }

        log.debug("옵션 상태 변경 요청 유효성 검증 완료");
    }

    /**
     * 옵션 상태 변경 검증
     */
    private void validateOptionStatusChange(MenuOptionItem optionItem, MenuOption optionGroup, Boolean newStatus) {
        log.debug("옵션 상태 변경 유효성 검증: {} -> {}", optionItem.getIsActive(), newStatus);

        // 비활성화하려는 경우 검증
        if (!newStatus && optionItem.getIsActive()) {
            // 필수 옵션 그룹에서 마지막 활성 아이템인지 확인
            if (optionGroup.getIsRequired()) {
                List<MenuOptionItem> otherActiveItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(optionGroup.getId())
                        .stream()
                        .filter(item -> item.getIsActive() && !item.getId().equals(optionItem.getId()))
                        .toList();

                if (otherActiveItems.isEmpty()) {
                    log.error("필수 옵션 그룹의 마지막 활성 아이템을 비활성화할 수 없습니다");
                    throw new IllegalArgumentException(
                            "필수 옵션 그룹에는 최소 1개의 활성 아이템이 있어야 합니다. " +
                                    "먼저 다른 아이템을 활성화하거나 옵션 그룹을 선택사항으로 변경해주세요."
                    );
                }

                log.debug("필수 옵션 그룹 비활성화 검증 완료 - 남은 활성 아이템: {}개", otherActiveItems.size());
            }

            // 단일 선택 옵션에서 활성 아이템이 1개만 남는 경우 경고
            if (optionGroup.getType() == OptionType.SINGLE) {
                List<MenuOptionItem> otherActiveItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(optionGroup.getId())
                        .stream()
                        .filter(item -> item.getIsActive() && !item.getId().equals(optionItem.getId()))
                        .toList();

                if (otherActiveItems.size() == 1) {
                    log.warn("단일 선택 옵션에서 활성 아이템이 1개만 남게 됩니다. 선택의 의미가 없을 수 있습니다.");
                }
            }
        }

        log.debug("옵션 상태 변경 유효성 검증 완료");
    }

    /**
     * 옵션 상태 변경 로그 기록
     */
    private void logOptionStatusChange(MenuOptionItem optionItem, MenuOption optionGroup,
                                       Boolean previousStatus, MenuOptionItemStatusRequest request) {
        log.info("옵션 아이템 상태 변경 상세:");
        log.info("  - 메뉴: {}", optionGroup.getMenu().getName());
        log.info("  - 옵션 그룹: {} ({})", optionGroup.getName(), optionGroup.getType());
        log.info("  - 옵션 아이템: {} (ID: {})", optionItem.getName(), optionItem.getId());
        log.info("  - 상태 변경: {} -> {}", previousStatus, request.getIsActive());
        log.info("  - 변경 사유: {}", request.getReason() != null ? request.getReason() : "사유 없음");
        log.info("  - 일시적 변경: {}", request.getIsTemporary());
        log.info("  - 변경 시간: {}", LocalDateTime.now());

        // 상태별 의미 설명
        String statusDescription = request.getIsActive() ?
                "고객이 선택할 수 있는 상태" :
                "고객에게 보이지 않거나 선택할 수 없는 상태";
        log.info("  - 상태 의미: {}", statusDescription);
    }

    /**
     * 옵션 그룹 순서 변경
     */
    @Transactional
    public MenuOption updateOptionGroupOrder(Long menuId, Long groupId, OptionGroupOrderUpdateRequest request) {
        log.info("=== 옵션 그룹 순서 변경 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);

        // 입력받은 값들 로그 출력
        log.info("옵션 그룹 순서 변경 요청 정보:");
        log.info("  - 메뉴 ID: {}", menuId);
        log.info("  - 이동할 그룹 ID: {}", groupId);
        log.info("  - 새로운 위치: {}", request.getNewPosition());
        log.info("  - 변경 사유: {}", request.getReason());

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 확인 완료 - 메뉴명: {}", menu.getName());

        // 이동할 옵션 그룹 조회 및 권한 확인
        log.info("이동할 옵션 그룹 조회 중... 그룹 ID: {}", groupId);
        MenuOption targetGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("옵션 그룹을 찾을 수 없습니다. 그룹 ID: {}", groupId);
                    return new RuntimeException("옵션 그룹을 찾을 수 없습니다");
                });

        // 옵션 그룹이 해당 메뉴에 속하는지 확인
        if (!targetGroup.getMenu().getId().equals(menuId)) {
            log.error("옵션 그룹이 해당 메뉴에 속하지 않습니다. 그룹 메뉴 ID: {}, 요청 메뉴 ID: {}",
                    targetGroup.getMenu().getId(), menuId);
            throw new RuntimeException("옵션 그룹이 해당 메뉴에 속하지 않습니다");
        }

        log.info("이동할 옵션 그룹 확인 완료 - 그룹명: {}, 현재 순서: {}",
                targetGroup.getName(), targetGroup.getDisplayOrder());

        // 해당 메뉴의 모든 옵션 그룹들을 현재 순서대로 조회
        List<MenuOption> optionGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);

        if (optionGroups.isEmpty()) {
            log.warn("순서를 변경할 옵션 그룹이 없습니다");
            throw new IllegalArgumentException("순서를 변경할 옵션 그룹이 없습니다");
        }

        log.info("메뉴 '{}' 의 옵션 그룹 조회 완료 - 총 {}개", menu.getName(), optionGroups.size());

        // 새로운 위치 유효성 검증
        if (request.getNewPosition() < 1 || request.getNewPosition() > optionGroups.size()) {
            log.error("잘못된 위치입니다. 요청 위치: {}, 총 그룹 수: {}", request.getNewPosition(), optionGroups.size());
            throw new IllegalArgumentException(
                    String.format("위치는 1부터 %d 사이여야 합니다", optionGroups.size())
            );
        }

        // 현재 위치 찾기
        int currentPosition = -1;
        for (int i = 0; i < optionGroups.size(); i++) {
            if (optionGroups.get(i).getId().equals(groupId)) {
                currentPosition = i + 1; // 1부터 시작
                break;
            }
        }

        if (currentPosition == -1) {
            log.error("옵션 그룹을 목록에서 찾을 수 없습니다. 그룹 ID: {}", groupId);
            throw new RuntimeException("옵션 그룹을 해당 메뉴에서 찾을 수 없습니다");
        }

        log.info("옵션 그룹 이동: {} (ID: {}) - 위치 {} -> {}",
                targetGroup.getName(), targetGroup.getId(),
                currentPosition, request.getNewPosition());

        // 위치가 같으면 변경하지 않음
        if (currentPosition == request.getNewPosition()) {
            log.info("이미 같은 위치입니다. 변경하지 않습니다.");
            return targetGroup;
        }

        // 옵션 그룹 리스트에서 이동할 그룹 제거
        MenuOption movingGroup = optionGroups.remove(currentPosition - 1);

        // 새로운 위치에 삽입 (인덱스는 0부터 시작하므로 -1)
        optionGroups.add(request.getNewPosition() - 1, movingGroup);

        // 모든 옵션 그룹의 displayOrder를 새로 설정
        List<MenuOption> updatedGroups = new ArrayList<>();

        for (int i = 0; i < optionGroups.size(); i++) {
            MenuOption group = optionGroups.get(i);
            int newDisplayOrder = i + 1;

            if (!group.getDisplayOrder().equals(newDisplayOrder)) {
                log.info("옵션 그룹 순서 업데이트: {} (ID: {}) - {} -> {}",
                        group.getName(), group.getId(),
                        group.getDisplayOrder(), newDisplayOrder);
                group.updateDisplayOrder(newDisplayOrder);
                updatedGroups.add(group);
            }
        }

        // 변경된 옵션 그룹들만 저장
        if (!updatedGroups.isEmpty()) {
            menuOptionRepository.saveAll(updatedGroups);
            log.info("총 {}개 옵션 그룹 순서가 업데이트되었습니다", updatedGroups.size());
        }

        log.info("=== 옵션 그룹 순서 변경 완료 ===");

        return targetGroup;
    }

    /**
     * 옵션 아이템 순서 변경
     */
    @Transactional
    public MenuOptionItem updateOptionItemOrder(Long menuId, Long groupId, Long optionId, OptionItemOrderUpdateRequest request) {
        log.info("=== 옵션 아이템 순서 변경 시작 ===");

        // 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("현재 사용자 ID: {}", currentUserId);

        // 입력받은 값들 로그 출력
        log.info("옵션 아이템 순서 변경 요청 정보:");
        log.info("  - 메뉴 ID: {}", menuId);
        log.info("  - 그룹 ID: {}", groupId);
        log.info("  - 이동할 아이템 ID: {}", optionId);
        log.info("  - 새로운 위치: {}", request.getNewPosition());
        log.info("  - 변경 사유: {}", request.getReason());

        // 내 가게 조회
        log.info("가게 정보 조회 중...");
        Store store = getMyStore();
        log.info("가게 조회 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());

        // 메뉴 조회 및 권한 확인
        log.info("메뉴 조회 중... 메뉴 ID: {}", menuId);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> {
                    log.error("메뉴를 찾을 수 없습니다. 메뉴 ID: {}, 가게 ID: {}", menuId, store.getId());
                    return new RuntimeException("메뉴를 찾을 수 없습니다");
                });

        log.info("메뉴 확인 완료 - 메뉴명: {}", menu.getName());

        // 옵션 그룹 조회 및 권한 확인
        log.info("옵션 그룹 조회 중... 그룹 ID: {}", groupId);
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("옵션 그룹을 찾을 수 없습니다. 그룹 ID: {}", groupId);
                    return new RuntimeException("옵션 그룹을 찾을 수 없습니다");
                });

        // 옵션 그룹이 해당 메뉴에 속하는지 확인
        if (!optionGroup.getMenu().getId().equals(menuId)) {
            log.error("옵션 그룹이 해당 메뉴에 속하지 않습니다. 그룹 메뉴 ID: {}, 요청 메뉴 ID: {}",
                    optionGroup.getMenu().getId(), menuId);
            throw new RuntimeException("옵션 그룹이 해당 메뉴에 속하지 않습니다");
        }

        log.info("옵션 그룹 확인 완료 - 그룹명: {}", optionGroup.getName());

        // 이동할 옵션 아이템 조회 및 권한 확인
        log.info("이동할 옵션 아이템 조회 중... 아이템 ID: {}", optionId);
        MenuOptionItem targetItem = menuOptionItemRepository.findById(optionId)
                .orElseThrow(() -> {
                    log.error("옵션 아이템을 찾을 수 없습니다. 아이템 ID: {}", optionId);
                    return new RuntimeException("옵션 아이템을 찾을 수 없습니다");
                });

        // 👇 수정된 부분: targetItem.getOption().getId() 사용
        if (!targetItem.getOption().getId().equals(groupId)) {
            log.error("옵션 아이템이 해당 그룹에 속하지 않습니다. 아이템 그룹 ID: {}, 요청 그룹 ID: {}",
                    targetItem.getOption().getId(), groupId);
            throw new RuntimeException("옵션 아이템이 해당 그룹에 속하지 않습니다");
        }

        log.info("이동할 옵션 아이템 확인 완료 - 아이템명: {}, 현재 순서: {}",
                targetItem.getName(), targetItem.getDisplayOrder());

        // 해당 그룹의 모든 옵션 아이템들을 현재 순서대로 조회
        List<MenuOptionItem> optionItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);

        if (optionItems.isEmpty()) {
            log.warn("순서를 변경할 옵션 아이템이 없습니다");
            throw new IllegalArgumentException("순서를 변경할 옵션 아이템이 없습니다");
        }

        log.info("그룹 '{}' 의 옵션 아이템 조회 완료 - 총 {}개", optionGroup.getName(), optionItems.size());

        // 새로운 위치 유효성 검증
        if (request.getNewPosition() < 1 || request.getNewPosition() > optionItems.size()) {
            log.error("잘못된 위치입니다. 요청 위치: {}, 총 아이템 수: {}", request.getNewPosition(), optionItems.size());
            throw new IllegalArgumentException(
                    String.format("위치는 1부터 %d 사이여야 합니다", optionItems.size())
            );
        }

        // 현재 위치 찾기
        int currentPosition = -1;
        for (int i = 0; i < optionItems.size(); i++) {
            if (optionItems.get(i).getId().equals(optionId)) {
                currentPosition = i + 1; // 1부터 시작
                break;
            }
        }

        if (currentPosition == -1) {
            log.error("옵션 아이템을 목록에서 찾을 수 없습니다. 아이템 ID: {}", optionId);
            throw new RuntimeException("옵션 아이템을 해당 그룹에서 찾을 수 없습니다");
        }

        log.info("옵션 아이템 이동: {} (ID: {}) - 위치 {} -> {}",
                targetItem.getName(), targetItem.getId(),
                currentPosition, request.getNewPosition());

        // 위치가 같으면 변경하지 않음
        if (currentPosition == request.getNewPosition()) {
            log.info("이미 같은 위치입니다. 변경하지 않습니다.");
            return targetItem;
        }

        // 옵션 아이템 리스트에서 이동할 아이템 제거
        MenuOptionItem movingItem = optionItems.remove(currentPosition - 1);

        // 새로운 위치에 삽입 (인덱스는 0부터 시작하므로 -1)
        optionItems.add(request.getNewPosition() - 1, movingItem);

        // 모든 옵션 아이템의 displayOrder를 새로 설정
        List<MenuOptionItem> updatedItems = new ArrayList<>();

        for (int i = 0; i < optionItems.size(); i++) {
            MenuOptionItem item = optionItems.get(i);
            int newDisplayOrder = i + 1;

            if (!item.getDisplayOrder().equals(newDisplayOrder)) {
                log.info("옵션 아이템 순서 업데이트: {} (ID: {}) - {} -> {}",
                        item.getName(), item.getId(),
                        item.getDisplayOrder(), newDisplayOrder);
                item.updateDisplayOrder(newDisplayOrder);
                updatedItems.add(item);
            }
        }

        // 변경된 옵션 아이템들만 저장
        if (!updatedItems.isEmpty()) {
            menuOptionItemRepository.saveAll(updatedItems);
            log.info("총 {}개 옵션 아이템 순서가 업데이트되었습니다", updatedItems.size());
        }

        log.info("=== 옵션 아이템 순서 변경 완료 ===");

        return targetItem;
    }
}