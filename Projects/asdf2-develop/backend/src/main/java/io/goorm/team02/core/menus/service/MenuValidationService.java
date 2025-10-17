package io.goorm.team02.core.menus.service;

import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.domain.TempUser;
import io.goorm.team02.core.stores.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuValidationService {

    private final StoreRepository storeRepository;

    // Rate Limiting을 위한 메모리 캐시 (실제로는 Redis 사용 권장)
    private final ConcurrentHashMap<Long, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, LocalDateTime> lastRequestTime = new ConcurrentHashMap<>();

    /**
     * 강화된 사용자 권한 검증
     */
    public Store getMyStore(TempUser currentUser) {
        log.debug("강화된 권한 검증 시작 - 사용자 ID: {}", currentUser.getId());

        // 1. Rate Limiting 검사
        validateRateLimit(currentUser.getId());

        // 2. 기본 사용자 상태 검증
        validateUserStatus(currentUser);

        // 3. 계정 보안 검증
        validateAccountSecurity(currentUser);

        // 4. 가게 권한 검증
        Store store = validateStoreAccess(currentUser);

        // 5. 세션 유효성 검증 (필요시)
        validateSessionSecurity(currentUser);

        log.debug("권한 검증 완료 - 가게 ID: {}", store.getId());
        return store;
    }

    /**
     * Rate Limiting 검증 (DDoS 방지)
     */
    private void validateRateLimit(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastRequest = lastRequestTime.get(userId);

        // 1분 단위로 요청 횟수 리셋
        if (lastRequest == null || lastRequest.isBefore(now.minusMinutes(1))) {
            requestCounts.put(userId, new AtomicInteger(1));
            lastRequestTime.put(userId, now);
            return;
        }

        AtomicInteger count = requestCounts.computeIfAbsent(userId, k -> new AtomicInteger(0));
        int currentCount = count.incrementAndGet();

        // 분당 100회 요청 제한
        if (currentCount > 100) {
            log.warn("Rate limit 초과 - 사용자 ID: {}, 요청 횟수: {}", userId, currentCount);
            throw new SecurityException("요청 횟수가 너무 많습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    /**
     * 사용자 상태 검증
     */
    private void validateUserStatus(TempUser currentUser) {
        if (currentUser == null) {
            throw new SecurityException("인증 정보가 없습니다");
        }

        if (!currentUser.getIsActive()) {
            log.warn("비활성화된 계정 접근 시도 - 사용자 ID: {}", currentUser.getId());
            throw new SecurityException("비활성화된 계정입니다");
        }

//        if (!currentUser.getEmailVerified()) {
//            log.warn("이메일 미인증 계정 접근 시도 - 사용자 ID: {}", currentUser.getId());
//            throw new SecurityException("이메일 인증이 필요합니다");
//        }
//
//        // 전화번호 인증도 필수로 변경
//        if (!currentUser.getPhoneVerified()) {
//            log.warn("전화번호 미인증 계정 접근 시도 - 사용자 ID: {}", currentUser.getId());
//            throw new SecurityException("전화번호 인증이 필요합니다");
//        }
    }

    /**
     * 계정 보안 검증
     */
    private void validateAccountSecurity(TempUser currentUser) {
        // 비밀번호 변경 필요 여부 확인 (90일)
        if (currentUser.getUpdatedAt() != null &&
                currentUser.getUpdatedAt().isBefore(LocalDateTime.now().minusDays(90))) {
            log.warn("비밀번호 변경이 오래된 계정 - 사용자 ID: {}", currentUser.getId());
            // 경고만 하고 진행 (필요시 강제 변경 가능)
        }

        // 의심스러운 활동 패턴 검사
        validateSuspiciousActivity(currentUser);
    }

    /**
     * 의심스러운 활동 패턴 검사
     */
    private void validateSuspiciousActivity(TempUser currentUser) {
        // 실제 구현에서는 더 정교한 패턴 분석 필요
        LocalDateTime now = LocalDateTime.now();

        // 예시: 심야 시간대 (새벽 2-5시) 대량 요청 검사
        if (now.getHour() >= 2 && now.getHour() <= 5) {
            AtomicInteger nightCount = requestCounts.get(currentUser.getId());
            if (nightCount != null && nightCount.get() > 50) {
                log.warn("심야 시간대 대량 요청 감지 - 사용자 ID: {}, 요청 수: {}",
                        currentUser.getId(), nightCount.get());
                // 필요시 추가 인증 요구
            }
        }
    }

    /**
     * 가게 접근 권한 검증
     */
    private Store validateStoreAccess(TempUser currentUser) {
        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser.getId())
                .orElse(null);

        if (store == null) {
            log.warn("등록된 가게가 없는 사용자의 접근 - 사용자 ID: {}", currentUser.getId());
            throw new SecurityException("등록된 가게가 없습니다");
        }

        // 가게 상태 검증
        if (!store.getIsActive()) {
            log.warn("비활성 가게 접근 시도 - 가게 ID: {}", store.getId());
            throw new SecurityException("비활성화된 가게입니다");
        }

        // 사업자등록번호 유효성 재검증 (필요시)
        if (store.getBusinessNumber() == null || store.getBusinessNumber().trim().isEmpty()) {
            log.warn("사업자등록번호가 없는 가게 - 가게 ID: {}", store.getId());
            throw new SecurityException("사업자 정보가 불완전합니다");
        }

        return store;
    }

    /**
     * 세션 보안 검증
     */
    private void validateSessionSecurity(TempUser currentUser) {
        // JWT 토큰 기반이므로 추가 세션 검증은 제한적
        // 필요시 Redis에 블랙리스트 토큰 확인 등 구현

        // IP 주소 검증 (같은 세션에서 IP가 자주 바뀌는 경우)
        // User-Agent 검증 등...
    }

    /**
     * 관리자 권한 검증
     */
    public void validateAdminPermission(TempUser currentUser, String action) {
        // 특정 민감한 작업에 대한 추가 권한 검증
        Store store = getMyStore(currentUser);

        switch (action) {
            case "FORCE_DELETE_CATEGORY":
            case "BULK_MENU_DELETE":
                // 추가 인증 요구 (예: SMS 인증, 이메일 확인 등)
                log.info("민감한 작업 수행 - 사용자 ID: {}, 작업: {}", currentUser.getId(), action);
                break;
        }
    }
}