-- reviews 테이블 생성
CREATE TABLE `reviews`
(
  `id`               BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id`         BIGINT  NOT NULL,
  `user_id`          BIGINT  NOT NULL,
  `store_id`         BIGINT  NOT NULL,
  `rating`           TINYINT NOT NULL COMMENT '1-5점',
  `content`          TEXT,
  `image_urls`       JSON COMMENT '["url1", "url2", "url3"]',
  `owner_reply`      TEXT,
  `owner_replied_at` TIMESTAMP,
  `created_at`       TIMESTAMP DEFAULT (now()),
  `updated_at`       TIMESTAMP DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE UNIQUE INDEX `reviews_index_1` ON `reviews` (`order_id`);
CREATE INDEX `reviews_index_2` ON `reviews` (`user_id`);
CREATE INDEX `reviews_index_3` ON `reviews` (`store_id`);