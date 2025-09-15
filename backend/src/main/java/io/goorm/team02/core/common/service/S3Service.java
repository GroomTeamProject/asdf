package io.goorm.team02.core.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${app.s3.folder:store}")
    private String baseFolder;

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * 파일을 S3에 업로드하고 URL 반환 (storeId 폴더 구조)
     */
    public String uploadFile(MultipartFile file, Long storeId) {
        // 파일 유효성 검사
        validateFile(file);

        // 고유한 파일명 생성
        String fileName = generateFileName(file.getOriginalFilename());
        // store/{storeId}/filename.jpg 형태로 경로 구성
        String key = baseFolder + "/" + storeId + "/" + fileName;

        try {
            // S3에 업로드
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 업로드된 파일의 URL 생성
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
            log.info("파일 업로드 성공 - Store ID: {}, URL: {}", storeId, fileUrl);

            return fileUrl;

        } catch (IOException e) {
            log.error("파일 업로드 실패 - Store ID: {}, 오류: {}", storeId, e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다", e);
        }
    }

    /**
     * 메뉴 이미지를 S3에 업로드 (store/{storeId}/menu/{fileName} 구조)
     */
    public String uploadMenuImage(MultipartFile file, Long storeId) {
        // 파일 유효성 검사
        validateFile(file);

        // 고유한 파일명 생성
        String fileName = generateFileName(file.getOriginalFilename());
        // store/{storeId}/menu/filename.jpg 형태로 경로 구성
        String key = baseFolder + "/" + storeId + "/menu/" + fileName;

        try {
            // S3에 업로드
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 업로드된 파일의 URL 생성
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
            log.info("메뉴 이미지 업로드 성공 - Store ID: {}, URL: {}", storeId, fileUrl);

            return fileUrl;

        } catch (IOException e) {
            log.error("메뉴 이미지 업로드 실패 - Store ID: {}, 오류: {}", storeId, e.getMessage());
            throw new RuntimeException("메뉴 이미지 업로드에 실패했습니다", e);
        }
    }

    /**
     * S3에서 파일 삭제
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            // URL에서 S3 key 추출
            String key = extractKeyFromUrl(fileUrl);

            // S3에서 파일 삭제
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("파일 삭제 성공: {}", key);

        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            throw new RuntimeException("파일 삭제에 실패했습니다", e);
        }
    }

    /**
     * 특정 스토어의 모든 이미지 삭제 (선택사항)
     */
    public void deleteStoreFolder(Long storeId) {
        try {
            String folderPrefix = baseFolder + "/" + storeId + "/";

            // 실제 구현시에는 listObjectsV2로 폴더 내 모든 객체를 조회 후 삭제
            // 지금은 간단히 폴더 경로만 로깅
            log.info("스토어 폴더 삭제 요청 - Store ID: {}, Prefix: {}", storeId, folderPrefix);

        } catch (Exception e) {
            log.error("스토어 폴더 삭제 실패 - Store ID: {}", storeId);
            throw new RuntimeException("스토어 폴더 삭제에 실패했습니다", e);
        }
    }

    /**
     * 파일 유효성 검사
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다");
        }

        // 파일 크기 체크 (10MB)
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다");
        }

        // 이미지 파일 형식 체크
        String contentType = file.getContentType();
        if (contentType == null || !isImageFile(contentType)) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다 (JPG, PNG, GIF, WEBP)");
        }
    }

    /**
     * 이미지 파일 형식 확인
     */
    private boolean isImageFile(String contentType) {
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp");
    }

    /**
     * 고유한 파일명 생성
     */
    private String generateFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String extension = getFileExtension(originalFilename);
        return uuid + extension;
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }

    /**
     * URL에서 S3 key 추출
     */
    private String extractKeyFromUrl(String fileUrl) {
        try {

            String pattern = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);

            if (fileUrl.startsWith(pattern)) {
                return fileUrl.substring(pattern.length());
            }

            // 다른 패턴도 처리 (s3.amazonaws.com 형태)
            String alternatePattern = String.format("https://%s.s3.amazonaws.com/", bucketName);
            if (fileUrl.startsWith(alternatePattern)) {
                return fileUrl.substring(alternatePattern.length());
            }

            throw new IllegalArgumentException("잘못된 S3 URL 형식입니다: " + fileUrl);

        } catch (Exception e) {
            log.error("URL에서 S3 key 추출 실패: {}", e.getMessage());
            throw new RuntimeException("URL 파싱에 실패했습니다", e);
        }
    }
}