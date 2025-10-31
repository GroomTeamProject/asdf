package io.goorm.team02.notification.dto;

import io.goorm.team02.notification.entity.Notification;
import io.goorm.team02.notification.entity.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "알림 응답 DTO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    @Schema(description = "알림 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "123")
    private Long userId;

    @Schema(description = "알림 타입", example = "ORDER_STATUS")
    private NotificationType type;

    @Schema(description = "알림 제목", example = "주문이 접수되었습니다")
    private String title;

    @Schema(description = "알림 내용", example = "주문번호 123이 접수되었습니다.")
    private String content;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환
     */
    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
