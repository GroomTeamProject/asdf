-- V14__add_rejected_status_to_orders.sql
-- orders 테이블의 status ENUM에 REJECTED 상태 추가

ALTER TABLE orders MODIFY COLUMN status ENUM('PENDING', 'ACCEPTED', 'COOKING', 'READY', 'PICKED_UP', 'DELIVERED', 'CANCELLED', 'REJECTED') DEFAULT 'PENDING';
