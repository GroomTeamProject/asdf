package io.goorm.team02.core.menus.domain;

import io.goorm.team02.core.menus.domain.enums.OptionType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "menu_options")
public class MenuOption  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@Column(nullable = false, length = 50)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OptionType type; // SINGLE, MULTIPLE

	@Column(nullable = false)
	private Boolean isRequired = false;

	private Integer displayOrder = 0;

	@OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MenuOptionItem> items;

	@Builder
	public MenuOption(Menu menu, String name, OptionType type, Boolean isRequired, Integer displayOrder) {
		this.menu = menu;
		this.name = name;
		this.type = type;
		this.isRequired = isRequired != null ? isRequired : false;
		this.displayOrder = displayOrder != null ? displayOrder : 0;
	}

	public void updateName(String name) {
		if (name != null && !name.trim().isEmpty()) {
			this.name = name.trim();
		}
	}

	public void updateType(OptionType type) {
		if (type != null) {
			this.type = type;
		}
	}

	public void updateIsRequired(Boolean isRequired) {
		if (isRequired != null) {
			this.isRequired = isRequired;
		}
	}

	public void updateDisplayOrder(Integer displayOrder) {
		if (displayOrder != null && displayOrder >= 0) {
			this.displayOrder = displayOrder;
		}
	}
}
