// controller/StoreController.java
package io.goorm.team02.core.stores.controller;

import io.goorm.team02.core.auth.annotation.CurrentUser;
import io.goorm.team02.core.stores.component.FileUploadValidator;
import io.goorm.team02.core.stores.controller.StoreControllerDocs;
import io.goorm.team02.core.stores.controller.dto.FileValidationResult;
import io.goorm.team02.core.stores.controller.dto.dashboard.StoreDashboardResponse;
import io.goorm.team02.core.stores.controller.dto.storemanagement.*;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.domain.StoreHour;
import io.goorm.team02.core.stores.domain.TempUser;
import io.goorm.team02.core.stores.service.StoreService;
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
    public Store createStore(@CurrentUser TempUser currentUser,
                             @RequestBody StoreCreateRequest request) {
        return storeService.createStore(currentUser, request);
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getMyStore(@CurrentUser TempUser currentUser) {
        Store store = storeService.getMyStore(currentUser);

        if (store == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "등록된 가게가 없습니다")); // 🔒 에러 정보 축소
        }

        return ResponseEntity.ok(StoreResponse.from(store));
    }

    @PutMapping
    @Override
    public ResponseEntity<StoreResponse> updateStore(@CurrentUser TempUser currentUser,
                                                     @RequestBody StoreUpdateRequest request) {
        Store updatedStore = storeService.updateStore(currentUser, request);
        return ResponseEntity.ok(StoreResponse.from(updatedStore));
    }

    @DeleteMapping
    @Override
    public ResponseEntity<Void> deleteStore(@CurrentUser TempUser currentUser) {
        storeService.deleteStore(currentUser);
        return ResponseEntity.ok().build();
    }

    // ================================
    // 2.2 가게 상세 설정
    // ================================

    @PutMapping("/contact")
    @Override
    public ResponseEntity<StoreResponse> updateContact(@CurrentUser TempUser currentUser,
                                                       @RequestBody StoreContactRequest request) {
        Store updatedStore = storeService.updateContact(currentUser, request);
        return ResponseEntity.ok(StoreResponse.from(updatedStore));
    }

    @PutMapping("/delivery")
    @Override
    public ResponseEntity<StoreResponse> updateDelivery(@CurrentUser TempUser currentUser,
                                                        @RequestBody StoreDeliveryRequest request) {
        Store updatedStore = storeService.updateDelivery(currentUser, request);
        return ResponseEntity.ok(StoreResponse.from(updatedStore));
    }

    @PutMapping("/location")
    @Override
    public ResponseEntity<StoreResponse> updateLocation(@CurrentUser TempUser currentUser,
                                                        @RequestBody StoreLocationRequest request) {
        Store updatedStore = storeService.updateLocation(currentUser, request);
        return ResponseEntity.ok(StoreResponse.from(updatedStore));
    }

    // ================================
// 2.3 이미지 관리
// ================================

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<String> uploadImage(@CurrentUser TempUser currentUser,
                                              @RequestParam("file") MultipartFile file) {

        // 🔒 파일 검증 (한 번에 처리)
        FileValidationResult validationResult = fileUploadValidator.validate(file, currentUser.getId());
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
    public ResponseEntity<Void> deleteImage(@CurrentUser TempUser currentUser,
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
    public ResponseEntity<List<StoreHourResponse>> getStoreHours(@CurrentUser TempUser currentUser) {
        List<StoreHour> storeHours = storeService.getStoreHours(currentUser);
        List<StoreHourResponse> response = storeHours.stream()
                .map(StoreHourResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/hours")
    @Override
    public List<StoreHourResponse> updateStoreHours(@CurrentUser TempUser currentUser,
                                                    @RequestBody List<StoreHourRequest> requests) {
        return storeService.updateStoreHours(currentUser, requests);
    }

    // ================================
    // 2.5 휴무일 관리
    // ================================

    @PostMapping("/holidays")
    @Override
    public ResponseEntity<String> createHoliday(@CurrentUser TempUser currentUser,
                                                @RequestBody StoreHolidayRequest request) {
        return storeService.createHoliday(currentUser, request);
    }

    @GetMapping("/holidays")
    @Override
    public List<StoreHolidayResponse> getHolidays(@CurrentUser TempUser currentUser) {
        return storeService.getHolidays(currentUser);
    }

    @DeleteMapping("/holidays/{id}")
    @Override
    public ResponseEntity<String> deleteHoliday(@CurrentUser TempUser currentUser,
                                                @PathVariable Long id) {
        return storeService.deleteHoliday(currentUser, id);
    }

    // ================================
    // 2.6 영업 상태 관리
    // ================================

    @GetMapping("/status")
    @Override
    public ResponseEntity<StoreStatusResponse> getStoreStatus(@CurrentUser TempUser currentUser) {
        StoreStatusResponse response = storeService.getStoreStatus(currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/status")
    @Override
    public ResponseEntity<StoreStatusModifyResponse> updateStoreStatus(@CurrentUser TempUser currentUser,
                                                                       @RequestBody StoreStatusRequest request) {
        StoreStatusModifyResponse response = storeService.updateStoreStatus(currentUser, request);
        return ResponseEntity.ok(response);
    }

    // ================================
    // 2.7 대시보드
    // ================================

    @GetMapping("/dashboard")
    @Override
    public ResponseEntity<StoreDashboardResponse> getDashboard(@CurrentUser TempUser currentUser) {
        StoreDashboardResponse dashboard = storeService.getDashboard(currentUser);
        return ResponseEntity.ok(dashboard);
    }
}