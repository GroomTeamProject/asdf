// MenuOptionRepository.java
package io.goorm.team02.core.owner.menus.repository;

import io.goorm.team02.core.owner.menus.domain.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {

    List<MenuOption> findByMenuIdOrderByDisplayOrderAsc(Long menuId);

    void deleteByMenuId(Long menuId);
}