package io.goorm.team02.core.menus.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "menu_option_items")
public class MenuOptionItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "option_id", nullable = false)
	private MenuOption option;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(precision = 10, scale = 2)
	private BigDecimal additionalPrice = BigDecimal.ZERO;

	private Integer displayOrder = 0;

	@Column(nullable = false)
	private Boolean isActive = true;

	@Builder
	public MenuOptionItem(MenuOption option, String name, BigDecimal additionalPrice,
						  Integer displayOrder, Boolean isActive) {
		this.option = option;
		this.name = name;
		this.additionalPrice = additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
		this.displayOrder = displayOrder != null ? displayOrder : 0;
		this.isActive = isActive != null ? isActive : true;
	}

	public void updateName(String name) {
		if (name != null && !name.trim().isEmpty()) {
			this.name = name.trim();
		}
	}

	public void updateAdditionalPrice(BigDecimal additionalPrice) {
		if (additionalPrice != null && additionalPrice.compareTo(BigDecimal.ZERO) >= 0) {
			this.additionalPrice = additionalPrice;
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
