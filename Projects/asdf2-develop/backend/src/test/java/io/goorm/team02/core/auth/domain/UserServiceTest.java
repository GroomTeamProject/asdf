package io.goorm.team02.core.auth.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.goorm.team02.core.users.repository.UserAddressRepository;
import io.goorm.team02.core.users.repository.UserinfoRepository;
import io.goorm.team02.core.auth.controller.dto.SignupRequest;
import io.goorm.team02.core.auth.controller.dto.SignupResponse;
import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.domain.UserAddress;
import io.goorm.team02.core.users.domain.enums.UserType;
import io.goorm.team02.core.users.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserinfoRepository userRepository;

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_WithAddress_Success() {
        // given
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setPasswordCheck("password123");
        request.setName("홍길동");
        request.setPhone("01012345678");
        request.setUserType(UserType.CUSTOMER);

        // 주소 정보
        request.setAddressName("집");
        request.setAddress("서울시 강남구 테헤란로");
        request.setDetailAddress("101동 202호");
        request.setZipcode("06234");
        request.setIsDefault(true);

        // Repository Mock 설정
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(request.getPhone())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setEmail(request.getEmail());
        savedUser.setName(request.getName());
        savedUser.setUserType(request.getUserType());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserAddress savedAddress = new UserAddress();
        savedAddress.setAddressName(request.getAddressName());
        savedAddress.setAddress(request.getAddress());

        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(savedAddress);

        // when
        SignupResponse response = userService.registerUser(request);

        // then 콘솔 검증
        System.out.println("===== User 저장 확인 =====");
        System.out.println("email: " + response.getEmail());
        System.out.println("name: " + response.getName());
        System.out.println("userType: " + response.getUserType());

        // then
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("홍길동", response.getName());
        assertEquals(UserType.CUSTOMER, response.getUserType());

        // User 저장 검증
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("test@example.com", capturedUser.getEmail());
        assertEquals("encodedPassword", capturedUser.getPassword());

        // Address 저장 검증
        ArgumentCaptor<UserAddress> addressCaptor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository, times(1)).save(addressCaptor.capture());
        UserAddress capturedAddress = addressCaptor.getValue();
        assertEquals("집", capturedAddress.getAddressName());
        assertEquals("서울시 강남구 테헤란로", capturedAddress.getAddress());
        assertEquals(savedUser, capturedAddress.getUser()); // User 연관 확인

        //콘솔 출력
        System.out.println("===== Address 저장 확인 =====");
        System.out.println("addressName: " + savedAddress.getAddressName());
        System.out.println("address: " + savedAddress.getAddress());
        System.out.println("detailAddress: " + savedAddress.getDetailAddress());
        System.out.println("zipcode: " + savedAddress.getZipcode());
        System.out.println("isDefault: " + savedAddress.getIsDefault());
        }
        

    @Test
    void testRegisterUser_EmailAlreadyExists_ThrowsException() {
        // given
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setPasswordCheck("password123");
        request.setName("홍길동");
        request.setPhone("01012345678");
        request.setUserType(UserType.CUSTOMER);

        // 주소 정보
        request.setAddressName("집");
        request.setAddress("서울시 강남구 테헤란로");
        request.setDetailAddress("101동 202호");
        request.setZipcode("06234");
        request.setIsDefault(true);

        // 이메일 존재 설정
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(request);
        });

        // when & then : 콘솔 검증 
        try {
            userService.registerUser(request);
            System.out.println("❌ 테스트 실패: 예외가 발생하지 않음");
        } catch (RuntimeException e) {
            if ("Email already exists".equals(e.getMessage())) {
                System.out.println("✅ 테스트 성공: 이메일 중복 예외 발생");
            } else {
                System.out.println("❌ 테스트 실패: 예외 메시지 불일치 - " + e.getMessage());
            }
        }

        assertEquals("Email already exists", exception.getMessage());

        // 전화번호/주소 저장이 호출되지 않아야 함
        verify(userRepository, never()).save(any(User.class));
        verify(userAddressRepository, never()).save(any(UserAddress.class));
    }

    @Test
    void testRegisterUser_PhoneDuplicate() {
        // given
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setPasswordCheck("password123");
        request.setName("홍길동");
        request.setPhone("010-1234-5678");
        request.setUserType(UserType.CUSTOMER);

        // 주소 정보
        request.setAddressName("집");
        request.setAddress("서울시 강남구 테헤란로");
        request.setDetailAddress("101동 202호");
        request.setZipcode("06234");
        request.setIsDefault(true);

        // 이메일은 사용 가능
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // 전화번호 중복
        User existingUser = new User();
        existingUser.setPhone("01012345678");
        when(userRepository.findByPhone("01012345678")).thenReturn(Optional.of(existingUser));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(request);
        });

        // 콘솔 검증
        System.out.println("전화번호 중복 예외 메시지: " + exception.getMessage());

        // 예외 메시지 확인
        assert(exception.getMessage().contains("Phone number already exists"));
    }

}

