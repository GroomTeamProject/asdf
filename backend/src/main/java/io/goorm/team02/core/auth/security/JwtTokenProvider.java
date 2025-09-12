package io.goorm.team02.core.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    //private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512); // 안전한 512비트 키

    private final Key key;
    private final long EXPIRATION = 1000L * 60 * 60; // 1시간

    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes); // 고정된 키 사용
    }
    
    // 이메일+ userId(PK) 포함
    public String generateToken(Authentication authentication, Long userId) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .setSubject(username)   // 기존이메일
                .claim("userId", userId)  // Pk 추가
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512) // HS512 + 고정 키
                //.signWith(key) // HS512 안전한 키 사용
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