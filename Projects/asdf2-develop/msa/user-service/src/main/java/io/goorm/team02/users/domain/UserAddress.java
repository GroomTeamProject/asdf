package io.goorm.team02.users.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;


import lombok.ToString;
import lombok.EqualsAndHashCode;


import java.math.BigDecimal;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@EqualsAndHashCode
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
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
