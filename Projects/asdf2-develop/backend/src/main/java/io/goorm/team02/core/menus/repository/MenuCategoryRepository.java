// MenuCategoryRepository.java에 추가
package io.goorm.team02.core.menus.repository;

import io.goorm.team02.core.menus.domain.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    List<MenuCategory> findByStoreIdAndIsActiveTrueOrderByDisplayOrderAsc(Long storeId);

    Optional<MenuCategory> findByIdAndStoreId(Long id, Long storeId);

    boolean existsByStoreIdAndName(Long storeId, String name);

    List<MenuCategory> findByStoreIdOrderByDisplayOrderAsc(Long storeId);

    @Query("SELECT c FROM MenuCategory c WHERE c.store.id = :storeId AND c.name = :name AND c.id != :excludeId AND c.isActive = true")
    boolean existsByStoreIdAndNameAndIdNot(@Param("storeId") Long storeId,
                                           @Param("name") String name,
                                           @Param("excludeId") Long excludeId);

    long countByStoreIdAndIsActiveTrue(Long storeId);

    Optional<MenuCategory> findByIdAndStoreIdAndIsActiveTrue(Long id, Long storeId);
}