-- V108__insert_test_reviews.sql
-- 테스트용 리뷰 데이터 (완성된 리뷰 프로세스)

-- 리뷰 데이터 (오더와 연결된 정상 데이터만)
INSERT INTO `reviews` (`id`, `order_id`, `user_id`, `store_id`, `rating`, `content`, `image_urls`, `owner_reply`, `owner_replied_at`, `is_reported`) VALUES
-- 완료된 리뷰 1: 김고객이 치킨집에 작성한 리뷰 (사장님 답글 있음)
(1, 1, 1, 1, 5, '정말 맛있었어요! 바삭바삭하고 양념도 딱 좋았습니다. 배달도 빨랐고요. 다음에도 주문할게요!', '["https://example.com/review1_1.jpg", "https://example.com/review1_2.jpg"]', '감사합니다! 항상 신선한 재료로 맛있게 만들어드리겠습니다. 또 주문해주세요!', '2025-01-08 21:00:00', FALSE),

-- 완료된 리뷰 2: 이고객이 중화요리에 작성한 리뷰 (사장님 답글 없음)
(2, 2, 2, 2, 4, '짜장면이 정말 맛있었어요. 볶음밥도 괜찮았고요. 다만 배달 시간이 조금 늦었네요.', '["https://example.com/review2_1.jpg"]', NULL, NULL, FALSE);
