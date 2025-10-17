-- orders 테이블에 주문 취소 사유 컬럼 추가
ALTER TABLE orders ADD COLUMN cancel_reason VARCHAR(500) NULL COMMENT '주문 취소 사유';

-- orders 테이블에 주문 거절 시간 컬럼 추가
ALTER TABLE orders ADD COLUMN rejected_at DATETIME NULL COMMENT '주문 거절 시간';
