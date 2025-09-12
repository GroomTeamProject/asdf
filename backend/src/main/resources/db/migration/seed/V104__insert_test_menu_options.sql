-- V14__insert_test_menu_options.sql
-- 테스트용 메뉴 옵션 데이터
INSERT INTO `menu_options` (`id`, `menu_id`, `name`, `type`, `is_required`, `display_order`) VALUES
-- 치킨 사이즈 옵션
(1, 1, '사이즈', 'SINGLE', TRUE, 1),
(2, 2, '사이즈', 'SINGLE', TRUE, 1),
(3, 3, '사이즈', 'SINGLE', TRUE, 1),
-- 치킨 맵기 옵션
(4, 1, '맵기', 'SINGLE', FALSE, 2),
(5, 2, '맵기', 'SINGLE', FALSE, 2),
(6, 3, '맵기', 'SINGLE', FALSE, 2),
-- 음료 사이즈 옵션
(7, 5, '사이즈', 'SINGLE', TRUE, 1),
(8, 6, '사이즈', 'SINGLE', TRUE, 1);

-- 테스트용 메뉴 옵션 아이템 데이터
INSERT INTO `menu_option_items` (`id`, `option_id`, `name`, `additional_price`, `display_order`, `is_active`) VALUES
-- 치킨 사이즈 옵션 아이템
(1, 1, '소', 0.00, 1, TRUE),
(2, 1, '중', 2000.00, 2, TRUE),
(3, 1, '대', 4000.00, 3, TRUE),
(4, 2, '소', 0.00, 1, TRUE),
(5, 2, '중', 2000.00, 2, TRUE),
(6, 2, '대', 4000.00, 3, TRUE),
(7, 3, '소', 0.00, 1, TRUE),
(8, 3, '중', 2000.00, 2, TRUE),
(9, 3, '대', 4000.00, 3, TRUE),
-- 치킨 맵기 옵션 아이템
(10, 4, '순한맛', 0.00, 1, TRUE),
(11, 4, '보통맛', 0.00, 2, TRUE),
(12, 4, '매운맛', 0.00, 3, TRUE),
(13, 5, '순한맛', 0.00, 1, TRUE),
(14, 5, '보통맛', 0.00, 2, TRUE),
(15, 5, '매운맛', 0.00, 3, TRUE),
(16, 6, '순한맛', 0.00, 1, TRUE),
(17, 6, '보통맛', 0.00, 2, TRUE),
(18, 6, '매운맛', 0.00, 3, TRUE),
-- 음료 사이즈 옵션 아이템
(19, 7, '소', 0.00, 1, TRUE),
(20, 7, '중', 500.00, 2, TRUE),
(21, 7, '대', 1000.00, 3, TRUE),
(22, 8, '소', 0.00, 1, TRUE),
(23, 8, '중', 500.00, 2, TRUE),
(24, 8, '대', 1000.00, 3, TRUE);
