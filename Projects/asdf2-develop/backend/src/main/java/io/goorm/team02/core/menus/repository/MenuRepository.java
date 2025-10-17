// MenuRepository.java
package io.goorm.team02.core.menus.repository;

import io.goorm.team02.core.menus.domain.Menu;
import io.goorm.team02.core.menus.domain.enums.MenuStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    // 가게별 전체 메뉴 조회 (표시순서대로)
    List<Menu> findByStoreIdOrderByDisplayOrderAsc(Long storeId);

    // 가게별 + 카테고리별 메뉴 조회 (표시순서대로)
    List<Menu> findByStoreIdAndCategoryIdOrderByDisplayOrderAsc(Long storeId, Long categoryId);

    // 가게별 메뉴 조회 + 상태 필터링
    List<Menu> findByStoreIdAndStatusOrderByDisplayOrderAsc(Long storeId, MenuStatus status);

    // 가게별 + 카테고리별 + 상태별 메뉴 조회
    List<Menu> findByStoreIdAndCategoryIdAndStatusOrderByDisplayOrderAsc(Long storeId, Long categoryId, MenuStatus status);

    // 특정 메뉴 조회 (권한 확인용)
    Optional<Menu> findByIdAndStoreId(Long id, Long storeId);

    // 가게별 메뉴명 중복 체크
    boolean existsByStoreIdAndName(Long storeId, String name);

    // 가게별 메뉴 개수
    long countByStoreId(Long storeId);

    // 카테고리별 메뉴 개수
    long countByStoreIdAndCategoryId(Long storeId, Long categoryId);
}