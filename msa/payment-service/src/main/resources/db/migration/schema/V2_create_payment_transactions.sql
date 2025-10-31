CREATE TABLE payment_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,                  -- APPROVAL / CANCEL / FAIL 등
    status VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    pg_transaction_id VARCHAR(255),             -- PG사에서 발급하는 트랜잭션 ID
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT fk_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);
