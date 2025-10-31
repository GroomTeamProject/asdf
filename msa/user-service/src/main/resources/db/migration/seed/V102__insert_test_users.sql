-- V102__insert_test_users.sql
-- 테스트용 사용자 데이터
INSERT INTO `users` (`id`, `email`, `password`, `phone`, `name`, `birth_date`, `user_type`, `is_active`, `email_verified`, `phone_verified`) VALUES
(5, 'rider1@test.com', 'password123', '01044444444', '김배달', '1990-01-01', 'RIDER', TRUE, TRUE, TRUE),
(6, 'rider2@test.com', 'password123', '01055555555', '박배달', '1992-05-15', 'RIDER', TRUE, TRUE, TRUE);
