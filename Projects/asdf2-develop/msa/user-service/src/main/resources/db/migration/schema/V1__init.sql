CREATE TABLE orders
(
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_message VARCHAR(100) NOT NULL,
  created_at TIMESTAMP DEFAULT current_timestamp,
  updated_at TIMESTAMP DEFAULT current_timestamp
);
