-- V104__create_users_table.sql
CREATE TABLE `users`
(
    `id`             BIGINT PRIMARY KEY AUTO_INCREMENT,
    `email`          VARCHAR(100) NOT NULL UNIQUE COMMENT '이메일 (로그인 ID)',
    `password`       VARCHAR(255) COMMENT '암호화된 비밀번호',
    `phone`          VARCHAR(20) UNIQUE COMMENT '휴대폰 번호',
    `name`           VARCHAR(50) NOT NULL COMMENT '사용자 이름',
    `birth_date`     DATE COMMENT '생년월일',
    `user_type`      ENUM('OWNER', 'CUSTOMER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER' COMMENT '사용자 유형',
    `is_active`      BOOLEAN DEFAULT TRUE COMMENT '계정 활성화 여부',
    `email_verified` BOOLEAN DEFAULT FALSE COMMENT '이메일 인증 여부',
    `phone_verified` BOOLEAN DEFAULT FALSE COMMENT '휴대폰 인증 여부',
    `created_at`     TIMESTAMP DEFAULT (now()),
    `updated_at`     TIMESTAMP DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

-- Social Accounts 테이블
CREATE TABLE `user_social_accounts`
(
    `id`           BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id`      BIGINT NOT NULL,
    `provider`     ENUM('GOOGLE', 'NAVER', 'KAKAO') NOT NULL COMMENT '소셜 로그인 제공자',
    `provider_id`  VARCHAR(100) NOT NULL COMMENT '소셜 제공자의 사용자 ID',
    `created_at`   TIMESTAMP DEFAULT (now()),
    `updated_at`   TIMESTAMP DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

-- User Addresses 테이블
CREATE TABLE `user_addresses`
(
    `id`           BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id`      BIGINT NOT NULL,
    `alias`        VARCHAR(20) COMMENT '주소 별칭 (집, 회사 등)',
    `address`      VARCHAR(200) NOT NULL COMMENT '기본 주소',
    `detail_address` VARCHAR(100) COMMENT '상세 주소',
    `postal_code`  VARCHAR(10) COMMENT '우편번호',
    `is_default`   BOOLEAN DEFAULT FALSE COMMENT '기본 주소 여부',
    `created_at`   TIMESTAMP DEFAULT (now()),
    `updated_at`   TIMESTAMP DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

-- Indexes 생성
CREATE INDEX `users_index_01` ON `users` (`email`);
CREATE INDEX `users_index_02` ON `users` (`phone`);
CREATE INDEX `users_index_03` ON `users` (`user_type`);
CREATE INDEX `users_index_04` ON `users` (`is_active`);
CREATE INDEX `user_social_accounts_index_05` ON `user_social_accounts` (`user_id`);
CREATE INDEX `user_social_accounts_index_06` ON `user_social_accounts` (`provider`, `provider_id`);
CREATE INDEX `user_addresses_index_07` ON `user_addresses` (`user_id`);

-- Foreign Keys 생성
ALTER TABLE `user_social_accounts`
    ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

ALTER TABLE `user_addresses`
    ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;