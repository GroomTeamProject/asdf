CREATE TABLE `stores`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT,
    `owner_id`          BIGINT             NOT NULL,
    `business_number`   VARCHAR(20) UNIQUE NOT NULL,
    `name`              VARCHAR(100)       NOT NULL,
    `description`       TEXT,
    `phone`             VARCHAR(20),
    `address`           VARCHAR(200)       NOT NULL,
    `detail_address`    VARCHAR(100),
    `latitude`          DECIMAL(10, 8),
    `longitude`         DECIMAL(11, 8),
    `category`          ENUM('KOREAN', 'CHINESE', 'WESTERN', 'JAPANESE', 'FAST_FOOD', 'CHICKEN', 'PIZZA', 'DESSERT') NOT NULL COMMENT '업종 카테고리',
    `min_order_amount`  DECIMAL(10, 2) DEFAULT 0,
    `delivery_fee`      DECIMAL(10, 2) DEFAULT 0,
    `delivery_time_min` INT            DEFAULT 30,
    `delivery_time_max` INT            DEFAULT 60,
    `status`            ENUM('OPEN', 'CLOSED', 'BREAK', 'TEMPORARILY_CLOSED') DEFAULT 'CLOSED' COMMENT '가게 상태',
    `rating`            DECIMAL(3, 2)  DEFAULT 0,
    `review_count`      INT            DEFAULT 0,
    `image_url`         VARCHAR(500),
    `is_active`         BOOLEAN        DEFAULT TRUE,
    `created_at`        TIMESTAMP      DEFAULT (now()),
    `updated_at`        TIMESTAMP      DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `store_hours`
(
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
    `store_id`    BIGINT  NOT NULL,
    `day_of_week` TINYINT NOT NULL COMMENT '0=일요일, 1=월요일, ..., 6=토요일',
    `open_time`   TIME,
    `close_time`  TIME,
    `is_closed`   BOOLEAN DEFAULT FALSE,
    `created_at`  TIMESTAMP DEFAULT (now()),
    `updated_at`  TIMESTAMP DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

-- 휴무일 테이블 추가
CREATE TABLE `store_holidays`
(
    `id`           BIGINT PRIMARY KEY AUTO_INCREMENT,
    `store_id`     BIGINT       NOT NULL,
    `date`         DATE         NOT NULL COMMENT '휴무일',
    `reason`       VARCHAR(255) COMMENT '휴무 사유',
    `is_recurring` BOOLEAN      DEFAULT FALSE COMMENT '매년 반복 여부',
    `created_at`   TIMESTAMP    DEFAULT (now()),
    `updated_at`   TIMESTAMP    DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

-- indexes
CREATE INDEX `stores_index_5` ON `stores` (`owner_id`);
CREATE INDEX `stores_index_6` ON `stores` (`category`);
CREATE INDEX `stores_index_7` ON `stores` (`status`);
CREATE INDEX `stores_index_8` ON `stores` (`latitude`, `longitude`);
CREATE UNIQUE INDEX `store_hours_index_9` ON `store_hours` (`store_id`, `day_of_week`);

-- 휴무일 테이블 인덱스 추가
CREATE INDEX `store_holidays_index_10` ON `store_holidays` (`store_id`, `date`);
CREATE INDEX `store_holidays_index_11` ON `store_holidays` (`date`);
CREATE INDEX `store_holidays_index_12` ON `store_holidays` (`is_recurring`);

-- foreign keys
ALTER TABLE `stores`
    ADD FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`);

ALTER TABLE `store_hours`
    ADD FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE;

-- 휴무일 테이블 외래키 추가
ALTER TABLE `store_holidays`
    ADD FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE;