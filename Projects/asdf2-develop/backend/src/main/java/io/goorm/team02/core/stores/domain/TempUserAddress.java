package io.goorm.team02.core.stores.domain;

import io.goorm.team02.core.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "user_addresses")
public class TempUserAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private TempUser user;

    @Column(name = "address_name", length = 50)
    private String addressName;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(name = "detail_address", length = 100)
    private String detailAddress;

    @Column(length = 10)
    private String zipcode;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(nullable = false)
    private Boolean isDefault = false;

}