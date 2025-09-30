
package io.goorm.team02.core.users.service;

import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.domain.enums.UserType;
import io.goorm.team02.core.auth.controller.dto.SignupRequest;
import io.goorm.team02.core.auth.controller.dto.SignupResponse;
import io.goorm.team02.core.users.repository.UserinfoRepository;
import io.goorm.team02.core.users.domain.UserAddress;
import io.goorm.team02.core.users.repository.UserAddressRepository;
import io.goorm.team02.core.users.controller.dto.UserUpdateRequest;
import io.goorm.team02.core.users.controller.dto.UserAddressRequest;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
// 회원가입 로직, user 정보/ user_adress 정보 각각의 db에 저장
public class UserService {

    private final UserinfoRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAddressRepository userAddressRepository;

    /**
     * 사용자 ID로 사용자 정보 조회
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
    }

    /**
     * 사용자 주소 목록 조회
     */
    public List<UserAddress> getUserAddresses(Long userId) {
        return userAddressRepository.findByUserId(userId);
    }

    /**
     * 사용자 정보 업데이트
     */
    @Transactional
    public User updateUser(Long userId, UserUpdateRequest request) {
        User user = getUserById(userId);

        // 전화번호 전처리 (-, 공백 등 제거 후 숫자만 남김)
        String cleanedPhone = null;
        if (request.getPhone() != null) {
            cleanedPhone = request.getPhone().replaceAll("[^0-9]", "");
            // 전화번호 중복 체크 (전처리된 값 기준)
            if (userRepository.findByPhone(cleanedPhone).isPresent()) {
                throw new RuntimeException("Phone number already exists");
            }
        }
        
        /*/ 전화번호 중복 체크 (다른 사용자가 사용 중인지 확인)
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.findByPhone(request.getPhone()).isPresent()) {
                throw new RuntimeException("이미 사용 중인 전화번호입니다.");
            }
        }*/
        
        // 업데이트할 필드만 변경
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (cleanedPhone!= null) {
            user.setPhone(cleanedPhone);
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        
        return userRepository.save(user);
    }

    /**
     * 사용자 주소 생성
     */
    @Transactional
    public UserAddress createUserAddress(Long userId, UserAddressRequest request) {
        System.out.println("createUserAddress called with userId: " + userId + ", request: " + request);
        User user = getUserById(userId);
        System.out.println("User found: " + user);
        
        // 기본 주소로 설정하는 경우, 기존 기본 주소들을 false로 변경
        if (request.getIsDefault() != null && request.getIsDefault()) {
            List<UserAddress> existingAddresses = userAddressRepository.findByUserId(userId);
            existingAddresses.forEach(addr -> addr.setIsDefault(false));
            userAddressRepository.saveAll(existingAddresses);
        }
        
        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setAddressName(request.getAddressName());
        address.setAddress(request.getAddress());
        address.setDetailAddress(request.getDetailAddress());
        address.setZipcode(request.getZipcode());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        
        System.out.println("Saving address: " + address);
        UserAddress savedAddress = userAddressRepository.save(address);
        System.out.println("Address saved successfully: " + savedAddress);
        return savedAddress;
    }

    /**
     * 사용자 주소 수정
     */
    @Transactional
    public UserAddress updateUserAddress(Long userId, Long addressId, UserAddressRequest request) {
        User user = getUserById(userId);
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("주소를 찾을 수 없습니다: " + addressId));
        
        // 주소가 해당 사용자의 것인지 확인
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 사용자의 주소가 아닙니다.");
        }
        
        // 기본 주소로 설정하는 경우, 기존 기본 주소들을 false로 변경
        if (request.getIsDefault() != null && request.getIsDefault()) {
            List<UserAddress> existingAddresses = userAddressRepository.findByUserId(userId);
            existingAddresses.stream()
                    .filter(addr -> !addr.getId().equals(addressId))
                    .forEach(addr -> addr.setIsDefault(false));
            userAddressRepository.saveAll(existingAddresses);
        }
        
        // 업데이트할 필드만 변경
        if (request.getAddressName() != null) {
            address.setAddressName(request.getAddressName());
        }
        if (request.getAddress() != null) {
            address.setAddress(request.getAddress());
        }
        if (request.getDetailAddress() != null) {
            address.setDetailAddress(request.getDetailAddress());
        }
        if (request.getZipcode() != null) {
            address.setZipcode(request.getZipcode());
        }
        if (request.getLatitude() != null) {
            address.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            address.setLongitude(request.getLongitude());
        }
        if (request.getIsDefault() != null) {
            address.setIsDefault(request.getIsDefault());
        }
        
        return userAddressRepository.save(address);
    }

    /**
     * 사용자 주소 삭제
     */
    @Transactional
    public void deleteUserAddress(Long userId, Long addressId) {
        User user = getUserById(userId);
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("주소를 찾을 수 없습니다: " + addressId));
        
        // 주소가 해당 사용자의 것인지 확인
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 사용자의 주소가 아닙니다.");
        }
        
        userAddressRepository.delete(address);
    }

    /**
     * 기본 배송지 설정
     */
    @Transactional
    public UserAddress setDefaultAddress(Long userId, Long addressId) {
        try {
            System.out.println("setDefaultAddress called with userId: " + userId + ", addressId: " + addressId);
            
            // 사용자 존재 확인
            User user = getUserById(userId);
            System.out.println("User found with ID: " + user.getId());
            
            // 주소 존재 확인
            UserAddress address = userAddressRepository.findById(addressId)
                    .orElseThrow(() -> new RuntimeException("주소를 찾을 수 없습니다: " + addressId));
            System.out.println("Address found with ID: " + address.getId());
            
            // 주소가 해당 사용자의 것인지 확인
            if (!address.getUser().getId().equals(userId)) {
                throw new RuntimeException("해당 사용자의 주소가 아닙니다.");
            }
            
            // 기존 기본 주소들을 모두 false로 변경
            List<UserAddress> existingAddresses = userAddressRepository.findByUserId(userId);
            System.out.println("Found " + existingAddresses.size() + " existing addresses");
            
            for (UserAddress existingAddr : existingAddresses) {
                existingAddr.setIsDefault(false);
            }
            userAddressRepository.saveAll(existingAddresses);
            
            // 선택한 주소를 기본 주소로 설정
            address.setIsDefault(true);
            System.out.println("Setting address " + addressId + " as default");
            
            UserAddress savedAddress = userAddressRepository.save(address);
            System.out.println("Address saved successfully with ID: " + savedAddress.getId());
            
            return savedAddress;
        } catch (Exception e) {
            System.err.println("Error in setDefaultAddress: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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


    public SignupResponse registerUser(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // 전화번호 전처리 (-, 공백 등 제거 후 숫자만 남김)
        String cleanedPhone = null;
        if (request.getPhone() != null) {
            cleanedPhone = request.getPhone().replaceAll("[^0-9]", "");
            // 전화번호 중복 체크 (전처리된 값 기준)
            if (userRepository.findByPhone(cleanedPhone).isPresent()) {
                throw new RuntimeException("Phone number already exists");
            }
        }

        /*// 전화번호 중복 체크
        if (request.getPhone() != null && userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Phone number already exists");
        }*/

        // 비밀번호 일치 확인
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // User 생성
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        //user.setPhone(request.getPhone());
        user.setPhone(cleanedPhone);
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

}
