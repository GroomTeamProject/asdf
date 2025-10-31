CREATE TABLE refunds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    refund_key VARCHAR(255) NOT NULL UNIQUE,    -- PG사 환불 고유 키
    amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL,                -- PENDING / COMPLETED / FAILED
    reason VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT fk_payment_refund FOREIGN KEY (payment_id) REFERENCES payments(id)
);
