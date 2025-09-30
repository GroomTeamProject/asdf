package io.goorm.team02.core.menus.domain;

import io.goorm.team02.core.auth.security.SecurityUtils;
import io.goorm.team02.core.common.service.S3Service;
import io.goorm.team02.core.menus.controller.dto.categorycreate.MenuCategoryCreateRequest;
import io.goorm.team02.core.menus.controller.dto.categorycreate.MenuCategoryUpdateRequest;
import io.goorm.team02.core.menus.domain.MenuCategory;
import io.goorm.team02.core.menus.repository.MenuCategoryRepository;
import io.goorm.team02.core.menus.repository.MenuRepository;
import io.goorm.team02.core.menus.repository.MenuOptionRepository;
import io.goorm.team02.core.menus.repository.MenuOptionItemRepository;
import io.goorm.team02.core.menus.service.MenuService;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.domain.TempUser;
import io.goorm.team02.core.stores.domain.enums.StoreCategory;
import io.goorm.team02.core.stores.domain.enums.StoreStatus;
import io.goorm.team02.core.stores.domain.enums.UserType;
import io.goorm.team02.core.stores.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // 이 어노테이션 추가
@DisplayName("MenuService 단위 테스트")
class MenuServiceUnitTest {

    @Mock
    private MenuCategoryRepository menuCategoryRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuOptionRepository menuOptionRepository;

    @Mock
    private MenuOptionItemRepository menuOptionItemRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private MenuService menuService;

    private TempUser testUser;
    private Store testStore;
    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = new TempUser();
        testUser.setId(TEST_USER_ID);
        testUser.setEmail("test@example.com");
        testUser.setName("테스트 사장님");
        testUser.setPhone("010-1234-5678");
        testUser.setUserType(UserType.OWNER);
        testUser.setBirthDate(LocalDate.of(1980, 1, 1));
        testUser.setIsActive(true);
        testUser.setEmailVerified(true);
        testUser.setPhoneVerified(true);

