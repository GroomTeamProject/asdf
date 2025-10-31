CREATE TABLE IF NOT EXISTS token_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    jti VARCHAR(128) NOT NULL, -- JWT의 고유 ID
    expires_at TIMESTAMP NOT NULL, -- 만료 시간
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_jti (jti)
);

