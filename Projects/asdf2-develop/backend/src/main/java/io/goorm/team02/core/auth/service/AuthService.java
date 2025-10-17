package io.goorm.team02.core.auth.service;

import io.goorm.team02.core.auth.controller.dto.LoginResponse;
import io.goorm.team02.core.auth.domain.RefreshToken;
import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.repository.UserinfoRepository;
import io.goorm.team02.core.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserinfoRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public LoginResponse login(String email, String password) {
        try {

            // DB에서 사용자 정보 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // 탈퇴 여부 확인 
            if (!user.getIsActive()) { // isActive: Soft Delete용 필드
                throw new RuntimeException("탈퇴한 계정입니다.");  // 휴면 계정입니다로 수정
            }
            
            // 이메일/비밀번호 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // 인증 성공 → JWT 발급
            String token = jwtTokenProvider.generateToken(authentication,user.getId());

            // Refresh Token 발급 및 DB 저장
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            // LoginResponse 반환
            return new LoginResponse(user.getId(), user.getEmail(), user.getName(), user.getUserType(), token, refreshToken.getToken());

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}
