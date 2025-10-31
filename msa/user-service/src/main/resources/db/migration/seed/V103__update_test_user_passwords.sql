-- V103__update_test_user_passwords
-- Encrypt user passwords.
UPDATE users SET password='$2b$10$REwoD5OB40AAUyPubK7Lp.lwisqSLmTDdVvRDXHOtbsTW.u82MNpy' WHERE id=1;
UPDATE users SET password='$2b$10$hc/h4vBGixp3Qny1zqQyQu6trQSqzOxGCkMWzk/dN7Y1fO583VYYm' WHERE id=2;
UPDATE users SET password='$2b$10$Vx6weAZyx5hQ8M6BJ8OAneHz6FDtFIVM/e86RpwLwueaIrmwEQPMW' WHERE id=3;
UPDATE users SET password='$2b$10$.9oGljVBahycz03lE0wueuWveYMp3ADrVAaBDZhQo.ILYTUcBZk.q' WHERE id=4;
UPDATE users SET password='$2b$10$oebsPNnxuxQAYxeKCrthjudnYT2T33PEeZxpqjHMmknh5AMC0g0nO' WHERE id=5;
UPDATE users SET password='$2b$10$uTPo6RMKOfhjhXVTR9pgGOCr3lfXc4BU6P0zVnDF1RtoJg8qpR1FW' WHERE id=6;