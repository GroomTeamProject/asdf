CREATE TABLE `deliveries`
(
  `id`               BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id`         BIGINT NOT NULL,
  `rider_id`         BIGINT,
  `pickup_address`   TEXT   NOT NULL,
  `delivery_address` TEXT   NOT NULL,
  `distance_km`      DECIMAL(5, 2),
  `estimated_time`   INT COMMENT '예상 배달 시간(분)',
  `status`           ENUM('REQUESTED','ACCEPTED','PICKED_UP','DELIVERED','CANCELLED')
                     DEFAULT 'REQUESTED' COMMENT '배달 상태',
  `requested_at`     TIMESTAMP DEFAULT (now()),
  `accepted_at`      TIMESTAMP,
  `picked_up_at`     TIMESTAMP,
  `delivered_at`     TIMESTAMP,
  `created_at`       TIMESTAMP DEFAULT (now()),
  `updated_at`       TIMESTAMP DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
  `delivery_fee`     DECIMAL(10, 2) DEFAULT 0
);

CREATE TABLE `delivery_locations`
(
  `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
  `delivery_id` BIGINT         NOT NULL,
  `latitude`    DECIMAL(10, 8) NOT NULL,
  `longitude`   DECIMAL(11, 8) NOT NULL,
  `created_at`  TIMESTAMP DEFAULT (now())
);

-- indexes
CREATE INDEX `deliveries_index_24` ON `deliveries` (`order_id`);
CREATE INDEX `deliveries_index_25` ON `deliveries` (`rider_id`);
CREATE INDEX `deliveries_index_26` ON `deliveries` (`status`);
CREATE INDEX `delivery_locations_index_27` ON `delivery_locations` (`delivery_id`);
CREATE INDEX `delivery_locations_index_28` ON `delivery_locations` (`created_at`);

-- foreign keys
ALTER TABLE `deliveries`
  ADD FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);
ALTER TABLE `deliveries`
  ADD FOREIGN KEY (`rider_id`) REFERENCES `users` (`id`);
ALTER TABLE `delivery_locations`
  ADD FOREIGN KEY (`delivery_id`) REFERENCES `deliveries` (`id`) ON DELETE CASCADE;
