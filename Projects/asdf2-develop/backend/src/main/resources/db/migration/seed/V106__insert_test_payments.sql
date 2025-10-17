-- V106__insert_test_payments.sql
-- 테스트용 결제 데이터 (완성된 결제 프로세스)

-- 결제 데이터 (오더와 연결된 정상 데이터만)
INSERT INTO `payments` (`id`, `order_id`, `payment_key`, `payment_method`, `amount`, `status`, `pg_provider`, `pg_tid`, `approved_at`, `failed_reason`) VALUES
-- 완료된 결제 1: 김고객의 치킨 주문 (카드 결제)
(1, 1, 'payment_key_20250108_001', 'CARD', 24000.00, 'COMPLETED', 'TOSS', 'TOSS_TID_20250108_001', '2025-01-08 18:30:15', NULL),

-- 완료된 결제 2: 이고객의 중화요리 주문 (카카오페이)
(2, 2, 'payment_key_20250108_002', 'KAKAO_PAY', 19500.00, 'COMPLETED', 'KAKAO', 'KAKAO_TID_20250108_002', '2025-01-08 19:15:20', NULL),

-- 완료된 결제 3: 김고객의 치킨 주문 (토스페이먼츠)
(3, 3, 'payment_key_20250109_001', 'TOSS', 22000.00, 'COMPLETED', 'TOSS', 'TOSS_TID_20250109_001', '2025-01-09 12:00:10', NULL),

-- 대기 중인 결제 4: 이고객의 중화요리 주문 (아직 결제 안됨)
(4, 4, 'payment_key_20250109_002', 'CARD', 27500.00, 'PENDING', 'TOSS', NULL, NULL, NULL),

-- 취소된 결제 5: 김고객의 치킨 주문 (결제 후 취소)
(5, 5, 'payment_key_20250109_003', 'CARD', 21000.00, 'CANCELLED', 'TOSS', 'TOSS_TID_20250109_003', '2025-01-09 13:00:05', NULL);
