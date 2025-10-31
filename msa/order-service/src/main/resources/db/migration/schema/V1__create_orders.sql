-- orders 테이블 생성
CREATE TABLE `orders`
(
  `id`                      BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_number`            VARCHAR(50) UNIQUE NOT NULL,
  `user_id`                 BIGINT             NOT NULL,
  `store_id`                BIGINT             NOT NULL,
  `delivery_address`        TEXT               NOT NULL,
  `delivery_detail_address` VARCHAR(100),
  `phone`                   VARCHAR(20)        NOT NULL,
  `order_memo`              TEXT,
  `menu_total_amount`       DECIMAL(10, 2)     NOT NULL,
  `delivery_fee`            DECIMAL(10, 2) DEFAULT 0,
  `discount_amount`         DECIMAL(10, 2) DEFAULT 0,
  `total_amount`            DECIMAL(10, 2)     NOT NULL,
  `status`                  ENUM('PENDING', 'ACCEPTED', 'COOKING', 'READY', 'PICKED_UP', 'DELIVERED', 'CANCELLED', 'REJECTED')
                            DEFAULT 'PENDING' COMMENT '주문 상태',
  `ordered_at`              TIMESTAMP      DEFAULT (now()),
  `accepted_at`             TIMESTAMP,
  `cooking_started_at`      TIMESTAMP,
  `cooking_completed_at`    TIMESTAMP,
  `delivered_at`            TIMESTAMP,
  `cancelled_at`            TIMESTAMP,
  `rejected_at`             TIMESTAMP,
  `picked_up_at`            TIMESTAMP,
  `reject_reason`           VARCHAR(500) NULL COMMENT '주문 거절 사유',
  `cancel_reason`           VARCHAR(500) NULL COMMENT '주문 취소 사유',
  `min_cooking_time`        INT NULL COMMENT '최소 조리 시간 (분)',
  `max_cooking_time`        INT NULL COMMENT '최대 조리 시간 (분)',
  `store_address`           VARCHAR(200) NOT NULL,
  `store_detail_address`    VARCHAR(100) NOT NULL,
  `created_at`              TIMESTAMP      DEFAULT (now()),
  `updated_at`              TIMESTAMP      DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

-- order_items 테이블 생성
CREATE TABLE `order_items`
(
  `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id`    BIGINT         NOT NULL,
  `menu_id`     BIGINT         NOT NULL,
  `menu_name`   VARCHAR(100)   NOT NULL COMMENT '주문 당시 메뉴명 스냅샷',
  `menu_price`  DECIMAL(10, 2) NOT NULL,
  `quantity`    INT            NOT NULL,
  `total_price` DECIMAL(10, 2) NOT NULL
);

-- order_item_options 테이블 생성
CREATE TABLE `order_item_options`
(
  `id`               BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_item_id`    BIGINT      NOT NULL,
  `option_name`      VARCHAR(50) NOT NULL,
  `option_item_name` VARCHAR(50) NOT NULL,
  `additional_price` DECIMAL(10, 2) DEFAULT 0
);

-- 인덱스 생성
CREATE INDEX `orders_index_1` ON `orders` (`user_id`);
CREATE INDEX `orders_index_2` ON `orders` (`store_id`);
CREATE INDEX `orders_index_3` ON `orders` (`status`);
CREATE INDEX `orders_index_4` ON `orders` (`ordered_at`);
CREATE INDEX `order_items_index_5` ON `order_items` (`order_id`);
CREATE INDEX `order_item_options_index_6` ON `order_item_options` (`order_item_id`);

-- 외래 키 제약조건
ALTER TABLE `order_items`
  ADD FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE;
ALTER TABLE `order_item_options`
  ADD FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`id`) ON DELETE CASCADE;
