package io.goorm.team02.core.owner.menus.service;

import io.goorm.team02.core.owner.stores.domain.Store;
import io.goorm.team02.core.owner.stores.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
     * 강화된 사용자 권한 검증 (userId 기반)
     */
    public Store getMyStore(Long currentUserId) {
        log.debug("강화된 권한 검증 시작 - 사용자 ID: {}", currentUserId);

        // 1. Rate Limiting 검사
        validateRateLimit(currentUserId);

        // 2. 기본 사용자 ID 검증
        validateUserId(currentUserId);

        // 3. 가게 접근 권한 검증
        Store store = validateStoreAccess(currentUserId);

        // 4. 추가 보안 검증
        validateAdditionalSecurity(currentUserId, store);

        log.debug("권한 검증 완료 - 사용자 ID: {}, 가게 ID: {}", currentUserId, store.getId());
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
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "요청 횟수가 너무 많습니다. 잠시 후 다시 시도해주세요.");
        }

        log.debug("Rate limit 검증 통과 - 사용자 ID: {}, 현재 요청 수: {}", userId, currentCount);
    }

    /**
     * 사용자 ID 기본 검증
     */
    private void validateUserId(Long userId) {
        if (userId == null) {
            log.warn("null 사용자 ID 접근 시도");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다");
        }

        if (userId <= 0) {
            log.warn("잘못된 사용자 ID 형식 - ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 사용자 정보입니다");
        }

        log.debug("사용자 ID 검증 완료 - ID: {}", userId);
    }

    /**
     * 가게 접근 권한 검증
     */
    private Store validateStoreAccess(Long userId) {
        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(userId)
                .orElse(null);

        if (store == null) {
            log.warn("등록된 가게가 없는 사용자의 접근 - 사용자 ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "등록된 가게가 없습니다");
        }

        // 가게 상태 검증
        if (!store.getIsActive()) {
            log.warn("비활성 가게 접근 시도 - 사용자 ID: {}, 가게 ID: {}", userId, store.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비활성화된 가게입니다");
        }

        // 사업자등록번호 유효성 재검증
        if (store.getBusinessNumber() == null || store.getBusinessNumber().trim().isEmpty()) {
            log.warn("사업자등록번호가 없는 가게 - 사용자 ID: {}, 가게 ID: {}", userId, store.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사업자 정보가 불완전합니다");
        }

        log.debug("가게 접근 권한 검증 완료 - 사용자 ID: {}, 가게 ID: {}, 가게명: {}",
                userId, store.getId(), store.getName());

        return store;
    }

    /**
     * 추가 보안 검증
     */
    private void validateAdditionalSecurity(Long userId, Store store) {
        // 의심스러운 활동 패턴 검사
        validateSuspiciousActivity(userId);

        // 가게와 사용자 매칭 재확인
        if (!store.getOwnerId().equals(userId)) {
            log.error("가게 소유자 불일치 - 사용자 ID: {}, 가게 소유자 ID: {}, 가게 ID: {}",
                    userId, store.getOwnerId(), store.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다");
        }

        log.debug("추가 보안 검증 완료 - 사용자 ID: {}", userId);
    }

    /**
     * 의심스러운 활동 패턴 검사
     */
    private void validateSuspiciousActivity(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        // 예시 1: 심야 시간대 (새벽 2-5시) 대량 요청 검사
        if (now.getHour() >= 2 && now.getHour() <= 5) {
            AtomicInteger nightCount = requestCounts.get(userId);
            if (nightCount != null && nightCount.get() > 50) {
                log.warn("심야 시간대 대량 요청 감지 - 사용자 ID: {}, 요청 수: {}",
                        userId, nightCount.get());
                // 필요시 추가 제한 로직 추가
            }
        }

        // 예시 2: 단시간 내 과도한 요청 패턴 검사
        AtomicInteger currentCount = requestCounts.get(userId);
        if (currentCount != null && currentCount.get() > 80) {
            log.warn("단시간 내 과도한 요청 패턴 - 사용자 ID: {}, 요청 수: {}",
                    userId, currentCount.get());
        }

        log.debug("의심스러운 활동 패턴 검사 완료 - 사용자 ID: {}", userId);
    }

    /**
     * 관리자 권한 검증 (특정 민감한 작업용)
     */
    public void validateAdminPermission(Long userId, String action) {
        log.info("관리자 권한 검증 시작 - 사용자 ID: {}, 작업: {}", userId, action);

        // 기본 가게 권한 먼저 확인
        Store store = getMyStore(userId);

        // 민감한 작업별 추가 검증
        switch (action) {
            case "FORCE_DELETE_CATEGORY":
                log.warn("강제 카테고리 삭제 시도 - 사용자 ID: {}, 가게 ID: {}", userId, store.getId());
                validateHighRiskOperation(userId, action);
                break;

            case "BULK_MENU_DELETE":
                log.warn("대량 메뉴 삭제 시도 - 사용자 ID: {}, 가게 ID: {}", userId, store.getId());
                validateHighRiskOperation(userId, action);
                break;

            case "STORE_DATA_EXPORT":
                log.info("가게 데이터 내보내기 - 사용자 ID: {}, 가게 ID: {}", userId, store.getId());
                validateDataExportPermission(userId);
                break;

            default:
                log.debug("일반 관리자 작업 - 사용자 ID: {}, 작업: {}", userId, action);
                break;
        }

        log.info("관리자 권한 검증 완료 - 사용자 ID: {}, 작업: {}", userId, action);
    }

    /**
     * 고위험 작업 검증
     */
    private void validateHighRiskOperation(Long userId, String action) {
        // 고위험 작업에 대한 추가 검증 로직
        AtomicInteger recentRequests = requestCounts.get(userId);

        if (recentRequests != null && recentRequests.get() > 5) {
            log.error("고위험 작업 과도한 시도 - 사용자 ID: {}, 작업: {}, 요청 수: {}",
                    userId, action, recentRequests.get());
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "민감한 작업은 제한된 횟수만 수행할 수 있습니다");
        }

        // 실제 환경에서는 여기서 추가 인증 요구 (SMS, 이메일 등)
        log.warn("고위험 작업 수행 중 - 사용자 ID: {}, 작업: {}", userId, action);
    }

    /**
     * 데이터 내보내기 권한 검증
     */
    private void validateDataExportPermission(Long userId) {
        // 데이터 내보내기는 특별한 권한이 필요할 수 있음
        log.info("데이터 내보내기 권한 검증 - 사용자 ID: {}", userId);

        // 실제 환경에서는 사용자의 데이터 처리 동의, GDPR 준수 등 확인
    }

    /**
     * 세션 기반 추가 검증 (필요시 사용)
     */
    public void validateSessionSecurity(Long userId, String sessionInfo) {
        log.debug("세션 보안 검증 - 사용자 ID: {}", userId);

        // JWT 기반이므로 제한적이지만, 필요시 추가 검증 로직
        // 예: Redis에 저장된 블랙리스트 토큰 확인
        // IP 주소 검증, User-Agent 검증 등

        if (sessionInfo != null && sessionInfo.contains("suspicious")) {
            log.warn("의심스러운 세션 정보 감지 - 사용자 ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비정상적인 접근이 감지되었습니다");
        }
    }

    /**
     * 캐시 정리 (메모리 누수 방지)
     */
    public void cleanupOldEntries() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(1);

        lastRequestTime.entrySet().removeIf(entry ->
                entry.getValue().isBefore(threshold));

        // 해당하는 요청 횟수도 함께 정리
        lastRequestTime.keySet().forEach(userId -> {
            if (!lastRequestTime.containsKey(userId)) {
                requestCounts.remove(userId);
            }
        });

        log.debug("오래된 캐시 엔트리 정리 완료");
    }

    /**
     * 특정 사용자의 Rate Limit 리셋 (관리자용)
     */
    public void resetRateLimit(Long userId) {
        requestCounts.remove(userId);
        lastRequestTime.remove(userId);
        log.info("사용자 Rate Limit 리셋 - 사용자 ID: {}", userId);
    }
}