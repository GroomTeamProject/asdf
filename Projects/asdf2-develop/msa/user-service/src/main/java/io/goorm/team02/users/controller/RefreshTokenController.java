package io.goorm.team02.users.controller;


import io.goorm.team02.users.domain.RefreshToken;
import io.goorm.team02.users.service.RefreshTokenService;
import io.goorm.team02.common.exception.errors.UnauthorizedException;
import io.goorm.team02.users.security.JwtTokenProvider;
import io.goorm.team02.users.controller.dto.RefreshRequest;
import io.goorm.team02.users.controller.dto.RefreshResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshTokenController(RefreshTokenService refreshTokenService,
                                  JwtTokenProvider jwtTokenProvider) {
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshTokenCookie) {
        if (refreshTokenCookie == null) { // 쿠키 없음 
            //throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token missing. Please login again.");
            throw new UnauthorizedException("Refresh token missing. Please login again.");
        }

        // db에 없는 토큰
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenCookie)
                //.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        // 만료 체크 : RuntimeException는 500 에러
        if (refreshToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            //throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired. Please login again.");
            throw new UnauthorizedException("Refresh token expired. Please login again.");
        }

        // Access Token 재발급
        String newAccessToken = jwtTokenProvider.generateTokenFromUser(refreshToken.getUser());

        // Response 반환
        return ResponseEntity.ok(new RefreshResponse(newAccessToken, refreshToken.getToken()));
    }
}
