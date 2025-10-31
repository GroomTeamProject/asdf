package io.goorm.team02.core.owner.auth.filter;


import io.goorm.team02.core.owner.auth.service.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtils; // JwtUtils 사용

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = jwtUtils.extractTokenFromHeader(authHeader);

        if (token != null && jwtUtils.validateToken(token)) { // 토큰 검증 추가
            try {
                Long userId = jwtUtils.getUserIdFromToken(token);
                log.info("JWT에서 추출한 사용자 ID: {}", userId);

                // request에 userId 설정
                request.setAttribute("userId", userId);

                // Spring Security 컨텍스트 설정
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (Exception e) {
                log.error("JWT 토큰 처리 중 오류: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}