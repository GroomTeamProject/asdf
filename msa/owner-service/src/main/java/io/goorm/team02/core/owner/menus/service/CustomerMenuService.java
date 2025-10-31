package io.goorm.team02.core.owner.menus.service;

import io.goorm.team02.core.owner.menus.domain.Menu;
import io.goorm.team02.core.owner.menus.domain.MenuCategory;
import io.goorm.team02.core.owner.menus.domain.enums.MenuStatus;
import io.goorm.team02.core.owner.menus.repository.MenuCategoryRepository;
import io.goorm.team02.core.owner.menus.repository.MenuRepository;
import io.goorm.team02.core.owner.stores.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 고객용 메뉴 서비스
 * 주문 도메인 구현을 위해 임시로 작성
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CustomerMenuService {

    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final StoreRepository storeRepository;

    /**
     * 가게의 메뉴 카테고리 목록 조회 (고객용)
     */
    public List<MenuCategory> getMenuCategoriesByStoreId(Long storeId) {
        log.info("가게 ID로 메뉴 카테고리 조회: {}", storeId);
        
        // 가게 존재 여부 확인
        if (!storeRepository.existsByIdAndIsActiveTrue(storeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없음");
        }
        
        return menuCategoryRepository.findByStoreIdAndIsActiveTrueOrderByDisplayOrderAsc(storeId);
    }

    /**
     * 가게의 메뉴 목록 조회 (고객용)
     */
    public List<Menu> getMenusByStoreId(Long storeId, Long categoryId) {
        log.info("가게 ID로 메뉴 조회: {}, 카테고리 ID: {}", storeId, categoryId);
        
        // 가게 존재 여부 확인
        if (!storeRepository.existsByIdAndIsActiveTrue(storeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없음");
        }
        
        List<Menu> menus;
        
        if (categoryId != null) {
            // 특정 카테고리의 메뉴 조회
            // 카테고리가 해당 가게의 것인지 확인
            menuCategoryRepository.findByIdAndStoreIdAndIsActiveTrue(categoryId, storeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없음"));
            
            // 판매중인 메뉴만 조회
            menus = menuRepository.findByStoreIdAndCategoryIdAndStatusOrderByDisplayOrderAsc(
                    storeId, categoryId, MenuStatus.AVAILABLE);
        } else {
            // 전체 메뉴 조회 (판매중인 것만)
            menus = menuRepository.findByStoreIdAndStatusOrderByDisplayOrderAsc(storeId, MenuStatus.AVAILABLE);
        }
        
        log.info("메뉴 조회 완료 - 가게 ID: {}, 카테고리 ID: {}, 메뉴 수: {}개", 
                storeId, categoryId, menus.size());
        
        return menus;
    }

    /**
     * 메뉴 상세 조회 (고객용)
     */
    public Menu getMenuById(Long menuId) {
        log.info("메뉴 ID로 조회: {}", menuId);
        
        return menuRepository.findById(menuId)
                .filter(menu -> menu.getStatus() == MenuStatus.AVAILABLE) // 판매중인 메뉴만
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없음"));
    }
}
