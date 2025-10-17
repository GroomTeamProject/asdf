// MenuCategory.java 수정
package io.goorm.team02.core.menus.domain;

import io.goorm.team02.core.stores.domain.Store;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "menu_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(nullable = false, length = 50)
	private String name;

	private Integer displayOrder = 0;

	@Column(nullable = false)
	private Boolean isActive = true;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Menu> menus;

	@Builder
	public MenuCategory(Store store, String name, Integer displayOrder, Boolean isActive) {
		this.store = store;
		this.name = name;
		this.displayOrder = displayOrder != null ? displayOrder : 0;
		this.isActive = isActive != null ? isActive : true;
	}

	// update 메서드들
	public void updateName(String name) {
		if (name != null && !name.trim().isEmpty()) {
			this.name = name.trim();
		}
	}

	public void updateDisplayOrder(Integer displayOrder) {
		if (displayOrder != null && displayOrder >= 0) {
			this.displayOrder = displayOrder;
		}
	}

	public void updateIsActive(Boolean isActive) {
		if (isActive != null) {
			this.isActive = isActive;
		}
	}
}