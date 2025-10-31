-- deliveries 테이블
CREATE TABLE deliveries (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            order_id BIGINT NOT NULL,
                            rider_id BIGINT,
                            pickup_address TEXT NOT NULL,
                            delivery_address TEXT NOT NULL,
                            distance_km DECIMAL(5, 2),
                            estimated_time INT COMMENT '예상 배달 시간(분)',
                            status VARCHAR(20) DEFAULT 'REQUESTED' COMMENT '배달 상태',
                            requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            accepted_at TIMESTAMP NULL,
                            picked_up_at TIMESTAMP NULL,
                            delivered_at TIMESTAMP NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'ON UPDATE CURRENT_TIMESTAMP',
                            delivery_fee DECIMAL(10, 2) DEFAULT 0,

                            INDEX deliveries_index_24 (order_id),
                            INDEX deliveries_index_25 (rider_id),
                            INDEX deliveries_index_26 (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- delivery_locations 테이블
CREATE TABLE delivery_locations (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    delivery_id BIGINT NOT NULL,
                                    latitude DECIMAL(10, 8) NOT NULL,
                                    longitude DECIMAL(11, 8) NOT NULL,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                    INDEX delivery_locations_index_27 (delivery_id),
                                    INDEX delivery_locations_index_28 (created_at),

                                    CONSTRAINT fk_delivery_locations_delivery
                                        FOREIGN KEY (delivery_id) REFERENCES deliveries(id)
                                            ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
