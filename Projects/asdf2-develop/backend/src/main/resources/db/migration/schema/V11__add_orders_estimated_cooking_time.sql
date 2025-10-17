-- orders 테이블에 예상 조리 시간 컬럼 추가
ALTER TABLE orders ADD COLUMN min_cooking_time INT NULL COMMENT '최소 조리 시간 (분)';
ALTER TABLE orders ADD COLUMN max_cooking_time INT NULL COMMENT '최대 조리 시간 (분)';
