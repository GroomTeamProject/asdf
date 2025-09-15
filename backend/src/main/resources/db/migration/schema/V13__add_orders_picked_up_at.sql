-- orders 테이블에 picked_up_at 컬럼 추가
ALTER TABLE orders ADD COLUMN picked_up_at TIMESTAMP NULL COMMENT '픽업 시간';
