package io.goorm.team02.core.owner.stores.listeners;

import io.goorm.team02.core.owner.common.service.S3Service;
import io.goorm.team02.core.owner.stores.events.ImageCleanupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageCleanupEventListener {

    private final S3Service s3Service;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleImageCleanup(ImageCleanupEvent event) {
        try {
            s3Service.deleteFile(event.getImageUrl());
        } catch (Exception e) {
            // 비치명적 오류 - 별도 배치에서 정리
        }
    }
}
