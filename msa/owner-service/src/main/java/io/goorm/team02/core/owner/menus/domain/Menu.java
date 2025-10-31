package io.goorm.team02.core.owner.menus.domain;



import io.goorm.team02.common.config.BaseEntity;
import io.goorm.team02.core.owner.menus.domain.enums.MenuStatus;
import io.goorm.team02.core.owner.stores.domain.Store;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "menus")
public class Menu extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private MenuCategory category;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Column(length = 500)
	private String imageUrl;

	@Column(nullable = false)
	private Boolean isPopular = false;

	@Column(nullable = false)
	private Boolean isRecommended = false;

	@Enumerated(EnumType.STRING)
	private MenuStatus status = MenuStatus.AVAILABLE;

	private Integer displayOrder = 0;

	@OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MenuOption> options;

	@Builder
	public Menu(Store store, MenuCategory category, String name, String description,
				BigDecimal price, String imageUrl, Boolean isPopular, Boolean isRecommended,
				MenuStatus status, Integer displayOrder) {
		this.store = store;
		this.category = category;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imageUrl = imageUrl;
		this.isPopular = isPopular != null ? isPopular : false;
		this.isRecommended = isRecommended != null ? isRecommended : false;
		this.status = status != null ? status : MenuStatus.AVAILABLE;
		this.displayOrder = displayOrder != null ? displayOrder : 0;
	}

	// 메뉴 정보 업데이트 메서드들
	public void updateName(String name) {
		if (name != null && !name.trim().isEmpty()) {
			this.name = name.trim();
		}
	}

	public void updateDescription(String description) {
		this.description = description; // null 허용 (설명 삭제 가능)
	}

	public void updatePrice(BigDecimal price) {
		if (price != null && price.compareTo(BigDecimal.ZERO) >= 0) {
			this.price = price;
		}
	}

	public void updateCategory(MenuCategory category) {
		this.category = category;
	}

	public void updateImageUrl(String imageUrl) {
		this.imageUrl = imageUrl; // null 허용 (이미지 삭제 가능)
	}

	public void updateStatus(MenuStatus status) {
		if (status != null) {
			this.status = status;
		}
	}

	public void updateDisplayOrder(Integer displayOrder) {
		if (displayOrder != null && displayOrder >= 0) {
			this.displayOrder = displayOrder;
		}
	}

	public void updateIsPopular(Boolean isPopular) {
		if (isPopular != null) {
			this.isPopular = isPopular;
		}
	}

	public void updateIsRecommended(Boolean isRecommended) {
		if (isRecommended != null) {
			this.isRecommended = isRecommended;
		}
	}

	// 이미지 제거
	public void removeImage() {
		this.imageUrl = null;
	}


}