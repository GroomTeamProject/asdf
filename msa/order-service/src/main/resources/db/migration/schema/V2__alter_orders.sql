-- orders 테이블의 금액 컬럼들을 INT로 변경
ALTER TABLE `orders` MODIFY COLUMN `menu_total_amount` INT NOT NULL;
ALTER TABLE `orders` MODIFY COLUMN `delivery_fee` INT NOT NULL;
ALTER TABLE `orders` MODIFY COLUMN `discount_amount` INT NOT NULL;
ALTER TABLE `orders` MODIFY COLUMN `total_amount` INT NOT NULL;

-- order_items 테이블의 금액 컬럼들을 INT로 변경
ALTER TABLE `order_items` MODIFY COLUMN `menu_price` INT NOT NULL;
ALTER TABLE `order_items` MODIFY COLUMN `total_price` INT NOT NULL;

-- order_item_options 테이블의 금액 컬럼을 INT로 변경
ALTER TABLE `order_item_options` MODIFY COLUMN `additional_price` INT NOT NULL;
