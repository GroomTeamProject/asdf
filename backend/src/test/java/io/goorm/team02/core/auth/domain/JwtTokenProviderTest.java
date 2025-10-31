package io.goorm.team02.core.auth.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.goorm.team02.core.auth.security.JwtTokenProvider;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        // 테스트용 secret key 설정
        jwtTokenProvider.setSecret("testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"); // 최소 32바이트 이상 필요
        jwtTokenProvider.init();
    }

    @Test
    void testGenerateAndValidateToken() {
        Authentication auth = new UsernamePasswordAuthenticationToken("test@example.com", null, Collections.emptyList());
        String token = jwtTokenProvider.generateToken(auth, 1L);

        System.out.println("=== Access Token ===");
        System.out.println(token);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));

        String email = jwtTokenProvider.getEmailFromToken(token);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        System.out.println("Email from token: " + email);
        System.out.println("UserId from token: " + userId);

        assertEquals("test@example.com", jwtTokenProvider.getEmailFromToken(token));
        assertEquals(1L, jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void testGenerateRefreshToken() {
        String refreshToken = jwtTokenProvider.generateRefreshToken("test@example.com", 1L);

        System.out.println("=== Refresh Token ===");
        System.out.println(refreshToken);

        assertNotNull(refreshToken);
        assertTrue(jwtTokenProvider.validateToken(refreshToken));
        assertEquals("test@example.com", jwtTokenProvider.getEmailFromToken(refreshToken));
    }

    @Test
    void testGetJtiFromToken() {
        Authentication auth = new UsernamePasswordAuthenticationToken("test@example.com", null, Collections.emptyList());
        String token = jwtTokenProvider.generateToken(auth, 1L);

        String jti = jwtTokenProvider.getJtiFromToken(token);
        System.out.println("JTI from token: " + jti);
        assertNotNull(jti);
    }

    @Test
    void testGetExpirationFromToken() {
        Authentication auth = new UsernamePasswordAuthenticationToken("test@example.com", null, Collections.emptyList());
        String token = jwtTokenProvider.generateToken(auth, 1L);

        Date exp = jwtTokenProvider.getExpirationFromToken(token);
        System.out.println("Expiration from token: " + exp);
        assertTrue(exp.after(new Date())); // 만료일은 현재보다 미래여야 함
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.value";

        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    /*@Test
    void testExpiredToken() throws InterruptedException {
        // 1초 만료 토큰 생성용 JwtTokenProvider
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider();
        shortLivedProvider.setSecret("testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"); // 최소 32바이트 이상 필요
        shortLivedProvider.init();

        // Reflection을 이용해 EXPIRATION 값을 1초로 변경
        try {
            java.lang.reflect.Field field = JwtTokenProvider.class.getDeclaredField("EXPIRATION");
            field.setAccessible(true);
            field.setLong(shortLivedProvider, 1000L); // 1초
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Authentication auth = new UsernamePasswordAuthenticationToken("test@example.com", null, Collections.emptyList());
        String token = shortLivedProvider.generateToken(auth, 1L);
        System.out.println("[TEST] 생성된 토큰: " + token);

        // 1) 바로 검증
        boolean validNow = shortLivedProvider.validateToken(token);
        System.out.println("[TEST] 토큰 생성 직후 유효성: " + validNow);
        assertTrue(validNow);

        // 1.5초 대기 → 토큰 만료
        // 3) 만료 대기
        System.out.println("[TEST] 토큰 만료 대기 1.5초...");
        Thread.sleep(1500);

        // 4) 만료 후 검증
        boolean validAfterExpiry = shortLivedProvider.validateToken(token);
        System.out.println("[TEST] 만료 후 토큰 유효성: " + validAfterExpiry);
        assertFalse(validAfterExpiry, "만료된 토큰은 유효하지 않아야 함");
    }*/
}
