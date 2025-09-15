-- V109__update_auto_increment_values.sql
-- 각 테이블의 auto_increment 값을 갱신하여 ID 충돌 방지

-- orders 테이블 auto_increment 갱신 (주문 5까지 사용됨)
ALTER TABLE orders AUTO_INCREMENT = 6;

-- order_items 테이블 auto_increment 갱신 (order_item 15까지 사용됨)
ALTER TABLE order_items AUTO_INCREMENT = 16;

-- order_item_options 테이블 auto_increment 갱신 (option 15까지 사용됨)
ALTER TABLE order_item_options AUTO_INCREMENT = 16;

-- reviews 테이블 auto_increment 갱신 (리뷰 2까지 사용됨)
ALTER TABLE reviews AUTO_INCREMENT = 3;
