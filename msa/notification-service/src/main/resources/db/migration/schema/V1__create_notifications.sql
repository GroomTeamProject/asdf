-- notifications 테이블 생성
CREATE TABLE `notifications`
(
  `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id`    BIGINT       NOT NULL,
  `type`       ENUM('ORDER_STATUS','PROMOTION','REVIEW_REQUEST','DELIVERY_ARRIVAL')
               NOT NULL COMMENT '알림 타입',
  `title`      VARCHAR(100) NOT NULL,
  `content`    TEXT         NOT NULL,
  `data`       JSON COMMENT '알림 관련 추가 데이터',
  `is_read`    BOOLEAN   DEFAULT FALSE,
  `created_at` TIMESTAMP DEFAULT (now()),
  `updated_at` TIMESTAMP DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX `notifications_index_1` ON `notifications` (`user_id`);
CREATE INDEX `notifications_index_2` ON `notifications` (`type`);
CREATE INDEX `notifications_index_3` ON `notifications` (`is_read`);
