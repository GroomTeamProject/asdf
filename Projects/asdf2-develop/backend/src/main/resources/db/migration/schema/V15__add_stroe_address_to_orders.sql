-- V15__add_stroe_address_to_orders.sql
-- orders 테이블에 store_address, store_detail_address 칼럼 추가

-- 1. 먼저 NULL 허용으로 컬럼 추가
ALTER TABLE orders ADD COLUMN store_address VARCHAR(200);
ALTER TABLE orders ADD COLUMN store_detail_address VARCHAR(100);

-- 2. 기존 데이터에 기본값 설정
UPDATE orders SET store_address = '' WHERE store_address IS NULL;
UPDATE orders SET store_detail_address = '' WHERE store_detail_address IS NULL;

-- 3. NOT NULL 제약조건 추가 (MariaDB 문법)
ALTER TABLE orders MODIFY COLUMN store_address VARCHAR(200) NOT NULL;
ALTER TABLE orders MODIFY COLUMN store_detail_address VARCHAR(100) NOT NULL;
