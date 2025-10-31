package io.goorm.team02.notification.controller;

import io.goorm.team02.notification.dto.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "알림", description = "알림 조회 API")
public interface NotificationApiDocs {

    @Operation(summary = "사용자 알림 조회", description = "특정 사용자의 알림을 조회합니다.")
    ResponseEntity<Page<NotificationResponse>> getUserNotificationsPage(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId,
            @Parameter(description = "페이징 정보") Pageable pageable);
}
