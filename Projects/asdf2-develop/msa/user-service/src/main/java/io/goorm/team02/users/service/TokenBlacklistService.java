package io.goorm.team02.users.service;


import io.goorm.team02.users.domain.TokenBlacklist;
import io.goorm.team02.users.repository.TokenBlacklistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TokenBlacklistService {

    private final TokenBlacklistRepository repository;

    public TokenBlacklistService(TokenBlacklistRepository repository) {
        this.repository = repository;
    }

    /**
     * 토큰의 jti와 만료시간으로 블랙리스트에 추가
     */
    @Transactional
    public void blacklistToken(String jti, Date expiresAtDate) {
        LocalDateTime expiresAt = LocalDateTime.ofInstant(expiresAtDate.toInstant(), ZoneId.systemDefault());
        System.out.println("[BLACKLIST SERVICE] Saving jti: " + jti + " expiresAt: " + expiresAt);
        
        TokenBlacklist entry = new TokenBlacklist();
        entry.setJti(jti);
        entry.setExpiresAt(expiresAt);
        repository.save(entry);
    }

    /**
     * 토큰이 블랙리스트에 존재하는지 확인
     */
    @Transactional(readOnly = true)
    public boolean isBlacklisted(String jti) {
        return repository.findByJti(jti).isPresent();
    }

    /**
     * 정리: 만료된 블랙리스트 항목 삭제 (스케줄러에서 주기적으로 호출)
     */
    @Transactional
    public void purgeExpired() {
        repository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
