package io.goorm.team02.core.auth.service;

import io.goorm.team02.core.auth.domain.RefreshToken;
import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.auth.repository.RefreshTokenRepository;
import io.goorm.team02.core.users.repository.UserinfoRepository;
import io.goorm.team02.core.auth.security.JwtTokenProvider;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.time.LocalDateTime;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               JwtTokenProvider jwtTokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    // DB에 저장할 JWT Refresh Token 생성
    public RefreshToken createRefreshToken(User user) {
        String token = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7)); // 7일 만료
        //refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(20)); // 테스트
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // 로그아웃시 refresh 토큰 삭제
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
            .ifPresent(refreshTokenRepository::delete);
    }

    // 비밀번호 변경시 토큰 삭제 , 모든 
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

}
