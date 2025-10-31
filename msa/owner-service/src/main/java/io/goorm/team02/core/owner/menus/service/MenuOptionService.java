package io.goorm.team02.core.owner.menus.service;

import io.goorm.team02.core.owner.menus.domain.Menu;
import io.goorm.team02.core.owner.menus.domain.MenuOption;
import io.goorm.team02.core.owner.menus.domain.MenuOptionItem;
import io.goorm.team02.core.owner.menus.domain.enums.OptionType;
import io.goorm.team02.core.owner.menus.mapper.MenuOptionMapper;
import io.goorm.team02.core.owner.menus.repository.MenuOptionItemRepository;
import io.goorm.team02.core.owner.menus.repository.MenuOptionRepository;
import io.goorm.team02.core.owner.menus.repository.MenuRepository;
import io.goorm.team02.core.owner.stores.domain.Store;
import io.goorm.team02.dto.owner.menus.menucreate.MenuOptionGroupCreateRequest;
import io.goorm.team02.dto.owner.menus.menucreate.MenuOptionGroupUpdateRequest;
import io.goorm.team02.dto.owner.menus.menucreate.MenuOptionItemCreateRequest;
import io.goorm.team02.dto.owner.menus.menucreate.MenuOptionItemUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MenuOptionService {

    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final MenuOptionItemRepository menuOptionItemRepository;
    private final MenuValidationService menuValidationService;
    private final MenuOptionMapper menuOptionMapper;

    /**
     * 메뉴 옵션 그룹 목록 조회
     */
    public List<MenuOption> getMenuOptionGroups(Long currentUser, Long menuId) {
        log.info("=== 메뉴 옵션 그룹 목록 조회 시작 - 사용자 ID: {}, 메뉴 ID: {} ===", currentUser, menuId);

        Store store = menuValidationService.getMyStore(currentUser);

        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

        List<MenuOption> optionGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);

        log.info("메뉴 옵션 그룹 조회 완료 - 사용자 ID: {}, 메뉴 ID: {}, 총 {}개 그룹",
                currentUser, menuId, optionGroups.size());
        return optionGroups;
    }

    /**
     * 메뉴 옵션 그룹 등록
     */
    @Transactional
    public MenuOption createOptionGroup(Long currentUser, Long menuId, MenuOptionGroupCreateRequest request) {
        log.info("=== 메뉴 옵션 그룹 등록 시작 - 사용자 ID: {}, 메뉴 ID: {} ===", currentUser, menuId);

        Store store = menuValidationService.getMyStore(currentUser);

        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

        // 입력값 검증
        validateOptionGroupCreateRequest(request, menuId);

        // 옵션 그룹 개수 제한 체크
        validateOptionGroupLimit(menuId);

        // String을 OptionType enum으로 변환
        OptionType optionType = menuOptionMapper.convertStringToOptionType(request.getType());

        // 표시 순서 자동 설정
        Integer displayOrder = request.getDisplayOrder();
        if (displayOrder == null) {
            Integer maxOrder = getMaxOptionGroupOrder(menuId);
            displayOrder = maxOrder + 1;
            log.info("표시 순서 자동 설정: {}", displayOrder);
        }

        MenuOption optionGroup = MenuOption.builder()
                .menu(menu)
                .name(request.getName().trim())
                .type(optionType)
                .isRequired(request.getIsRequired() != null ? request.getIsRequired() : false)
                .displayOrder(displayOrder)
                .build();

        MenuOption savedOptionGroup = menuOptionRepository.save(optionGroup);

        // 옵션 아이템들 생성 및 저장
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            createOptionItemsForGroup(savedOptionGroup, request.getItems());
        }

        log.info("메뉴 옵션 그룹 등록 완료! 사용자 ID: {}, 그룹 ID: {}, 이름: {}",
                currentUser, savedOptionGroup.getId(), savedOptionGroup.getName());
        return savedOptionGroup;
    }

    /**
     * 메뉴 옵션 그룹 수정
     */
    @Transactional
    public MenuOption updateOptionGroup(Long currentUser, Long menuId, Long groupId, MenuOptionGroupUpdateRequest request) {
        log.info("=== 메뉴 옵션 그룹 수정 시작 - 사용자 ID: {}, 메뉴 ID: {}, 그룹 ID: {} ===",
                currentUser, menuId, groupId);

        Store store = menuValidationService.getMyStore(currentUser);

        // 메뉴 권한 확인
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

        // 옵션 그룹 조회 및 권한 확인
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "옵션 그룹을 찾을 수 없습니다"));

        if (!optionGroup.getMenu().getId().equals(menuId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 메뉴의 옵션 그룹입니다");
        }

        // 입력값 검증
        validateOptionGroupUpdateRequest(request, menuId, groupId);

        boolean hasChanges = false;

        // 옵션 그룹명 변경
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String newName = request.getName().trim();
            if (!newName.equals(optionGroup.getName())) {
                optionGroup.updateName(newName);
                hasChanges = true;
            }
        }

        // 옵션 타입 변경
        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            OptionType newType = menuOptionMapper.convertStringToOptionType(request.getType());
            if (!newType.equals(optionGroup.getType())) {
                validateOptionTypeChange(optionGroup, newType);
                optionGroup.updateType(newType);
                hasChanges = true;
            }
        }

        // 필수 옵션 여부 변경
        if (request.getIsRequired() != null && !request.getIsRequired().equals(optionGroup.getIsRequired())) {
            if (request.getIsRequired()) {
                validateRequiredOptionChange(optionGroup);
            }
            optionGroup.updateIsRequired(request.getIsRequired());
            hasChanges = true;
        }

        // 표시 순서 변경
        if (request.getDisplayOrder() != null && !request.getDisplayOrder().equals(optionGroup.getDisplayOrder())) {
            optionGroup.updateDisplayOrder(request.getDisplayOrder());
            hasChanges = true;
        }

        if (!hasChanges) {
            log.info("변경된 정보가 없습니다.");
            return optionGroup;
        }

        MenuOption savedOptionGroup = menuOptionRepository.save(optionGroup);
        log.info("메뉴 옵션 그룹 수정 완료! 사용자 ID: {}", currentUser);
        return savedOptionGroup;
    }

    /**
     * 메뉴 옵션 그룹 삭제
     */
    @Transactional
    public void deleteOptionGroup(Long currentUser, Long menuId, Long groupId) {
        log.info("=== 메뉴 옵션 그룹 삭제 시작 - 사용자 ID: {}, 메뉴 ID: {}, 그룹 ID: {} ===",
                currentUser, menuId, groupId);

        Store store = menuValidationService.getMyStore(currentUser);

        // 메뉴 권한 확인
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

        // 옵션 그룹 조회 및 권한 확인
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "옵션 그룹을 찾을 수 없습니다"));

        if (!optionGroup.getMenu().getId().equals(menuId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 메뉴의 옵션 그룹입니다");
        }

        // 삭제 전 검증
        validateOptionGroupDeletion(optionGroup, menuId);

        // 옵션 그룹에 속한 아이템들 먼저 삭제
        if (optionGroup.getItems() != null && !optionGroup.getItems().isEmpty()) {
            menuOptionItemRepository.deleteByOptionId(optionGroup.getId());
        }

        menuOptionRepository.delete(optionGroup);
        log.info("메뉴 옵션 그룹 삭제 완료 - 사용자 ID: {}, 그룹명: {}", currentUser, optionGroup.getName());
    }

    /**
     * 메뉴 옵션 목록 조회
     */
    public List<MenuOptionItem> getMenuOptions(Long currentUser, Long menuId, Long groupId) {
        log.info("=== 메뉴 옵션 목록 조회 시작 - 사용자 ID: {}, 메뉴 ID: {}, 그룹 ID: {} ===",
                currentUser, menuId, groupId);

        Store store = menuValidationService.getMyStore(currentUser);

        // 메뉴 권한 확인
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

        // 옵션 그룹 권한 확인
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "옵션 그룹을 찾을 수 없습니다"));

        if (!optionGroup.getMenu().getId().equals(menuId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 메뉴의 옵션 그룹입니다");
        }

        List<MenuOptionItem> optionItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);

        log.info("메뉴 옵션 목록 조회 완료 - 사용자 ID: {}, 총 {}개 옵션", currentUser, optionItems.size());
        return optionItems;
    }

    /**
     * 메뉴 옵션 등록
     */
    @Transactional
    public MenuOptionItem createOption(Long currentUser, Long menuId, Long groupId, MenuOptionItemCreateRequest request) {
        log.info("=== 메뉴 옵션 등록 시작 - 사용자 ID: {}, 메뉴 ID: {}, 그룹 ID: {} ===",
                currentUser, menuId, groupId);

        Store store = menuValidationService.getMyStore(currentUser);

        // 메뉴 권한 확인
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

        // 옵션 그룹 권한 확인
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "옵션 그룹을 찾을 수 없습니다"));

        if (!optionGroup.getMenu().getId().equals(menuId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 메뉴의 옵션 그룹입니다");
        }

        // 입력값 검증
        validateOptionItemCreateRequest(request, groupId);

        // 표시 순서 자동 설정
        Integer displayOrder = request.getDisplayOrder();
        if (displayOrder == null) {
            Integer maxOrder = getMaxOptionItemOrder(groupId);
            displayOrder = maxOrder + 1;
            log.info("표시 순서 자동 설정: {}", displayOrder);
        }

        MenuOptionItem optionItem = MenuOptionItem.builder()
                .option(optionGroup)
                .name(request.getName().trim())
                .additionalPrice(request.getAdditionalPrice() != null ?
                        request.getAdditionalPrice() : BigDecimal.ZERO)
                .displayOrder(displayOrder)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        MenuOptionItem savedOptionItem = menuOptionItemRepository.save(optionItem);

        log.info("메뉴 옵션 등록 완료! 사용자 ID: {}, 옵션 ID: {}, 이름: {}",
                currentUser, savedOptionItem.getId(), savedOptionItem.getName());
        return savedOptionItem;
    }

    /**
     * 메뉴 옵션 수정
     */
    @Transactional
    public MenuOptionItem updateOption(Long currentUser, Long menuId, Long groupId, Long optionId, MenuOptionItemUpdateRequest request) {
        log.info("=== 메뉴 옵션 수정 시작 - 사용자 ID: {}, 메뉴 ID: {}, 그룹 ID: {}, 옵션 ID: {} ===",
                currentUser, menuId, groupId, optionId);

        // 권한 확인 체인
        validateOptionItemAccess(currentUser, menuId, groupId, optionId);

        MenuOptionItem optionItem = menuOptionItemRepository.findById(optionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "옵션 아이템을 찾을 수 없습니다"));

        // 입력값 검증
        validateOptionItemUpdateRequest(request, groupId, optionId);

        boolean hasChanges = false;

        // 옵션 아이템명 변경
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String newName = request.getName().trim();
            if (!newName.equals(optionItem.getName())) {
                optionItem.updateName(newName);
                hasChanges = true;
            }
        }

        // 추가 금액 변경
        if (request.getAdditionalPrice() != null &&
                request.getAdditionalPrice().compareTo(optionItem.getAdditionalPrice()) != 0) {
            optionItem.updateAdditionalPrice(request.getAdditionalPrice());
            hasChanges = true;
        }

        // 표시 순서 변경
        if (request.getDisplayOrder() != null &&
                !request.getDisplayOrder().equals(optionItem.getDisplayOrder())) {
            optionItem.updateDisplayOrder(request.getDisplayOrder());
            hasChanges = true;
        }

        // 활성화 상태 변경
        if (request.getIsActive() != null &&
                !request.getIsActive().equals(optionItem.getIsActive())) {
            if (!request.getIsActive()) {
                validateOptionItemDeactivation(optionItem, optionItem.getOption());
            }
            optionItem.updateIsActive(request.getIsActive());
            hasChanges = true;
        }

        if (!hasChanges) {
            log.info("변경된 정보가 없습니다.");
            return optionItem;
        }

        MenuOptionItem savedOptionItem = menuOptionItemRepository.save(optionItem);
        log.info("메뉴 옵션 수정 완료! 사용자 ID: {}", currentUser);
        return savedOptionItem;
    }

    /**
     * 메뉴 옵션 삭제
     */
    @Transactional
    public void deleteOption(Long currentUser, Long menuId, Long groupId, Long optionId) {
        log.info("=== 메뉴 옵션 삭제 시작 - 사용자 ID: {}, 메뉴 ID: {}, 그룹 ID: {}, 옵션 ID: {} ===",
                currentUser, menuId, groupId, optionId);

        // 권한 확인
        validateOptionItemAccess(currentUser, menuId, groupId, optionId);

        MenuOptionItem optionItem = menuOptionItemRepository.findById(optionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "옵션 아이템을 찾을 수 없습니다"));

        // 삭제 전 검증
        validateOptionItemDeletion(optionItem, optionItem.getOption());

        menuOptionItemRepository.delete(optionItem);
        log.info("메뉴 옵션 삭제 완료 - 사용자 ID: {}, 옵션명: {}", currentUser, optionItem.getName());
    }

    // ================================
    // Private Helper Methods
    // ================================

    private void validateOptionGroupCreateRequest(MenuOptionGroupCreateRequest request, Long menuId) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션 그룹명은 필수입니다");
        }

        if (request.getName().trim().length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션 그룹명은 50자를 초과할 수 없습니다");
        }

        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션 타입은 필수입니다");
        }

        // 옵션 타입 검증
        try {
            menuOptionMapper.convertStringToOptionType(request.getType());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "표시 순서는 0 이상이어야 합니다");
        }

        // 옵션 그룹명 중복 체크
        List<MenuOption> existingGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);
        boolean isDuplicate = existingGroups.stream()
                .anyMatch(group -> group.getName().equals(request.getName().trim()));

        if (isDuplicate) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 옵션 그룹명입니다: " + request.getName().trim());
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션 아이템은 최소 1개 이상이어야 합니다");
        }

        // 단일 선택인데 아이템이 1개뿐인 경우 경고
        try {
            OptionType optionType = menuOptionMapper.convertStringToOptionType(request.getType());
            if (optionType == OptionType.SINGLE && request.getItems().size() == 1) {
                log.warn("단일 선택 옵션 그룹에 아이템이 1개뿐입니다");
            }
        } catch (IllegalArgumentException e) {
            // 이미 위에서 검증했으므로 무시
        }
    }

    private void validateOptionGroupUpdateRequest(MenuOptionGroupUpdateRequest request, Long menuId, Long groupId) {
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String trimmedName = request.getName().trim();

            if (trimmedName.length() > 50) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션 그룹명은 50자를 초과할 수 없습니다");
            }

            // 중복 체크
            List<MenuOption> existingGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);
            boolean isDuplicate = existingGroups.stream()
                    .anyMatch(group -> !group.getId().equals(groupId) &&
                            group.getName().equals(trimmedName));

            if (isDuplicate) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 옵션 그룹명입니다: " + trimmedName);
            }
        }

        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            // 옵션 타입 검증
            try {
                menuOptionMapper.convertStringToOptionType(request.getType());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "표시 순서는 0 이상이어야 합니다");
        }
    }

    private void validateOptionTypeChange(MenuOption optionGroup, OptionType newType) {
        if (optionGroup.getType() == OptionType.MULTIPLE && newType == OptionType.SINGLE) {
            log.warn("다중 선택에서 단일 선택으로 변경합니다. 고객의 선택 방식이 제한됩니다.");
        } else if (optionGroup.getType() == OptionType.SINGLE && newType == OptionType.MULTIPLE) {
            log.info("단일 선택에서 다중 선택으로 변경합니다. 고객이 여러 옵션을 선택할 수 있게 됩니다.");
        }
    }

    private void validateRequiredOptionChange(MenuOption optionGroup) {
        if (optionGroup.getItems() != null) {
            long activeItemCount = optionGroup.getItems().stream()
                    .filter(item -> item.getIsActive())
                    .count();

            if (activeItemCount == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "필수 옵션으로 변경하려면 최소 1개 이상의 활성화된 아이템이 있어야 합니다");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "필수 옵션으로 변경하려면 옵션 아이템이 있어야 합니다");
        }
    }

    private void validateOptionItemAccess(Long currentUser, Long menuId, Long groupId, Long optionId) {
        Store store = menuValidationService.getMyStore(currentUser);

        // 메뉴 권한 확인
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

        // 옵션 그룹 권한 확인
        MenuOption optionGroup = menuOptionRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "옵션 그룹을 찾을 수 없습니다"));

        if (!optionGroup.getMenu().getId().equals(menuId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 메뉴의 옵션 그룹입니다");
        }

        // 옵션 아이템 존재 확인
        MenuOptionItem optionItem = menuOptionItemRepository.findById(optionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "옵션 아이템을 찾을 수 없습니다"));

        if (!optionItem.getOption().getId().equals(groupId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 옵션 그룹의 아이템입니다");
        }
    }

    private Integer getMaxOptionGroupOrder(Long menuId) {
        List<MenuOption> optionGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);
        if (optionGroups.isEmpty()) {
            return 0;
        }
        return optionGroups.stream()
                .mapToInt(MenuOption::getDisplayOrder)
                .max()
                .orElse(0);
    }

    private Integer getMaxOptionItemOrder(Long groupId) {
        List<MenuOptionItem> optionItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);
        if (optionItems.isEmpty()) {
            return 0;
        }
        return optionItems.stream()
                .mapToInt(MenuOptionItem::getDisplayOrder)
                .max()
                .orElse(0);
    }

    private void createOptionItemsForGroup(MenuOption optionGroup, List<MenuOptionItemCreateRequest> itemRequests) {
        for (int i = 0; i < itemRequests.size(); i++) {
            MenuOptionItemCreateRequest itemRequest = itemRequests.get(i);

            Integer itemDisplayOrder = itemRequest.getDisplayOrder();
            if (itemDisplayOrder == null) {
                itemDisplayOrder = i + 1;
            }

            MenuOptionItem optionItem = MenuOptionItem.builder()
                    .option(optionGroup)
                    .name(itemRequest.getName().trim())
                    .additionalPrice(itemRequest.getAdditionalPrice() != null ?
                            itemRequest.getAdditionalPrice() : BigDecimal.ZERO)
                    .displayOrder(itemDisplayOrder)
                    .isActive(itemRequest.getIsActive() != null ? itemRequest.getIsActive() : true)
                    .build();

            menuOptionItemRepository.save(optionItem);
        }
    }

    private void validateOptionGroupLimit(Long menuId) {
        List<MenuOption> existingGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId);
        int maxOptionGroups = 10;
        if (existingGroups.size() >= maxOptionGroups) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "메뉴당 옵션 그룹은 최대 " + maxOptionGroups + "개까지 등록할 수 있습니다");
        }
    }

    private void validateOptionItemLimit(Long groupId) {
        List<MenuOptionItem> existingItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);
        int maxOptionItems = 20;
        if (existingItems.size() >= maxOptionItems) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "옵션 그룹당 아이템은 최대 " + maxOptionItems + "개까지 등록할 수 있습니다");
        }
    }

    private void validateOptionGroupDeletion(MenuOption optionGroup, Long menuId) {
        if (optionGroup.getIsRequired()) {
            List<MenuOption> remainingRequiredGroups = menuOptionRepository.findByMenuIdOrderByDisplayOrderAsc(menuId)
                    .stream()
                    .filter(group -> !group.getId().equals(optionGroup.getId()) && group.getIsRequired())
                    .toList();

            if (remainingRequiredGroups.isEmpty()) {
                log.warn("이 메뉴의 마지막 필수 옵션 그룹을 삭제합니다");
                // 필요에 따라 삭제를 막거나 경고만 할 수 있음
            }
        }
    }

    private void validateOptionItemDeletion(MenuOptionItem optionItem, MenuOption optionGroup) {
        if (optionGroup.getIsRequired()) {
            List<MenuOptionItem> activeItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(optionGroup.getId())
                    .stream()
                    .filter(MenuOptionItem::getIsActive)
                    .toList();

            if (optionItem.getIsActive() && activeItems.size() <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "필수 옵션 그룹에는 최소 1개의 활성 아이템이 있어야 합니다. 먼저 다른 아이템을 활성화하거나 옵션 그룹을 선택사항으로 변경해주세요.");
            }
        }

        if (optionGroup.getType() == OptionType.SINGLE) {
            List<MenuOptionItem> allItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(optionGroup.getId());
            if (allItems.size() == 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션 그룹에는 최소 1개의 아이템이 있어야 합니다");
            }
        }
    }

    private void validateOptionItemDeactivation(MenuOptionItem optionItem, MenuOption optionGroup) {
        if (optionGroup.getIsRequired()) {
            List<MenuOptionItem> activeItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(optionGroup.getId())
                    .stream()
                    .filter(item -> item.getIsActive() && !item.getId().equals(optionItem.getId()))
                    .toList();

            if (activeItems.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "필수 옵션 그룹에는 최소 1개의 활성 아이템이 있어야 합니다");
            }
        }
    }

    private void validateOptionItemCreateRequest(MenuOptionItemCreateRequest request, Long groupId) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션 아이템명은 필수입니다");
        }

        if (request.getName().trim().length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션 아이템명은 50자를 초과할 수 없습니다");
        }

        if (request.getAdditionalPrice() != null &&
                request.getAdditionalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "추가 금액은 0원 이상이어야 합니다");
        }

        if (request.getAdditionalPrice() != null &&
                request.getAdditionalPrice().compareTo(new BigDecimal("100000")) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "추가 금액은 10만원을 초과할 수 없습니다");
        }

        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "표시 순서는 0 이상이어야 합니다");
        }

        // 옵션 아이템명 중복 체크
        List<MenuOptionItem> existingItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);
        boolean isDuplicate = existingItems.stream()
                .anyMatch(item -> item.getName().equals(request.getName().trim()));

        if (isDuplicate) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 옵션 아이템명입니다: " + request.getName().trim());
        }

        // 옵션 아이템 개수 제한 체크
        validateOptionItemLimit(groupId);
    }

    private void validateOptionItemUpdateRequest(MenuOptionItemUpdateRequest request, Long groupId, Long optionId) {
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String trimmedName = request.getName().trim();

            if (trimmedName.length() > 50) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "옵션 아이템명은 50자를 초과할 수 없습니다");
            }

            // 중복 체크
            List<MenuOptionItem> existingItems = menuOptionItemRepository.findByOptionIdOrderByDisplayOrderAsc(groupId);
            boolean isDuplicate = existingItems.stream()
                    .anyMatch(item -> !item.getId().equals(optionId) &&
                            item.getName().equals(trimmedName));

            if (isDuplicate) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 옵션 아이템명입니다: " + trimmedName);
            }
        }

        if (request.getAdditionalPrice() != null) {
            if (request.getAdditionalPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "추가 금액은 0원 이상이어야 합니다");
            }
            if (request.getAdditionalPrice().compareTo(new BigDecimal("100000")) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "추가 금액은 10만원을 초과할 수 없습니다");
            }
        }

        if (request.getDisplayOrder() != null && request.getDisplayOrder() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "표시 순서는 0 이상이어야 합니다");
        }
    }
}