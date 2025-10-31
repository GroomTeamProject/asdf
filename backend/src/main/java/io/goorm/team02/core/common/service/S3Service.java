package io.goorm.team02.core.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
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
     * 트랜잭션 안전한 파일 업로드 (임시 -> 최종 경로)
     */
    public String uploadFileWithTransaction(MultipartFile file, Long storeId) {
        validateFile(file);

        String fileName = generateFileName(file.getOriginalFilename());
        String tempKey = "temp/" + storeId + "/" + fileName;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(tempKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, tempKey);

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드에 실패했습니다", e);
        }
    }

    /**
     * 임시 파일을 최종 경로로 이동
     */
    public String moveToFinalPath(String tempUrl, Long storeId) {
        try {
            String tempKey = extractKeyFromUrl(tempUrl);
            String fileName = tempKey.substring(tempKey.lastIndexOf("/") + 1);
            String finalKey = baseFolder + "/" + storeId + "/" + fileName;

            // S3 내에서 복사
            s3Client.copyObject(
                    CopyObjectRequest.builder()
                            .sourceBucket(bucketName)
                            .sourceKey(tempKey)
                            .destinationBucket(bucketName)
                            .destinationKey(finalKey)
                            .build()
            );

            // 임시 파일 삭제
            deleteFileByKey(tempKey);

            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, finalKey);

        } catch (Exception e) {
            throw new RuntimeException("파일 이동에 실패했습니다", e);
        }
    }

    /**
     * 기존 파일 업로드 (호환성 유지)
     */
    public String uploadFile(MultipartFile file, Long storeId) {
        validateFile(file);

        String fileName = generateFileName(file.getOriginalFilename());
        String key = baseFolder + "/" + storeId + "/" + fileName;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드에 실패했습니다", e);
        }
    }

    /**
     * 메뉴 이미지 업로드
     */
    public String uploadMenuImage(MultipartFile file, Long storeId) {
        validateFile(file);

        String fileName = generateFileName(file.getOriginalFilename());
        String key = baseFolder + "/" + storeId + "/menu/" + fileName;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);

        } catch (IOException e) {
            throw new RuntimeException("메뉴 이미지 업로드에 실패했습니다", e);
        }
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String key = extractKeyFromUrl(fileUrl);
            deleteFileByKey(key);
        } catch (Exception e) {
            throw new RuntimeException("파일 삭제에 실패했습니다", e);
        }
    }

    /**
     * Key로 파일 삭제
     */
    private void deleteFileByKey(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
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

        // 파일 확장자 더블 체크
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && !isValidImageExtension(originalFilename)) {
            throw new IllegalArgumentException("허용되지 않는 파일 확장자입니다");
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
     * 파일 확장자 검증
     */
    private boolean isValidImageExtension(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        return lowerCaseFilename.endsWith(".jpg") ||
                lowerCaseFilename.endsWith(".jpeg") ||
                lowerCaseFilename.endsWith(".png") ||
                lowerCaseFilename.endsWith(".gif") ||
                lowerCaseFilename.endsWith(".webp");
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

            String alternatePattern = String.format("https://%s.s3.amazonaws.com/", bucketName);
            if (fileUrl.startsWith(alternatePattern)) {
                return fileUrl.substring(alternatePattern.length());
            }

            throw new IllegalArgumentException("잘못된 S3 URL 형식입니다: " + fileUrl);

        } catch (Exception e) {
            throw new RuntimeException("URL 파싱에 실패했습니다", e);
        }
    }
}