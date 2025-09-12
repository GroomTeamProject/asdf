CREATE TABLE `payments`
(
  `id`             BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id`       BIGINT         NOT NULL,
  `payment_key`    VARCHAR(200) UNIQUE COMMENT 'PG사 결제 키',
  `payment_method` ENUM('CARD','TRANSFER','KAKAO_PAY','NAVER_PAY','TOSS','CASH') NOT NULL COMMENT '결제 수단',
  `amount`         DECIMAL(10, 2) NOT NULL,
  `status`         ENUM('PENDING','COMPLETED','FAILED','CANCELLED','REFUNDED') DEFAULT 'PENDING' COMMENT '결제 상태',
  `pg_provider`    VARCHAR(50) COMMENT '토스페이먼츠, 아임포트 등',
  `pg_tid`         VARCHAR(100) COMMENT 'PG사 거래번호',
  `approved_at`    TIMESTAMP,
  `failed_reason`  TEXT,
  `created_at`     TIMESTAMP DEFAULT (now()),
  `updated_at`     TIMESTAMP DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

-- indexes
CREATE INDEX `payments_index_22` ON `payments` (`order_id`);
CREATE INDEX `payments_index_23` ON `payments` (`status`);

-- foreign keys
ALTER TABLE `payments`
  ADD FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);
