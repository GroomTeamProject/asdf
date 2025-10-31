package io.goorm.team02.core.owner.stores.service;

import io.goorm.team02.core.owner.common.service.S3Service;
import io.goorm.team02.core.owner.stores.client.OrderServiceClient;
import io.goorm.team02.core.owner.stores.domain.Store;
import io.goorm.team02.core.owner.stores.domain.StoreHoliday;
import io.goorm.team02.core.owner.stores.domain.StoreHour;
import io.goorm.team02.core.owner.stores.domain.enums.StoreCategory;
import io.goorm.team02.core.owner.stores.domain.enums.StoreStatus;
import io.goorm.team02.core.owner.stores.events.ImageCleanupEvent;
import io.goorm.team02.core.owner.stores.mapper.StoreMapper;
import io.goorm.team02.core.owner.stores.repository.StoreHolidayRepository;
import io.goorm.team02.core.owner.stores.repository.StoreHourRepository;
import io.goorm.team02.core.owner.stores.repository.StoreRepository;
import io.goorm.team02.dto.orders.OrderDashboardDto;
import io.goorm.team02.dto.orders.OrderItemDto;
import io.goorm.team02.dto.orders.RecentOrderDto;
import io.goorm.team02.dto.owner.stores.dashboard.StoreDashboardResponse;
import io.goorm.team02.dto.owner.stores.storemanagement.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreHourRepository storeHourRepository;
    private final StoreHolidayRepository storeHolidayRepository;
    private final S3Service s3Service;
    private final ApplicationEventPublisher eventPublisher;
    private final StoreMapper storeMapper;
    private final OrderServiceClient orderServiceClient;

    /**
     * 가게 등록 (최초 1회)
     */
    @Transactional
    public StoreResponse createStore(Long currentUser, StoreCreateRequest request) {
        log.info("=== 가게 등록 시작 - 사용자 ID: {} ===", currentUser);

        // 사용자가 이미 가게를 가지고 있는지 확인
        if (storeRepository.existsByOwnerIdAndIsActiveTrue(currentUser)) {
            log.warn("이미 등록된 가게가 있습니다. 사용자 ID: {}", currentUser);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 가게가 있습니다");
        }

        // 입력값 유효성 검증
        storeMapper.validateStoreCreateRequest(request);

        try {
            // String을 Enum으로 변환
            StoreCategory category = storeMapper.convertStringToStoreCategory(request.getCategory());

            Store store = new Store();
            store.setOwnerId(currentUser);
            store.setBusinessNumber(request.getBusinessNumber());
            store.setName(request.getName());
            store.setDescription(request.getDescription());
            store.setPhone(request.getPhone());
            store.setAddress(request.getAddress());
            store.setDetailAddress(request.getDetailAddress());
            store.setLatitude(request.getLatitude());
            store.setLongitude(request.getLongitude());
            store.setCategory(category);
            store.setMinOrderAmount(request.getMinOrderAmount());
            store.setDeliveryFee(request.getDeliveryFee());
            store.setDeliveryTimeMin(request.getDeliveryTimeMin());
            store.setDeliveryTimeMax(request.getDeliveryTimeMax());
            store.setImageUrl(request.getImageUrl());

            Store savedStore = storeRepository.save(store);
            log.info("가게 등록 완료! 생성된 가게 ID: {}, 가게명: {}", savedStore.getId(), savedStore.getName());

            return storeMapper.toStoreResponse(savedStore);

        } catch (DataIntegrityViolationException e) {
            log.error("데이터 무결성 위반: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복된 사업자등록번호입니다");
        } catch (Exception e) {
            log.error("가게 등록 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류");
        }
    }

    /**
     * 내 가게 정보 조회
     */
    public StoreResponse getMyStore(Long currentUser) {
        log.debug("가게 정보 조회 - 사용자 ID: {}", currentUser);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElse(null);

        return store != null ? storeMapper.toStoreResponse(store) : null;
    }

    /**
     * 내 가게 Entity 조회 (내부용)
     */
    public Store getMyStoreEntity(Long currentUser) {
        return storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElse(null);
    }

    /**
     * 가게 정보 수정
     */
    @Transactional
    public StoreResponse updateStore(Long currentUser, StoreUpdateRequest request) {
        log.info("=== 가게 정보 수정 시작 - 사용자 ID: {} ===", currentUser);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        // 입력값 검증
        storeMapper.validateStoreUpdateRequest(request);

        boolean hasChanges = false;

        if (request.getName() != null && !request.getName().equals(store.getName())) {
            log.info("가게명 변경: {} -> {}", store.getName(), request.getName());
            store.setName(request.getName());
            hasChanges = true;
        }

        if (request.getDescription() != null && !request.getDescription().equals(store.getDescription())) {
            log.info("설명 변경: {} -> {}", store.getDescription(), request.getDescription());
            store.setDescription(request.getDescription());
            hasChanges = true;
        }

        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            StoreCategory newCategory = storeMapper.convertStringToStoreCategory(request.getCategory());
            if (!newCategory.equals(store.getCategory())) {
                log.info("카테고리 변경: {} -> {}", store.getCategory(), newCategory);
                store.setCategory(newCategory);
                hasChanges = true;
            }
        }

        if (request.getImageUrl() != null && !request.getImageUrl().equals(store.getImageUrl())) {
            log.info("이미지URL 변경: {} -> {}", store.getImageUrl(), request.getImageUrl());
            store.setImageUrl(request.getImageUrl());
            hasChanges = true;
        }

        if (hasChanges) {
            Store savedStore = storeRepository.save(store);
            log.info("=== 가게 정보 수정 완료 ===");
            return storeMapper.toStoreResponse(savedStore);
        }

        return storeMapper.toStoreResponse(store);
    }

    /**
     * 연락처 정보 수정
     */
    @Transactional
    public StoreResponse updateContact(Long currentUser, StoreContactRequest request) {
        log.info("=== 연락처 정보 수정 시작 - 사용자 ID: {} ===", currentUser);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        boolean hasChanges = false;

        if (request.getPhone() != null && !request.getPhone().equals(store.getPhone())) {
            log.info("연락처 변경: {} -> {}", store.getPhone(), request.getPhone());
            store.setPhone(request.getPhone());
            hasChanges = true;
        }

        if (hasChanges) {
            Store savedStore = storeRepository.save(store);
            log.info("=== 연락처 정보 수정 완료 ===");
            return storeMapper.toStoreResponse(savedStore);
        }

        log.info("변경된 연락처 정보가 없습니다.");
        return storeMapper.toStoreResponse(store);
    }

    /**
     * 배달 정보 수정
     */
    @Transactional
    public StoreResponse updateDelivery(Long currentUser, StoreDeliveryRequest request) {
        log.info("=== 배달 정보 수정 시작 - 사용자 ID: {} ===", currentUser);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        boolean hasChanges = false;

        if (request.getDeliveryFee() != null && !request.getDeliveryFee().equals(store.getDeliveryFee())) {
            log.info("배달비 변경: {} -> {}", store.getDeliveryFee(), request.getDeliveryFee());
            store.setDeliveryFee(request.getDeliveryFee());
            hasChanges = true;
        }

        if (request.getMinOrderAmount() != null && !request.getMinOrderAmount().equals(store.getMinOrderAmount())) {
            log.info("최소주문금액 변경: {} -> {}", store.getMinOrderAmount(), request.getMinOrderAmount());
            store.setMinOrderAmount(request.getMinOrderAmount());
            hasChanges = true;
        }

        if (request.getDeliveryTimeMin() != null && !request.getDeliveryTimeMin().equals(store.getDeliveryTimeMin())) {
            log.info("최소배달시간 변경: {}분 -> {}분", store.getDeliveryTimeMin(), request.getDeliveryTimeMin());
            store.setDeliveryTimeMin(request.getDeliveryTimeMin());
            hasChanges = true;
        }

        if (request.getDeliveryTimeMax() != null && !request.getDeliveryTimeMax().equals(store.getDeliveryTimeMax())) {
            log.info("최대배달시간 변경: {}분 -> {}분", store.getDeliveryTimeMax(), request.getDeliveryTimeMax());
            store.setDeliveryTimeMax(request.getDeliveryTimeMax());
            hasChanges = true;
        }

        if (hasChanges) {
            Store savedStore = storeRepository.save(store);
            log.info("=== 배달 정보 수정 완료 ===");
            return storeMapper.toStoreResponse(savedStore);
        }

        log.info("변경된 배달 정보가 없습니다.");
        return storeMapper.toStoreResponse(store);
    }

    /**
     * 위치 정보 수정
     */
    @Transactional
    public StoreResponse updateLocation(Long currentUser, StoreLocationRequest request) {
        log.info("=== 위치 정보 수정 시작 - 사용자 ID: {} ===", currentUser);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        boolean hasChanges = false;

        if (request.getAddress() != null && !request.getAddress().equals(store.getAddress())) {
            log.info("주소 변경: {} -> {}", store.getAddress(), request.getAddress());
            store.setAddress(request.getAddress());
            hasChanges = true;
        }

        if (request.getDetailAddress() != null && !request.getDetailAddress().equals(store.getDetailAddress())) {
            log.info("상세주소 변경: {} -> {}", store.getDetailAddress(), request.getDetailAddress());
            store.setDetailAddress(request.getDetailAddress());
            hasChanges = true;
        }

        if (hasChanges) {
            Store savedStore = storeRepository.save(store);
            log.info("=== 위치 정보 수정 완료 ===");
            return storeMapper.toStoreResponse(savedStore);
        }

        log.info("변경된 위치 정보가 없습니다.");
        return storeMapper.toStoreResponse(store);
    }

    /**
     * 가게 삭제 (비활성화)
     */
    @Transactional
    public void deleteStore(Long currentUser) {
        log.info("=== 가게 삭제 시작 - 사용자 ID: {} ===", currentUser);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        store.setIsActive(false);
        storeRepository.save(store);

        log.info("가게 삭제 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());
    }

    /**
     * 트랜잭션 안전한 이미지 업로드
     */
    @Transactional
    public String uploadImage(Long currentUser, MultipartFile file) {
        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        String oldImageUrl = store.getImageUrl();
        String tempImageUrl = null;
        String finalImageUrl = null;

        try {
            // 1단계: 임시 위치에 업로드
            tempImageUrl = s3Service.uploadFileWithTransaction(file, store.getId());

            // 2단계: DB 트랜잭션 내에서 파일 이동 및 저장
            finalImageUrl = s3Service.moveToFinalPath(tempImageUrl, store.getId());
            store.setImageUrl(finalImageUrl);
            storeRepository.save(store);

            // 3단계: 기존 파일 비동기 삭제 (트랜잭션 커밋 후)
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                eventPublisher.publishEvent(new ImageCleanupEvent(oldImageUrl));
            }

            return finalImageUrl;

        } catch (Exception e) {
            // 보상 트랜잭션: 실패시 업로드된 파일들 정리
            cleanupFailedUpload(tempImageUrl, finalImageUrl);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다");
        }
    }

    /**
     * 안전한 이미지 삭제
     */
    @Transactional
    public void deleteImage(Long currentUser, Long imageId) {
        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        String currentImageUrl = store.getImageUrl();

        if (currentImageUrl == null || currentImageUrl.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제할 이미지가 없습니다");
        }

        // DB에서 먼저 제거 (트랜잭션 내)
        store.setImageUrl(null);
        storeRepository.save(store);

        // S3에서 비동기 삭제 (트랜잭션 커밋 후)
        eventPublisher.publishEvent(new ImageCleanupEvent(currentImageUrl));
    }

    /**
     * 실패한 업로드 정리
     */
    private void cleanupFailedUpload(String tempUrl, String finalUrl) {
        if (tempUrl != null) {
            try {
                s3Service.deleteFile(tempUrl);
            } catch (Exception e) {
                // 무시 - 배치에서 정리
            }
        }
        if (finalUrl != null) {
            try {
                s3Service.deleteFile(finalUrl);
            } catch (Exception e) {
                // 무시 - 배치에서 정리
            }
        }
    }

    /**
     * 운영시간 조회
     */
    public List<StoreHourResponse> getStoreHours(Long currentUser) {
        log.info("=== 운영시간 조회 - 사용자 ID: {} ===", currentUser);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        List<StoreHour> storeHours = store.getStoreHours();
        log.info("운영시간 조회 완료: {}개의 운영시간 설정", storeHours.size());

        return storeMapper.toHourResponseList(storeHours);
    }

    /**
     * 운영시간 설정
     */
    @Transactional
    public List<StoreHourResponse> updateStoreHours(Long currentUser, List<StoreHourRequest> requests) {
        log.info("=== 운영시간 설정 시작 - 사용자 ID: {} ===", currentUser);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        // 업데이트 대상 요일 수집
        Set<Integer> targetDays = new HashSet<>();
        for (StoreHourRequest request : requests) {
            if (request.getDayOfWeek() == 7) {
                for (int day = 0; day <= 6; day++) {
                    targetDays.add(day);
                }
            } else {
                targetDays.add(request.getDayOfWeek());
            }
        }

        // 기존 운영시간 삭제
        storeHourRepository.deleteByStoreIdAndDayOfWeekIn(store.getId(), targetDays);
        store.getStoreHours().removeIf(hour -> targetDays.contains(hour.getDayOfWeek()));

        // 새로운 운영시간 생성
        List<StoreHour> updatedHours = new ArrayList<>();
        for (StoreHourRequest request : requests) {
            if (request.getDayOfWeek() == 7) {
                for (int day = 0; day <= 6; day++) {
                    StoreHour storeHour = StoreHour.builder()
                            .store(store)
                            .dayOfWeek(day)
                            .openTime(request.getOpenTime())
                            .closeTime(request.getCloseTime())
                            .isClosed(request.getIsClosed())
                            .build();

                    // 검증
                    storeMapper.validateStoreHour(storeHour);
                    updatedHours.add(storeHour);
                }
            } else {
                StoreHour storeHour = StoreHour.builder()
                        .store(store)
                        .dayOfWeek(request.getDayOfWeek())
                        .openTime(request.getOpenTime())
                        .closeTime(request.getCloseTime())
                        .isClosed(request.getIsClosed())
                        .build();

                // 검증
                storeMapper.validateStoreHour(storeHour);
                updatedHours.add(storeHour);
            }
        }

        List<StoreHour> savedHours = storeHourRepository.saveAll(updatedHours);
        log.info("=== 운영시간 설정 완료 ===");

        return storeMapper.toHourResponseList(savedHours);
    }

    /**
     * 휴무일 등록
     */
    @Transactional
    public ResponseEntity<String> createHoliday(Long currentUser, StoreHolidayRequest request) {
        log.info("=== 휴무일 등록 시작 - 사용자 ID: {} ===", currentUser);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        if (request.getDate() == null) {
            return ResponseEntity.badRequest().body("휴무일을 입력해주세요");
        }

        if (request.getDate().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().body("과거 날짜는 휴무일로 설정할 수 없습니다");
        }

        if (storeHolidayRepository.existsByStoreIdAndDate(store.getId(), request.getDate())) {
            return ResponseEntity.badRequest().body("이미 등록된 휴무일입니다: " + request.getDate());
        }

        StoreHoliday holiday = StoreHoliday.builder()
                .store(store)
                .date(request.getDate())
                .reason(request.getReason())
                .isRecurring(request.getIsRecurring() != null ? request.getIsRecurring() : false)
                .build();

        storeHolidayRepository.save(holiday);
        log.info("휴무일 등록 완료: {} - {}", request.getDate(), request.getReason());

        return ResponseEntity.ok("휴무일이 성공적으로 등록되었습니다");
    }

    /**
     * 휴무일 목록 조회
     */
    public List<StoreHolidayResponse> getHolidays(Long currentUser) {
        log.info("=== 휴무일 조회 시작 - 사용자 ID: {} ===", currentUser);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        List<StoreHoliday> holidays = storeHolidayRepository
                .findByStoreIdAndDateGreaterThanEqualOrderByDateAsc(store.getId(), LocalDate.now());

        List<StoreHolidayResponse> result = storeMapper.toHolidayResponseList(holidays);

        log.info("휴무일 조회 완료: {}개", result.size());
        return result;
    }

    /**
     * 휴무일 삭제
     */
    @Transactional
    public ResponseEntity<String> deleteHoliday(Long currentUser, Long holidayId) {
        log.info("=== 휴무일 삭제 시작 - 사용자 ID: {}, 휴무일 ID: {} ===", currentUser, holidayId);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        // 권한 검증을 위한 Repository 메소드 사용
        StoreHoliday holiday = storeHolidayRepository.findByIdAndStoreId(holidayId, store.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "휴무일을 찾을 수 없습니다"));

        storeHolidayRepository.delete(holiday);
        log.info("휴무일 삭제 완료: {} - {}", holiday.getDate(), holiday.getReason());

        return ResponseEntity.ok("휴무일이 성공적으로 삭제되었습니다");
    }

    /**
     * 가게 상태 조회
     */
    public StoreStatusResponse getStoreStatus(Long currentUser) {
        log.info("=== 가게 상태 조회 시작 - 사용자 ID: {} ===", currentUser);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        boolean isCurrentlyOpen = checkIfCurrentlyOpen(store);
        String currentDayStatus = getCurrentDayStatusMessage(store);

        log.info("가게 상태 조회 완료 - 현재 영업 중: {}", isCurrentlyOpen);

        return storeMapper.toStoreStatusResponse(store, isCurrentlyOpen, currentDayStatus);
    }

    /**
     * 영업 상태 변경
     */
    @Transactional
    public StoreStatusModifyResponse updateStoreStatus(Long currentUser, StoreStatusRequest request) {
        log.info("=== 영업 상태 변경 시작 - 사용자 ID: {} ===", currentUser);

        // Mapper를 사용해서 검증 및 변환
        StoreStatus newStatus = storeMapper.validateAndConvertStoreStatus(request);
        storeMapper.validateStatusChangeMessage(request);

        Store store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다"));

        if (store.getStatus() == newStatus) {
            log.info("이미 동일한 상태입니다: {}", newStatus);
            return storeMapper.toStatusModifyResponse(store, "이미 동일한 상태입니다.");
        }

        store.setStatus(newStatus);

        if (request.getMessage() != null && !request.getMessage().trim().isEmpty()) {
            log.info("영업 상태 변경 사유: {}", request.getMessage());
        }

        Store savedStore = storeRepository.save(store);
        log.info("영업 상태가 {}로 변경되었습니다.", newStatus);

        return storeMapper.toStatusModifyResponse(savedStore, "영업 상태가 성공적으로 변경되었습니다.");
    }

    // ================================
    // Private Helper Methods
    // ================================

    /**
     * 현재 영업 중인지 체크하는 헬퍼 메서드
     */
    private boolean checkIfCurrentlyOpen(Store store) {
        // 가게가 비활성화되어 있으면 영업 중이 아님
        if (!store.getIsActive()) {
            return false;
        }

        // 가게 상태가 CLOSED이면 영업 중이 아님
        if (store.getStatus() != null && store.getStatus() != StoreStatus.OPEN) {
            return false;
        }

        // 현재 시간과 영업시간 비교 로직
        LocalDateTime now = LocalDateTime.now();
        int currentDayOfWeek = now.getDayOfWeek().getValue(); // 1=월요일, 7=일요일
        LocalTime currentTime = now.toLocalTime();

        // 오늘의 영업시간 조회
        Optional<StoreHour> todayHour = store.getStoreHours().stream()
                .filter(hour -> hour.getDayOfWeek().equals(currentDayOfWeek))
                .findFirst();

        if (todayHour.isEmpty()) {
            return false; // 오늘 영업시간 정보가 없으면 영업 중이 아님
        }

        StoreHour hour = todayHour.get();

        // 오늘 휴무인지 체크
        if (hour.getIsClosed()) {
            return false;
        }

        // 영업시간 내인지 체크
        LocalTime openTime = hour.getOpenTime();
        LocalTime closeTime = hour.getCloseTime();

        if (openTime != null && closeTime != null) {
            return !currentTime.isBefore(openTime) && !currentTime.isAfter(closeTime);
        }

        return false;
    }

    /**
     * 오늘 영업 상태 메시지 생성
     */
    private String getCurrentDayStatusMessage(Store store) {
        LocalDateTime now = LocalDateTime.now();
        int currentDayOfWeek = now.getDayOfWeek().getValue();

        Optional<StoreHour> todayHour = store.getStoreHours().stream()
                .filter(hour -> hour.getDayOfWeek().equals(currentDayOfWeek))
                .findFirst();

        if (todayHour.isEmpty()) {
            return "영업시간 정보가 없습니다";
        }

        StoreHour hour = todayHour.get();

        if (hour.getIsClosed()) {
            return "오늘은 휴무일입니다";
        }

        if (hour.getOpenTime() != null && hour.getCloseTime() != null) {
            return String.format("영업시간: %s - %s",
                    hour.getOpenTime().toString(),
                    hour.getCloseTime().toString());
        }

        return "영업시간 정보를 확인해주세요";
    }

    /**
     * 오늘의 운영시간만 문자열로 반환
     */
    private String getTodayOperatingHours(Store store) {
        LocalDateTime now = LocalDateTime.now();
        int currentDayOfWeek = now.getDayOfWeek().getValue(); // 1=월요일, 7=일요일

        // Store의 요일 체계에 맞게 변환 (0=일요일, 1=월요일 ... 6=토요일)
        int storeDayOfWeek = currentDayOfWeek == 7 ? 0 : currentDayOfWeek;

        Optional<StoreHour> todayHour = store.getStoreHours().stream()
                .filter(hour -> hour.getDayOfWeek().equals(storeDayOfWeek))
                .findFirst();

        if (todayHour.isEmpty()) {
            return "운영시간 미설정";
        }

        StoreHour hour = todayHour.get();

        if (hour.getIsClosed()) {
            return "휴무";
        }

        if (hour.getOpenTime() != null && hour.getCloseTime() != null) {
            return String.format("%s - %s",
                    hour.getOpenTime().toString(),
                    hour.getCloseTime().toString());
        }

        return "운영시간 확인 필요";
    }

    public StoreDashboardResponse getDashboard(Long currentUser) {
        log.info("=== 대시보드 조회 시작 - 사용자 ID: {} ===", currentUser);

        // 1. 내 가게 정보 조회
        Store store = getMyStoreEntity(currentUser);
        if (store == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다");
        }

        try {
            // 2. Order 서비스에서 주문 관련 데이터 조회
            OrderDashboardDto orderData = orderServiceClient.getDashboardData(store.getId());

            // 3. 가게 운영시간 정보 조회
            List<StoreHour> storeHours = store.getStoreHours();

            // 4. StoreDashboardResponse 생성 (record는 생성자 사용)
            StoreDashboardResponse response = new StoreDashboardResponse(
                    // 오늘 통계
                    new StoreDashboardResponse.TodayStats(
                            orderData.getTodayOrderCount(),
                            orderData.getTodayRevenue()
                    ),

                    // 가게 정보
                    new StoreDashboardResponse.RestaurantInfo(
                            store.getId(),
                            store.getName(),
                            orderData.getAverageRating(),
                            orderData.getReviewCount(),
                            orderData.getTotalOrderCount()
                    ),

                    // 운영시간 정보
                    convertToStoreHourInfoList(storeHours),

                    // 최근 주문 정보
                    convertToRecentOrderInfoList(orderData.getRecentOrders())
            );

            log.info("=== 대시보드 조회 완료 - 오늘 주문: {}, 오늘 매출: {} ===",
                    orderData.getTodayOrderCount(), orderData.getTodayRevenue());

            return response;

        } catch (Exception e) {
            log.error("대시보드 데이터 조회 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "대시보드 데이터를 불러올 수 없습니다");
        }
    }

    /**
     * StoreHour를 StoreHourInfo로 변환
     */
    private List<StoreDashboardResponse.StoreHourInfo> convertToStoreHourInfoList(List<StoreHour> storeHours) {
        return storeHours.stream()
                .map(hour -> new StoreDashboardResponse.StoreHourInfo(
                        hour.getDayOfWeek(),
                        hour.getOpenTime() != null ? hour.getOpenTime().toString() : null,
                        hour.getCloseTime() != null ? hour.getCloseTime().toString() : null,
                        hour.getIsClosed()
                ))
                .collect(Collectors.toList());
    }

    /**
     * RecentOrderDto를 RecentOrderInfo로 변환
     */
    private List<StoreDashboardResponse.RecentOrderInfo> convertToRecentOrderInfoList(List<RecentOrderDto> recentOrders) {
        return recentOrders.stream()
                .map(order -> new StoreDashboardResponse.RecentOrderInfo(
                        order.getId(),
                        order.getOrderNumber(),
                        order.getCustomerName(),
                        order.getTotal(),
                        order.getStatus(),
                        order.getOrderTime().toString(),
                        convertToOrderItemInfoList(order.getItems())
                ))
                .collect(Collectors.toList());
    }

    /**
     * OrderItemDto를 OrderItemInfo로 변환
     */
    private List<StoreDashboardResponse.OrderItemInfo> convertToOrderItemInfoList(List<OrderItemDto> items) {
        return items.stream()
                .map(item -> new StoreDashboardResponse.OrderItemInfo(
                        item.getName(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());
    }
}