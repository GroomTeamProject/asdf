-- V11__insert_test_stores.sql
-- 테스트용 가게 데이터
INSERT INTO `stores` (`id`, `owner_id`, `business_number`, `name`, `description`, `phone`, `address`, `detail_address`, `latitude`, `longitude`, `category`, `min_order_amount`, `delivery_fee`, `delivery_time_min`, `delivery_time_max`, `status`, `rating`, `review_count`, `image_url`, `is_active`) VALUES
(1, 3, '123-45-67890', '맛있는 치킨집', '바삭바삭한 치킨과 다양한 사이드 메뉴를 제공합니다.', '02-1234-5678', '서울시 강남구 테헤란로 123', '1층', 37.5665, 126.9780, 'CHICKEN', 15000.00, 3000.00, 30, 45, 'OPEN', 4.5, 150, 'https://example.com/chicken.jpg', TRUE),
(2, 4, '234-56-78901', '정통 중화요리', '정통 중국 요리를 맛볼 수 있는 곳입니다.', '02-2345-6789', '서울시 서초구 서초대로 456', '2층', 37.4947, 127.0276, 'CHINESE', 20000.00, 2500.00, 25, 40, 'OPEN', 4.2, 89, 'https://example.com/chinese.jpg', TRUE);

-- 테스트용 가게 운영시간 데이터
INSERT INTO `store_hours` (`store_id`, `day_of_week`, `open_time`, `close_time`, `is_closed`) VALUES
-- 치킨집 운영시간 (매일 11:00-23:00)
(1, 0, '11:00:00', '23:00:00', FALSE), -- 일요일
(1, 1, '11:00:00', '23:00:00', FALSE), -- 월요일
(1, 2, '11:00:00', '23:00:00', FALSE), -- 화요일
(1, 3, '11:00:00', '23:00:00', FALSE), -- 수요일
(1, 4, '11:00:00', '23:00:00', FALSE), -- 목요일
(1, 5, '11:00:00', '23:00:00', FALSE), -- 금요일
(1, 6, '11:00:00', '23:00:00', FALSE), -- 토요일
-- 중화요리 운영시간 (화-일 12:00-22:00, 월요일 휴무)
(2, 0, '12:00:00', '22:00:00', FALSE), -- 일요일
(2, 1, NULL, NULL, TRUE), -- 월요일 휴무
(2, 2, '12:00:00', '22:00:00', FALSE), -- 화요일
(2, 3, '12:00:00', '22:00:00', FALSE), -- 수요일
(2, 4, '12:00:00', '22:00:00', FALSE), -- 목요일
(2, 5, '12:00:00', '22:00:00', FALSE), -- 금요일
(2, 6, '12:00:00', '22:00:00', FALSE); -- 토요일
