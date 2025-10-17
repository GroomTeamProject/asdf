package io.goorm.team02.users.config;

import io.goorm.team02.users.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import io.goorm.team02.users.security.RateLimitFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          RateLimitFilter rateLimitFilter /*...*/) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/v2/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 활성화
                .cors(cors -> cors.and())

                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // 세션을 STATELESS로 설정 (JWT 사용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 요청 허용 추가
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 공통접근
                        .requestMatchers("/api/auth/**", "/error").permitAll() // 로그인/회원가입 허용

                        // 마이페이지 접근 허용
                        .requestMatchers("/api/users/me/password",
                                "api/users/me/deactivate")
                        .authenticated()

                        // 역할별 접근(임시, api맞춰야함)
                        .requestMatchers("/api/stores/**").hasRole("CUSTOMER") // 이용자 전용
                        .requestMatchers("/api/orders/**").hasAnyRole("CUSTOMER", "OWNER", "RIDER")
                        .requestMatchers("/api/payments/**").hasRole("CUSTOMER")  // 결제창
                        .requestMatchers("/api/owner/**").hasRole("OWNER")
                        .requestMatchers("/api/rider/**").hasRole("RIDER")

                        .anyRequest().authenticated() // 나머지 요청 인증 필요
                )

                // 폼 로그인, HTTP Basic 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        // Rate limit filter를 먼저 거친 뒤 JWT 인증 필터
        http.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
        // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}