-- V105__insert_test_users.sql
-- 테스트용 더미 데이터 삽입
INSERT INTO `users` (`email`, `password`, `phone`, `name`, `birth_date`, `user_type`, `is_active`, `email_verified`, `phone_verified`)
VALUES
    ('owner1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tWaIhQNKLWn.Iu', '010-1111-2222', '사장님1', '1975-03-10', 'OWNER', TRUE, TRUE, TRUE);