package io.goorm.team02.users.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class RateLimitService {

    private final Map<String, BucketEntry> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    // 기본 정책 (로그인/가입은 각각 다르게 만들 수 있으므로 factory 사용)
    public Bucket newLoginBucket() {
        // 예: 5 요청 / 1분 (burst 허용)
        Refill refill = Refill.greedy(5, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(5, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    public Bucket newSignupBucket() {
        // 예: 10 요청 / 1분
        Refill refill = Refill.greedy(10, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(10, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    public Bucket resolveBucket(String key, EndpointType type) {
        BucketEntry entry = cache.computeIfAbsent(key, k -> new BucketEntry(createBucketByType(type)));
        entry.lastAccess = Instant.now();
        return entry.bucket;
    }

    private Bucket createBucketByType(EndpointType type) {
        return switch (type) {
            case LOGIN -> newLoginBucket();
            case SIGNUP -> newSignupBucket();
            case GENERIC -> newLoginBucket(); // 기본
        };
    }

    @PostConstruct
    private void startCleaner() {
        // 1분마다 캐시 항목 중 30분 이상 안쓴 항목 제거
        cleaner.scheduleAtFixedRate(() -> {
            Instant now = Instant.now();
            cache.entrySet().removeIf(e -> Duration.between(e.getValue().lastAccess, now).toMinutes() > 30);
        }, 1, 1, TimeUnit.MINUTES);
    }

    @PreDestroy
    private void shutdown() {
        cleaner.shutdownNow();
    }

    private static class BucketEntry {
        final Bucket bucket;
        Instant lastAccess;
        BucketEntry(Bucket bucket) {
            this.bucket = bucket;
            this.lastAccess = Instant.now();
        }
    }

    public enum EndpointType {
        LOGIN, SIGNUP, GENERIC
    }
}