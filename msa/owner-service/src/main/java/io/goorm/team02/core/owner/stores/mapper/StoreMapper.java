package io.goorm.team02.core.owner.stores.mapper;

import io.goorm.team02.core.owner.stores.domain.Store;
import io.goorm.team02.core.owner.stores.domain.StoreHoliday;
import io.goorm.team02.core.owner.stores.domain.StoreHour;
import io.goorm.team02.core.owner.stores.domain.enums.StoreCategory;
import io.goorm.team02.core.owner.stores.domain.enums.StoreStatus;
import io.goorm.team02.dto.owner.stores.storemanagement.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StoreMapper {

    // ================================
    // Store 변환 메서드들
    // ================================

    /**
     * Store Entity -> StoreResponse DTO 변환
     */
    public StoreResponse toStoreResponse(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .businessNumber(maskBusinessNumber(store.getBusinessNumber())) // 보안을 위한 마스킹
                .name(store.getName())
                .description(store.getDescription())
                .phone(store.getPhone())
                .address(store.getAddress())
                .detailAddress(store.getDetailAddress())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .category(convertStoreCategoryToString(store.getCategory()))
                .minOrderAmount(store.getMinOrderAmount())
                .deliveryFee(store.getDeliveryFee())
                .deliveryTimeMin(store.getDeliveryTimeMin())
                .deliveryTimeMax(store.getDeliveryTimeMax())
                .imageUrl(validateImageUrl(store.getImageUrl()))
                .status(convertStoreStatusToString(store.getStatus()))
                .rating(store.getRating())
                .reviewCount(store.getReviewCount())
                .isActive(store.getIsActive())
                .build();
    }

    /**
     * List<Store> -> List<StoreResponse> 변환
     */
    public List<StoreResponse> toStoreResponseList(List<Store> stores) {
        return stores.stream()
                .map(this::toStoreResponse)
                .collect(Collectors.toList());
    }

    // ================================
    // StoreStatus 관련 변환 메서드들
    // ================================

    /**
     * Store Entity -> StoreStatusResponse DTO 변환 (풀버전)
     */
    public StoreStatusResponse toStoreStatusResponse(Store store, boolean isCurrentlyOpen, String currentDayStatus) {
        return StoreStatusResponse.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .status(convertStoreStatusToString(store.getStatus()))
                .isActive(store.getIsActive())
                .isCurrentlyOpen(isCurrentlyOpen)
                .currentDayStatus(currentDayStatus)
                .lastUpdated(store.getUpdatedAt() != null ? store.getUpdatedAt() : LocalDateTime.now())
                .build();
    }

    /**
     * Store Entity -> StoreStatusResponse DTO 변환 (간단버전)
     */
    public StoreStatusResponse toStoreStatusResponse(Store store, boolean isCurrentlyOpen) {
        return toStoreStatusResponse(store, isCurrentlyOpen, null);
    }

    /**
     * Store Entity -> StoreStatusResponse DTO 변환 (기본버전)
     */
    public StoreStatusResponse toStoreStatusResponse(Store store) {
        return toStoreStatusResponse(store, false, "영업 시간을 확인하세요");
    }

    /**
     * Store Entity -> StoreStatusModifyResponse DTO 변환
     */
    public StoreStatusModifyResponse toStatusModifyResponse(Store store, String message) {
        return new StoreStatusModifyResponse(
                store.getId(),
                store.getName(),
                convertStoreStatusToString(store.getStatus()),
                message
        );
    }

    /**
     * 직접 파라미터로 StoreStatusResponse 생성
     */
    public StoreStatusResponse createStoreStatusResponse(Long storeId, String storeName, String status,
                                                         Boolean isActive, Boolean isCurrentlyOpen,
                                                         String currentDayStatus, LocalDateTime lastUpdated) {
        return StoreStatusResponse.builder()
                .storeId(storeId)
                .storeName(storeName)
                .status(status)
                .isActive(isActive)
                .isCurrentlyOpen(isCurrentlyOpen)
                .currentDayStatus(currentDayStatus)
                .lastUpdated(lastUpdated != null ? lastUpdated : LocalDateTime.now())
                .build();
    }

    /**
     * 상태 변경 응답 생성 (Builder 패턴 방식)
     */
    public StoreStatusModifyResponse createStatusModifyResponse(Long storeId, String storeName,
                                                                String status, String message) {
        return new StoreStatusModifyResponse(storeId, storeName, status, message);
    }

    // ================================
    // StoreHour 변환 메서드들
    // ================================

    /**
     * StoreHour Entity -> StoreHourResponse DTO 변환
     */
    public StoreHourResponse toHourResponse(StoreHour storeHour) {
        return StoreHourResponse.builder()
                .id(storeHour.getId())
                .dayOfWeek(storeHour.getDayOfWeek())
                .openTime(storeHour.getOpenTime())
                .closeTime(storeHour.getCloseTime())
                .isClosed(storeHour.getIsClosed())
                .build();
    }

    /**
     * List<StoreHour> -> List<StoreHourResponse> 변환
     */
    public List<StoreHourResponse> toHourResponseList(List<StoreHour> storeHours) {
        return storeHours.stream()
                .map(this::toHourResponse)
                .collect(Collectors.toList());
    }

    // ================================
    // StoreHoliday 변환 메서드들
    // ================================

    /**
     * StoreHoliday Entity -> StoreHolidayResponse DTO 변환
     */
    public StoreHolidayResponse toHolidayResponse(StoreHoliday holiday) {
        return StoreHolidayResponse.builder()
                .id(holiday.getId())
                .date(holiday.getDate())
                .reason(holiday.getReason())
                .isRecurring(holiday.getIsRecurring())
                .build();
    }

    /**
     * List<StoreHoliday> -> List<StoreHolidayResponse> 변환
     */
    public List<StoreHolidayResponse> toHolidayResponseList(List<StoreHoliday> holidays) {
        return holidays.stream()
                .map(this::toHolidayResponse)
                .collect(Collectors.toList());
    }

    // ================================
    // StoreCategory 변환 메서드들
    // ================================

    /**
     * String을 StoreCategory enum으로 변환
     */
    public StoreCategory convertStringToStoreCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return StoreCategory.KOREAN;
        }

        try {
            return StoreCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 가게 카테고리입니다: " + category +
                    ". 사용 가능한 카테고리: KOREAN, CHINESE, WESTERN, JAPANESE, FAST_FOOD, CHICKEN, PIZZA, DESSERT");
        }
    }

    /**
     * StoreCategory enum을 String으로 변환
     */
    public String convertStoreCategoryToString(StoreCategory category) {
        return category != null ? category.name() : "KOREAN";
    }

    // ================================
    // StoreStatus 변환 메서드들
    // ================================

    /**
     * StoreStatus enum을 String으로 변환
     */
    public String convertStoreStatusToString(StoreStatus status) {
        return status != null ? status.name() : "CLOSED";
    }

    /**
     * String을 StoreStatus enum으로 변환
     */
    public StoreStatus convertStringToStoreStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return StoreStatus.CLOSED;
        }

        try {
            return StoreStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 가게 상태입니다: " + status +
                    ". 사용 가능한 상태: OPEN, CLOSED, TEMPORARILY_CLOSED, SUSPENDED");
        }
    }

    // ================================
    // 검증 메서드들
    // ================================

    /**
     * StoreCreateRequest 검증
     */
    public void validateStoreCreateRequest(StoreCreateRequest request) {
        // 배달시간 검증
        if (request.getDeliveryTimeMin() != null && request.getDeliveryTimeMax() != null) {
            if (request.getDeliveryTimeMin() >= request.getDeliveryTimeMax()) {
                throw new IllegalArgumentException("최소 배달시간은 최대 배달시간보다 작아야 합니다");
            }
        }

        // 카테고리 검증
        convertStringToStoreCategory(request.getCategory());

        // 기본 필수 필드 검증
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("가게명은 필수입니다");
        }

        if (request.getBusinessNumber() == null || request.getBusinessNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("사업자등록번호는 필수입니다");
        }
    }

    /**
     * StoreUpdateRequest 검증
     */
    public void validateStoreUpdateRequest(StoreUpdateRequest request) {
        // 가게명 검증
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String trimmedName = request.getName().trim();
            if (trimmedName.length() > 100) {
                throw new IllegalArgumentException("가게명은 100자를 초과할 수 없습니다");
            }

            // XSS 방지
            if (containsInvalidCharacters(trimmedName)) {
                throw new IllegalArgumentException("가게명에 허용되지 않은 문자가 포함되어 있습니다");
            }
        }

        // 설명 검증
        if (request.getDescription() != null && request.getDescription().length() > 500) {
            throw new IllegalArgumentException("가게 설명은 500자를 초과할 수 없습니다");
        }

        // 카테고리 검증
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            convertStringToStoreCategory(request.getCategory());
        }

        // 이미지 URL 검증
        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            validateImageUrlSecurity(request.getImageUrl());
        }
    }

    /**
     * StoreStatusRequest 검증 및 변환
     */
    public StoreStatus validateAndConvertStoreStatus(StoreStatusRequest request) {
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("가게 상태는 필수입니다");
        }

        return convertStringToStoreStatus(request.getStatus());
    }

    /**
     * 상태 변경 사유 검증
     */
    public void validateStatusChangeMessage(StoreStatusRequest request) {
        if (request.getMessage() != null && request.getMessage().length() > 200) {
            throw new IllegalArgumentException("상태 변경 사유는 200자를 초과할 수 없습니다");
        }
    }

    /**
     * 운영시간 검증
     */
    public void validateStoreHour(StoreHour storeHour) {
        if (!storeHour.getIsClosed()) {
            if (storeHour.getOpenTime() == null || storeHour.getCloseTime() == null) {
                throw new IllegalArgumentException("영업일에는 오픈시간과 마감시간이 필요합니다");
            }
        }

        if (storeHour.getDayOfWeek() < 1 || storeHour.getDayOfWeek() > 7) {
            throw new IllegalArgumentException("요일은 1(월요일)부터 7(일요일)까지 입니다");
        }
    }

    // ================================
    // 보안 및 데이터 처리 메서드들
    // ================================

    /**
     * 사업자등록번호 마스킹 (보안)
     */
    private String maskBusinessNumber(String businessNumber) {
        if (businessNumber == null || businessNumber.length() < 10) {
            return businessNumber;
        }

        // 123-45-67890 -> 123-**-***90
        return businessNumber.substring(0, 3) + "-**-***" + businessNumber.substring(businessNumber.length() - 2);
    }

    /**
     * 이미지 URL 검증 (응답용)
     */
    private String validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        // HTTPS URL만 허용
        if (!imageUrl.startsWith("https://")) {
            return null;
        }

        // 허용된 도메인만 노출
        String[] allowedDomains = {
                "s3.amazonaws.com",
                "cloudfront.net",
                "your-cdn-domain.com" // 실제 CDN 도메인으로 변경 필요
        };

        boolean isAllowed = false;
        for (String domain : allowedDomains) {
            if (imageUrl.contains(domain)) {
                isAllowed = true;
                break;
            }
        }

        return isAllowed ? imageUrl : null;
    }

    /**
     * 이미지 URL 보안 검증 (입력값용)
     */
    private void validateImageUrlSecurity(String imageUrl) {
        if (!imageUrl.startsWith("https://")) {
            throw new IllegalArgumentException("이미지 URL은 HTTPS 프로토콜을 사용해야 합니다");
        }

        if (imageUrl.length() > 500) {
            throw new IllegalArgumentException("이미지 URL은 500자를 초과할 수 없습니다");
        }

        // 위험한 URL 패턴 검사
        if (containsInvalidUrlPatterns(imageUrl)) {
            throw new IllegalArgumentException("허용되지 않은 URL 패턴입니다");
        }
    }

    /**
     * 허용되지 않은 문자 검증 (XSS 방지)
     */
    private boolean containsInvalidCharacters(String input) {
        if (input == null) return false;

        String[] dangerousPatterns = {
                "<script", "</script>", "javascript:", "vbscript:",
                "onload=", "onerror=", "onclick=", "eval(", "alert("
        };

        String lowerInput = input.toLowerCase();
        for (String pattern : dangerousPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 위험한 URL 패턴 검사
     */
    private boolean containsInvalidUrlPatterns(String url) {
        if (url == null) return false;

        String[] dangerousPatterns = {
                "javascript:", "data:", "vbscript:", "file:",
                "../", "..\\", "<script", "eval("
        };

        String lowerUrl = url.toLowerCase();
        for (String pattern : dangerousPatterns) {
            if (lowerUrl.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * SQL 인젝션 패턴 검증
     */
    public boolean containsSqlInjectionPattern(String input) {
        if (input == null) return false;

        String[] sqlPatterns = {
                "union", "select", "insert", "delete", "update", "drop",
                "exec", "execute", "sp_", "xp_", "--", "/*", "*/"
        };

        String lowerInput = input.toLowerCase();
        for (String pattern : sqlPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    // ================================
    // 유틸리티 메서드들
    // ================================

    /**
     * 가게 상태 한글 설명 반환
     */
    public String getStoreStatusDescription(StoreStatus status) {
        return switch (status) {
            case OPEN -> "영업중";
            case CLOSED -> "휴업";
            case TEMPORARILY_CLOSED -> "임시휴업";
            case BREAK -> "브레이크 타임";
        };
    }

    /**
     * 카테고리 한글 설명 반환
     */
    public String getStoreCategoryDescription(StoreCategory category) {
        return switch (category) {
            case KOREAN -> "한식";
            case CHINESE -> "중식";
            case WESTERN -> "양식";
            case JAPANESE -> "일식";
            case FAST_FOOD -> "패스트푸드";
            case CHICKEN -> "치킨";
            case PIZZA -> "피자";
            case DESSERT -> "디저트";
        };
    }
}