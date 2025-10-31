CREATE TABLE payments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          payment_key VARCHAR(255) NOT NULL UNIQUE,   -- Toss 등 PG사 고유 키
                          order_id BIGINT NOT NULL,                    -- Order Service의 주문 ID (DB 직접 참조X)
                          user_id BIGINT NOT NULL,                     -- 결제한 사용자 ID
                          amount DECIMAL(15,2) NOT NULL,
                          currency VARCHAR(10) DEFAULT 'KRW',
                          payment_method VARCHAR(50) NOT NULL,
                          pg_provider VARCHAR(50) NOT NULL,
                          status VARCHAR(20) NOT NULL,                -- PENDING / COMPLETED / FAILED
                          created_at DATETIME NOT NULL,
                          updated_at DATETIME,
                          CONSTRAINT uq_payment UNIQUE(payment_key)
);
