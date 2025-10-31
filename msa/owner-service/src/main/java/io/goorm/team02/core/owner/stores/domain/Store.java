package io.goorm.team02.core.owner.stores.domain;


import io.goorm.team02.common.config.BaseEntity;
import io.goorm.team02.core.owner.menus.domain.MenuCategory;
import io.goorm.team02.core.owner.stores.domain.enums.StoreCategory;
import io.goorm.team02.core.owner.stores.domain.enums.StoreStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "stores")
public class Store extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// TempUser 참조 대신 ownerId 필드 추가
	@Column(name = "owner_id", nullable = false)
	private Long ownerId;

//	@ManyToOne
//	@JoinColumn(name = "owner_id", nullable = false)
//	private TempUser owner;

	@Column(name = "business_number", nullable = false, length = 20, unique = true)
	private String businessNumber;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(length = 20)
	private String phone;

	@Column(nullable = false, length = 200)
	private String address;

	@Column(name = "detail_address", length = 100)
	private String detailAddress;

	@Column(precision = 15, scale = 8)
	private BigDecimal latitude;

	@Column(precision = 15, scale = 8)
	private BigDecimal longitude;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StoreCategory category;

	@Column(precision = 10, scale = 2)
	private BigDecimal minOrderAmount = BigDecimal.ZERO;

	@Column(precision = 10, scale = 2)
	private BigDecimal deliveryFee = BigDecimal.ZERO;

	private Integer deliveryTimeMin = 30;
	private Integer deliveryTimeMax = 60;

	@Enumerated(EnumType.STRING)
	private StoreStatus status = StoreStatus.CLOSED;

	@Column(precision = 3, scale = 2)
	private BigDecimal rating = BigDecimal.ZERO;

	private Integer reviewCount = 0;

	@Column(length = 500)
	private String imageUrl;

	@Column(nullable = false)
	private Boolean isActive = true;

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StoreHour> storeHours;

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MenuCategory> menuCategories;

}
