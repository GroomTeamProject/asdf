CREATE TABLE `menu_categories`
(
  `id`            BIGINT PRIMARY KEY AUTO_INCREMENT,
  `store_id`      BIGINT      NOT NULL,
  `name`          VARCHAR(50) NOT NULL,
  `display_order` INT     DEFAULT 0,
  `is_active`     BOOLEAN DEFAULT TRUE
);

CREATE TABLE `menus`
(
  `id`             BIGINT PRIMARY KEY AUTO_INCREMENT,
  `store_id`       BIGINT         NOT NULL,
  `category_id`    BIGINT,
  `name`           VARCHAR(100)   NOT NULL,
  `description`    TEXT,
  `price`          DECIMAL(10, 2) NOT NULL,
  `image_url`      VARCHAR(500),
  `is_popular`     BOOLEAN   DEFAULT FALSE,
  `is_recommended` BOOLEAN   DEFAULT FALSE,
  `status`         ENUM('AVAILABLE', 'SOLD_OUT', 'HIDDEN') DEFAULT 'AVAILABLE' COMMENT '메뉴 상태',
  `display_order`  INT       DEFAULT 0,
  `created_at`     TIMESTAMP DEFAULT (now()),
  `updated_at`     TIMESTAMP DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `menu_options`
(
  `id`            BIGINT PRIMARY KEY AUTO_INCREMENT,
  `menu_id`       BIGINT      NOT NULL,
  `name`          VARCHAR(50) NOT NULL COMMENT '사이즈, 맵기 정도 등',
  `type`          ENUM('SINGLE', 'MULTIPLE') NOT NULL COMMENT '옵션 선택 방식',
  `is_required`   BOOLEAN DEFAULT FALSE,
  `display_order` INT     DEFAULT 0
);

CREATE TABLE `menu_option_items`
(
  `id`               BIGINT PRIMARY KEY AUTO_INCREMENT,
  `option_id`        BIGINT      NOT NULL,
  `name`             VARCHAR(50) NOT NULL COMMENT '소, 중, 대 / 순한맛, 보통, 매운맛',
  `additional_price` DECIMAL(10, 2) DEFAULT 0,
  `display_order`    INT            DEFAULT 0,
  `is_active`        BOOLEAN        DEFAULT TRUE
);

-- indexes
CREATE INDEX `menu_categories_index_10` ON `menu_categories` (`store_id`);
CREATE INDEX `menus_index_11` ON `menus` (`store_id`);
CREATE INDEX `menus_index_12` ON `menus` (`category_id`);
CREATE INDEX `menus_index_13` ON `menus` (`status`);
CREATE INDEX `menu_options_index_14` ON `menu_options` (`menu_id`);
CREATE INDEX `menu_option_items_index_15` ON `menu_option_items` (`option_id`);

-- foreign keys
ALTER TABLE `menu_categories`
  ADD FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE;
ALTER TABLE `menus`
  ADD FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE;
ALTER TABLE `menus`
  ADD FOREIGN KEY (`category_id`) REFERENCES `menu_categories` (`id`) ON DELETE SET NULL;
ALTER TABLE `menu_options`
  ADD FOREIGN KEY (`menu_id`) REFERENCES `menus` (`id`) ON DELETE CASCADE;
ALTER TABLE `menu_option_items`
  ADD FOREIGN KEY (`option_id`) REFERENCES `menu_options` (`id`) ON DELETE CASCADE;
