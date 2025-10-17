package io.goorm.team02.core.stores.controller;

import io.goorm.team02.core.auth.annotation.CurrentUser;
import io.goorm.team02.core.stores.controller.dto.dashboard.StoreDashboardResponse;
import io.goorm.team02.core.stores.controller.dto.storemanagement.*;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.domain.StoreHour;
import io.goorm.team02.core.stores.domain.TempUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Store Management", description = "가게 관리 API")
@SecurityRequirement(name = "JWT Token")
public interface StoreControllerDocs {

    // ================================
    // 테스트
    // ================================

    @Operation(summary = "테스트", description = "컨트롤러 동작 테스트")
    String test();

    // ================================
    // 2.1 가게 기본 정보
    // ================================

    @Operation(summary = "가게 등록", description = "새로운 가게를 등록합니다 (최초 1회)")
    @Tag(name = "Store Basic Info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 등록된 가게가 있음")
    })
    Store createStore(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser,
            @Parameter(description = "가게 생성 요청 정보", required = true)
            @RequestBody StoreCreateRequest request
    );

    @Operation(summary = "내 가게 정보 조회", description = "현재 사용자의 가게 정보를 조회합니다")
    @Tag(name = "Store Basic Info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<?> getMyStore(@Parameter(hidden = true) @CurrentUser TempUser currentUser);

    @Operation(summary = "가게 기본 정보 수정", description = "가게의 기본 정보를 수정합니다")
    @Tag(name = "Store Basic Info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<StoreResponse> updateStore(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser,
            @Parameter(description = "가게 수정 요청 정보", required = true)
            @RequestBody StoreUpdateRequest request
    );

    @Operation(summary = "가게 삭제", description = "가게를 비활성화합니다 (완전 삭제가 아닌 상태 변경)")
    @Tag(name = "Store Basic Info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<Void> deleteStore(@Parameter(hidden = true) @CurrentUser TempUser currentUser);

    // ================================
    // 2.2 가게 상세 설정
    // ================================

    @Operation(summary = "연락처 정보 수정", description = "가게의 연락처 정보를 수정합니다")
    @Tag(name = "Store Detail Settings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연락처 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 연락처 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<StoreResponse> updateContact(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser,
            @Parameter(description = "연락처 수정 요청 정보", required = true)
            @RequestBody StoreContactRequest request
    );

    @Operation(summary = "배달 설정 수정", description = "배달비 및 최소주문금액을 설정합니다")
    @Tag(name = "Store Detail Settings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배달 설정 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 배달 설정 값"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<StoreResponse> updateDelivery(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser,
            @Parameter(description = "배달 설정 요청 정보", required = true)
            @RequestBody StoreDeliveryRequest request
    );

    @Operation(summary = "위치 정보 수정", description = "가게 주소 및 배달 가능 지역을 설정합니다")
    @Tag(name = "Store Detail Settings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위치 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 주소 정보"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<StoreResponse> updateLocation(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser,
            @Parameter(description = "위치 정보 요청", required = true)
            @RequestBody StoreLocationRequest request
    );

    // ================================
    // 2.3 이미지 관리
    // ================================

    @Operation(summary = "가게 이미지 업로드", description = "가게 대표 이미지를 업로드합니다")
    @Tag(name = "Store Image Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "파일이 비어있거나 잘못된 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "413", description = "파일 크기가 너무 큼"),
            @ApiResponse(responseCode = "500", description = "파일 업로드 실패")
    })
    ResponseEntity<String> uploadImage(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser,
            @Parameter(description = "업로드할 이미지 파일 (JPG, PNG 등)", required = true)
            @RequestParam("file") MultipartFile file
    );

    @Operation(summary = "가게 이미지 삭제", description = "업로드된 가게 이미지를 삭제합니다")
    @Tag(name = "Store Image Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "삭제 권한이 없음"),
            @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    })
    ResponseEntity<Void> deleteImage(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser,
            @Parameter(description = "삭제할 이미지 ID", required = true, example = "1")
            @PathVariable Long id
    );

    // ================================
    // 2.4 운영시간 관리
    // ================================

    @Operation(summary = "운영시간 조회", description = "가게의 요일별 운영시간을 조회합니다")
    @Tag(name = "Store Hours Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "운영시간 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<List<StoreHourResponse>> getStoreHours(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser
    );

    @Operation(summary = "운영시간 설정", description = "원하는 혹은 전체 요일의 운영시간을 설정합니다")
    @Tag(name = "Store Hours Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "운영시간 설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 시간 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    List<StoreHourResponse> updateStoreHours(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser,
            @Parameter(description = "요일 (0:일요일, 1:월요일, ..., 6:토요일, 7:일괄)", required = true)
            @RequestBody List<StoreHourRequest> requests
    );

    // ================================
    // 2.5 휴무일 관리
    // ================================

    @Operation(summary = "휴무일 등록", description = "특정 날짜를 휴무일로 등록합니다")
    @Tag(name = "Store Holiday Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "휴무일 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 등록된 휴무일"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<String> createHoliday(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser,
            @Parameter(description = "휴무일 등록 정보", required = true)
            @RequestBody StoreHolidayRequest request
    );

    @Operation(summary = "휴무일 목록 조회", description = "등록된 휴무일 목록을 조회합니다")
    @Tag(name = "Store Holiday Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "휴무일 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    List<StoreHolidayResponse> getHolidays(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser
    );

    @Operation(summary = "휴무일 삭제", description = "등록된 휴무일을 삭제합니다")
    @Tag(name = "Store Holiday Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "휴무일 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 휴무일"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "삭제 권한이 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<String> deleteHoliday(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser,
            @Parameter(description = "삭제할 휴무일 ID", required = true, example = "1")
            @PathVariable Long id
    );

    // ================================
    // 2.6 영업 상태 관리
    // ================================

    @Operation(summary = "가게 상태 조회", description = "가게의 현재 운영 상태를 조회합니다")
    @Tag(name = "Store Status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 상태 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<StoreStatusResponse> getStoreStatus(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser
    );

    @Operation(summary = "영업 상태 변경", description = "가게의 영업 상태를 변경합니다 (영업중/준비중/마감)")
    @Tag(name = "Store Status Management")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "영업 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 상태 값"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<StoreStatusModifyResponse> updateStoreStatus(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser,
            @Parameter(description = "영업 상태 변경 요청", required = true)
            @RequestBody StoreStatusRequest request
    );

    // ================================
    // 2.7 대시보드
    // ================================

    @Operation(summary = "가게 대시보드 조회", description = "가게 운영에 필요한 통계 정보를 조회합니다")
    @Tag(name = "Store Dashboard")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대시보드 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음")
    })
    ResponseEntity<StoreDashboardResponse> getDashboard(
            @Parameter(hidden = true) @CurrentUser TempUser currentUser
    );
}