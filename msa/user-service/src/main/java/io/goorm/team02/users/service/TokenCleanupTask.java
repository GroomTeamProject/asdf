package io.goorm.team02.users.service;

import io.goorm.team02.users.service.TokenBlacklistService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanupTask {

    private final TokenBlacklistService tokenBlacklistService;

    public TokenCleanupTask(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    // 하루에 한 번 또는 적절한 주기로 실행
    @Scheduled(cron = "0 0 3 * * ?") // 매일 3시
    public void cleanup() {
        tokenBlacklistService.purgeExpired();
    }
}