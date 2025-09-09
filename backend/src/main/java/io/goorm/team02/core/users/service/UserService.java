
package io.goorm.team02.core.users.service;

import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.domain.enums.UserType;
<<<<<<< HEAD
import io.goorm.team02.core.auth.controller.dto.SignupRequest;
import io.goorm.team02.core.auth.controller.dto.SignupResponse;
=======
import io.goorm.team02.core.users.controller.dto.ProfileUpdateRequest;
import io.goorm.team02.core.users.controller.dto.SignupRequest;
import io.goorm.team02.core.users.controller.dto.SignupResponse;
>>>>>>> dcc87e2 (feat: 마이페이지(조회, 수정))
import io.goorm.team02.core.users.repository.UserinfoRepository;
import io.goorm.team02.core.users.domain.UserAddress;
import io.goorm.team02.core.users.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.goorm.team02.core.users.controller.dto.ProfileUpdateRequest;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
// 회원가입 로직, user 정보/ user_adress 정보 각각의 db에 저장
public class UserService {

    private final UserinfoRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAddressRepository userAddressRepository;

    public SignupResponse registerUser(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // 전화번호 중복 체크
        if (request.getPhone() != null && userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Phone number already exists");
        }

        // 비밀번호 일치 확인
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // User 생성
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType());
        user.setIsActive(true);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);

        User savedUser = userRepository.save(user);

        // UserAddress 저장
        UserAddress address = new UserAddress();
        address.setUser(savedUser);
        address.setAddressName(request.getAddressName());
        address.setAddress(request.getAddress());
        address.setDetailAddress(request.getDetailAddress());
        address.setZipcode(request.getZipcode());
        address.setIsDefault(request.getIsDefault());

        userAddressRepository.save(address);

        // ✅ Response DTO 반환 : email, name, user_type
        return new SignupResponse(savedUser.getEmail(), savedUser.getName(), savedUser.getUserType());
    }

    // ✅ 이메일로 사용자 조회
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ 프로필 업데이트
    @Transactional
    public User updateUserProfile(String email, ProfileUpdateRequest request) {
        User user = getUserByEmail(email);
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        return userRepository.save(user);
    }

}
