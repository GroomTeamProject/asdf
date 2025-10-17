package io.goorm.team02.core.auth.domain;

import io.goorm.team02.core.auth.controller.dto.LoginResponse;
import io.goorm.team02.core.auth.domain.RefreshToken;
import io.goorm.team02.core.auth.security.JwtTokenProvider;
import io.goorm.team02.core.auth.service.AuthService;
import io.goorm.team02.core.auth.service.RefreshTokenService;
import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.domain.enums.UserType;
import io.goorm.team02.core.users.repository.UserinfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserinfoRepository userRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        String email = "test@example.com";
        String password = "password123";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setName("홍길동");
        user.setUserType(UserType.CUSTOMER);
        user.setIsActive(true);

        Authentication authentication = mock(Authentication.class);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token-value");
        refreshToken.setUser(user);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication, user.getId())).thenReturn("jwt-token-value");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        // when
        LoginResponse response = authService.login(email, password);

        // 콘솔 출력
        System.out.println("===== LoginResponse =====");
        System.out.println("ID: " + response.getId());
        System.out.println("Email: " + response.getEmail());
        System.out.println("Name: " + response.getName());
        System.out.println("UserType: " + response.getUserType());
        System.out.println("AccessToken: " + response.getToken());
        System.out.println("RefreshToken: " + response.getRefreshtoken());
        System.out.println("========================");

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(email, response.getEmail());
        assertEquals("홍길동", response.getName());
        assertEquals(UserType.CUSTOMER, response.getUserType());
        assertEquals("jwt-token-value", response.getToken());
        assertEquals("refresh-token-value", response.getRefreshtoken());

        // Verify interactions
        verify(userRepository, times(1)).findByEmail(email);
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtTokenProvider, times(1)).generateToken(authentication, user.getId());
        verify(refreshTokenService, times(1)).createRefreshToken(user);
    }

    @Test
    @DisplayName("탈퇴계정 로그인 시도")
    void login_Fail_InactiveUser() {
        // given
        String email = "inactive@example.com";
        String password = "password123";

        User user = new User();
        user.setIsActive(false); // 탈퇴 계정

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(email, password));

        // 콘솔 출력
        System.out.println("Login Fail - Inactive User: " + ex.getMessage());
        assertEquals("탈퇴한 계정입니다.", ex.getMessage());
        
    }

    @Test
    @DisplayName("비밀번호 불일치")
    void login_Fail_InvalidCredentials() {
        // given
        String email = "test@example.com";
        String password = "wrongpassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(email, password));
        // 콘솔 출력
        System.out.println("Login Fail - Invalid Credentials: " + ex.getMessage());
        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    @DisplayName("없는 계정 로그인")
    void login_Fail_UserNotFound() {
        // given
        String email = "notfound@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(email, password));
        // 콘솔 출력
        System.out.println("Login Fail - User Not Found: " + ex.getMessage());
        assertEquals("User not found", ex.getMessage());
    }
}
