
package io.goorm.team02.core.auth.controller;

import io.goorm.team02.core.auth.controller.dto.LoginRequest;
import io.goorm.team02.core.auth.controller.dto.LoginResponse;
import io.goorm.team02.core.auth.controller.dto.SignupRequest;
import io.goorm.team02.core.auth.controller.dto.SignupResponse;
import io.goorm.team02.core.auth.service.AuthService;
import io.goorm.team02.core.auth.service.RefreshTokenService;
import io.goorm.team02.core.users.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    // ✅ 회원가입 API
    @PostMapping("/signup")
    //@RequestBody SignupRequest request→ 클라이언트에서 보낸 JSON 데이터를 SignupRequest 객체로 변환
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        // 실제 회원가입 처리 : 받은 데이터로 userservice에서 하는 회원가입처리
        //userService.registerUser(request); 
        //return ResponseEntity.ok("User registered successfully");
        SignupResponse response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    // ✅ 로그인 API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.login(request.getEmail(), request.getPassword());
            // JSON에는 Access Token만 내려주고
            LoginResponse responseBody = new LoginResponse(
                loginResponse.getId(),
                loginResponse.getEmail(),
                loginResponse.getName(),
                loginResponse.getUserType(),
                loginResponse.getToken(), // Access Token
                null // refreshToken 제거
            );

            ResponseCookie cookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshtoken())
                    .httpOnly(true)
                    .secure(false) // HTTPS 환경에서는 true
                    .path("/api/auth/refresh")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());

            return ResponseEntity.ok(responseBody);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", ex.getMessage()));
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                HttpServletResponse response) {

        // 1) 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/auth/refresh")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", deleteCookie.toString());

        // 2) DB에서 Refresh Token 삭제
        if (refreshToken != null) {
            refreshTokenService.deleteRefreshToken(refreshToken);
        }

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }


}
