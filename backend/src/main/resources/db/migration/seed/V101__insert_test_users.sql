-- V11__insert_test_users.sql
-- 테스트용 사용자 데이터
INSERT INTO `users` (`id`, `email`, `password`, `phone`, `name`, `birth_date`, `user_type`, `is_active`, `email_verified`, `phone_verified`) VALUES
(1, 'customer1@test.com', 'password123', '01012345678', '김고객', '1990-01-01', 'CUSTOMER', TRUE, TRUE, TRUE),
(2, 'customer2@test.com', 'password123', '01023456789', '이고객', '1992-05-15', 'CUSTOMER', TRUE, TRUE, TRUE),
(3, 'owner1@test.com', 'password123', '01034567890', '박사장', '1985-03-20', 'OWNER', TRUE, TRUE, TRUE),
(4, 'owner2@test.com', 'password123', '01045678901', '최사장', '1988-07-10', 'OWNER', TRUE, TRUE, TRUE),
(5,'rider1@test.com','password123','01044444444','김라이더','1998-06-24','RIDER',TRUE,TRUE,TRUE),
(6,'rider2@test.com','password123','01055555555','박라이더','1922-02-02','RIDER',TRUE,TRUE,TRUE);

-- 테스트용 사용자 주소 데이터
INSERT INTO `user_addresses` (`user_id`, `address_name`, `address`, `detail_address`, `zipcode`, `latitude`, `longitude`, `is_default`) VALUES
(1, '집', '서울시 강남구 역삼동 123-45', '101호', '06292', 37.5665, 126.9780, TRUE),
(1, '회사', '서울시 서초구 서초동 456-78', '5층', '06620', 37.4947, 127.0276, FALSE),
(2, '집', '서울시 마포구 홍대입구역 789-12', '203호', '04066', 37.5563, 126.9226, TRUE);
