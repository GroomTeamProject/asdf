package io.goorm.team02.core.auth.domain;

import io.goorm.team02.core.auth.controller.AuthController;
import io.goorm.team02.core.auth.security.JwtTokenProvider;
import io.goorm.team02.core.auth.service.RefreshTokenService;
import io.goorm.team02.core.auth.service.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerLogoutTest {

    private AuthController authController;
    private RefreshTokenService refreshTokenService;
    private TokenBlacklistService blacklistService;
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        refreshTokenService = mock(RefreshTokenService.class);
        blacklistService = mock(TokenBlacklistService.class);
        jwtTokenProvider = new JwtTokenProvider();
        jwtTokenProvider.setSecret("testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"); // 최소 32바이트
        jwtTokenProvider.init();

        authController = new AuthController(
                null, // AuthService 필요 없으므로 null
                null, // UserService 필요 없으므로 null
                refreshTokenService,
                blacklistService,
                jwtTokenProvider
        );
    }

    @Test
    void testLogout_blacklistAccessTokenAndDeleteRefreshToken() {
        // Access Token 생성
        String token = jwtTokenProvider.generateToken(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "test@example.com", null
                ), 1L
        );

        // Mock HttpServletResponse
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Logout 호출
        var result = authController.logout("dummy-refresh-token", "Bearer " + token, response);

        // RefreshToken 삭제 검증
        verify(refreshTokenService).deleteRefreshToken("dummy-refresh-token");

        // AccessToken 블랙리스트 등록 검증
        ArgumentCaptor<String> jtiCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Date> expCaptor = ArgumentCaptor.forClass(Date.class);
        verify(blacklistService).blacklistToken(jtiCaptor.capture(), expCaptor.capture());

        assertNotNull(jtiCaptor.getValue());
        assertNotNull(expCaptor.getValue());

        // Response body 확인
        assertTrue(result.getBody() instanceof Map);
        assertEquals("Logged out successfully", ((Map<?, ?>) result.getBody()).get("message"));
    }
}
