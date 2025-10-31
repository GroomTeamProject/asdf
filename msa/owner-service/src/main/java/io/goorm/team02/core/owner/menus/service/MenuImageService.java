package io.goorm.team02.core.owner.menus.service;

import io.goorm.team02.core.owner.common.service.S3Service;
import io.goorm.team02.core.owner.menus.domain.Menu;
import io.goorm.team02.core.owner.menus.repository.MenuRepository;
import io.goorm.team02.core.owner.stores.domain.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MenuImageService {

    private final MenuRepository menuRepository;
    private final MenuValidationService menuValidationService;
    private final S3Service s3Service;
    private final SecureFileValidationService fileValidationService;

    /**
     * 메뉴 이미지 업로드
     */
    @Transactional
    public String uploadMenuImage(Long currentUser, Long menuId, MultipartFile file) {
        log.info("=== 메뉴 이미지 업로드 시작 - 사용자 ID: {}, 메뉴 ID: {} ===", currentUser, menuId);

        fileValidationService.validateImageFile(file);

        Store store = menuValidationService.getMyStore(currentUser);

        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

        // 파일 유효성 검증
        validateImageFile(file);

        try {
            String oldImageUrl = menu.getImageUrl();
            String newImageUrl = s3Service.uploadMenuImage(file, store.getId());

            menu.updateImageUrl(newImageUrl);
            menuRepository.save(menu);

            // 기존 이미지가 있다면 S3에서 삭제
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                try {
                    s3Service.deleteFile(oldImageUrl);
                    log.info("기존 메뉴 이미지 삭제 완료: {}", oldImageUrl);
                } catch (Exception e) {
                    log.warn("기존 메뉴 이미지 삭제 실패 (무시): {}", e.getMessage());
                }
            }

            log.info("메뉴 이미지 업로드 완료! 사용자 ID: {}, 메뉴: {}, 이미지 URL: {}",
                    currentUser, menu.getName(), newImageUrl);
            return newImageUrl;

        } catch (Exception e) {
            log.error("메뉴 이미지 업로드 실패 - 사용자 ID: {}, 메뉴 ID: {}", currentUser, menuId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 메뉴 이미지 정보 조회
     */
    public Map<String, Object> getMenuImageInfo(Long currentUser, Long menuId) {
        log.info("메뉴 이미지 정보 조회 - 사용자 ID: {}, 메뉴 ID: {}", currentUser, menuId);

        Store store = menuValidationService.getMyStore(currentUser);
        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

        Map<String, Object> imageInfo = new HashMap<>();
        imageInfo.put("menuId", menu.getId());
        imageInfo.put("menuName", menu.getName());
        imageInfo.put("hasImage", menu.getImageUrl() != null && !menu.getImageUrl().isEmpty());
        imageInfo.put("imageUrl", menu.getImageUrl());

        if (menu.getImageUrl() != null) {
            // 이미지 ID는 URL 해시값으로 생성
            long imageId = Math.abs(menu.getImageUrl().hashCode()) % 1000000;
            imageInfo.put("imageId", imageId);
        }

        log.info("메뉴 이미지 정보 조회 완료 - 사용자 ID: {}, 메뉴 ID: {}, 이미지 존재: {}",
                currentUser, menuId, imageInfo.get("hasImage"));

        return imageInfo;
    }

    /**
     * 메뉴 이미지 삭제
     */
    @Transactional
    public void deleteMenuImage(Long currentUser, Long menuId, Long imageId) {
        log.info("=== 메뉴 이미지 삭제 시작 - 사용자 ID: {}, 메뉴 ID: {}, 이미지 ID: {} ===",
                currentUser, menuId, imageId);

        Store store = menuValidationService.getMyStore(currentUser);

        Menu menu = menuRepository.findByIdAndStoreId(menuId, store.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

        // 삭제할 이미지가 있는지 확인
        if (menu.getImageUrl() == null || menu.getImageUrl().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제할 이미지가 없습니다");
        }

        // 이미지 ID 검증
        if (!isValidImageId(menu.getImageUrl(), imageId)) {
            log.warn("잘못된 이미지 ID 접근 시도 - 사용자 ID: {}, 메뉴 ID: {}, 이미지 ID: {}",
                    currentUser, menuId, imageId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 이미지 ID입니다");
        }

        try {
            String imageUrl = menu.getImageUrl();
            log.info("S3에서 메뉴 이미지 삭제 처리 중: {}", imageUrl);
            s3Service.deleteFile(imageUrl);

            menu.removeImage();
            menuRepository.save(menu);

            log.info("메뉴 이미지 삭제 완료! 사용자 ID: {}, 메뉴: {}, 삭제된 이미지: {}",
                    currentUser, menu.getName(), imageUrl);

        } catch (Exception e) {
            log.error("메뉴 이미지 삭제 실패 - 사용자 ID: {}, 메뉴 ID: {}", currentUser, menuId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    // ================================
    // Private Helper Methods
    // ================================

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일이 비어있습니다");
        }

        // 파일 크기 체크 (10MB 제한)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일 크기는 10MB를 초과할 수 없습니다");
        }

        // 파일 형식 검증
        String contentType = file.getContentType();
        if (contentType == null || !isValidImageType(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "지원하지 않는 파일 형식입니다. JPG, PNG, GIF만 업로드 가능합니다");
        }

        // 파일명 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바른 파일명이 필요합니다");
        }

        // 파일 확장자 검증
        String extension = getFileExtension(originalFilename);
        if (!isValidImageExtension(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 확장자입니다");
        }
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp");
    }

    private boolean isValidImageExtension(String extension) {
        return extension.matches("(?i)\\.(jpg|jpeg|png|gif|webp)$");
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }

    private boolean isValidImageId(String imageUrl, Long imageId) {
        // 간단한 검증: URL의 해시값과 imageId 비교
        long urlHash = Math.abs(imageUrl.hashCode());
        return urlHash % 1000000 == imageId % 1000000;
    }
}