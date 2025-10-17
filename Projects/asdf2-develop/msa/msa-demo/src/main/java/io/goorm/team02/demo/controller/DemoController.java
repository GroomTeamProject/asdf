package io.goorm.team02.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.goorm.team02.security.annotation.CurrentUser;
import io.goorm.team02.demo.dto.OrderResponse;
import io.goorm.team02.demo.dto.TestEventResponse;
import io.goorm.team02.demo.service.DemoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/demo")
public class DemoController {

    private final DemoService demoService;

    /**
     * 테스트 API
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(demoService.getOrderById(id));
    }

    /**
     * 이벤트 조회 테스트
     */
    @GetMapping("/events")
    public TestEventResponse getTestEvents() {
        return demoService.getEvents();
    }

    /**
     * 이벤트 발행 테스트
     */
    @PostMapping("/events")
    public TestEventResponse publishTestEvent() {
        return demoService.publishEvent();
    }

    /**
     * 동기 이벤트 발행 테스트
     */
    @PostMapping("/events/sync")
    public TestEventResponse publishSyncTestEvent() {
        return demoService.publishEventSync();
    }

    /**
     * @CurrentUser 테스트
     */
    @PostMapping("/private-route")
    public ResponseEntity<Long> publishCurrentUserTestEvent(@CurrentUser Long userId) {
        return ResponseEntity.ok(userId);
    }
}
