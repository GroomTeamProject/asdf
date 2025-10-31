package io.goorm.team02.core.owner.stores.controller;

import io.goorm.team02.core.owner.auth.annotation.CurrentUser;
import io.goorm.team02.core.owner.stores.component.FileUploadValidator;
import io.goorm.team02.core.owner.stores.service.StoreService;
import io.goorm.team02.dto.owner.stores.dashboard.StoreDashboardResponse;
import io.goorm.team02.dto.owner.stores.storemanagement.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner/store")
@RequiredArgsConstructor
public class StoreController implements StoreControllerDocs {

    private final StoreService storeService;
    private final FileUploadValidator fileUploadValidator;

    // ================================
    // 테스트
    // ================================

    @GetMapping("/test")
    @Override
    public String test() {
        return "StoreController is working!";
    }

    // ================================
    // 2.1 가게 기본 정보
    // ================================

    @PostMapping
    @Override
    public ResponseEntity<StoreResponse> createStore(@CurrentUser Long currentUser,
                                                     @RequestBody StoreCreateRequest request) {
        StoreResponse response = storeService.createStore(currentUser, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getMyStore(@CurrentUser Long currentUser) {
        StoreResponse store = storeService.getMyStore(currentUser);

        if (store == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "등록된 가게가 없습니다"));
        }

        return ResponseEntity.ok(store);
    }

    @PutMapping
    @Override
    public ResponseEntity<StoreResponse> updateStore(@CurrentUser Long currentUser,
                                                     @RequestBody StoreUpdateRequest request) {
        StoreResponse response = storeService.updateStore(currentUser, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Override
    public ResponseEntity<Void> deleteStore(@CurrentUser Long currentUser) {
        storeService.deleteStore(currentUser);
        return ResponseEntity.ok().build();
    }

    // ================================
    // 2.2 가게 상세 설정
    // ================================

    @PutMapping("/contact")
    @Override
    public ResponseEntity<StoreResponse> updateContact(@CurrentUser Long currentUser,
                                                       @RequestBody StoreContactRequest request) {
        StoreResponse response = storeService.updateContact(currentUser, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/delivery")
    @Override
    public ResponseEntity<StoreResponse> updateDelivery(@CurrentUser Long currentUser,
                                                        @RequestBody StoreDeliveryRequest request) {
        StoreResponse response = storeService.updateDelivery(currentUser, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/location")
    @Override
    public ResponseEntity<StoreResponse> updateLocation(@CurrentUser Long currentUser,
                                                        @RequestBody StoreLocationRequest request) {
        StoreResponse response = storeService.updateLocation(currentUser, request);
        return ResponseEntity.ok(response);
    }

    // ================================
    // 2.3 이미지 관리
    // ================================

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<String> uploadImage(@CurrentUser Long currentUser,
                                              @RequestParam("file") MultipartFile file) {

        // 파일 검증
        FileValidationResult validationResult = fileUploadValidator.validate(file, currentUser);
        if (!validationResult.isValid()) {
            return ResponseEntity.status(validationResult.getStatus())
                    .body(validationResult.getMessage());
        }

        try {
            String imageUrl = storeService.uploadImage(currentUser, file);
            return ResponseEntity.ok(imageUrl);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미지 업로드에 실패했습니다");
        }
    }

    @DeleteMapping("/images/{id}")
    @Override
    public ResponseEntity<Void> deleteImage(@CurrentUser Long currentUser,
                                            @PathVariable Long id) {
        try {
            storeService.deleteImage(currentUser, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ================================
    // 2.4 운영시간 관리
    // ================================

    @GetMapping("/hours")
    @Override
    public ResponseEntity<List<StoreHourResponse>> getStoreHours(@CurrentUser Long currentUser) {
        List<StoreHourResponse> response = storeService.getStoreHours(currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/hours")
    @Override
    public ResponseEntity<List<StoreHourResponse>> updateStoreHours(@CurrentUser Long currentUser,
                                                                    @RequestBody List<StoreHourRequest> requests) {
        List<StoreHourResponse> response = storeService.updateStoreHours(currentUser, requests);
        return ResponseEntity.ok(response);
    }

    // ================================
    // 2.5 휴무일 관리
    // ================================

    @PostMapping("/holidays")
    @Override
    public ResponseEntity<String> createHoliday(@CurrentUser Long currentUser,
                                                @RequestBody StoreHolidayRequest request) {
        return storeService.createHoliday(currentUser, request);
    }

    @GetMapping("/holidays")
    @Override
    public ResponseEntity<List<StoreHolidayResponse>> getHolidays(@CurrentUser Long currentUser) {
        List<StoreHolidayResponse> response = storeService.getHolidays(currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/holidays/{id}")
    @Override
    public ResponseEntity<String> deleteHoliday(@CurrentUser Long currentUser,
                                                @PathVariable Long id) {
        return storeService.deleteHoliday(currentUser, id);
    }

    // ================================
    // 2.6 영업 상태 관리
    // ================================

    @GetMapping("/status")
    @Override
    public ResponseEntity<StoreStatusResponse> getStoreStatus(@CurrentUser Long currentUser) {
        StoreStatusResponse response = storeService.getStoreStatus(currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/status")
    @Override
    public ResponseEntity<StoreStatusModifyResponse> updateStoreStatus(@CurrentUser Long currentUser,
                                                                       @RequestBody StoreStatusRequest request) {
        StoreStatusModifyResponse response = storeService.updateStoreStatus(currentUser, request);
        return ResponseEntity.ok(response);
    }

    // ================================
    // 2.7 대시보드
    // ================================

    @GetMapping("/dashboard")
    @Override
    public ResponseEntity<StoreDashboardResponse> getDashboard(@CurrentUser Long currentUser) {
        StoreDashboardResponse dashboard = storeService.getDashboard(currentUser);
        return ResponseEntity.ok(dashboard);
    }
}