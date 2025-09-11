package io.goorm.team02.core.users.domain;

import io.goorm.team02.core.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user_addresses")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAddress extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
	@JsonBackReference   // ✅ User와의 순환 참조 방지
	private User user;

	@Column(name = "address_name", length = 50)
	private String addressName;   // 집, 회사

	@Column(nullable = false, length = 200)  // 실제 주소지 적음
	private String address;

	@Column(name = "detail_address", length = 100)
	private String detailAddress;   // 동, 호수

	@Column(length = 10)
	private String zipcode;   // 우편번호

	@Column(precision = 10, scale = 8)
	private BigDecimal latitude;    // 위도 경도
 
	@Column(precision = 11, scale = 8)
	private BigDecimal longitude;

	@Column(nullable = false)
	private Boolean isDefault = false;   // 기본 배송지 여부(false로 기본설정)

}