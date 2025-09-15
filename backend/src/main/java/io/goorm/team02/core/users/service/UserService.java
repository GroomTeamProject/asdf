
package io.goorm.team02.core.users.service;

import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.domain.enums.UserType;
import io.goorm.team02.core.users.controller.dto.ProfileUpdateRequest;
import io.goorm.team02.core.auth.controller.dto.SignupRequest;
import io.goorm.team02.core.auth.controller.dto.SignupResponse;
import io.goorm.team02.core.users.repository.UserinfoRepository;
import io.goorm.team02.core.users.domain.UserAddress;
import io.goorm.team02.core.users.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

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
    // 비밀번호 변경
    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // 새 비밀번호 인코딩 후 저장
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 계정삭제
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 기존 이메일 가져오기
        String originalEmail = user.getEmail();

        // 개인정보 마스킹 : 이메일로
        user.setEmail("deleted_" + userId + "_" + originalEmail);
        user.setName("탈퇴회원"+"("+user.getName()+")");

        user.setIsActive(false); // 비활성화

        userRepository.save(user);
    }

}
