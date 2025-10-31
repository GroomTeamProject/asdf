-- 주문 테이블에 가게 정보 컬럼 추가
ALTER TABLE orders
    ADD COLUMN store_name VARCHAR(255) NOT NULL COMMENT '가게 이름';

ALTER TABLE orders
    ADD COLUMN store_phone VARCHAR(20) NOT NULL COMMENT '가게 전화번호';

