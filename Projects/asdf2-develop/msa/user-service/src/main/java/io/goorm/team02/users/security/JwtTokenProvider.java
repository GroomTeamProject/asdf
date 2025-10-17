package io.goorm.team02.users.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.goorm.team02.users.domain.User;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    //private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512); // 안전한 512비트 키
    @Value("${jwt.secret}")
    private String secret;
    private Key key;

    public void setSecret(String secret) {  // 테스트용
        this.secret = secret;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private final long EXPIRATION = 1000L * 60 * 10; // 1000L * 60 * 10;
    private final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7일  1000L * 60 * 60 * 24 * 7;

    // access 토큰 : 이메일+ userId(PK) 포함
    public String generateToken(Authentication authentication, Long userId) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION);
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setSubject(username)   // 기존이메일
                .claim("userId", userId)  // Pk 추가
                .setId(jti) // // jti 설정
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key) // HS512 안전한 키 사용
                .compact();
    }

    // refresh 토큰
    public String generateRefreshToken(String email, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_EXPIRATION);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // access 토큰 재발급. ??  
    public String generateTokenFromUser(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
        }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // user_id 추출
    public Long getUserIdFromToken(String token) {
        return ((Number) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId")).longValue();
    }

    // jti 추출
    public String getJtiFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getId();
    }

    // 만료시간 읽어서
    public Date getExpirationFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    // jwt검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
