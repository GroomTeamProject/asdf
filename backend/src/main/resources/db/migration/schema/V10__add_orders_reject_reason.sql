-- 주문 거절 사유 컬럼 추가
ALTER TABLE orders ADD COLUMN reject_reason VARCHAR(500) NULL COMMENT '주문 거절 사유';
