package io.goorm.team02.core.stores.service;

import io.goorm.team02.core.common.service.S3Service;
import io.goorm.team02.core.orders.controller.dto.OrderResponse;
import io.goorm.team02.core.orders.domain.Order;
import io.goorm.team02.core.orders.service.OrderService;
import io.goorm.team02.core.reviews.repository.ReviewRepository;
import io.goorm.team02.core.stores.controller.dto.dashboard.RecentOrderInfo;
import io.goorm.team02.core.stores.controller.dto.dashboard.StoreDashboardResponse;
import io.goorm.team02.core.stores.controller.dto.ordermanagement.StoreOrderDetailResponse;
import io.goorm.team02.core.stores.controller.dto.ordermanagement.StoreOrderItemDetailResponse;
import io.goorm.team02.core.stores.controller.dto.ordermanagement.StoreOrderOptionResponse;
import io.goorm.team02.core.stores.controller.dto.ordermanagement.StoreOrderResponse;
import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreContactRequest;
import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreCreateRequest;
import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreDeliveryRequest;
import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreHolidayRequest;
import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreHolidayResponse;
import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreHourRequest;
import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreHourResponse;
import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreLocationRequest;
import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreStatusModifyResponse;
import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreStatusRequest;
import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreStatusResponse;
import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreUpdateRequest;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.domain.StoreHoliday;
import io.goorm.team02.core.stores.domain.StoreHour;
import io.goorm.team02.core.stores.repository.StoreHolidayRepository;
import io.goorm.team02.core.stores.repository.StoreHourRepository;
import io.goorm.team02.core.stores.domain.TempUser;
import io.goorm.team02.core.stores.repository.StoreRepository;
import io.goorm.team02.core.stores.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static io.goorm.team02.core.auth.security.SecurityUtils.getCurrentUserId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreHourRepository storeHourRepository;
    private final StoreHolidayRepository storeHolidayRepository;
    private final OrderService orderService;
    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;

    /**
     * 가게 등록 (최초 1회)
     */
    @Transactional
    public Store createStore(StoreCreateRequest request) {
        log.info("=== 가게 등록 시작 ===");

        // JWT 토큰에서 현재 로그인한 사용자 ID 가져오기
        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        // 입력받은 값들 로그 출력
        log.info("입력받은 가게 정보:");
        log.info("  - 사업자등록번호: {}", request.getBusinessNumber());
        log.info("  - 가게명: {}", request.getName());
        log.info("  - 설명: {}", request.getDescription());
        log.info("  - 전화번호: {}", request.getPhone());
        log.info("  - 주소: {}", request.getAddress());
        log.info("  - 상세주소: {}", request.getDetailAddress());
        log.info("  - 위도: {}", request.getLatitude());
        log.info("  - 경도: {}", request.getLongitude());
        log.info("  - 카테고리: {}", request.getCategory());
        log.info("  - 최소주문금액: {}", request.getMinOrderAmount());
        log.info("  - 배달료: {}", request.getDeliveryFee());
        log.info("  - 최소배달시간: {}분", request.getDeliveryTimeMin());
        log.info("  - 최대배달시간: {}분", request.getDeliveryTimeMax());
        log.info("  - 이미지URL: {}", request.getImageUrl());

        // 사용자 조회 및 검증
        log.info("사용자 조회 중...");
        TempUser owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> {
                    log.error("JWT 토큰의 사용자 ID로 사용자를 찾을 수 없습니다. ID: {}", currentUserId);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 실패");  // 👈 변경
                });

        // 사용자가 이미 가게를 가지고 있는지 확인
        log.info("기존 가게 존재 여부 확인 중...");
        if (storeRepository.existsByOwnerIdAndIsActiveTrue(currentUserId)) {
            log.warn("이미 등록된 가게가 있습니다. 사용자 ID: {}", currentUserId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 가게가 있음");  // 👈 변경
        }
        log.info("기존 가게 없음 - 새 가게 등록 진행");

        // 입력값 유효성 검증 추가
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청");  // 👈 추가
        }

        if (request.getBusinessNumber() == null || request.getBusinessNumber().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청");  // 👈 추가
        }

        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청");  // 👈 추가
        }

        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청");  // 👈 추가
        }

        // Store 엔티티 생성
        log.info("Store 엔티티 생성 중...");
        Store store = new Store();

        try {
            store.setOwner(owner);
            store.setBusinessNumber(request.getBusinessNumber());
            store.setName(request.getName());
            store.setDescription(request.getDescription());
            store.setPhone(request.getPhone());
            store.setAddress(request.getAddress());
            store.setDetailAddress(request.getDetailAddress());
            store.setLatitude(request.getLatitude());
            store.setLongitude(request.getLongitude());
            store.setCategory(request.getCategory());
            store.setMinOrderAmount(request.getMinOrderAmount());
            store.setDeliveryFee(request.getDeliveryFee());
            store.setDeliveryTimeMin(request.getDeliveryTimeMin());
            store.setDeliveryTimeMax(request.getDeliveryTimeMax());
            store.setImageUrl(request.getImageUrl());

            log.info("Store 엔티티 생성 완료");
            log.info("데이터베이스 저장 중...");

            Store savedStore = storeRepository.save(store);

            log.info("가게 등록 완료! 생성된 가게 ID: {}, 가게명: {}", savedStore.getId(), savedStore.getName());
            log.info("=== 가게 등록 종료 ===");

            return savedStore;

        } catch (DataIntegrityViolationException e) {
            log.error("데이터 무결성 위반 (중복 사업자등록번호 등): {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청");  // 👈 추가
        } catch (Exception e) {
            log.error("가게 등록 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류");  // 👈 추가
        }
    }

    /**
     * 내 가게 정보 조회
     */
    public Store getMyStore() {
        Long currentUserId = getCurrentUserId();
        log.debug("JWT에서 추출한 사용자 ID로 가게 정보 조회: {}", currentUserId);

        Optional<Store> store = storeRepository.findByOwnerIdAndIsActiveTrue(currentUserId);

        if (store.isEmpty()) {
            log.warn("등록된 가게가 없습니다. 사용자 ID: {}", currentUserId);
            return null;  // 👈 null 반환
        }

        return store.get();
    }

    /**
     * 통합 가게 정보 수정 (동시성 문제 해결)
     * 모든 가게 정보를 하나의 메소드에서 처리
     */
    @Transactional
    public synchronized Store updateStore(StoreUpdateRequest request) {
        log.info("=== 통합 가게 정보 수정 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        Store store = getMyStore();
        boolean hasChanges = false;

        // 🏪 기본 정보 업데이트
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

        if (request.getCategory() != null && !request.getCategory().equals(store.getCategory())) {
            log.info("카테고리 변경: {} -> {}", store.getCategory(), request.getCategory());
            store.setCategory(request.getCategory());
            hasChanges = true;
        }

        if (request.getImageUrl() != null && !request.getImageUrl().equals(store.getImageUrl())) {
            log.info("이미지URL 변경: {} -> {}", store.getImageUrl(), request.getImageUrl());
            store.setImageUrl(request.getImageUrl());
            hasChanges = true;
        }

        if (!hasChanges) {
            log.info("변경된 정보가 없습니다.");
        } else {
            Store savedStore = storeRepository.save(store);
            log.info("=== 통합 가게 정보 수정 완료 ===");
            return savedStore;
        }

        return store;
    }

    /**
     * 통합 연락처 정보 수정
     */
    @Transactional
    public synchronized Store updateContact(StoreContactRequest request) {
        log.info("=== 가게 연락처 변경 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        Store store = getMyStore();
        boolean hasChanges = false;

        if (request.getPhone() != null && !request.getPhone().equals(store.getPhone())) {
            log.info("연락처 변경: {} -> {}", store.getPhone(), request.getPhone());
            store.setPhone(request.getPhone());
            hasChanges = true;
        }

        if (hasChanges) {
            Store savedStore = storeRepository.save(store);
            log.info("=== 가게 연락처 변경 완료 ===");
            return savedStore;
        }

        log.info("변경된 연락처 정보가 없습니다.");
        return store;
    }

    /**
     * 통합 배달 정보 수정
     */
    @Transactional
    public synchronized Store updateDelivery(StoreDeliveryRequest request) {
        log.info("=== 배달비/최소주문금액 변경 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        Store store = getMyStore();
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
            log.info("=== 배달 정보 변경 완료 ===");
            return savedStore;
        }

        log.info("변경된 배달 정보가 없습니다.");
        return store;
    }

    /**
     * 통합 위치 정보 수정
     */
    @Transactional
    public synchronized Store updateLocation(StoreLocationRequest request) {
        log.info("=== 가게 위치 정보 변경 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        Store store = getMyStore();
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

//        if (request.getLatitude() != null && !request.getLatitude().equals(store.getLatitude())) {
//            log.info("위도 변경: {} -> {}", store.getLatitude(), request.getLatitude());
//            store.setLatitude(request.getLatitude());
//            hasChanges = true;
//        }
//
//        if (request.getLongitude() != null && !request.getLongitude().equals(store.getLongitude())) {
//            log.info("경도 변경: {} -> {}", store.getLongitude(), request.getLongitude());
//            store.setLongitude(request.getLongitude());
//            hasChanges = true;
//        }

        if (hasChanges) {
            Store savedStore = storeRepository.save(store);
            log.info("=== 가게 위치 정보 변경 완료 ===");
            return savedStore;
        }

        log.info("변경된 위치 정보가 없습니다.");
        return store;
    }

    /**
     * 가게 삭제 (비활성화)
     */
    @Transactional
    public void deleteStore() {
        log.info("=== 가게 삭제(비활성화) 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        Store store = getMyStore();
        store.setIsActive(false);
        storeRepository.save(store);

        log.info("가게 삭제 완료 - 가게 ID: {}, 가게명: {}", store.getId(), store.getName());
        log.info("=== 가게 삭제(비활성화) 완료 ===");
    }

    /**
     * 가게 이미지 업로드
     */
    @Transactional
    public synchronized String uploadImage(MultipartFile file) {
        log.info("=== 가게 이미지 수정 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        Store store = getMyStore();
        Long storeId = store.getId();
        String oldImageUrl = store.getImageUrl();

        try {
            // S3에 새 이미지 업로드 (storeId 폴더에)
            String newImageUrl = s3Service.uploadFile(file, storeId);
            log.info("S3 업로드 성공 - Store ID: {}, URL: {}", storeId, newImageUrl);

            // DB에 새 URL 저장
            log.info("이미지 URL 변경: {} -> {}", oldImageUrl, newImageUrl);
            store.setImageUrl(newImageUrl);
            Store savedStore = storeRepository.save(store);

            // 기존 이미지 삭제 (새 이미지 저장 후)
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                try {
                    s3Service.deleteFile(oldImageUrl);
                    log.info("기존 이미지 삭제 완료: {}", oldImageUrl);
                } catch (Exception e) {
                    log.warn("기존 이미지 삭제 실패 (무시): {}", e.getMessage());
                }
            }

            log.info("=== 가게 이미지 수정 완료 ===");
            return savedStore.getImageUrl();

        } catch (Exception e) {
            log.error("이미지 업로드 실패 - Store ID: {}, 오류: {}", storeId, e.getMessage());
            throw new RuntimeException("이미지 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 가게 이미지 삭제
     */
    @Transactional
    public void deleteImage(Long imageId) {
        log.info("=== 가게 이미지 삭제 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        Store store = getMyStore();
        Long storeId = store.getId();
        String currentImageUrl = store.getImageUrl();

        if (currentImageUrl == null || currentImageUrl.isEmpty()) {
            log.info("삭제할 이미지가 없습니다 - Store ID: {}", storeId);
            return;
        }

        try {
            // S3에서 이미지 삭제
            s3Service.deleteFile(currentImageUrl);
            log.info("S3에서 이미지 삭제 완료 - Store ID: {}, URL: {}", storeId, currentImageUrl);

            // DB에서 URL 제거
            store.setImageUrl(null);
            storeRepository.save(store);

            log.info("=== 가게 이미지 삭제 완료 - Store ID: {} ===", storeId);

        } catch (Exception e) {
            log.error("이미지 삭제 실패 - Store ID: {}, 오류: {}", storeId, e.getMessage());
            throw new RuntimeException("이미지 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 가게 운영시간 조회
     */
    public List<StoreHour> getStoreHours() {
        log.info("=== 가게 운영 시간 조회 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        Store store = getMyStore();
        List<StoreHour> storeHours = store.getStoreHours();

        log.info("운영시간 조회 완료: {}개의 운영시간 설정", storeHours.size());
        return storeHours;
    }

    /**
     * 가게 운영시간 설정
     */
    @Transactional
    public List<StoreHourResponse> updateStoreHours(List<StoreHourRequest> requests) {
        log.info("=== 가게 운영 시간 설정 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        Store store = getMyStore();

        // 업데이트 대상 요일 수집
        Set<Integer> targetDays = new HashSet<>();

        for (StoreHourRequest request : requests) {
            if (request.getDayOfWeek() == 7) {
                // 전체 요일 (0~6)
                for (int day = 0; day <= 6; day++) {
                    targetDays.add(day);
                }
            } else {
                // 특정 요일
                targetDays.add(request.getDayOfWeek());
            }
        }

        // 해당 요일들만 DB에서 삭제
        storeHourRepository.deleteByStoreIdAndDayOfWeekIn(store.getId(), targetDays);

        // 메모리에서도 해당 요일만 삭제
        store.getStoreHours().removeIf(hour -> targetDays.contains(hour.getDayOfWeek()));

        // 새로운 운영시간 설정
        List<StoreHour> updatedHours = new ArrayList<>();

        for (StoreHourRequest request : requests) {
            // dayOfWeek가 7이면 일주일 모든 요일에 적용 (0~6)
            if (request.getDayOfWeek() == 7) {
                for (int day = 0; day <= 6; day++) {
                    StoreHour storeHour = StoreHour.builder()
                            .store(store)
                            .dayOfWeek(day)
                            .openTime(request.getOpenTime())
                            .closeTime(request.getCloseTime())
                            .isClosed(request.getIsClosed())
                            .build();

                    updatedHours.add(storeHour);
                }
            } else {
                // 특정 요일만 설정
                StoreHour storeHour = StoreHour.builder()
                        .store(store)
                        .dayOfWeek(request.getDayOfWeek())
                        .openTime(request.getOpenTime())
                        .closeTime(request.getCloseTime())
                        .isClosed(request.getIsClosed())
                        .build();

                updatedHours.add(storeHour);
            }
        }

        // 새로운 운영시간들을 DB에 저장
        List<StoreHour> savedHours = storeHourRepository.saveAll(updatedHours);

        log.info("=== 가게 운영 시간 설정 완료 ===");

        // DTO로 변환해서 리턴
        return savedHours.stream()
                .map(StoreHourResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 휴무일 등록
     */
    @Transactional
    public ResponseEntity<String> createHoliday(StoreHolidayRequest request) {
        log.info("=== 휴무일 설정 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        try {
            Store store = getMyStore();

            if (request.getDate() == null) {
                return ResponseEntity.badRequest().body("휴무일을 입력해주세요");
            }

            if (request.getDate().isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest().body("과거 날짜는 휴무일로 설정할 수 없습니다");
            }

            boolean exists = storeHolidayRepository.existsByStoreIdAndDate(
                    store.getId(),
                    request.getDate()
            );

            if (exists) {
                return ResponseEntity.badRequest().body("이미 등록된 휴무일입니다: " + request.getDate());
            }

            StoreHoliday holiday = StoreHoliday.builder()
                    .store(store)
                    .date(request.getDate())
                    .reason(request.getReason())
                    .isRecurring(request.getIsRecurring() != null ? request.getIsRecurring() : false)
                    .build();

            storeHolidayRepository.save(holiday);

            log.info("휴무일 등록 완료: {} - {} (매년반복: {})",
                    request.getDate(),
                    request.getReason(),
                    holiday.getIsRecurring());
            log.info("=== 휴무일 설정 완료 ===");

            return ResponseEntity.ok("휴무일이 성공적으로 등록되었습니다");

        } catch (IllegalStateException e) {
            log.error("휴무일 등록 실패 - 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());

        } catch (IllegalArgumentException e) {
            log.error("휴무일 등록 실패 - 잘못된 입력: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            log.error("휴무일 등록 실패 - 서버 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("휴무일 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 휴무일 목록 조회
     */
    public List<StoreHolidayResponse> getHolidays() {
        log.info("=== 휴무일 조회 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        try {
            Store store = getMyStore();

            // 현재 날짜 이후의 휴무일들만 조회
            List<StoreHoliday> holidays = storeHolidayRepository
                    .findByStoreIdAndDateGreaterThanEqualOrderByDateAsc(store.getId(), LocalDate.now());

            // Entity -> DTO 변환
            List<StoreHolidayResponse> result = holidays.stream()
                    .map(StoreHolidayResponse::from)
                    .collect(Collectors.toList());

            log.info("휴무일 조회 완료: {}개", result.size());
            log.info("=== 휴무일 조회 완료 ===");

            return result;

        } catch (IllegalStateException e) {
            log.error("휴무일 조회 실패 - 인증 오류: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("휴무일 조회 실패: {}", e.getMessage());
            throw new RuntimeException("휴무일 조회 중 오류가 발생했습니다", e);
        }
    }

    /**
     * 휴무일 삭제
     */
    @Transactional
    public ResponseEntity<String> deleteHoliday(Long holidayId) {
        log.info("=== 휴무일 삭제 시작 ===");
        log.info("삭제 요청 휴무일 ID: {}", holidayId);

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        try {
            Store store = getMyStore();

            // 휴무일 존재 및 권한 확인
            StoreHoliday holiday = storeHolidayRepository.findById(holidayId)
                    .orElse(null);

            if (holiday == null) {
                return ResponseEntity.badRequest().body("존재하지 않는 휴무일입니다");
            }

            if (!holiday.getStore().getId().equals(store.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("다른 가게의 휴무일은 삭제할 수 없습니다");
            }

            // 삭제
            storeHolidayRepository.delete(holiday);

            log.info("휴무일 삭제 완료: {} - {}", holiday.getDate(), holiday.getReason());
            log.info("=== 휴무일 삭제 완료 ===");

            return ResponseEntity.ok("휴무일이 성공적으로 삭제되었습니다");

        } catch (IllegalStateException e) {
            log.error("휴무일 삭제 실패 - 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());

        } catch (Exception e) {
            log.error("휴무일 삭제 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("휴무일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 가게 상태 조회
     */
    public StoreStatusResponse getStoreStatus() {
        log.info("=== 가게 상태 조회 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        Store store = getMyStore();

        // 현재 시간 기준 영업 상태 체크
        boolean isCurrentlyOpen = checkIfCurrentlyOpen(store);

        // 오늘 영업 상태 메시지 생성
        String currentDayStatus = getCurrentDayStatusMessage(store);

        log.info("가게 상태 조회 완료 - 현재 영업 중: {}", isCurrentlyOpen);
        log.info("=== 가게 상태 조회 완료 ===");

        return StoreStatusResponse.from(store, isCurrentlyOpen, currentDayStatus);
    }

    /**
     * 영업 상태 변경
     */
    @Transactional
    public synchronized StoreStatusModifyResponse updateStoreStatus(StoreStatusRequest request) {
        log.info("=== 영업 상태 변경 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        // 유효성 검사
        if (request.getStatus() == null) {
            throw new IllegalArgumentException("영업 상태는 필수입니다.");
        }

        Store store = getMyStore();

        // 현재 상태와 동일한지 확인
        if (store.getStatus() == request.getStatus()) {
            log.info("이미 동일한 상태입니다: {}", request.getStatus());
            return StoreStatusModifyResponse.of(store, "이미 동일한 상태입니다.");
        }

        // 상태 변경
        store.setStatus(request.getStatus());

        // 변경 사유 로그 기록
        if (request.getMessage() != null && !request.getMessage().trim().isEmpty()) {
            log.info("영업 상태 변경 사유: {}", request.getMessage());
        }

        log.info("영업 상태가 {}로 변경되었습니다.", request.getStatus());

        // 저장
        Store savedStore = storeRepository.save(store);
        log.info("=== 영업 상태 변경 완료 ===");

        // Response 객체 반환
        return StoreStatusModifyResponse.of(savedStore, "영업 상태가 성공적으로 변경되었습니다.");
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
        if (store.getStatus() != null && "CLOSED".equals(store.getStatus().toString())) {
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
     * 가게 대시보드 데이터 조회
     */
    public StoreDashboardResponse getDashboard() {
        log.info("=== 가게 대시보드 조회 시작 ===");

        Long currentUserId = getCurrentUserId();
        log.info("JWT에서 추출한 사용자 ID: {}", currentUserId);

        Store store = getMyStore();
        if (store == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없음");
        }

        try {
            // 1. 오늘 통계 데이터
            Long todayOrderCount = storeRepository.countTodayOrdersByStoreId(store.getId());
            BigDecimal todayRevenue = storeRepository.getTodayRevenueByStoreId(store.getId());

            StoreDashboardResponse.TodayStats todayStats = new StoreDashboardResponse.TodayStats(todayOrderCount, todayRevenue);

            // 2. 가게 정보
            BigDecimal storeRating = reviewRepository.findAverageRatingByStoreId(store.getId());
            if (storeRating == null) {
                storeRating = BigDecimal.ZERO;
            }
            Long reviewCount = reviewRepository.countByStoreId(store.getId());
            Long totalOrderCount = storeRepository.countTotalOrdersByStoreId(store.getId());

            StoreDashboardResponse.RestaurantInfo restaurant = new StoreDashboardResponse.RestaurantInfo(
                    store.getId(),
                    store.getName(),
                    storeRating.setScale(1, java.math.RoundingMode.HALF_UP),
                    reviewCount,
                    totalOrderCount
            );

            // 3. 운영시간 정보
            List<StoreDashboardResponse.StoreHourInfo> storeHours = store.getStoreHours().stream()
                    .map(hour -> new StoreDashboardResponse.StoreHourInfo(
                            hour.getDayOfWeek(),
                            hour.getOpenTime() != null ? hour.getOpenTime().toString() : null,
                            hour.getCloseTime() != null ? hour.getCloseTime().toString() : null,
                            hour.getIsClosed()
                    ))
                    .collect(Collectors.toList());

            // 4. 최근 주문 정보
            List<StoreDashboardResponse.RecentOrderInfo> recentOrders = getRecentOrdersForDashboard(store.getId());

            StoreDashboardResponse response = new StoreDashboardResponse(
                    todayStats,
                    restaurant,
                    storeHours,
                    recentOrders
            );

            log.info("대시보드 조회 완료 - 오늘 주문: {}건, 오늘 매출: {}원, 평점: {}",
                    todayOrderCount, todayRevenue, storeRating);
            log.info("=== 가게 대시보드 조회 완료 ===");

            return response;

        } catch (Exception e) {
            log.error("대시보드 조회 실패: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "대시보드 조회 중 오류가 발생했습니다");
        }
    }

    /**
     * 대시보드용 최근 주문 정보 조회
     */
    private List<StoreDashboardResponse.RecentOrderInfo> getRecentOrdersForDashboard(Long storeId) {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(0, 5);

        List<Order> recentOrders = storeRepository.findRecentOrdersByStoreId(storeId, pageable);

        return recentOrders.stream()
                .map(order -> {
                    // 주문 상태를 프론트엔드 형식으로 변환
                    String frontendStatus = convertOrderStatusToFrontend(order.getStatus());

                    // 주문 아이템 정보
                    List<StoreDashboardResponse.OrderItemInfo> items = order.getOrderItems().stream()
                            .map(item -> new StoreDashboardResponse.OrderItemInfo(
                                    item.getMenuName(),
                                    item.getQuantity()
                            ))
                            .collect(Collectors.toList());

                    // 주문 시간 포맷팅
                    String orderTime = order.getOrderedAt().format(
                            java.time.format.DateTimeFormatter.ofPattern("MM월 dd일 HH:mm")
                    );

                    return new StoreDashboardResponse.RecentOrderInfo(  // 여기도 수정!
                            order.getId(),
                            order.getOrderNumber(),
                            order.getUser() != null ? order.getUser().getName() : "알 수 없음",
                            order.getTotalAmount(),
                            frontendStatus,
                            orderTime,
                            items
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 주문 상태를 프론트엔드 형식으로 변환
     */
    private String convertOrderStatusToFrontend(io.goorm.team02.core.orders.domain.enums.OrderStatus status) {
        switch (status) {
            case PENDING: return "pending";
            case ACCEPTED: return "preparing";
            case COOKING: return "preparing";
            case READY: return "ready";
            case DELIVERED: return "delivered";
            default: return status.toString().toLowerCase();
        }
    }

    /**
     * 긴급 주문 여부 판단
     */
    private boolean isUrgentOrder(Order order) {
        // PENDING 상태이고 30분 이상 지난 주문
        if (!"PENDING".equals(order.getStatus().toString())) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime orderedAt = order.getOrderedAt();

        return orderedAt.isBefore(now.minusMinutes(30));
    }

    // 새로 추가할 메서드 - 오늘의 운영시간만 문자열로 반환
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

}