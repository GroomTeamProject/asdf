package io.goorm.team02.notification.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SSEConnectionService {

    private final Map<Long, SseEmitter> userConnections = new ConcurrentHashMap<>();

    /**
     * SSE 연결 등록
     * 
     * @param userId 사용자 ID
     * @return SseEmitter
     */
    public SseEmitter createConnection(Long userId) {
        log.info("사용자 {} SSE 연결 요청", userId);

        // 프록시 idle timeout보다 크게 설정 (15분)
        SseEmitter emitter = new SseEmitter(15 * 60 * 1000L); // 15분 타임아웃
        
        // 연결 완료/타임아웃/에러 시 정리
        emitter.onCompletion(() -> {
            userConnections.remove(userId);
            log.info("사용자 {} SSE 연결 완료", userId);
        });        
        emitter.onTimeout(() -> {
            userConnections.remove(userId);
            log.info("사용자 {} SSE 연결 타임아웃", userId);
        });        
        emitter.onError((ex) -> {
            userConnections.remove(userId);
            log.error("사용자 {} SSE 연결 에러: {}", userId, ex.getMessage());
        });
        
        userConnections.put(userId, emitter);
        log.info("사용자 {} SSE 연결 등록", userId);
        
        // 초기 이벤트 전송으로 연결 확정
        try {
            emitter.send(event().name("connected").data("ok"));
            log.info("사용자 {} 초기 연결 이벤트 전송 완료", userId);
        } catch (Exception e) {
            log.error("사용자 {} 초기 연결 이벤트 전송 실패: {}", userId, e.getMessage());
            userConnections.remove(userId);
            throw new RuntimeException("SSE 연결 초기화 실패", e);
        }
        
        return emitter;
    }

    /**
     * 사용자 연결 조회
     * 
     * @param userId 사용자 ID
     * @return SseEmitter
     */
    public SseEmitter getConnection(Long userId) {
        return userConnections.get(userId);
    }

    /**
     * 사용자 연결 제거
     * 
     * @param userId 사용자 ID
     */
    public void removeConnection(Long userId) {
        userConnections.remove(userId);
    }

    /**
     * 사용자 연결 상태 확인
     * 
     * @param userId 사용자 ID
     * @return 연결 상태
     */
    public boolean isUserConnected(Long userId) {
        return userConnections.containsKey(userId);
    }

    /**
     * 정기 하트비트 전송 (25초 주기)
     */
    @Scheduled(fixedRate = 25000)
    public void sendHeartbeat() {
        if (userConnections.isEmpty()) {
            return;
        }
        
        log.info("하트비트 전송 시작 - 연결된 사용자 수: {}", userConnections.size());
        
        userConnections.entrySet().removeIf(entry -> {
            Long userId = entry.getKey();
            SseEmitter emitter = entry.getValue();
            
            try {
                emitter.send(event().name("heartbeat").data("ping"));
                return false; // 연결 유지
            } catch (Exception e) {
                log.warn("사용자 {} 하트비트 전송 실패, 연결 제거: {}", userId, e.getMessage());
                return true; // 연결 제거
            }
        });
    }

}
