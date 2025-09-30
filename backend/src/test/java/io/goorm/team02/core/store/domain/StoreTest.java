package io.goorm.team02.core.store.domain;

import io.goorm.team02.core.stores.events.ImageCleanupEvent;
import io.goorm.team02.core.common.service.S3Service;
import io.goorm.team02.core.orders.service.OrderService;
import io.goorm.team02.core.reviews.repository.ReviewRepository;
import io.goorm.team02.core.stores.controller.dto.dashboard.StoreDashboardResponse;
import io.goorm.team02.core.stores.controller.dto.storemanagement.*;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.domain.StoreHour;
import io.goorm.team02.core.stores.domain.StoreHoliday;
import io.goorm.team02.core.stores.domain.TempUser;
import io.goorm.team02.core.stores.domain.enums.StoreCategory;
import io.goorm.team02.core.stores.domain.enums.StoreStatus;
import io.goorm.team02.core.stores.domain.enums.UserType;
import io.goorm.team02.core.stores.repository.StoreHolidayRepository;
import io.goorm.team02.core.stores.repository.StoreHourRepository;
import io.goorm.team02.core.stores.repository.StoreRepository;
import io.goorm.team02.core.stores.repository.UserRepository;
import io.goorm.team02.core.stores.service.StoreService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreService 테스트")
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreHourRepository storeHourRepository;

    @Mock
    private StoreHolidayRepository storeHolidayRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ApplicationEventPublisher eventPublisher; // 👈 추가

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private StoreService storeService;

    private TempUser testUser;
    private Store testStore;
    private StoreCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = new TempUser();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("테스트 사장님");
        testUser.setUserType(UserType.OWNER);
        testUser.setPhone("010-1234-5678");
        testUser.setIsActive(true);

        // 테스트용 가게 생성
        testStore = new Store();
        testStore.setId(1L);
        testStore.setOwner(testUser);
        testStore.setBusinessNumber("123-45-67890");
        testStore.setName("테스트 가게");
        testStore.setDescription("테스트 가게 설명");
        testStore.setPhone("02-1234-5678");
        testStore.setAddress("서울시 강남구");
        testStore.setDetailAddress("123번지");
        testStore.setLatitude(new BigDecimal("37.5665"));
        testStore.setLongitude(new BigDecimal("126.9780"));
        testStore.setCategory(StoreCategory.KOREAN);
        testStore.setMinOrderAmount(new BigDecimal("15000"));
        testStore.setDeliveryFee(new BigDecimal("3000"));
        testStore.setDeliveryTimeMin(30);
        testStore.setDeliveryTimeMax(50);
        testStore.setStatus(StoreStatus.CLOSED);
        testStore.setIsActive(true);
        testStore.setStoreHours(new ArrayList<>());
        testStore.setImageUrl(null);

        // 테스트용 가게 생성 요청 DTO
        createRequest = new StoreCreateRequest();
        createRequest.setBusinessNumber("123-45-67890");
        createRequest.setName("테스트 가게");
        createRequest.setDescription("테스트 가게 설명");
        createRequest.setPhone("02-1234-5678");
        createRequest.setAddress("서울시 강남구");
        createRequest.setDetailAddress("123번지");
        createRequest.setLatitude(new BigDecimal("37.5665"));
        createRequest.setLongitude(new BigDecimal("126.9780"));
        createRequest.setCategory(StoreCategory.KOREAN);
        createRequest.setMinOrderAmount(new BigDecimal("15000"));
        createRequest.setDeliveryFee(new BigDecimal("3000"));
        createRequest.setDeliveryTimeMin(30);
        createRequest.setDeliveryTimeMax(50);
        createRequest.setImageUrl("https://example.com/image.jpg");
    }

    @Nested
    @DisplayName("가게 등록 테스트")
    class CreateStoreTest {

        @Test
        @DisplayName("정상적인 가게 등록 성공")
        void createStore_Success() {
            // Given
            when(storeRepository.existsByOwnerIdAndIsActiveTrue(testUser.getId())).thenReturn(false);
            when(storeRepository.save(any(Store.class))).thenReturn(testStore);

            // When
            Store result = storeService.createStore(testUser, createRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("테스트 가게");
            assertThat(result.getOwner()).isEqualTo(testUser);
            assertThat(result.getBusinessNumber()).isEqualTo("123-45-67890");

            verify(storeRepository).existsByOwnerIdAndIsActiveTrue(testUser.getId());
            verify(storeRepository).save(any(Store.class));
        }

        @Test
        @DisplayName("이미 가게가 등록된 경우 예외 발생")
        void createStore_StoreAlreadyExists_ThrowsException() {
            // Given
            when(storeRepository.existsByOwnerIdAndIsActiveTrue(testUser.getId())).thenReturn(true);

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.createStore(testUser, createRequest)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(exception.getReason()).isEqualTo("이미 등록된 가게가 있습니다");
        }

        @Test
        @DisplayName("필수 필드가 없는 경우 예외 발생 - 가게명")
        void createStore_MissingName_ThrowsException() {
            // Given
            createRequest.setName(null);
            when(storeRepository.existsByOwnerIdAndIsActiveTrue(testUser.getId())).thenReturn(false);

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.createStore(testUser, createRequest)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(exception.getReason()).isEqualTo("가게명은 필수입니다");
        }

        @Test
        @DisplayName("필수 필드가 없는 경우 예외 발생 - 사업자등록번호")
        void createStore_MissingBusinessNumber_ThrowsException() {
            // Given
            createRequest.setBusinessNumber(null);
            when(storeRepository.existsByOwnerIdAndIsActiveTrue(testUser.getId())).thenReturn(false);

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.createStore(testUser, createRequest)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(exception.getReason()).isEqualTo("사업자등록번호는 필수입니다");
        }

        @Test
        @DisplayName("데이터 무결성 위반 시 예외 발생")
        void createStore_DataIntegrityViolation_ThrowsException() {
            // Given
            when(storeRepository.existsByOwnerIdAndIsActiveTrue(testUser.getId())).thenReturn(false);
            when(storeRepository.save(any(Store.class)))
                    .thenThrow(new DataIntegrityViolationException("중복 사업자등록번호"));

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.createStore(testUser, createRequest)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(exception.getReason()).isEqualTo("중복된 사업자등록번호입니다");
        }
    }

    @Nested
    @DisplayName("내 가게 정보 조회 테스트")
    class GetMyStoreTest {

        @Test
        @DisplayName("가게 정보 조회 성공")
        void getMyStore_Success() {
            // Given
            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));

            // When
            Store result = storeService.getMyStore(testUser);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("테스트 가게");

            verify(storeRepository).findByOwnerIdAndIsActiveTrue(testUser.getId());
        }

        @Test
        @DisplayName("등록된 가게가 없는 경우 null 반환")
        void getMyStore_NoStore_ReturnsNull() {
            // Given
            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.empty());

            // When
            Store result = storeService.getMyStore(testUser);

            // Then
            assertThat(result).isNull();
            verify(storeRepository).findByOwnerIdAndIsActiveTrue(testUser.getId());
        }
    }

    @Nested
    @DisplayName("가게 정보 수정 테스트")
    class UpdateStoreTest {

        @Test
        @DisplayName("가게 정보 수정 성공")
        void updateStore_Success() {
            // Given
            StoreUpdateRequest updateRequest = new StoreUpdateRequest();
            updateRequest.setName("수정된 가게명");
            updateRequest.setDescription("수정된 설명");
            updateRequest.setCategory(StoreCategory.CHINESE);

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeRepository.save(any(Store.class))).thenReturn(testStore);

            // When
            Store result = storeService.updateStore(testUser, updateRequest);

            // Then
            assertThat(result).isNotNull();
            verify(storeRepository).save(any(Store.class));
        }

        @Test
        @DisplayName("변경된 정보가 없는 경우")
        void updateStore_NoChanges() {
            // Given
            StoreUpdateRequest updateRequest = new StoreUpdateRequest();
            // 모든 필드를 null로 두어 변경사항이 없도록 함

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));

            // When
            Store result = storeService.updateStore(testUser, updateRequest);

            // Then
            assertThat(result).isNotNull();
            verify(storeRepository, never()).save(any(Store.class));
        }

        @Test
        @DisplayName("가게를 찾을 수 없는 경우 예외 발생")
        void updateStore_StoreNotFound_ThrowsException() {
            // Given
            StoreUpdateRequest updateRequest = new StoreUpdateRequest();
            updateRequest.setName("수정된 가게명");

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.empty());

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.updateStore(testUser, updateRequest)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(exception.getReason()).isEqualTo("가게를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("연락처 정보 수정 테스트")
    class UpdateContactTest {

        @Test
        @DisplayName("연락처 정보 수정 성공")
        void updateContact_Success() {
            // Given
            StoreContactRequest contactRequest = new StoreContactRequest();
            contactRequest.setPhone("02-9999-8888");

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeRepository.save(any(Store.class))).thenReturn(testStore);

            // When
            Store result = storeService.updateContact(testUser, contactRequest);

            // Then
            assertThat(result).isNotNull();
            verify(storeRepository).save(any(Store.class));
        }

        @Test
        @DisplayName("변경된 연락처 정보가 없는 경우")
        void updateContact_NoChanges() {
            // Given
            StoreContactRequest contactRequest = new StoreContactRequest();
            contactRequest.setPhone(testStore.getPhone()); // 동일한 연락처

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));

            // When
            Store result = storeService.updateContact(testUser, contactRequest);

            // Then
            assertThat(result).isNotNull();
            verify(storeRepository, never()).save(any(Store.class));
        }
    }

    @Nested
    @DisplayName("배달 정보 수정 테스트")
    class UpdateDeliveryTest {

        @Test
        @DisplayName("배달 정보 수정 성공")
        void updateDelivery_Success() {
            // Given
            StoreDeliveryRequest deliveryRequest = new StoreDeliveryRequest();
            deliveryRequest.setDeliveryFee(new BigDecimal("2500"));
            deliveryRequest.setMinOrderAmount(new BigDecimal("12000"));

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeRepository.save(any(Store.class))).thenReturn(testStore);

            // When
            Store result = storeService.updateDelivery(testUser, deliveryRequest);

            // Then
            assertThat(result).isNotNull();
            verify(storeRepository).save(any(Store.class));
        }
    }

    @Nested
    @DisplayName("위치 정보 수정 테스트")
    class UpdateLocationTest {

        @Test
        @DisplayName("위치 정보 수정 성공")
        void updateLocation_Success() {
            // Given
            StoreLocationRequest locationRequest = new StoreLocationRequest();
            locationRequest.setAddress("서울시 서초구");
            locationRequest.setDetailAddress("456번지");

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeRepository.save(any(Store.class))).thenReturn(testStore);

            // When
            Store result = storeService.updateLocation(testUser, locationRequest);

            // Then
            assertThat(result).isNotNull();
            verify(storeRepository).save(any(Store.class));
        }
    }

    @Nested
    @DisplayName("가게 삭제 테스트")
    class DeleteStoreTest {

        @Test
        @DisplayName("가게 삭제(비활성화) 성공")
        void deleteStore_Success() {
            // Given
            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeRepository.save(any(Store.class))).thenReturn(testStore);

            // When
            assertDoesNotThrow(() -> storeService.deleteStore(testUser));

            // Then
            verify(storeRepository).save(any(Store.class));
            assertThat(testStore.getIsActive()).isFalse();
        }

        @Test
        @DisplayName("가게를 찾을 수 없는 경우 예외 발생")
        void deleteStore_StoreNotFound_ThrowsException() {
            // Given
            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.empty());

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.deleteStore(testUser)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(exception.getReason()).isEqualTo("가게를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("트랜잭션 안전한 이미지 관리 테스트") // 👈 새로운 테스트 그룹
    class TransactionalImageManagementTest {

        @Test
        @DisplayName("트랜잭션 안전한 이미지 업로드 성공")
        void uploadImage_TransactionSafe_Success() {
            // Given
            String oldImageUrl = "https://s3.amazonaws.com/bucket/store/1/old-image.jpg";
            String tempImageUrl = "https://s3.amazonaws.com/bucket/temp/1/temp-image.jpg";
            String finalImageUrl = "https://s3.amazonaws.com/bucket/store/1/new-image.jpg";

            testStore.setImageUrl(oldImageUrl);

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(s3Service.uploadFileWithTransaction(mockFile, testStore.getId()))
                    .thenReturn(tempImageUrl);
            when(s3Service.moveToFinalPath(tempImageUrl, testStore.getId()))
                    .thenReturn(finalImageUrl);
            when(storeRepository.save(any(Store.class))).thenReturn(testStore);

            // When
            String result = storeService.uploadImage(testUser, mockFile);

            // Then
            assertThat(result).isEqualTo(finalImageUrl);

            // 이벤트 발행 검증
            ArgumentCaptor<ImageCleanupEvent> eventCaptor = ArgumentCaptor.forClass(ImageCleanupEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().getImageUrl()).isEqualTo(oldImageUrl);

            // 메소드 호출 순서 및 횟수 검증
            verify(s3Service).uploadFileWithTransaction(mockFile, testStore.getId());
            verify(s3Service).moveToFinalPath(tempImageUrl, testStore.getId());
            verify(storeRepository).save(any(Store.class));
        }

        @Test
        @DisplayName("임시 업로드 실패 시 보상 처리")
        void uploadImage_TempUploadFails_CompensationHandling() {
            // Given
            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(s3Service.uploadFileWithTransaction(mockFile, testStore.getId()))
                    .thenThrow(new RuntimeException("S3 임시 업로드 실패"));

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.uploadImage(testUser, mockFile)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(exception.getReason()).isEqualTo("이미지 업로드에 실패했습니다");

            // 실패 시 이벤트 발행되지 않음
            verify(eventPublisher, never()).publishEvent(any(ImageCleanupEvent.class));
            // DB 저장되지 않음
            verify(storeRepository, never()).save(any(Store.class));
        }

        @Test
        @DisplayName("파일 이동 실패 시 보상 처리")
        void uploadImage_MoveToFinalPathFails_CompensationHandling() {
            // Given
            String tempImageUrl = "https://s3.amazonaws.com/bucket/temp/1/temp-image.jpg";

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(s3Service.uploadFileWithTransaction(mockFile, testStore.getId()))
                    .thenReturn(tempImageUrl);
            when(s3Service.moveToFinalPath(tempImageUrl, testStore.getId()))
                    .thenThrow(new RuntimeException("파일 이동 실패"));

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.uploadImage(testUser, mockFile)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            // 보상 처리로 임시 파일 삭제 시도 (실패해도 무시)
            verify(s3Service).deleteFile(tempImageUrl);
        }

        @Test
        @DisplayName("DB 저장 실패 시 보상 처리")
        void uploadImage_DbSaveFails_CompensationHandling() {
            // Given
            String tempImageUrl = "https://s3.amazonaws.com/bucket/temp/1/temp-image.jpg";
            String finalImageUrl = "https://s3.amazonaws.com/bucket/store/1/new-image.jpg";

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(s3Service.uploadFileWithTransaction(mockFile, testStore.getId()))
                    .thenReturn(tempImageUrl);
            when(s3Service.moveToFinalPath(tempImageUrl, testStore.getId()))
                    .thenReturn(finalImageUrl);
            when(storeRepository.save(any(Store.class)))
                    .thenThrow(new RuntimeException("DB 저장 실패"));

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.uploadImage(testUser, mockFile)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            // 보상 처리로 최종 파일 삭제 시도
            verify(s3Service).deleteFile(finalImageUrl);
            // 이벤트 발행되지 않음
            verify(eventPublisher, never()).publishEvent(any(ImageCleanupEvent.class));
        }

        @Test
        @DisplayName("기존 이미지가 없는 경우 이벤트 발행하지 않음")
        void uploadImage_NoOldImage_NoEventPublished() {
            // Given
            String tempImageUrl = "https://s3.amazonaws.com/bucket/temp/1/temp-image.jpg";
            String finalImageUrl = "https://s3.amazonaws.com/bucket/store/1/new-image.jpg";

            testStore.setImageUrl(null); // 기존 이미지 없음

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(s3Service.uploadFileWithTransaction(mockFile, testStore.getId()))
                    .thenReturn(tempImageUrl);
            when(s3Service.moveToFinalPath(tempImageUrl, testStore.getId()))
                    .thenReturn(finalImageUrl);
            when(storeRepository.save(any(Store.class))).thenReturn(testStore);

            // When
            String result = storeService.uploadImage(testUser, mockFile);

            // Then
            assertThat(result).isEqualTo(finalImageUrl);

            // 기존 이미지가 없으므로 이벤트 발행되지 않음
            verify(eventPublisher, never()).publishEvent(any(ImageCleanupEvent.class));
        }

        @Test
        @DisplayName("안전한 이미지 삭제 - 이벤트 기반")
        void deleteImage_EventBased_Success() {
            // Given
            String existingImageUrl = "https://s3.amazonaws.com/bucket/store/1/old-image.jpg";
            testStore.setImageUrl(existingImageUrl);

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeRepository.save(any(Store.class))).thenReturn(testStore);

            // When
            assertDoesNotThrow(() -> storeService.deleteImage(testUser, 1L));

            // Then
            // DB에서 URL 제거
            verify(storeRepository).save(any(Store.class));
            assertThat(testStore.getImageUrl()).isNull();

            // 비동기 삭제를 위한 이벤트 발행
            ArgumentCaptor<ImageCleanupEvent> eventCaptor = ArgumentCaptor.forClass(ImageCleanupEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().getImageUrl()).isEqualTo(existingImageUrl);

            // S3 삭제는 이벤트 리스너에서 처리하므로 직접 호출되지 않음
            verify(s3Service, never()).deleteFile(anyString());
        }

        @Test
        @DisplayName("삭제할 이미지가 없는 경우 예외 발생")
        void deleteImage_NoImage_ThrowsException() {
            // Given
            testStore.setImageUrl(null);

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.deleteImage(testUser, 1L)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(exception.getReason()).isEqualTo("삭제할 이미지가 없습니다");

            // 이벤트 발행되지 않음
            verify(eventPublisher, never()).publishEvent(any(ImageCleanupEvent.class));
            verify(s3Service, never()).deleteFile(any());
        }
    }

    @Nested
    @DisplayName("운영시간 관리 테스트")
    class StoreHoursTest {

        @Test
        @DisplayName("운영시간 조회 성공")
        void getStoreHours_Success() {
            // Given
            List<StoreHour> storeHours = List.of(
                    StoreHour.builder()
                            .id(1L)
                            .store(testStore)
                            .dayOfWeek(1)
                            .openTime(LocalTime.of(9, 0))
                            .closeTime(LocalTime.of(22, 0))
                            .isClosed(false)
                            .build()
            );
            testStore.setStoreHours(storeHours);

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));

            // When
            List<StoreHour> result = storeService.getStoreHours(testUser);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDayOfWeek()).isEqualTo(1);
        }

        @Test
        @DisplayName("운영시간 설정 성공")
        void updateStoreHours_Success() {
            // Given
            StoreHourRequest hourRequest = new StoreHourRequest();
            hourRequest.setDayOfWeek(1);
            hourRequest.setOpenTime(LocalTime.of(9, 0));
            hourRequest.setCloseTime(LocalTime.of(22, 0));
            hourRequest.setIsClosed(false);

            List<StoreHourRequest> hourRequests = List.of(hourRequest);

            StoreHour savedHour = StoreHour.builder()
                    .id(1L)
                    .store(testStore)
                    .dayOfWeek(1)
                    .openTime(LocalTime.of(9, 0))
                    .closeTime(LocalTime.of(22, 0))
                    .isClosed(false)
                    .build();

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeHourRepository.saveAll(anyList())).thenReturn(List.of(savedHour));

            // When
            List<StoreHourResponse> result = storeService.updateStoreHours(testUser, hourRequests);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(1);

            verify(storeHourRepository).deleteByStoreIdAndDayOfWeekIn(eq(testStore.getId()), any());
            verify(storeHourRepository).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("휴무일 관리 테스트")
    class StoreHolidayTest {

        @Test
        @DisplayName("휴무일 등록 성공")
        void createHoliday_Success() {
            // Given
            StoreHolidayRequest holidayRequest = new StoreHolidayRequest();
            holidayRequest.setDate(LocalDate.now().plusDays(1));
            holidayRequest.setReason("정기 휴무");
            holidayRequest.setIsRecurring(false);

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeHolidayRepository.existsByStoreIdAndDate(testStore.getId(), holidayRequest.getDate()))
                    .thenReturn(false);

            StoreHoliday savedHoliday = StoreHoliday.builder()
                    .id(1L)
                    .store(testStore)
                    .date(holidayRequest.getDate())
                    .reason(holidayRequest.getReason())
                    .isRecurring(holidayRequest.getIsRecurring())
                    .build();
            when(storeHolidayRepository.save(any(StoreHoliday.class))).thenReturn(savedHoliday);

            // When
            ResponseEntity<String> result = storeService.createHoliday(testUser, holidayRequest);

            // Then
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isEqualTo("휴무일이 성공적으로 등록되었습니다");

            verify(storeHolidayRepository).existsByStoreIdAndDate(testStore.getId(), holidayRequest.getDate());
            verify(storeHolidayRepository).save(any(StoreHoliday.class));
        }

        @Test
        @DisplayName("과거 날짜 휴무일 등록 시 실패")
        void createHoliday_PastDate_Failure() {
            // Given
            StoreHolidayRequest holidayRequest = new StoreHolidayRequest();
            holidayRequest.setDate(LocalDate.now().minusDays(1));
            holidayRequest.setReason("과거 휴무");

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));

            // When
            ResponseEntity<String> result = storeService.createHoliday(testUser, holidayRequest);

            // Then
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.getBody()).isEqualTo("과거 날짜는 휴무일로 설정할 수 없습니다");

            verify(storeHolidayRepository, never()).save(any(StoreHoliday.class));
        }

        @Test
        @DisplayName("휴무일 삭제 성공")
        void deleteHoliday_Success() {
            // Given
            Long holidayId = 1L;

            StoreHoliday holiday = StoreHoliday.builder()
                    .id(holidayId)
                    .store(testStore)
                    .date(LocalDate.now().plusDays(1))
                    .reason("삭제할 휴무일")
                    .isRecurring(false)
                    .build();

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeHolidayRepository.findByIdAndStoreId(holidayId, testStore.getId()))
                    .thenReturn(Optional.of(holiday));

            // When
            ResponseEntity<String> result = storeService.deleteHoliday(testUser, holidayId);

            // Then
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isEqualTo("휴무일이 성공적으로 삭제되었습니다");

            verify(storeHolidayRepository).findByIdAndStoreId(holidayId, testStore.getId());
            verify(storeHolidayRepository).delete(holiday);
        }

        @Test
        @DisplayName("존재하지 않는 휴무일 삭제 시 실패")
        void deleteHoliday_NotFound_Failure() {
            // Given
            Long holidayId = 999L;

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeHolidayRepository.findByIdAndStoreId(holidayId, testStore.getId()))
                    .thenReturn(Optional.empty());

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.deleteHoliday(testUser, holidayId)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(exception.getReason()).isEqualTo("휴무일을 찾을 수 없습니다");

            verify(storeHolidayRepository, never()).delete(any(StoreHoliday.class));
        }
    }

    @Nested
    @DisplayName("영업 상태 관리 테스트")
    class StoreStatusTest {

        @Test
        @DisplayName("영업 상태 조회 성공")
        void getStoreStatus_Success() {
            // Given
            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));

            // When
            StoreStatusResponse result = storeService.getStoreStatus(testUser);

            // Then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("영업 상태 변경 성공")
        void updateStoreStatus_Success() {
            // Given
            testStore.setStatus(StoreStatus.CLOSED);

            StoreStatusRequest statusRequest = new StoreStatusRequest();
            statusRequest.setStatus(StoreStatus.OPEN);
            statusRequest.setMessage("영업 시작");

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeRepository.save(any(Store.class))).thenReturn(testStore);

            // When
            StoreStatusModifyResponse result = storeService.updateStoreStatus(testUser, statusRequest);

            // Then
            assertThat(result).isNotNull();
            verify(storeRepository).save(any(Store.class));
        }

        @Test
        @DisplayName("null 상태로 변경 시도 시 예외 발생")
        void updateStoreStatus_NullStatus_ThrowsException() {
            // Given
            StoreStatusRequest statusRequest = new StoreStatusRequest();
            statusRequest.setStatus(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> storeService.updateStoreStatus(testUser, statusRequest)
            );

            assertThat(exception.getMessage()).isEqualTo("영업 상태는 필수입니다.");
        }

        @Test
        @DisplayName("동일한 상태로 변경 시도 시 메시지 반환")
        void updateStoreStatus_SameStatus_ReturnsMessage() {
            // Given
            testStore.setStatus(StoreStatus.CLOSED);

            StoreStatusRequest statusRequest = new StoreStatusRequest();
            statusRequest.setStatus(StoreStatus.CLOSED); // 동일한 상태
            statusRequest.setMessage("동일한 상태 설정");

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));

            // When
            StoreStatusModifyResponse result = storeService.updateStoreStatus(testUser, statusRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getMessage()).contains("이미 동일한 상태입니다");

            // 동일한 상태인 경우 save()가 호출되지 않음
            verify(storeRepository, never()).save(any(Store.class));
        }
    }

    @Nested
    @DisplayName("대시보드 테스트")
    class DashboardTest {

        @Test
        @DisplayName("대시보드 조회 성공")
        void getDashboard_Success() {
            // Given
            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeRepository.countTodayOrdersByStoreId(testStore.getId())).thenReturn(5L);
            when(storeRepository.getTodayRevenueByStoreId(testStore.getId())).thenReturn(new BigDecimal("50000"));
            when(reviewRepository.findAverageRatingByStoreId(testStore.getId())).thenReturn(new BigDecimal("4.5"));
            when(reviewRepository.countByStoreId(testStore.getId())).thenReturn(10L);
            when(storeRepository.countTotalOrdersByStoreId(testStore.getId())).thenReturn(100L);
            when(storeRepository.findRecentOrdersByStoreId(eq(testStore.getId()), any()))
                    .thenReturn(new ArrayList<>());

            // When
            StoreDashboardResponse result = storeService.getDashboard(testUser);

            // Then
            assertThat(result).isNotNull();
            verify(storeRepository).countTodayOrdersByStoreId(testStore.getId());
            verify(storeRepository).getTodayRevenueByStoreId(testStore.getId());
            verify(reviewRepository).findAverageRatingByStoreId(testStore.getId());
        }

        @Test
        @DisplayName("가게를 찾을 수 없는 경우 예외 발생")
        void getDashboard_StoreNotFound_ThrowsException() {
            // Given
            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.empty());

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.getDashboard(testUser)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(exception.getReason()).isEqualTo("가게를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("Helper 메소드 테스트")
    class HelperMethodTest {

        @Test
        @DisplayName("입력값 검증 - 모든 필수 필드 누락 시 예외 발생")
        void validateStoreCreateRequest_AllFieldsMissing_ThrowsException() {
            // Given
            StoreCreateRequest invalidRequest = new StoreCreateRequest();

            // When & Then - 가게명 검증에서 먼저 실패
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.createStore(testUser, invalidRequest)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(exception.getReason()).isEqualTo("가게명은 필수입니다");
        }

        @Test
        @DisplayName("입력값 검증 - 빈 문자열 필드")
        void validateStoreCreateRequest_EmptyString_ThrowsException() {
            // Given
            StoreCreateRequest invalidRequest = new StoreCreateRequest();
            invalidRequest.setName("   "); // 공백만 있는 문자열
            invalidRequest.setBusinessNumber("123-45-67890");
            invalidRequest.setAddress("주소");
            invalidRequest.setPhone("전화번호");

            when(storeRepository.existsByOwnerIdAndIsActiveTrue(testUser.getId())).thenReturn(false);

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.createStore(testUser, invalidRequest)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(exception.getReason()).isEqualTo("가게명은 필수입니다");
        }
    }

    @Nested
    @DisplayName("예외 상황 테스트")
    class ExceptionTest {

        @Test
        @DisplayName("트랜잭션 안전 이미지 업로드 실패 시 예외 발생")
        void uploadImage_TransactionSafeUploadFails_ThrowsException() {
            // Given
            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(s3Service.uploadFileWithTransaction(mockFile, testStore.getId()))
                    .thenThrow(new RuntimeException("S3 업로드 실패"));

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.uploadImage(testUser, mockFile)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(exception.getReason()).isEqualTo("이미지 업로드에 실패했습니다");
        }

        @Test
        @DisplayName("이미지 삭제 시 DB 오류 발생")
        void deleteImage_DatabaseError_ThrowsException() {
            // Given
            testStore.setImageUrl("https://s3.amazonaws.com/bucket/store/1/image.jpg");

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeRepository.save(any(Store.class)))
                    .thenThrow(new RuntimeException("DB 저장 실패"));

            // When & Then
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> storeService.deleteImage(testUser, 1L)
            );

            assertThat(exception.getMessage()).isEqualTo("DB 저장 실패");

            // 트랜잭션 롤백으로 이벤트가 발행되지 않음
            verify(eventPublisher, never()).publishEvent(any(ImageCleanupEvent.class));
        }

        @Test
        @DisplayName("대시보드 조회 중 오류 발생 시 예외 발생")
        void getDashboard_RepositoryError_ThrowsException() {
            // Given
            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeRepository.countTodayOrdersByStoreId(testStore.getId()))
                    .thenThrow(new RuntimeException("DB 연결 오류"));

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.getDashboard(testUser)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(exception.getReason()).isEqualTo("대시보드 조회 중 오류가 발생했습니다");
        }
    }

    @Nested
    @DisplayName("권한 검증 테스트")
    class AuthorizationTest {

        @Test
        @DisplayName("다른 사용자로 가게 조회 시 null 반환")
        void getMyStore_DifferentUser_ReturnsNull() {
            // Given
            TempUser anotherUser = new TempUser();
            anotherUser.setId(999L);
            anotherUser.setEmail("another@example.com");

            when(storeRepository.findByOwnerIdAndIsActiveTrue(anotherUser.getId()))
                    .thenReturn(Optional.empty());

            // When
            Store result = storeService.getMyStore(anotherUser);

            // Then
            assertThat(result).isNull();
            verify(storeRepository).findByOwnerIdAndIsActiveTrue(anotherUser.getId());
        }

        @Test
        @DisplayName("다른 사용자가 가게 수정 시도 시 예외 발생")
        void updateStore_DifferentUser_ThrowsException() {
            // Given
            TempUser anotherUser = new TempUser();
            anotherUser.setId(999L);
            anotherUser.setEmail("another@example.com");

            StoreUpdateRequest updateRequest = new StoreUpdateRequest();
            updateRequest.setName("해킹 시도");

            when(storeRepository.findByOwnerIdAndIsActiveTrue(anotherUser.getId()))
                    .thenReturn(Optional.empty());

            // When & Then
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> storeService.updateStore(anotherUser, updateRequest)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(exception.getReason()).isEqualTo("가게를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("권한 검증을 통한 휴무일 삭제 - 권한이 있는 경우")
        void deleteHoliday_WithPermission_Success() {
            // Given
            Long holidayId = 1L;
            StoreHoliday holiday = StoreHoliday.builder()
                    .id(holidayId)
                    .store(testStore) // 현재 사용자의 가게
                    .date(LocalDate.now().plusDays(1))
                    .reason("정당한 삭제")
                    .build();

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeHolidayRepository.findByIdAndStoreId(holidayId, testStore.getId()))
                    .thenReturn(Optional.of(holiday));

            // When
            ResponseEntity<String> result = storeService.deleteHoliday(testUser, holidayId);

            // Then
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(storeHolidayRepository).findByIdAndStoreId(holidayId, testStore.getId());
            verify(storeHolidayRepository).delete(holiday);
        }
    }

    @Nested
    @DisplayName("비즈니스 로직 테스트")
    class BusinessLogicTest {

        @Test
        @DisplayName("운영시간 전체 요일 설정 (dayOfWeek = 7)")
        void updateStoreHours_AllDays_Success() {
            // Given
            StoreHourRequest hourRequest = new StoreHourRequest();
            hourRequest.setDayOfWeek(7); // 모든 요일
            hourRequest.setOpenTime(LocalTime.of(9, 0));
            hourRequest.setCloseTime(LocalTime.of(22, 0));
            hourRequest.setIsClosed(false);

            List<StoreHourRequest> hourRequests = List.of(hourRequest);

            List<StoreHour> savedHours = new ArrayList<>();
            for (int day = 0; day <= 6; day++) {
                StoreHour hour = StoreHour.builder()
                        .id((long) (day + 1))
                        .store(testStore)
                        .dayOfWeek(day)
                        .openTime(LocalTime.of(9, 0))
                        .closeTime(LocalTime.of(22, 0))
                        .isClosed(false)
                        .build();
                savedHours.add(hour);
            }

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeHourRepository.saveAll(anyList())).thenReturn(savedHours);

            // When
            List<StoreHourResponse> result = storeService.updateStoreHours(testUser, hourRequests);

            // Then
            assertThat(result).hasSize(7); // 7개 요일 모두 생성
            verify(storeHourRepository).deleteByStoreIdAndDayOfWeekIn(eq(testStore.getId()), any());
            verify(storeHourRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("중복 휴무일 등록 시 실패")
        void createHoliday_DuplicateDate_Failure() {
            // Given
            StoreHolidayRequest holidayRequest = new StoreHolidayRequest();
            holidayRequest.setDate(LocalDate.now().plusDays(1));
            holidayRequest.setReason("중복 휴무");

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeHolidayRepository.existsByStoreIdAndDate(testStore.getId(), holidayRequest.getDate()))
                    .thenReturn(true); // 이미 존재

            // When
            ResponseEntity<String> result = storeService.createHoliday(testUser, holidayRequest);

            // Then
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.getBody()).contains("이미 등록된 휴무일입니다");
            verify(storeHolidayRepository, never()).save(any(StoreHoliday.class));
        }

        @Test
        @DisplayName("휴무일 날짜가 null인 경우 실패")
        void createHoliday_NullDate_Failure() {
            // Given
            StoreHolidayRequest holidayRequest = new StoreHolidayRequest();
            holidayRequest.setDate(null); // null 날짜
            holidayRequest.setReason("날짜 없는 휴무");

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));

            // When
            ResponseEntity<String> result = storeService.createHoliday(testUser, holidayRequest);

            // Then
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.getBody()).isEqualTo("휴무일을 입력해주세요");
            verify(storeHolidayRepository, never()).save(any(StoreHoliday.class));
        }
    }

    @Nested
    @DisplayName("이벤트 발행 검증 테스트")
    class EventPublishingTest {

        @Test
        @DisplayName("이미지 업로드 시 기존 이미지 정리 이벤트 발행")
        void uploadImage_PublishesCleanupEvent() {
            // Given
            String oldImageUrl = "https://s3.amazonaws.com/bucket/store/1/old-image.jpg";
            String tempImageUrl = "https://s3.amazonaws.com/bucket/temp/1/temp-image.jpg";
            String finalImageUrl = "https://s3.amazonaws.com/bucket/store/1/new-image.jpg";

            testStore.setImageUrl(oldImageUrl);

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(s3Service.uploadFileWithTransaction(mockFile, testStore.getId()))
                    .thenReturn(tempImageUrl);
            when(s3Service.moveToFinalPath(tempImageUrl, testStore.getId()))
                    .thenReturn(finalImageUrl);
            when(storeRepository.save(any(Store.class))).thenReturn(testStore);

            // When
            storeService.uploadImage(testUser, mockFile);

            // Then
            ArgumentCaptor<ImageCleanupEvent> eventCaptor = ArgumentCaptor.forClass(ImageCleanupEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());

            ImageCleanupEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.getImageUrl()).isEqualTo(oldImageUrl);
        }

        @Test
        @DisplayName("이미지 삭제 시 정리 이벤트 발행")
        void deleteImage_PublishesCleanupEvent() {
            // Given
            String existingImageUrl = "https://s3.amazonaws.com/bucket/store/1/existing-image.jpg";
            testStore.setImageUrl(existingImageUrl);

            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(storeRepository.save(any(Store.class))).thenReturn(testStore);

            // When
            storeService.deleteImage(testUser, 1L);

            // Then
            ArgumentCaptor<ImageCleanupEvent> eventCaptor = ArgumentCaptor.forClass(ImageCleanupEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());

            ImageCleanupEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.getImageUrl()).isEqualTo(existingImageUrl);
        }

        @Test
        @DisplayName("업로드 실패 시 이벤트 발행하지 않음")
        void uploadImage_Failure_NoEventPublished() {
            // Given
            when(storeRepository.findByOwnerIdAndIsActiveTrue(testUser.getId()))
                    .thenReturn(Optional.of(testStore));
            when(s3Service.uploadFileWithTransaction(mockFile, testStore.getId()))
                    .thenThrow(new RuntimeException("업로드 실패"));

            // When & Then
            assertThrows(ResponseStatusException.class,
                    () -> storeService.uploadImage(testUser, mockFile));

            // 실패 시 이벤트 발행되지 않음
            verify(eventPublisher, never()).publishEvent(any(ImageCleanupEvent.class));
        }
    }
}