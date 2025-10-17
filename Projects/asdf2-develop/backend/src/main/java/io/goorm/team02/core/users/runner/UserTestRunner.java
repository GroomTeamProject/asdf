/*package io.goorm.team02.core.users.runner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.domain.enums.UserType;
import io.goorm.team02.core.users.repository.UserRepository;


import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class UserTestRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        // 1. 테스트용 사용자 생성
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword("1234");          // 테스트용, 실제는 BCrypt 등으로 암호화
        user.setPhone("01012345678");
        user.setName("테스트 사용자");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setUserType(UserType.CUSTOMER); // UserType Enum 값에 맞게 설정
        user.setIsActive(true);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);

        // 연관 리스트 초기화
        user.setSocialAccounts(new ArrayList<>());
        user.setAddresses(new ArrayList<>());

        userRepository.save(user);

        // 2. 저장된 사용자 조회
        userRepository.findByEmail("testuser@example.com").ifPresent(u -> {
            System.out.println("User found:");
            System.out.println("ID: " + u.getId());
            System.out.println("Email: " + u.getEmail());
            System.out.println("Name: " + u.getName());
            System.out.println("Phone: " + u.getPhone());
            System.out.println("BirthDate: " + u.getBirthDate());
            System.out.println("UserType: " + u.getUserType());
        });
    }
}*/
