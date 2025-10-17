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
  `is_reported`      BOOLEAN   DEFAULT FALSE,
  `created_at`       TIMESTAMP DEFAULT (now()),
  `updated_at`       TIMESTAMP DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

-- indexes
CREATE UNIQUE INDEX `reviews_index_29` ON `reviews` (`order_id`);
CREATE INDEX `reviews_index_30` ON `reviews` (`user_id`);
CREATE INDEX `reviews_index_31` ON `reviews` (`store_id`);

-- foreign keys
ALTER TABLE `reviews`
  ADD FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);
ALTER TABLE `reviews`
  ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
ALTER TABLE `reviews`
  ADD FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`);
