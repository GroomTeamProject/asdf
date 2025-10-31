-- V12__insert_test_menus.sql
-- 테스트용 메뉴 카테고리 데이터
INSERT INTO `menu_categories` (`id`, `store_id`, `name`, `display_order`, `is_active`) VALUES
(1, 1, '치킨', 1, TRUE),
(2, 1, '사이드 메뉴', 2, TRUE),
(3, 1, '음료', 3, TRUE),
(4, 2, '중화요리', 1, TRUE),
(5, 2, '면류', 2, TRUE),
(6, 2, '밥류', 3, TRUE);

-- 테스트용 메뉴 데이터
INSERT INTO `menus` (`id`, `store_id`, `category_id`, `name`, `description`, `price`, `image_url`, `is_popular`, `is_recommended`, `status`, `display_order`) VALUES
-- 치킨집 메뉴
(1, 1, 1, '후라이드 치킨', '바삭바삭한 후라이드 치킨', 18000.00, 'https://example.com/fried_chicken.jpg', TRUE, TRUE, 'AVAILABLE', 1),
(2, 1, 1, '양념 치킨', '달콤짭짤한 양념 치킨', 19000.00, 'https://example.com/seasoned_chicken.jpg', TRUE, FALSE, 'AVAILABLE', 2),
(3, 1, 1, '간장 치킨', '진한 간장 소스의 치킨', 19000.00, 'https://example.com/soy_chicken.jpg', FALSE, TRUE, 'AVAILABLE', 3),
(4, 1, 2, '치킨무', '치킨과 함께 먹는 무', 0.00, 'https://example.com/radish.jpg', FALSE, FALSE, 'AVAILABLE', 1),
(5, 1, 2, '콜라', '시원한 콜라', 2000.00, 'https://example.com/cola.jpg', FALSE, FALSE, 'AVAILABLE', 1),
(6, 1, 2, '사이다', '시원한 사이다', 2000.00, 'https://example.com/sprite.jpg', FALSE, FALSE, 'AVAILABLE', 2),
-- 중화요리 메뉴
(7, 2, 4, '짜장면', '정통 중국 짜장면', 8000.00, 'https://example.com/jjajang.jpg', TRUE, TRUE, 'AVAILABLE', 1),
(8, 2, 4, '짬뽕', '해산물이 가득한 짬뽕', 9000.00, 'https://example.com/jjamppong.jpg', TRUE, FALSE, 'AVAILABLE', 2),
(9, 2, 5, '볶음밥', '야채와 고기가 들어간 볶음밥', 10000.00, 'https://example.com/fried_rice.jpg', FALSE, TRUE, 'AVAILABLE', 1);
