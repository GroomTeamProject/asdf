package io.goorm.team02.core.menus.domain;

import io.goorm.team02.core.menus.controller.dto.categorycreate.*;
import io.goorm.team02.core.menus.controller.dto.menucreate.*;
import io.goorm.team02.core.menus.domain.enums.MenuStatus;
import io.goorm.team02.core.menus.domain.enums.OptionType;
import io.goorm.team02.core.menus.service.*;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.domain.TempUser;
import io.goorm.team02.core.stores.domain.enums.StoreCategory;
import io.goorm.team02.core.stores.domain.enums.StoreStatus;
import io.goorm.team02.core.stores.domain.enums.UserType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MenuService 통합 테스트")
class MenuServiceIntegrationTest {

    @Mock
    private MenuCategoryService menuCategoryService;

    @Mock
    private MenuCrudService menuCrudService;

    @Mock
    private MenuImageService menuImageService;

    @Mock
    private MenuOptionService menuOptionService;

    @Mock
    private MenuValidationService menuValidationService;

    @InjectMocks
    private MenuService menuService;

    private TempUser testUser;
    private Store testStore;
    private MenuCategory testCategory;
    private Menu testMenu;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        setupTestData();
        mockFile = mock(MultipartFile.class);
    }

    // ===================================
    // 클래스 레벨 헬퍼 메서드들 (모든 중첩 클래스에서 사용 가능)
    // ===================================

    private void setupTestData() {
        testUser = createTestUser();
        testStore = createTestStore();
        testCategory = createTestCategory();
        testMenu = createTestMenu();
    }

    private TempUser createTestUser() {
        TempUser user = new TempUser();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("테스트 사장님");
        user.setUserType(UserType.OWNER);
        user.setPhone("010-1234-5678");
        user.setBirthDate(LocalDate.of(1980, 1, 1));
        user.setIsActive(true);
        user.setEmailVerified(true);
        user.setPhoneVerified(true);
        return user;
    }

    private Store createTestStore() {
        Store store = new Store();
        store.setId(1L);
        store.setOwner(testUser);
        store.setBusinessNumber("123-45-67890");
        store.setName("테스트 가게");
        store.setDescription("테스트용 가게");
        store.setPhone("02-1234-5678");
        store.setAddress("서울시 강남구 테스트로 123");
        store.setDetailAddress("1층");
        store.setLatitude(new BigDecimal("37.5665"));
        store.setLongitude(new BigDecimal("126.9780"));
        store.setCategory(StoreCategory.KOREAN);
        store.setMinOrderAmount(new BigDecimal("15000"));
        store.setDeliveryFee(new BigDecimal("3000"));
        store.setDeliveryTimeMin(30);
        store.setDeliveryTimeMax(60);
        store.setStatus(StoreStatus.OPEN);
        store.setRating(new BigDecimal("4.5"));
        store.setReviewCount(100);
        store.setIsActive(true);
        return store;
    }

    private MenuCategory createTestCategory() {
        MenuCategory category = MenuCategory.builder()
                .store(testStore)
                .name("메인메뉴")
                .displayOrder(1)
                .isActive(true)
                .build();
        ReflectionTestUtils.setField(category, "id", 1L);
        return category;
    }

    private Menu createTestMenu() {
        Menu menu = Menu.builder()
                .store(testStore)
                .category(testCategory)
                .name("김치찌개")
                .description("집에서 직접 담근 김치로 끓인 얼큰한 김치찌개")
                .price(new BigDecimal("8000"))
                .imageUrl("https://example.com/kimchi-jjigae.jpg")
                .isPopular(false)
                .isRecommended(false)
                .status(MenuStatus.AVAILABLE)
                .displayOrder(1)
                .build();
        ReflectionTestUtils.setField(menu, "id", 1L);
        return menu;
    }

    // 옵션 관련 헬퍼 메서드들
    private MenuOption createTestOptionGroup() {
        MenuOption option = MenuOption.builder()
                .menu(testMenu)
                .name("사이즈")
                .type(OptionType.SINGLE)
                .isRequired(true)
                .displayOrder(1)
                .build();
        ReflectionTestUtils.setField(option, "id", 1L);
        return option;
    }

    private MenuOptionItem createTestOptionItem() {
        MenuOptionItem item = MenuOptionItem.builder()
                .option(createTestOptionGroup())
                .name("대")
                .additionalPrice(new BigDecimal("1000"))
                .displayOrder(1)
                .isActive(true)
                .build();
        ReflectionTestUtils.setField(item, "id", 1L);
        return item;
    }

    private MenuOptionGroupCreateRequest createOptionGroupCreateRequest() {
        MenuOptionGroupCreateRequest request = new MenuOptionGroupCreateRequest();
        request.setName("사이즈");
        request.setType(OptionType.SINGLE);
        request.setIsRequired(true);

        List<MenuOptionItemCreateRequest> items = new ArrayList<>();
        MenuOptionItemCreateRequest itemRequest = new MenuOptionItemCreateRequest();
        itemRequest.setName("대");
        itemRequest.setAdditionalPrice(new BigDecimal("1000"));
        items.add(itemRequest);

        request.setItems(items);
        return request;
    }

    private MenuOptionItemCreateRequest createOptionItemCreateRequest() {
        MenuOptionItemCreateRequest request = new MenuOptionItemCreateRequest();
        request.setName("대");
        request.setAdditionalPrice(new BigDecimal("1000"));
        request.setDisplayOrder(1);
        request.setIsActive(true);
        return request;
    }

    private MenuCreateRequest createMenuCreateRequest() {
        MenuCreateRequest request = new MenuCreateRequest();
        request.setName("김치찌개");
        request.setDescription("집에서 직접 담근 김치로 끓인 얼큰한 김치찌개");
        request.setPrice(new BigDecimal("8000"));
        request.setCategoryId(1L);
        request.setStatus(MenuStatus.AVAILABLE);
        return request;
    }

    // ===================================
    // 테스트 메서드들
    // ===================================

    @Nested
    @DisplayName("카테고리 관리 테스트")
    class CategoryManagementTest {

        @Test
        @DisplayName("카테고리 목록 조회 성공")
        void getMenuCategories_Success() {
            // Given
            List<MenuCategory> expectedCategories = List.of(testCategory);
            when(menuCategoryService.getMenuCategories(testUser))
                    .thenReturn(expectedCategories);

            // When
            List<MenuCategory> result = menuService.getMenuCategories(testUser);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("메인메뉴");
            verify(menuCategoryService).getMenuCategories(testUser);
        }

        @Test
        @DisplayName("카테고리 생성 성공")
        void createCategory_Success() {
            // Given
            MenuCategoryCreateRequest request = new MenuCategoryCreateRequest();
            request.setName("사이드메뉴");
            request.setDisplayOrder(2);
            request.setIsActive(true);

            MenuCategory newCategory = MenuCategory.builder()
                    .store(testStore)
                    .name("사이드메뉴")
                    .displayOrder(2)
                    .isActive(true)
                    .build();

            when(menuCategoryService.createCategory(testUser, request))
                    .thenReturn(newCategory);

            // When
            MenuCategory result = menuService.createCategory(testUser, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("사이드메뉴");
            verify(menuCategoryService).createCategory(testUser, request);
        }

        @Test
        @DisplayName("카테고리 수정 성공")
        void updateCategory_Success() {
            // Given
            Long categoryId = 1L;
            MenuCategoryUpdateRequest request = new MenuCategoryUpdateRequest();
            request.setName("수정된 메인메뉴");
            request.setIsActive(false);

            MenuCategory updatedCategory = MenuCategory.builder()
                    .store(testStore)
                    .name("수정된 메인메뉴")
                    .displayOrder(1)
                    .isActive(false)
                    .build();

            when(menuCategoryService.updateCategory(testUser, categoryId, request))
                    .thenReturn(updatedCategory);

            // When
            MenuCategory result = menuService.updateCategory(testUser, categoryId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("수정된 메인메뉴");
            assertThat(result.getIsActive()).isFalse();
            verify(menuCategoryService).updateCategory(testUser, categoryId, request);
        }

        @Test
        @DisplayName("카테고리 삭제 성공")
        void deleteCategory_Success() {
            // Given
            Long categoryId = 1L;
            doNothing().when(menuCategoryService).deleteCategory(testUser, categoryId);

            // When & Then
            assertDoesNotThrow(() -> menuService.deleteCategory(testUser, categoryId));
            verify(menuCategoryService).deleteCategory(testUser, categoryId);
        }

        @Test
        @DisplayName("카테고리 순서 변경 성공")
        void updateCategoryOrder_Success() {
            // Given
            CategoryMoveRequest request = new CategoryMoveRequest();
            request.setCategoryId(1L);
            request.setNewPosition(2);

            List<MenuCategory> reorderedCategories = List.of(testCategory);
            when(menuCategoryService.updateCategoryOrder(testUser, request))
                    .thenReturn(reorderedCategories);

            // When
            List<MenuCategory> result = menuService.updateCategoryOrder(testUser, request);

            // Then
            assertThat(result).hasSize(1);
            verify(menuCategoryService).updateCategoryOrder(testUser, request);
        }
    }

    @Nested
    @DisplayName("메뉴 CRUD 테스트")
    class MenuCrudTest {

        @Test
        @DisplayName("메뉴 목록 조회 성공 - 전체")
        void getMenus_All_Success() {
            // Given
            List<Menu> expectedMenus = List.of(testMenu);
            when(menuCrudService.getMenus(testUser, null))
                    .thenReturn(expectedMenus);

            // When
            List<Menu> result = menuService.getMenus(testUser, null);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("김치찌개");
            verify(menuCrudService).getMenus(testUser, null);
        }

        @Test
        @DisplayName("메뉴 목록 조회 성공 - 카테고리별")
        void getMenus_ByCategory_Success() {
            // Given
            Long categoryId = 1L;
            List<Menu> expectedMenus = List.of(testMenu);
            when(menuCrudService.getMenus(testUser, categoryId))
                    .thenReturn(expectedMenus);

            // When
            List<Menu> result = menuService.getMenus(testUser, categoryId);

            // Then
            assertThat(result).hasSize(1);
            verify(menuCrudService).getMenus(testUser, categoryId);
        }

        @Test
        @DisplayName("메뉴 상세 조회 성공")
        void getMenu_Success() {
            // Given
            Long menuId = 1L;
            when(menuCrudService.getMenu(testUser, menuId))
                    .thenReturn(testMenu);

            // When
            Menu result = menuService.getMenu(testUser, menuId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("김치찌개");
            verify(menuCrudService).getMenu(testUser, menuId);
        }

        @Test
        @DisplayName("메뉴 생성 성공")
        void createMenu_Success() {
            // Given
            MenuCreateRequest request = createMenuCreateRequest();
            when(menuCrudService.createMenu(testUser, request))
                    .thenReturn(testMenu);

            // When
            Menu result = menuService.createMenu(testUser, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("김치찌개");
            verify(menuCrudService).createMenu(testUser, request);
        }

        @Test
        @DisplayName("메뉴 수정 성공")
        void updateMenu_Success() {
            // Given
            Long menuId = 1L;
            MenuUpdateRequest request = new MenuUpdateRequest();
            request.setName("수정된 김치찌개");
            request.setPrice(new BigDecimal("9000"));

            Menu updatedMenu = Menu.builder()
                    .store(testStore)
                    .category(testCategory)
                    .name("수정된 김치찌개")
                    .price(new BigDecimal("9000"))
                    .status(MenuStatus.AVAILABLE)
                    .build();

            when(menuCrudService.updateMenu(testUser, menuId, request))
                    .thenReturn(updatedMenu);

            // When
            Menu result = menuService.updateMenu(testUser, menuId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("수정된 김치찌개");
            assertThat(result.getPrice()).isEqualTo(new BigDecimal("9000"));
            verify(menuCrudService).updateMenu(testUser, menuId, request);
        }

        @Test
        @DisplayName("메뉴 삭제 성공")
        void deleteMenu_Success() {
            // Given
            Long menuId = 1L;
            doNothing().when(menuCrudService).deleteMenu(testUser, menuId);

            // When & Then
            assertDoesNotThrow(() -> menuService.deleteMenu(testUser, menuId));
            verify(menuCrudService).deleteMenu(testUser, menuId);
        }

        @Test
        @DisplayName("메뉴 상태 변경 성공")
        void updateMenuStatus_Success() {
            // Given
            Long menuId = 1L;
            MenuStatusRequest request = new MenuStatusRequest();
            request.setStatus(MenuStatus.SOLD_OUT);
            request.setReason("재료 소진");

            Menu updatedMenu = Menu.builder()
                    .store(testStore)
                    .category(testCategory)
                    .name("김치찌개")
                    .status(MenuStatus.SOLD_OUT)
                    .build();

            when(menuCrudService.updateMenuStatus(testUser, menuId, request))
                    .thenReturn(updatedMenu);

            // When
            Menu result = menuService.updateMenuStatus(testUser, menuId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(MenuStatus.SOLD_OUT);
            verify(menuCrudService).updateMenuStatus(testUser, menuId, request);
        }

        @Test
        @DisplayName("메뉴 순서 변경 성공")
        void updateMenuOrder_Success() {
            // Given
            MenuOrderUpdateRequest request = new MenuOrderUpdateRequest();
            request.setMenuId(1L);
            request.setNewPosition(2);

            List<Menu> reorderedMenus = List.of(testMenu);
            when(menuCrudService.updateMenuOrder(testUser, request))
                    .thenReturn(reorderedMenus);

            // When
            List<Menu> result = menuService.updateMenuOrder(testUser, request);

            // Then
            assertThat(result).hasSize(1);
            verify(menuCrudService).updateMenuOrder(testUser, request);
        }
    }

    @Nested
    @DisplayName("메뉴 이미지 관리 테스트")
    class MenuImageTest {

        @BeforeEach
        void setUpImageTest() {
            // 각 이미지 테스트마다 Mock 리셋
            reset(menuImageService);
        }

        @Test
        @DisplayName("메뉴 이미지 업로드 성공")
        void uploadMenuImage_Success() {
            // Given
            Long menuId = 1L;
            String expectedUrl = "https://s3.amazonaws.com/test-bucket/menu-image.jpg";

            when(menuImageService.uploadMenuImage(testUser, menuId, mockFile))
                    .thenReturn(expectedUrl);

            // When
            String result = menuService.uploadMenuImage(testUser, menuId, mockFile);

            // Then
            assertThat(result).isEqualTo(expectedUrl);
            verify(menuImageService).uploadMenuImage(testUser, menuId, mockFile);
        }

        @Test
        @DisplayName("메뉴 이미지 정보 조회 성공")
        void getMenuImageInfo_Success() {
            // Given
            Long menuId = 1L;
            Map<String, Object> expectedInfo = Map.of(
                    "menuId", menuId,
                    "hasImage", true,
                    "imageUrl", "https://example.com/image.jpg"
            );

            when(menuImageService.getMenuImageInfo(testUser, menuId))
                    .thenReturn(expectedInfo);

            // When
            Map<String, Object> result = menuService.getMenuImageInfo(testUser, menuId);

            // Then
            assertThat(result).containsKey("menuId");
            assertThat(result).containsKey("hasImage");
            assertThat(result).containsKey("imageUrl");
            verify(menuImageService).getMenuImageInfo(testUser, menuId);
        }

        @Test
        @DisplayName("메뉴 이미지 삭제 성공")
        void deleteMenuImage_Success() {
            // Given
            Long menuId = 1L;
            Long imageId = 123456L;
            doNothing().when(menuImageService).deleteMenuImage(testUser, menuId, imageId);

            // When & Then
            assertDoesNotThrow(() -> menuService.deleteMenuImage(testUser, menuId, imageId));
            verify(menuImageService).deleteMenuImage(testUser, menuId, imageId);
        }
    }

    @Nested
    @DisplayName("메뉴 옵션 관리 테스트")
    class MenuOptionTest {

        @Test
        @DisplayName("옵션 그룹 목록 조회 성공")
        void getMenuOptionGroups_Success() {
            // Given
            Long menuId = 1L;
            MenuOption optionGroup = createTestOptionGroup();
            List<MenuOption> expectedGroups = List.of(optionGroup);

            when(menuOptionService.getMenuOptionGroups(testUser, menuId))
                    .thenReturn(expectedGroups);

            // When
            List<MenuOption> result = menuService.getMenuOptionGroups(testUser, menuId);

            // Then
            assertThat(result).hasSize(1);
            verify(menuOptionService).getMenuOptionGroups(testUser, menuId);
        }

        @Test
        @DisplayName("옵션 그룹 생성 성공")
        void createOptionGroup_Success() {
            // Given
            Long menuId = 1L;
            MenuOptionGroupCreateRequest request = createOptionGroupCreateRequest();
            MenuOption expectedGroup = createTestOptionGroup();

            when(menuOptionService.createOptionGroup(testUser, menuId, request))
                    .thenReturn(expectedGroup);

            // When
            MenuOption result = menuService.createOptionGroup(testUser, menuId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("사이즈");
            verify(menuOptionService).createOptionGroup(testUser, menuId, request);
        }

        @Test
        @DisplayName("옵션 그룹 수정 성공")
        void updateOptionGroup_Success() {
            // Given
            Long menuId = 1L;
            Long groupId = 1L;
            MenuOptionGroupUpdateRequest request = new MenuOptionGroupUpdateRequest();
            request.setName("수정된 사이즈");

            MenuOption updatedGroup = createTestOptionGroup();
            updatedGroup.updateName("수정된 사이즈");

            when(menuOptionService.updateOptionGroup(testUser, menuId, groupId, request))
                    .thenReturn(updatedGroup);

            // When
            MenuOption result = menuService.updateOptionGroup(testUser, menuId, groupId, request);

            // Then
            assertThat(result).isNotNull();
            verify(menuOptionService).updateOptionGroup(testUser, menuId, groupId, request);
        }

        @Test
        @DisplayName("옵션 그룹 삭제 성공")
        void deleteOptionGroup_Success() {
            // Given
            Long menuId = 1L;
            Long groupId = 1L;
            doNothing().when(menuOptionService).deleteOptionGroup(testUser, menuId, groupId);

            // When & Then
            assertDoesNotThrow(() -> menuService.deleteOptionGroup(testUser, menuId, groupId));
            verify(menuOptionService).deleteOptionGroup(testUser, menuId, groupId);
        }

        @Test
        @DisplayName("옵션 아이템 목록 조회 성공")
        void getMenuOptions_Success() {
            // Given
            Long menuId = 1L;
            Long groupId = 1L;
            MenuOptionItem optionItem = createTestOptionItem();
            List<MenuOptionItem> expectedItems = List.of(optionItem);

            when(menuOptionService.getMenuOptions(testUser, menuId, groupId))
                    .thenReturn(expectedItems);

            // When
            List<MenuOptionItem> result = menuService.getMenuOptions(testUser, menuId, groupId);

            // Then
            assertThat(result).hasSize(1);
            verify(menuOptionService).getMenuOptions(testUser, menuId, groupId);
        }

        @Test
        @DisplayName("옵션 아이템 생성 성공")
        void createOption_Success() {
            // Given
            Long menuId = 1L;
            Long groupId = 1L;
            MenuOptionItemCreateRequest request = createOptionItemCreateRequest();
            MenuOptionItem expectedItem = createTestOptionItem();

            when(menuOptionService.createOption(testUser, menuId, groupId, request))
                    .thenReturn(expectedItem);

            // When
            MenuOptionItem result = menuService.createOption(testUser, menuId, groupId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("대");
            verify(menuOptionService).createOption(testUser, menuId, groupId, request);
        }

        @Test
        @DisplayName("옵션 아이템 수정 성공")
        void updateOption_Success() {
            // Given
            Long menuId = 1L;
            Long groupId = 1L;
            Long optionId = 1L;
            MenuOptionItemUpdateRequest request = new MenuOptionItemUpdateRequest();
            request.setName("수정된 대");

            MenuOptionItem updatedItem = createTestOptionItem();
            updatedItem.updateName("수정된 대");

            when(menuOptionService.updateOption(testUser, menuId, groupId, optionId, request))
                    .thenReturn(updatedItem);

            // When
            MenuOptionItem result = menuService.updateOption(testUser, menuId, groupId, optionId, request);

            // Then
            assertThat(result).isNotNull();
            verify(menuOptionService).updateOption(testUser, menuId, groupId, optionId, request);
        }

        @Test
        @DisplayName("옵션 아이템 삭제 성공")
        void deleteOption_Success() {
            // Given
            Long menuId = 1L;
            Long groupId = 1L;
            Long optionId = 1L;
            doNothing().when(menuOptionService).deleteOption(testUser, menuId, groupId, optionId);

            // When & Then
            assertDoesNotThrow(() -> menuService.deleteOption(testUser, menuId, groupId, optionId));
            verify(menuOptionService).deleteOption(testUser, menuId, groupId, optionId);
        }
    }

    @Nested
    @DisplayName("예외 처리 테스트")
    class ExceptionHandlingTest {

        @Test
        @DisplayName("카테고리 서비스 예외 전파")
        void categoryService_ExceptionPropagation() {
            // Given
            MenuCategoryCreateRequest request = new MenuCategoryCreateRequest();
            request.setName("테스트 카테고리");

            when(menuCategoryService.createCategory(testUser, request))
                    .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "이미 존재하는 카테고리명입니다"));

            // When & Then
            assertThatThrownBy(() -> menuService.createCategory(testUser, request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("이미 존재하는 카테고리명입니다");
        }

        @Test
        @DisplayName("메뉴 CRUD 서비스 예외 전파")
        void menuCrudService_ExceptionPropagation() {
            // Given
            Long menuId = 999L;
            when(menuCrudService.getMenu(testUser, menuId))
                    .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

            // When & Then
            assertThatThrownBy(() -> menuService.getMenu(testUser, menuId))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("메뉴를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("이미지 서비스 예외 전파")
        void imageService_ExceptionPropagation() {
            // Given
            Long menuId = 1L;
            MultipartFile mockFile = mock(MultipartFile.class);

            when(menuImageService.uploadMenuImage(testUser, menuId, mockFile))
                    .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다"));

            // When & Then
            assertThatThrownBy(() -> menuService.uploadMenuImage(testUser, menuId, mockFile))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("지원하지 않는 파일 형식입니다");
        }

        @Test
        @DisplayName("옵션 서비스 예외 전파")
        void optionService_ExceptionPropagation() {
            // Given
            Long menuId = 1L;

            when(menuOptionService.getMenuOptionGroups(testUser, menuId))
                    .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

            // When & Then
            assertThatThrownBy(() -> menuService.getMenuOptionGroups(testUser, menuId))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("메뉴를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("서비스 위임 검증 테스트")
    class ServiceDelegationTest {

        @BeforeEach
        void setUpDelegationTest() {
            // 각 위임 테스트마다 Mock 리셋
            reset(menuCategoryService, menuCrudService, menuImageService, menuOptionService);
        }

        @Test
        @DisplayName("모든 카테고리 관련 메서드가 올바른 서비스로 위임되는지 확인")
        void categoryMethods_ProperDelegation() {
            // Given
            MenuCategoryCreateRequest createRequest = new MenuCategoryCreateRequest();
            MenuCategoryUpdateRequest updateRequest = new MenuCategoryUpdateRequest();
            CategoryMoveRequest moveRequest = new CategoryMoveRequest();

            // Mock 설정
            when(menuCategoryService.getMenuCategories(testUser)).thenReturn(List.of());
            when(menuCategoryService.createCategory(testUser, createRequest)).thenReturn(testCategory);
            when(menuCategoryService.updateCategory(testUser, 1L, updateRequest)).thenReturn(testCategory);
            when(menuCategoryService.updateCategoryOrder(testUser, moveRequest)).thenReturn(List.of());
            doNothing().when(menuCategoryService).deleteCategory(testUser, 1L);

            // When
            menuService.getMenuCategories(testUser);
            menuService.createCategory(testUser, createRequest);
            menuService.updateCategory(testUser, 1L, updateRequest);
            menuService.deleteCategory(testUser, 1L);
            menuService.updateCategoryOrder(testUser, moveRequest);

            // Then - 모든 메서드가 정확히 한 번씩 호출되었는지 확인
            verify(menuCategoryService, times(1)).getMenuCategories(testUser);
            verify(menuCategoryService, times(1)).createCategory(testUser, createRequest);
            verify(menuCategoryService, times(1)).updateCategory(testUser, 1L, updateRequest);
            verify(menuCategoryService, times(1)).deleteCategory(testUser, 1L);
            verify(menuCategoryService, times(1)).updateCategoryOrder(testUser, moveRequest);
        }

        @Test
        @DisplayName("모든 메뉴 CRUD 메서드가 올바른 서비스로 위임되는지 확인")
        void menuCrudMethods_ProperDelegation() {
            // Given
            MenuCreateRequest createRequest = new MenuCreateRequest();
            MenuUpdateRequest updateRequest = new MenuUpdateRequest();
            MenuStatusRequest statusRequest = new MenuStatusRequest();
            MenuOrderUpdateRequest orderRequest = new MenuOrderUpdateRequest();

            // Mock 설정
            when(menuCrudService.getMenus(testUser, null)).thenReturn(List.of());
            when(menuCrudService.getMenu(testUser, 1L)).thenReturn(testMenu);
            when(menuCrudService.createMenu(testUser, createRequest)).thenReturn(testMenu);
            when(menuCrudService.updateMenu(testUser, 1L, updateRequest)).thenReturn(testMenu);
            when(menuCrudService.updateMenuStatus(testUser, 1L, statusRequest)).thenReturn(testMenu);
            when(menuCrudService.updateMenuOrder(testUser, orderRequest)).thenReturn(List.of());
            doNothing().when(menuCrudService).deleteMenu(testUser, 1L);

            // When
            menuService.getMenus(testUser, null);
            menuService.getMenu(testUser, 1L);
            menuService.createMenu(testUser, createRequest);
            menuService.updateMenu(testUser, 1L, updateRequest);
            menuService.deleteMenu(testUser, 1L);
            menuService.updateMenuStatus(testUser, 1L, statusRequest);
            menuService.updateMenuOrder(testUser, orderRequest);

            // Then
            verify(menuCrudService, times(1)).getMenus(testUser, null);
            verify(menuCrudService, times(1)).getMenu(testUser, 1L);
            verify(menuCrudService, times(1)).createMenu(testUser, createRequest);
            verify(menuCrudService, times(1)).updateMenu(testUser, 1L, updateRequest);
            verify(menuCrudService, times(1)).deleteMenu(testUser, 1L);
            verify(menuCrudService, times(1)).updateMenuStatus(testUser, 1L, statusRequest);
            verify(menuCrudService, times(1)).updateMenuOrder(testUser, orderRequest);
        }

        @Test
        @DisplayName("모든 이미지 관리 메서드가 올바른 서비스로 위임되는지 확인")
        void imageMethods_ProperDelegation() {
            // Given
            MultipartFile mockFile = mock(MultipartFile.class);
            Map<String, Object> imageInfo = Map.of("hasImage", true);

            // Mock 설정
            when(menuImageService.uploadMenuImage(testUser, 1L, mockFile)).thenReturn("image-url");
            when(menuImageService.getMenuImageInfo(testUser, 1L)).thenReturn(imageInfo);
            doNothing().when(menuImageService).deleteMenuImage(testUser, 1L, 123L);

            // When
            menuService.uploadMenuImage(testUser, 1L, mockFile);
            menuService.getMenuImageInfo(testUser, 1L);
            menuService.deleteMenuImage(testUser, 1L, 123L);

            // Then
            verify(menuImageService, times(1)).uploadMenuImage(testUser, 1L, mockFile);
            verify(menuImageService, times(1)).getMenuImageInfo(testUser, 1L);
            verify(menuImageService, times(1)).deleteMenuImage(testUser, 1L, 123L);
        }

        @Test
        @DisplayName("모든 옵션 관리 메서드가 올바른 서비스로 위임되는지 확인")
        void optionMethods_ProperDelegation() {
            // Given
            MenuOptionGroupCreateRequest groupCreateRequest = new MenuOptionGroupCreateRequest();
            MenuOptionGroupUpdateRequest groupUpdateRequest = new MenuOptionGroupUpdateRequest();
            MenuOptionItemCreateRequest itemCreateRequest = new MenuOptionItemCreateRequest();
            MenuOptionItemUpdateRequest itemUpdateRequest = new MenuOptionItemUpdateRequest();

            MenuOption testOptionGroup = createTestOptionGroup();
            MenuOptionItem testOptionItem = createTestOptionItem();

            // Mock 설정
            when(menuOptionService.getMenuOptionGroups(testUser, 1L)).thenReturn(List.of());
            when(menuOptionService.createOptionGroup(testUser, 1L, groupCreateRequest)).thenReturn(testOptionGroup);
            when(menuOptionService.updateOptionGroup(testUser, 1L, 1L, groupUpdateRequest)).thenReturn(testOptionGroup);
            when(menuOptionService.getMenuOptions(testUser, 1L, 1L)).thenReturn(List.of());
            when(menuOptionService.createOption(testUser, 1L, 1L, itemCreateRequest)).thenReturn(testOptionItem);
            when(menuOptionService.updateOption(testUser, 1L, 1L, 1L, itemUpdateRequest)).thenReturn(testOptionItem);
            doNothing().when(menuOptionService).deleteOptionGroup(testUser, 1L, 1L);
            doNothing().when(menuOptionService).deleteOption(testUser, 1L, 1L, 1L);

            // When
            menuService.getMenuOptionGroups(testUser, 1L);
            menuService.createOptionGroup(testUser, 1L, groupCreateRequest);
            menuService.updateOptionGroup(testUser, 1L, 1L, groupUpdateRequest);
            menuService.deleteOptionGroup(testUser, 1L, 1L);
            menuService.getMenuOptions(testUser, 1L, 1L);
            menuService.createOption(testUser, 1L, 1L, itemCreateRequest);
            menuService.updateOption(testUser, 1L, 1L, 1L, itemUpdateRequest);
            menuService.deleteOption(testUser, 1L, 1L, 1L);

            // Then
            verify(menuOptionService, times(1)).getMenuOptionGroups(testUser, 1L);
            verify(menuOptionService, times(1)).createOptionGroup(testUser, 1L, groupCreateRequest);
            verify(menuOptionService, times(1)).updateOptionGroup(testUser, 1L, 1L, groupUpdateRequest);
            verify(menuOptionService, times(1)).deleteOptionGroup(testUser, 1L, 1L);
            verify(menuOptionService, times(1)).getMenuOptions(testUser, 1L, 1L);
            verify(menuOptionService, times(1)).createOption(testUser, 1L, 1L, itemCreateRequest);
            verify(menuOptionService, times(1)).updateOption(testUser, 1L, 1L, 1L, itemUpdateRequest);
            verify(menuOptionService, times(1)).deleteOption(testUser, 1L, 1L, 1L);
        }
    }

    @Nested
    @DisplayName("트랜잭션 경계 테스트")
    class TransactionBoundaryTest {

        @Test
        @DisplayName("읽기 전용 메서드들이 올바르게 호출되는지 확인")
        void readOnlyMethods_ProperExecution() {
            // Given
            when(menuCategoryService.getMenuCategories(testUser)).thenReturn(List.of(testCategory));
            when(menuCrudService.getMenus(testUser, null)).thenReturn(List.of(testMenu));
            when(menuCrudService.getMenu(testUser, 1L)).thenReturn(testMenu);
            when(menuImageService.getMenuImageInfo(testUser, 1L)).thenReturn(Map.of());
            when(menuOptionService.getMenuOptionGroups(testUser, 1L)).thenReturn(List.of());
            when(menuOptionService.getMenuOptions(testUser, 1L, 1L)).thenReturn(List.of());

            // When & Then - 예외 없이 실행되어야 함
            assertDoesNotThrow(() -> {
                menuService.getMenuCategories(testUser);
                menuService.getMenus(testUser, null);
                menuService.getMenu(testUser, 1L);
                menuService.getMenuImageInfo(testUser, 1L);
                menuService.getMenuOptionGroups(testUser, 1L);
                menuService.getMenuOptions(testUser, 1L, 1L);
            });

            // 모든 읽기 메서드가 호출되었는지 확인
            verify(menuCategoryService).getMenuCategories(testUser);
            verify(menuCrudService).getMenus(testUser, null);
            verify(menuCrudService).getMenu(testUser, 1L);
            verify(menuImageService).getMenuImageInfo(testUser, 1L);
            verify(menuOptionService).getMenuOptionGroups(testUser, 1L);
            verify(menuOptionService).getMenuOptions(testUser, 1L, 1L);
        }

        @Test
        @DisplayName("쓰기 작업 메서드들이 올바르게 호출되는지 확인")
        void writeOperationMethods_ProperExecution() {
            // Given
            MenuCategoryCreateRequest categoryRequest = new MenuCategoryCreateRequest();
            MenuCreateRequest menuRequest = new MenuCreateRequest();
            MultipartFile mockFile = mock(MultipartFile.class);
            MenuOptionGroupCreateRequest optionRequest = new MenuOptionGroupCreateRequest();

            when(menuCategoryService.createCategory(testUser, categoryRequest)).thenReturn(testCategory);
            when(menuCrudService.createMenu(testUser, menuRequest)).thenReturn(testMenu);
            when(menuImageService.uploadMenuImage(testUser, 1L, mockFile)).thenReturn("image-url");
            when(menuOptionService.createOptionGroup(testUser, 1L, optionRequest)).thenReturn(createTestOptionGroup());
            doNothing().when(menuCategoryService).deleteCategory(testUser, 1L);
            doNothing().when(menuCrudService).deleteMenu(testUser, 1L);
            doNothing().when(menuImageService).deleteMenuImage(testUser, 1L, 123L);
            doNothing().when(menuOptionService).deleteOptionGroup(testUser, 1L, 1L);

            // When & Then - 예외 없이 실행되어야 함
            assertDoesNotThrow(() -> {
                menuService.createCategory(testUser, categoryRequest);
                menuService.createMenu(testUser, menuRequest);
                menuService.uploadMenuImage(testUser, 1L, mockFile);
                menuService.createOptionGroup(testUser, 1L, optionRequest);
                menuService.deleteCategory(testUser, 1L);
                menuService.deleteMenu(testUser, 1L);
                menuService.deleteMenuImage(testUser, 1L, 123L);
                menuService.deleteOptionGroup(testUser, 1L, 1L);
            });

            // 모든 쓰기 메서드가 호출되었는지 확인
            verify(menuCategoryService).createCategory(testUser, categoryRequest);
            verify(menuCrudService).createMenu(testUser, menuRequest);
            verify(menuImageService).uploadMenuImage(testUser, 1L, mockFile);
            verify(menuOptionService).createOptionGroup(testUser, 1L, optionRequest);
            verify(menuCategoryService).deleteCategory(testUser, 1L);
            verify(menuCrudService).deleteMenu(testUser, 1L);
            verify(menuImageService).deleteMenuImage(testUser, 1L, 123L);
            verify(menuOptionService).deleteOptionGroup(testUser, 1L, 1L);
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    class ComplexScenarioTest {

        @Test
        @DisplayName("메뉴 전체 생성 플로우 테스트")
        void completeMenuCreationFlow_Success() {
            // Given - 카테고리 생성 -> 메뉴 생성 -> 옵션 그룹 생성 -> 이미지 업로드
            MenuCategoryCreateRequest categoryRequest = new MenuCategoryCreateRequest();
            categoryRequest.setName("신규 카테고리");

            MenuCreateRequest menuRequest = new MenuCreateRequest();
            menuRequest.setName("신규 메뉴");
            menuRequest.setCategoryId(1L);

            MenuOptionGroupCreateRequest optionRequest = new MenuOptionGroupCreateRequest();
            optionRequest.setName("옵션 그룹");

            MultipartFile mockFile = mock(MultipartFile.class);

            // Mock 설정
            when(menuCategoryService.createCategory(testUser, categoryRequest)).thenReturn(testCategory);
            when(menuCrudService.createMenu(testUser, menuRequest)).thenReturn(testMenu);
            when(menuOptionService.createOptionGroup(testUser, 1L, optionRequest)).thenReturn(createTestOptionGroup());
            when(menuImageService.uploadMenuImage(testUser, 1L, mockFile)).thenReturn("image-url");

            // When
            MenuCategory createdCategory = menuService.createCategory(testUser, categoryRequest);
            Menu createdMenu = menuService.createMenu(testUser, menuRequest);
            MenuOption createdOption = menuService.createOptionGroup(testUser, 1L, optionRequest);
            String uploadedImageUrl = menuService.uploadMenuImage(testUser, 1L, mockFile);

            // Then
            assertThat(createdCategory).isNotNull();
            assertThat(createdMenu).isNotNull();
            assertThat(createdOption).isNotNull();
            assertThat(uploadedImageUrl).isNotEmpty();

            // 각 서비스가 순서대로 호출되었는지 확인
            verify(menuCategoryService).createCategory(testUser, categoryRequest);
            verify(menuCrudService).createMenu(testUser, menuRequest);
            verify(menuOptionService).createOptionGroup(testUser, 1L, optionRequest);
            verify(menuImageService).uploadMenuImage(testUser, 1L, mockFile);
        }

        @Test
        @DisplayName("메뉴 전체 삭제 플로우 테스트")
        void completeMenuDeletionFlow_Success() {
            // Given - 옵션 삭제 -> 이미지 삭제 -> 메뉴 삭제 -> 카테고리 삭제
            doNothing().when(menuOptionService).deleteOption(testUser, 1L, 1L, 1L);
            doNothing().when(menuOptionService).deleteOptionGroup(testUser, 1L, 1L);
            doNothing().when(menuImageService).deleteMenuImage(testUser, 1L, 123L);
            doNothing().when(menuCrudService).deleteMenu(testUser, 1L);
            doNothing().when(menuCategoryService).deleteCategory(testUser, 1L);

            // When & Then
            assertDoesNotThrow(() -> {
                menuService.deleteOption(testUser, 1L, 1L, 1L);
                menuService.deleteOptionGroup(testUser, 1L, 1L);
                menuService.deleteMenuImage(testUser, 1L, 123L);
                menuService.deleteMenu(testUser, 1L);
                menuService.deleteCategory(testUser, 1L);
            });

            // 삭제 순서대로 호출되었는지 확인
            verify(menuOptionService).deleteOption(testUser, 1L, 1L, 1L);
            verify(menuOptionService).deleteOptionGroup(testUser, 1L, 1L);
            verify(menuImageService).deleteMenuImage(testUser, 1L, 123L);
            verify(menuCrudService).deleteMenu(testUser, 1L);
            verify(menuCategoryService).deleteCategory(testUser, 1L);
        }

        @Test
        @DisplayName("메뉴 수정 플로우 테스트")
        void completeMenuUpdateFlow_Success() {
            // Given - 카테고리 수정 -> 메뉴 수정 -> 옵션 수정 -> 이미지 교체
            Long categoryId = 1L;
            Long menuId = 1L;
            Long groupId = 1L;
            Long optionId = 1L;

            MenuCategoryUpdateRequest categoryUpdateRequest = new MenuCategoryUpdateRequest();
            categoryUpdateRequest.setName("수정된 카테고리");

            MenuUpdateRequest menuUpdateRequest = new MenuUpdateRequest();
            menuUpdateRequest.setName("수정된 메뉴");
            menuUpdateRequest.setPrice(new BigDecimal("9000"));

            MenuOptionGroupUpdateRequest optionGroupUpdateRequest = new MenuOptionGroupUpdateRequest();
            optionGroupUpdateRequest.setName("수정된 옵션 그룹");

            MenuOptionItemUpdateRequest optionItemUpdateRequest = new MenuOptionItemUpdateRequest();
            optionItemUpdateRequest.setName("수정된 옵션");
            optionItemUpdateRequest.setAdditionalPrice(new BigDecimal("1500"));

            MultipartFile newImageFile = mock(MultipartFile.class);

            // Mock 설정
            MenuCategory updatedCategory = MenuCategory.builder()
                    .store(testStore)
                    .name("수정된 카테고리")
                    .displayOrder(1)
                    .isActive(true)
                    .build();

            Menu updatedMenu = Menu.builder()
                    .store(testStore)
                    .category(updatedCategory)
                    .name("수정된 메뉴")
                    .price(new BigDecimal("9000"))
                    .status(MenuStatus.AVAILABLE)
                    .build();

            MenuOption updatedOptionGroup = createTestOptionGroup();
            updatedOptionGroup.updateName("수정된 옵션 그룹");

            MenuOptionItem updatedOptionItem = createTestOptionItem();
            updatedOptionItem.updateName("수정된 옵션");
            updatedOptionItem.updateAdditionalPrice(new BigDecimal("1500"));

            when(menuCategoryService.updateCategory(testUser, categoryId, categoryUpdateRequest)).thenReturn(updatedCategory);
            when(menuCrudService.updateMenu(testUser, menuId, menuUpdateRequest)).thenReturn(updatedMenu);
            when(menuOptionService.updateOptionGroup(testUser, menuId, groupId, optionGroupUpdateRequest)).thenReturn(updatedOptionGroup);
            when(menuOptionService.updateOption(testUser, menuId, groupId, optionId, optionItemUpdateRequest)).thenReturn(updatedOptionItem);
            when(menuImageService.uploadMenuImage(testUser, menuId, newImageFile)).thenReturn("new-image-url");

            // When
            MenuCategory categoryResult = menuService.updateCategory(testUser, categoryId, categoryUpdateRequest);
            Menu menuResult = menuService.updateMenu(testUser, menuId, menuUpdateRequest);
            MenuOption optionGroupResult = menuService.updateOptionGroup(testUser, menuId, groupId, optionGroupUpdateRequest);
            MenuOptionItem optionItemResult = menuService.updateOption(testUser, menuId, groupId, optionId, optionItemUpdateRequest);
            String newImageUrl = menuService.uploadMenuImage(testUser, menuId, newImageFile);

            // Then
            assertThat(categoryResult).isNotNull();
            assertThat(categoryResult.getName()).isEqualTo("수정된 카테고리");
            assertThat(menuResult).isNotNull();
            assertThat(menuResult.getName()).isEqualTo("수정된 메뉴");
            assertThat(menuResult.getPrice()).isEqualTo(new BigDecimal("9000"));
            assertThat(optionGroupResult).isNotNull();
            assertThat(optionItemResult).isNotNull();
            assertThat(newImageUrl).isEqualTo("new-image-url");

            // 모든 서비스가 호출되었는지 확인
            verify(menuCategoryService).updateCategory(testUser, categoryId, categoryUpdateRequest);
            verify(menuCrudService).updateMenu(testUser, menuId, menuUpdateRequest);
            verify(menuOptionService).updateOptionGroup(testUser, menuId, groupId, optionGroupUpdateRequest);
            verify(menuOptionService).updateOption(testUser, menuId, groupId, optionId, optionItemUpdateRequest);
            verify(menuImageService).uploadMenuImage(testUser, menuId, newImageFile);
        }

        @Test
        @DisplayName("예외 상황에서의 서비스 동작 검증")
        void exceptionScenario_ProperHandling() {
            // Given
            MenuCreateRequest menuRequest = new MenuCreateRequest();
            MenuOptionGroupCreateRequest optionRequest = new MenuOptionGroupCreateRequest();

            // 메뉴 생성은 성공하지만 옵션 생성에서 실패하는 시나리오
            when(menuCrudService.createMenu(testUser, menuRequest)).thenReturn(testMenu);
            when(menuOptionService.createOptionGroup(testUser, 1L, optionRequest))
                    .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "옵션 생성 실패"));

            // When
            Menu createdMenu = menuService.createMenu(testUser, menuRequest);

            // Then
            assertThat(createdMenu).isNotNull();

            // 옵션 생성에서 예외 발생 확인
            assertThatThrownBy(() -> menuService.createOptionGroup(testUser, 1L, optionRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("옵션 생성 실패");

            verify(menuCrudService).createMenu(testUser, menuRequest);
            verify(menuOptionService).createOptionGroup(testUser, 1L, optionRequest);
        }

        @Test
        @DisplayName("연속적인 메뉴 상태 변경 테스트")
        void continuousMenuStatusChange_Success() {
            // Given - 판매중 -> 품절 -> 숨김 -> 판매중 순서로 상태 변경
            Long menuId = 1L;

            MenuStatusRequest soldOutRequest = new MenuStatusRequest();
            soldOutRequest.setStatus(MenuStatus.SOLD_OUT);
            soldOutRequest.setReason("재료 소진");

            MenuStatusRequest hiddenRequest = new MenuStatusRequest();
            hiddenRequest.setStatus(MenuStatus.HIDDEN);
            hiddenRequest.setReason("메뉴 개선 작업");

            MenuStatusRequest availableRequest = new MenuStatusRequest();
            availableRequest.setStatus(MenuStatus.AVAILABLE);
            availableRequest.setReason("재료 보충 완료");

            // 각 상태별 메뉴 객체 생성
            Menu soldOutMenu = Menu.builder()
                    .store(testStore)
                    .category(testCategory)
                    .name("김치찌개")
                    .status(MenuStatus.SOLD_OUT)
                    .build();

            Menu hiddenMenu = Menu.builder()
                    .store(testStore)
                    .category(testCategory)
                    .name("김치찌개")
                    .status(MenuStatus.HIDDEN)
                    .build();

            Menu availableMenu = Menu.builder()
                    .store(testStore)
                    .category(testCategory)
                    .name("김치찌개")
                    .status(MenuStatus.AVAILABLE)
                    .build();

            // Mock 설정
            when(menuCrudService.updateMenuStatus(testUser, menuId, soldOutRequest)).thenReturn(soldOutMenu);
            when(menuCrudService.updateMenuStatus(testUser, menuId, hiddenRequest)).thenReturn(hiddenMenu);
            when(menuCrudService.updateMenuStatus(testUser, menuId, availableRequest)).thenReturn(availableMenu);

            // When
            Menu result1 = menuService.updateMenuStatus(testUser, menuId, soldOutRequest);
            Menu result2 = menuService.updateMenuStatus(testUser, menuId, hiddenRequest);
            Menu result3 = menuService.updateMenuStatus(testUser, menuId, availableRequest);

            // Then
            assertThat(result1.getStatus()).isEqualTo(MenuStatus.SOLD_OUT);
            assertThat(result2.getStatus()).isEqualTo(MenuStatus.HIDDEN);
            assertThat(result3.getStatus()).isEqualTo(MenuStatus.AVAILABLE);

            // 상태 변경이 순서대로 호출되었는지 확인
            verify(menuCrudService, times(3)).updateMenuStatus(eq(testUser), eq(menuId), any(MenuStatusRequest.class));
        }

        @Test
        @DisplayName("대량 메뉴 작업 시뮬레이션")
        void bulkMenuOperations_Success() {
            // Given - 여러 메뉴와 카테고리를 한번에 처리하는 시나리오
            List<MenuCategoryCreateRequest> categoryRequests = List.of(
                    createCategoryRequest("한식"),
                    createCategoryRequest("중식"),
                    createCategoryRequest("일식")
            );

            List<MenuCreateRequest> menuRequests = List.of(
                    createMenuRequest("김치찌개", new BigDecimal("8000")),
                    createMenuRequest("짜장면", new BigDecimal("6000")),
                    createMenuRequest("초밥", new BigDecimal("12000"))
            );

            // Mock 설정
            when(menuCategoryService.createCategory(eq(testUser), any(MenuCategoryCreateRequest.class)))
                    .thenReturn(testCategory);
            when(menuCrudService.createMenu(eq(testUser), any(MenuCreateRequest.class)))
                    .thenReturn(testMenu);

            // When
            List<MenuCategory> createdCategories = new ArrayList<>();
            List<Menu> createdMenus = new ArrayList<>();

            for (MenuCategoryCreateRequest categoryRequest : categoryRequests) {
                createdCategories.add(menuService.createCategory(testUser, categoryRequest));
            }

            for (MenuCreateRequest menuRequest : menuRequests) {
                createdMenus.add(menuService.createMenu(testUser, menuRequest));
            }

            // Then
            assertThat(createdCategories).hasSize(3);
            assertThat(createdMenus).hasSize(3);

            // 각 서비스가 예상한 횟수만큼 호출되었는지 확인
            verify(menuCategoryService, times(3)).createCategory(eq(testUser), any(MenuCategoryCreateRequest.class));
            verify(menuCrudService, times(3)).createMenu(eq(testUser), any(MenuCreateRequest.class));
        }

        // 헬퍼 메서드들
        private MenuCategoryCreateRequest createCategoryRequest(String name) {
            MenuCategoryCreateRequest request = new MenuCategoryCreateRequest();
            request.setName(name);
            request.setIsActive(true);
            return request;
        }

        private MenuCreateRequest createMenuRequest(String name, BigDecimal price) {
            MenuCreateRequest request = new MenuCreateRequest();
            request.setName(name);
            request.setPrice(price);
            request.setCategoryId(1L);
            request.setStatus(MenuStatus.AVAILABLE);
            return request;
        }
    }

    @Nested
    @DisplayName("성능 및 동시성 테스트")
    class PerformanceAndConcurrencyTest {

        @Test
        @DisplayName("동시 메뉴 생성 요청 처리")
        void concurrentMenuCreation_Success() {
            // Given
            MenuCreateRequest request1 = createMenuCreateRequest();
            request1.setName("메뉴1");

            MenuCreateRequest request2 = createMenuCreateRequest();
            request2.setName("메뉴2");

            Menu menu1 = Menu.builder()
                    .store(testStore)
                    .category(testCategory)
                    .name("메뉴1")
                    .price(new BigDecimal("8000"))
                    .status(MenuStatus.AVAILABLE)
                    .build();

            Menu menu2 = Menu.builder()
                    .store(testStore)
                    .category(testCategory)
                    .name("메뉴2")
                    .price(new BigDecimal("8000"))
                    .status(MenuStatus.AVAILABLE)
                    .build();

            when(menuCrudService.createMenu(testUser, request1)).thenReturn(menu1);
            when(menuCrudService.createMenu(testUser, request2)).thenReturn(menu2);

            // When - 동시 실행 시뮬레이션
            Menu result1 = menuService.createMenu(testUser, request1);
            Menu result2 = menuService.createMenu(testUser, request2);

            // Then
            assertThat(result1).isNotNull();
            assertThat(result2).isNotNull();
            assertThat(result1.getName()).isEqualTo("메뉴1");
            assertThat(result2.getName()).isEqualTo("메뉴2");

            verify(menuCrudService, times(2)).createMenu(eq(testUser), any(MenuCreateRequest.class));
        }

        @Test
        @DisplayName("대량 데이터 조회 성능 테스트")
        void bulkDataRetrieval_Performance() {
            // Given - 대량의 메뉴 데이터 시뮬레이션
            List<Menu> largeMenuList = new ArrayList<>();
            List<MenuCategory> largeCategoryList = new ArrayList<>();

            // 100개의 메뉴와 10개의 카테고리 생성
            for (int i = 1; i <= 100; i++) {
                Menu menu = Menu.builder()
                        .store(testStore)
                        .category(testCategory)
                        .name("메뉴" + i)
                        .price(new BigDecimal("8000"))
                        .status(MenuStatus.AVAILABLE)
                        .build();
                largeMenuList.add(menu);
            }

            for (int i = 1; i <= 10; i++) {
                MenuCategory category = MenuCategory.builder()
                        .store(testStore)
                        .name("카테고리" + i)
                        .displayOrder(i)
                        .isActive(true)
                        .build();
                largeCategoryList.add(category);
            }

            when(menuCrudService.getMenus(testUser, null)).thenReturn(largeMenuList);
            when(menuCategoryService.getMenuCategories(testUser)).thenReturn(largeCategoryList);

            // When
            long startTime = System.currentTimeMillis();

            List<Menu> menuResult = menuService.getMenus(testUser, null);
            List<MenuCategory> categoryResult = menuService.getMenuCategories(testUser);

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // Then
            assertThat(menuResult).hasSize(100);
            assertThat(categoryResult).hasSize(10);
            assertThat(executionTime).isLessThan(1000); // 1초 이내 실행

            verify(menuCrudService).getMenus(testUser, null);
            verify(menuCategoryService).getMenuCategories(testUser);
        }
    }

    @Nested
    @DisplayName("에지 케이스 테스트")
    class EdgeCaseTest {

        @Test
        @DisplayName("null 파라미터 처리 테스트")
        void handleNullParameters() {
            // Given - null 값들
            MenuCreateRequest nullRequest = null;
            Long nullId = null;

            // Mock 설정 - 서비스에서 적절한 예외 발생
            when(menuCrudService.createMenu(testUser, nullRequest))
                    .thenThrow(new IllegalArgumentException("요청이 null입니다"));
            when(menuCrudService.getMenu(testUser, nullId))
                    .thenThrow(new IllegalArgumentException("메뉴 ID가 null입니다"));

            // When & Then
            assertThatThrownBy(() -> menuService.createMenu(testUser, nullRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("요청이 null입니다");

            assertThatThrownBy(() -> menuService.getMenu(testUser, nullId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("메뉴 ID가 null입니다");
        }

        @Test
        @DisplayName("빈 리스트 반환 테스트")
        void handleEmptyLists() {
            // Given
            when(menuCategoryService.getMenuCategories(testUser)).thenReturn(List.of());
            when(menuCrudService.getMenus(testUser, null)).thenReturn(List.of());
            when(menuOptionService.getMenuOptionGroups(testUser, 1L)).thenReturn(List.of());

            // When
            List<MenuCategory> categories = menuService.getMenuCategories(testUser);
            List<Menu> menus = menuService.getMenus(testUser, null);
            List<MenuOption> options = menuService.getMenuOptionGroups(testUser, 1L);

            // Then
            assertThat(categories).isEmpty();
            assertThat(menus).isEmpty();
            assertThat(options).isEmpty();

            verify(menuCategoryService).getMenuCategories(testUser);
            verify(menuCrudService).getMenus(testUser, null);
            verify(menuOptionService).getMenuOptionGroups(testUser, 1L);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외 처리")
        void handleNonExistentIds() {
            // Given
            Long nonExistentMenuId = 999L;
            Long nonExistentCategoryId = 888L;

            when(menuCrudService.getMenu(testUser, nonExistentMenuId))
                    .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));
            when(menuOptionService.getMenuOptionGroups(testUser, nonExistentMenuId))
                    .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다"));

            // When & Then
            assertThatThrownBy(() -> menuService.getMenu(testUser, nonExistentMenuId))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("메뉴를 찾을 수 없습니다");

            assertThatThrownBy(() -> menuService.getMenuOptionGroups(testUser, nonExistentMenuId))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("메뉴를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("매우 큰 숫자 ID 처리")
        void handleLargeIds() {
            // Given
            Long largeId = Long.MAX_VALUE;

            when(menuCrudService.getMenu(testUser, largeId)).thenReturn(testMenu);
            when(menuImageService.getMenuImageInfo(testUser, largeId)).thenReturn(Map.of("menuId", largeId));

            // When
            Menu result = menuService.getMenu(testUser, largeId);
            Map<String, Object> imageInfo = menuService.getMenuImageInfo(testUser, largeId);

            // Then
            assertThat(result).isNotNull();
            assertThat(imageInfo).containsKey("menuId");
            assertThat(imageInfo.get("menuId")).isEqualTo(largeId);

            verify(menuCrudService).getMenu(testUser, largeId);
            verify(menuImageService).getMenuImageInfo(testUser, largeId);
        }
    }

    @Nested
    @DisplayName("메서드 시그니처 검증 테스트")
    class MethodSignatureValidationTest {

        @Test
        @DisplayName("모든 서비스 메서드가 TempUser를 첫 번째 파라미터로 받는지 확인")
        void allServiceMethodsReceiveTempUserAsFirstParameter() {
            // Given
            MenuCategoryCreateRequest categoryRequest = new MenuCategoryCreateRequest();
            MenuCreateRequest menuRequest = new MenuCreateRequest();
            MenuUpdateRequest updateRequest = new MenuUpdateRequest();
            MenuStatusRequest statusRequest = new MenuStatusRequest();
            MenuOrderUpdateRequest orderRequest = new MenuOrderUpdateRequest();
            MenuOptionGroupCreateRequest optionGroupRequest = new MenuOptionGroupCreateRequest();
            MenuOptionItemCreateRequest optionItemRequest = new MenuOptionItemCreateRequest();
            MultipartFile mockFile = mock(MultipartFile.class);

            // Mock 설정 - 모든 메서드에서 첫 번째 파라미터가 TempUser인지 확인
            when(menuCategoryService.getMenuCategories(testUser)).thenReturn(List.of());
            when(menuCategoryService.createCategory(testUser, categoryRequest)).thenReturn(testCategory);
            when(menuCrudService.getMenus(testUser, null)).thenReturn(List.of());
            when(menuCrudService.createMenu(testUser, menuRequest)).thenReturn(testMenu);
            when(menuCrudService.updateMenu(testUser, 1L, updateRequest)).thenReturn(testMenu);
            when(menuCrudService.updateMenuStatus(testUser, 1L, statusRequest)).thenReturn(testMenu);
            when(menuCrudService.updateMenuOrder(testUser, orderRequest)).thenReturn(List.of());
            when(menuImageService.uploadMenuImage(testUser, 1L, mockFile)).thenReturn("url");
            when(menuImageService.getMenuImageInfo(testUser, 1L)).thenReturn(Map.of());
            when(menuOptionService.getMenuOptionGroups(testUser, 1L)).thenReturn(List.of());
            when(menuOptionService.createOptionGroup(testUser, 1L, optionGroupRequest)).thenReturn(createTestOptionGroup());
            when(menuOptionService.createOption(testUser, 1L, 1L, optionItemRequest)).thenReturn(createTestOptionItem());

            // When - 모든 메서드 호출
            assertDoesNotThrow(() -> {
                menuService.getMenuCategories(testUser);
                menuService.createCategory(testUser, categoryRequest);
                menuService.getMenus(testUser, null);
                menuService.createMenu(testUser, menuRequest);
                menuService.updateMenu(testUser, 1L, updateRequest);
                menuService.updateMenuStatus(testUser, 1L, statusRequest);
                menuService.updateMenuOrder(testUser, orderRequest);
                menuService.uploadMenuImage(testUser, 1L, mockFile);
                menuService.getMenuImageInfo(testUser, 1L);
                menuService.getMenuOptionGroups(testUser, 1L);
                menuService.createOptionGroup(testUser, 1L, optionGroupRequest);
                menuService.createOption(testUser, 1L, 1L, optionItemRequest);
            });

            // Then - 모든 Mock이 올바른 파라미터로 호출되었는지 확인
            verify(menuCategoryService).getMenuCategories(testUser);
            verify(menuCategoryService).createCategory(testUser, categoryRequest);
            verify(menuCrudService).getMenus(testUser, null);
            verify(menuCrudService).createMenu(testUser, menuRequest);
            verify(menuCrudService).updateMenu(testUser, 1L, updateRequest);
            verify(menuCrudService).updateMenuStatus(testUser, 1L, statusRequest);
            verify(menuCrudService).updateMenuOrder(testUser, orderRequest);
            verify(menuImageService).uploadMenuImage(testUser, 1L, mockFile);
            verify(menuImageService).getMenuImageInfo(testUser, 1L);
            verify(menuOptionService).getMenuOptionGroups(testUser, 1L);
            verify(menuOptionService).createOptionGroup(testUser, 1L, optionGroupRequest);
            verify(menuOptionService).createOption(testUser, 1L, 1L, optionItemRequest);
        }

        @Test
        @DisplayName("반환 타입이 올바른지 확인")
        void validateReturnTypes() {
            // Given
            when(menuCategoryService.getMenuCategories(testUser)).thenReturn(List.of(testCategory));
            when(menuCrudService.getMenu(testUser, 1L)).thenReturn(testMenu);
            when(menuImageService.uploadMenuImage(testUser, 1L, mockFile)).thenReturn("url"); // mockFile 사용
            when(menuOptionService.getMenuOptionGroups(testUser, 1L)).thenReturn(List.of(createTestOptionGroup()));

            // When
            Object categoriesResult = menuService.getMenuCategories(testUser);
            Object menuResult = menuService.getMenu(testUser, 1L);
            Object imageResult = menuService.uploadMenuImage(testUser, 1L, mockFile); // mockFile 사용
            Object optionsResult = menuService.getMenuOptionGroups(testUser, 1L);

            // Then - 반환 타입 확인
            assertThat(categoriesResult).isInstanceOf(List.class);
            assertThat(menuResult).isInstanceOf(Menu.class);
            assertThat(imageResult).isInstanceOf(String.class);
            assertThat(optionsResult).isInstanceOf(List.class);
        }
    }

    @Nested
    @DisplayName("서비스 간 상호작용 테스트")
    class ServiceInteractionTest {

        @Test
        @DisplayName("서비스 간 데이터 일관성 확인")
        void validateDataConsistencyBetweenServices() {
            // Given - 연관된 데이터들
            MenuCategory category = testCategory;
            Menu menu = testMenu;
            MenuOption option = createTestOptionGroup();

            MenuCreateRequest menuRequest = createMenuCreateRequest();
            MenuOptionGroupCreateRequest optionRequest = createOptionGroupCreateRequest();

            // 서비스 간 일관된 데이터 반환 설정
            when(menuCrudService.createMenu(testUser, menuRequest)).thenReturn(menu);
            when(menuOptionService.createOptionGroup(testUser, menu.getId(), optionRequest)).thenReturn(option);
            when(menuCrudService.getMenu(testUser, menu.getId())).thenReturn(menu);

            // When
            Menu createdMenu = menuService.createMenu(testUser, menuRequest);
            MenuOption createdOption = menuService.createOptionGroup(testUser, createdMenu.getId(), optionRequest);
            Menu retrievedMenu = menuService.getMenu(testUser, createdMenu.getId());

            // Then - 데이터가 일관되게 연결되어 있는지 확인
            assertThat(createdMenu.getId()).isEqualTo(retrievedMenu.getId());
            assertThat(createdMenu.getName()).isEqualTo(retrievedMenu.getName());
            assertThat(createdOption.getMenu()).isEqualTo(menu);

            verify(menuCrudService).createMenu(testUser, menuRequest);
            verify(menuOptionService).createOptionGroup(testUser, menu.getId(), optionRequest);
            verify(menuCrudService).getMenu(testUser, menu.getId());
        }

        @Test
        @DisplayName("서비스 호출 순서가 비즈니스 로직에 맞는지 확인")
        void validateServiceCallOrder() {
            // Given
            MenuCategoryCreateRequest categoryRequest = new MenuCategoryCreateRequest();
            MenuCreateRequest menuRequest = new MenuCreateRequest();

            when(menuCategoryService.createCategory(testUser, categoryRequest)).thenReturn(testCategory);
            when(menuCrudService.createMenu(testUser, menuRequest)).thenReturn(testMenu);

            // When - 순서대로 호출
            MenuCategory category = menuService.createCategory(testUser, categoryRequest);
            Menu menu = menuService.createMenu(testUser, menuRequest);

            // Then - 호출 순서 확인 (InOrder 사용)
            var inOrder = inOrder(menuCategoryService, menuCrudService);
            inOrder.verify(menuCategoryService).createCategory(testUser, categoryRequest);
            inOrder.verify(menuCrudService).createMenu(testUser, menuRequest);
        }

        @Test
        @DisplayName("여러 서비스에서 동일한 사용자 객체 사용 확인")
        void validateSameUserObjectAcrossServices() {
            // Given
            when(menuCategoryService.getMenuCategories(testUser)).thenReturn(List.of());
            when(menuCrudService.getMenus(testUser, null)).thenReturn(List.of());
            when(menuImageService.getMenuImageInfo(testUser, 1L)).thenReturn(Map.of());
            when(menuOptionService.getMenuOptionGroups(testUser, 1L)).thenReturn(List.of());

            // When
            menuService.getMenuCategories(testUser);
            menuService.getMenus(testUser, null);
            menuService.getMenuImageInfo(testUser, 1L);
            menuService.getMenuOptionGroups(testUser, 1L);

            // Then - 모든 서비스가 동일한 testUser 객체를 받았는지 확인
            verify(menuCategoryService).getMenuCategories(same(testUser));
            verify(menuCrudService).getMenus(same(testUser), isNull());
            verify(menuImageService).getMenuImageInfo(same(testUser), eq(1L));
            verify(menuOptionService).getMenuOptionGroups(same(testUser), eq(1L));
        }
    }

    @Nested
    @DisplayName("Mock 검증 테스트")
    class MockVerificationTest {

        @Test
        @DisplayName("불필요한 Mock 호출이 없는지 확인")
        void verifyNoUnnecessaryMockInteractions() {
            // Given
            when(menuCategoryService.getMenuCategories(testUser)).thenReturn(List.of(testCategory));

            // When
            List<MenuCategory> result = menuService.getMenuCategories(testUser);

            // Then
            assertThat(result).hasSize(1);

            // 호출된 Mock만 검증
            verify(menuCategoryService).getMenuCategories(testUser);

            // 다른 서비스들은 호출되지 않았는지 확인
            verifyNoInteractions(menuCrudService);
            verifyNoInteractions(menuImageService);
            verifyNoInteractions(menuOptionService);
        }

        @Test
        @DisplayName("Mock 호출 횟수가 정확한지 확인")
        void verifyExactMockCallCounts() {
            // Given
            MenuCreateRequest request1 = createMenuCreateRequest();
            request1.setName("메뉴1");
            MenuCreateRequest request2 = createMenuCreateRequest();
            request2.setName("메뉴2");

            when(menuCrudService.createMenu(testUser, request1)).thenReturn(testMenu);
            when(menuCrudService.createMenu(testUser, request2)).thenReturn(testMenu);

            // When
            menuService.createMenu(testUser, request1);
            menuService.createMenu(testUser, request2);

            // Then - 정확히 2번 호출되었는지 확인
            verify(menuCrudService, times(2)).createMenu(eq(testUser), any(MenuCreateRequest.class));
            verify(menuCrudService, times(1)).createMenu(testUser, request1);
            verify(menuCrudService, times(1)).createMenu(testUser, request2);
        }

        @Test
        @DisplayName("예외 발생 후에도 Mock 상태가 올바른지 확인")
        void verifyMockStateAfterException() {
            // Given
            MenuCreateRequest failingRequest = new MenuCreateRequest();
            MenuCreateRequest successRequest = createMenuCreateRequest();

            when(menuCrudService.createMenu(testUser, failingRequest))
                    .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "실패"));
            when(menuCrudService.createMenu(testUser, successRequest)).thenReturn(testMenu);

            // When & Then
            assertThatThrownBy(() -> menuService.createMenu(testUser, failingRequest))
                    .isInstanceOf(ResponseStatusException.class);

            // 예외 발생 후에도 다른 요청은 정상 처리되는지 확인
            Menu result = menuService.createMenu(testUser, successRequest);
            assertThat(result).isNotNull();

            // Mock 호출 확인
            verify(menuCrudService).createMenu(testUser, failingRequest);
            verify(menuCrudService).createMenu(testUser, successRequest);
        }
    }
}