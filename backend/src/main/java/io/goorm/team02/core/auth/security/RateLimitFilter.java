package io.goorm.team02.core.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.goorm.team02.core.auth.service.RateLimitService;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        // 로그인/회원가입 대상만 체크 (필요하면 다른 엔드포인트 추가)
        RateLimitService.EndpointType endpointType = null;
        if ("/api/auth/login".equalsIgnoreCase(path) && "POST".equalsIgnoreCase(request.getMethod())) {
            endpointType = RateLimitService.EndpointType.LOGIN;
        } else if ("/api/auth/signup".equalsIgnoreCase(path) && "POST".equalsIgnoreCase(request.getMethod())) {
            endpointType = RateLimitService.EndpointType.SIGNUP;
        }

        if (endpointType != null) {
            String clientKey = resolveClientKey(request);
            var bucket = rateLimitService.resolveBucket(clientKey, endpointType);

            // tryConsumeAndReturnRemaining 사용하면 남은 토큰 확인 가능
            boolean consumed = bucket.tryConsume(1);
            if (!consumed) {
                // 초과 -> 429
                response.setStatus(429);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                // Retry-After : 초 단위로 남은 refill 시간 계산(간단히 60초)
                response.setHeader("Retry-After", "60");
                response.getWriter().write("{\"error\":\"Too many requests\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientKey(HttpServletRequest request) {
        // X-Forwarded-For 우선, 없으면 remoteAddr
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            // 첫번째 IP 사용
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
