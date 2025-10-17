CREATE TABLE `users`
(
  `id`             BIGINT PRIMARY KEY AUTO_INCREMENT,
  `email`          VARCHAR(100) UNIQUE NOT NULL,
  `password`       VARCHAR(255) NULL COMMENT '소셜로그인 시 NULL 가능',
  `phone`          VARCHAR(20) UNIQUE,
  `name`           VARCHAR(50)         NOT NULL,
  `birth_date`     DATE,
  `user_type`      ENUM('CUSTOMER', 'OWNER', 'RIDER') NOT NULL COMMENT 'CUSTOMER, OWNER, RIDER',
  `is_active`      BOOLEAN   DEFAULT TRUE,
  `email_verified` BOOLEAN   DEFAULT FALSE,
  `phone_verified` BOOLEAN   DEFAULT FALSE,
  `created_at`     TIMESTAMP DEFAULT (now()),
  `updated_at`     TIMESTAMP DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `social_accounts`
(
  `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id`     BIGINT       NOT NULL,
  `provider`    ENUM('KAKAO', 'NAVER', 'GOOGLE') NOT NULL COMMENT 'KAKAO, NAVER, GOOGLE',
  `provider_id` VARCHAR(100) NOT NULL
);

CREATE TABLE `user_addresses`
(
  `id`             BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id`        BIGINT       NOT NULL,
  `address_name`   VARCHAR(50) COMMENT '집, 회사 등',
  `address`        VARCHAR(200) NOT NULL,
  `detail_address` VARCHAR(100),
  `zipcode`        VARCHAR(10),
  `latitude`       DECIMAL(10, 8),
  `longitude`      DECIMAL(11, 8),
  `is_default`     BOOLEAN   DEFAULT FALSE,
  `created_at`     TIMESTAMP DEFAULT (now()),
  `updated_at`     TIMESTAMP DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(512) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);



-- indexes
CREATE INDEX `users_index_0` ON `users` (`email`);
CREATE INDEX `users_index_1` ON `users` (`phone`);
CREATE INDEX `users_index_2` ON `users` (`user_type`);
CREATE UNIQUE INDEX `social_accounts_index_3` ON `social_accounts` (`provider`, `provider_id`);
CREATE INDEX `user_addresses_index_4` ON `user_addresses` (`user_id`);

-- foreign keys
ALTER TABLE `social_accounts`
  ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

ALTER TABLE `user_addresses`
  ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
