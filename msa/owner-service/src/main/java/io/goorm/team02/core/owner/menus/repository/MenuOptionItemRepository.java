// MenuOptionItemRepository.java
package io.goorm.team02.core.owner.menus.repository;

import io.goorm.team02.core.owner.menus.domain.MenuOptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuOptionItemRepository extends JpaRepository<MenuOptionItem, Long> {

    List<MenuOptionItem> findByOptionIdOrderByDisplayOrderAsc(Long optionId);

    void deleteByOptionId(Long optionId);
}