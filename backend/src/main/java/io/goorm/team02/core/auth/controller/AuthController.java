
package io.goorm.team02.core.auth.controller;

import io.goorm.team02.core.auth.controller.dto.LoginRequest;
import io.goorm.team02.core.auth.controller.dto.LoginResponse;
import io.goorm.team02.core.auth.controller.dto.SignupRequest;
import io.goorm.team02.core.auth.controller.dto.SignupResponse;
import io.goorm.team02.core.auth.service.AuthService;
import io.goorm.team02.core.users.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", ex.getMessage()));
        }
    }

}
