package io.goorm.team02.users.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.goorm.team02.dto.user.LoginRequest;
import io.goorm.team02.dto.user.LoginResponse;
import io.goorm.team02.users.domain.RefreshToken;
import io.goorm.team02.users.service.RefreshTokenService;
import io.goorm.team02.users.repository.UserinfoRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import io.goorm.team02.users.domain.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 로그인 요청 처리 및 JWT 발급 필터
 */
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserinfoRepository userRepository; // DB 조회용
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 로그인 시도
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            // 클라이언트 JSON 요청 -> LoginRequest 객체로 변환
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            // 이메일과 패스워드로 AuthenticationToken 생성
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

            // AuthenticationManager로 인증 진행
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 로그인 성공 시 JWT 발급
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication)
            throws IOException, ServletException {

        // 인증 성공한 이메일 가져오기
        String email = authentication.getName();

        // DB에서 user 가져오기 -> PK 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 인증 성공 시 JWT 발급 (email + userId 포함)
        String token = jwtTokenProvider.generateToken(authentication, user.getId());

        // Refresh Token 발급 및 DB 저장
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        
        // 응답: JSON으로 id, email, name, userType, token
        LoginResponse loginResponse = new LoginResponse(user.getId(), user.getEmail(), user.getName(), user.getUserType().name(), token, refreshToken.getToken());

        // 응답 헤더에 JWT 추가
        response.setHeader("Authorization", "Bearer " + token);
        response.setContentType("application/json");
        //response.getWriter().write(objectMapper.writeValueAsString(Map.of("token", token)));
        response.getWriter().write(objectMapper.writeValueAsString(loginResponse));
    }

    /**
     * 로그인 실패 시
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("error", "Login failed")));

            }
}