        // 테스트용 가게 생성
        testStore = new Store();
        testStore.setId(1L);
        testStore.setOwner(testUser);
        testStore.setBusinessNumber("123-45-67890");
        testStore.setName("테스트 가게");
        testStore.setDescription("테스트용 가게입니다");
        testStore.setPhone("02-1234-5678");
        testStore.setAddress("서울시 강남구 테스트로 123");
        testStore.setDetailAddress("1층");
        testStore.setLatitude(new BigDecimal("37.5665"));
        testStore.setLongitude(new BigDecimal("126.9780"));
        testStore.setCategory(StoreCategory.KOREAN);
        testStore.setMinOrderAmount(new BigDecimal("15000"));
        testStore.setDeliveryFee(new BigDecimal("3000"));
        testStore.setDeliveryTimeMin(30);
        testStore.setDeliveryTimeMax(60);
        testStore.setStatus(StoreStatus.OPEN);
        testStore.setRating(new BigDecimal("4.5"));
        testStore.setReviewCount(100);
        testStore.setIsActive(true);
    }

    @Test
    @DisplayName("카테고리 생성 - 성공")
    void createCategory_Success() {
        // Given
        MenuCategoryCreateRequest request = new MenuCategoryCreateRequest();
        request.setName("메인메뉴");
        request.setDisplayOrder(1);
        request.setIsActive(true);

        MenuCategory expectedCategory = MenuCategory.builder()
                .store(testStore)
                .name("메인메뉴")
                .displayOrder(1)
                .isActive(true)
                .build();

        // Mock 설정
        when(storeRepository.findByOwnerIdAndIsActiveTrue(TEST_USER_ID))
                .thenReturn(Optional.of(testStore));
        when(menuCategoryRepository.existsByStoreIdAndName(testStore.getId(), "메인메뉴"))
                .thenReturn(false);
        when(menuCategoryRepository.findByStoreIdAndIsActiveTrueOrderByDisplayOrderAsc(testStore.getId()))
                .thenReturn(List.of());
        when(menuCategoryRepository.save(any(MenuCategory.class)))
                .thenReturn(expectedCategory);

        // When & Then
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(TEST_USER_ID);

            MenuCategory result = menuService.createCategory(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("메인메뉴");
            assertThat(result.getDisplayOrder()).isEqualTo(1);
            assertThat(result.getIsActive()).isTrue();
            assertThat(result.getStore()).isEqualTo(testStore);
        }
    }

    @Test
    @DisplayName("카테고리 생성 - 가게 없음 실패")
    void createCategory_StoreNotFound_ShouldFail() {
        // Given
        MenuCategoryCreateRequest request = new MenuCategoryCreateRequest();
        request.setName("메인메뉴");

        // Mock 설정
        when(storeRepository.findByOwnerIdAndIsActiveTrue(TEST_USER_ID))
                .thenReturn(Optional.empty());

        // When & Then
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(TEST_USER_ID);

            assertThatThrownBy(() -> menuService.createCategory(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("등록된 가게가 없습니다");
        }
    }

    @Test
    @DisplayName("카테고리 생성 - 중복 이름 실패")
    void createCategory_DuplicateName_ShouldFail() {
        // Given
        MenuCategoryCreateRequest request = new MenuCategoryCreateRequest();
        request.setName("메인메뉴");

        // Mock 설정
        when(storeRepository.findByOwnerIdAndIsActiveTrue(TEST_USER_ID))
                .thenReturn(Optional.of(testStore));
        when(menuCategoryRepository.existsByStoreIdAndName(testStore.getId(), "메인메뉴"))
                .thenReturn(true);

        // When & Then
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(TEST_USER_ID);

            assertThatThrownBy(() -> menuService.createCategory(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("이미 존재하는 카테고리명입니다");
        }
    }

    @Test
    @DisplayName("카테고리 생성 - 필수값 누락 실패")
    void createCategory_MissingRequiredField_ShouldFail() {
        // Given
        MenuCategoryCreateRequest request = new MenuCategoryCreateRequest();
        request.setName(null);
        request.setDisplayOrder(1);
        request.setIsActive(true);

        // Mock 설정
        when(storeRepository.findByOwnerIdAndIsActiveTrue(TEST_USER_ID))
                .thenReturn(Optional.of(testStore));

        // When & Then
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(TEST_USER_ID);

            assertThatThrownBy(() -> menuService.createCategory(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("카테고리명은 필수입니다");
        }
    }

    @Test
    @DisplayName("카테고리 수정 - 성공")
    void updateCategory_Success() {
        // Given
        Long categoryId = 1L;
        MenuCategoryUpdateRequest request = new MenuCategoryUpdateRequest();
        request.setName("수정된 카테고리");
        request.setDisplayOrder(2);
        request.setIsActive(false);

        MenuCategory existingCategory = MenuCategory.builder()
                .store(testStore)
                .name("기존 카테고리")
                .displayOrder(1)
                .isActive(true)
                .build();

        // Mock 설정
        when(storeRepository.findByOwnerIdAndIsActiveTrue(TEST_USER_ID))
                .thenReturn(Optional.of(testStore));
        when(menuCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(existingCategory));
        when(menuCategoryRepository.save(any(MenuCategory.class)))
                .thenReturn(existingCategory);

        // When
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(TEST_USER_ID);

            MenuCategory result = menuService.updateCategory(categoryId, request);

            // Then
            assertThat(result).isNotNull();
        }
    }

    @Test
    @DisplayName("인증되지 않은 사용자 접근 실패")
    void categoryOperations_UnauthenticatedUser_ShouldFail() {
        // Given
        MenuCategoryCreateRequest request = new MenuCategoryCreateRequest();
        request.setName("테스트 카테고리");

        // When & Then
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId)
                    .thenThrow(new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

            assertThatThrownBy(() -> menuService.createCategory(request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("인증된 사용자 정보를 찾을 수 없습니다");
        }
    }
}