export const mocks = {
  // 가게 정보 (stores 테이블)
  stores: [
    {
      id: 1,
      ownerId: 1,
      businessNumber: '123-45-67890',
      name: '황금 치킨',
      description: '바삭하고 맛있는 치킨 전문점입니다. 신선한 재료와 비법 양념으로 최고의 치킨을 제공합니다.',
      phone: '02-1234-5678',
      address: '서울시 강남구 테헤란로 123',
      detailAddress: '1층',
      latitude: 37.5665,
      longitude: 126.9780,
      category: 'CHICKEN',
      minOrderAmount: 15000,
      deliveryFee: 2500,
      deliveryTimeMin: 25,
      deliveryTimeMax: 35,
      status: 'OPEN',
      rating: 4.5,
      reviewCount: 234,
      imageUrl: null,
      isActive: true
    },
    {
      id: 2,
      ownerId: 2,
      businessNumber: '234-56-78901',
      name: '이탈리아 피자하우스',
      description: '정통 이탈리아 피자를 맛볼 수 있는 곳입니다. 신선한 재료와 수제 도우로 만든 진짜 피자를 경험하세요.',
      phone: '02-2345-6789',
      address: '서울시 서초구 서초대로 456',
      detailAddress: '2층',
      latitude: 37.4979,
      longitude: 127.0276,
      category: 'WESTERN',
      minOrderAmount: 20000,
      deliveryFee: 3000,
      deliveryTimeMin: 30,
      deliveryTimeMax: 45,
      status: 'OPEN',
      rating: 4.3,
      reviewCount: 189,
      imageUrl: null,
      isActive: true
    },
    {
      id: 3,
      ownerId: 3,
      businessNumber: '345-67-89012',
      name: '한옥마을 한식당',
      description: '전통 한식의 깊은 맛을 현대적으로 재해석한 요리를 선보입니다. 정성스러운 손맛을 느껴보세요.',
      phone: '02-3456-7890',
      address: '서울시 종로구 인사동길 789',
      detailAddress: '지하1층',
      latitude: 37.5735,
      longitude: 126.9854,
      category: 'KOREAN',
      minOrderAmount: 12000,
      deliveryFee: 2000,
      deliveryTimeMin: 35,
      deliveryTimeMax: 50,
      status: 'OPEN',
      rating: 4.7,
      reviewCount: 156,
      imageUrl: null,
      isActive: true
    }
  ],

  // 카테고리 정보
  categories: [
    { id: 'KOREAN', name: '한식' },
    { id: 'CHINESE', name: '중식' },
    { id: 'WESTERN', name: '양식' },
    { id: 'JAPANESE', name: '일식' },
    { id: 'FAST_FOOD', name: '패스트푸드' },
    { id: 'CHICKEN', name: '치킨' },
    { id: 'PIZZA', name: '피자' },
    { id: 'DESSERT', name: '디저트' }
  ],

  // 메뉴 카테고리 (menu_categories 테이블)
  menuCategories: [
    { id: 1, storeId: 1, name: '인기메뉴', displayOrder: 1, isActive: true },
    { id: 2, storeId: 1, name: '치킨', displayOrder: 2, isActive: true },
    { id: 3, storeId: 1, name: '사이드', displayOrder: 3, isActive: true },
    { id: 4, storeId: 2, name: '피자', displayOrder: 1, isActive: true },
    { id: 5, storeId: 2, name: '파스타', displayOrder: 2, isActive: true },
    { id: 6, storeId: 3, name: '메인요리', displayOrder: 1, isActive: true },
    { id: 7, storeId: 3, name: '국물요리', displayOrder: 2, isActive: true }
  ],

  // 메뉴 정보 (menus 테이블)
  menuItems: [
    // 황금 치킨 (storeId: 1)
    {
      id: 1,
      storeId: 1,
      categoryId: 1,
      name: '황금 후라이드 치킨',
      description: '바삭바삭한 클래식 후라이드 치킨으로, 원조의 맛을 그대로 느낄 수 있습니다. 신선한 국내산 닭으로만 만듭니다.',
      price: 18000,
      imageUrl: null,
      isPopular: true,
      isRecommended: true,
      status: 'AVAILABLE',
      displayOrder: 1
    },
    {
      id: 2,
      storeId: 1,
      categoryId: 1,
      name: '황금 양념치킨',
      description: '달콤매콤한 비법 양념소스에 버무린 치킨으로, 한 번 먹으면 멈출 수 없는 중독성 있는 맛입니다.',
      price: 20000,
      imageUrl: null,
      isPopular: true,
      isRecommended: false,
      status: 'AVAILABLE',
      displayOrder: 2
    },
    {
      id: 3,
      storeId: 1,
      categoryId: 2,
      name: '마늘 간장치킨',
      description: '고소한 마늘과 깔끔한 간장의 조화가 일품인 치킨입니다.',
      price: 19000,
      imageUrl: null,
      isPopular: false,
      isRecommended: true,
      status: 'AVAILABLE',
      displayOrder: 3
    },
    {
      id: 4,
      storeId: 1,
      categoryId: 3,
      name: '치킨무',
      description: '아삭하고 시원한 치킨무로 치킨과 함께 드시면 더욱 맛있습니다.',
      price: 2000,
      imageUrl: null,
      isPopular: false,
      isRecommended: false,
      status: 'AVAILABLE',
      displayOrder: 4
    },
    {
      id: 5,
      storeId: 1,
      categoryId: 3,
      name: '감자튀김',
      description: '바삭하고 고소한 감자튀김입니다.',
      price: 3000,
      imageUrl: null,
      isPopular: false,
      isRecommended: false,
      status: 'AVAILABLE',
      displayOrder: 5
    },

    // 이탈리아 피자하우스 (storeId: 2)
    {
      id: 6,
      storeId: 2,
      categoryId: 4,
      name: '마르게리타 피자',
      description: '신선한 토마토, 모짜렐라 치즈, 바질이 어우러진 클래식 피자입니다.',
      price: 24000,
      imageUrl: null,
      isPopular: true,
      isRecommended: true,
      status: 'AVAILABLE',
      displayOrder: 1
    },
    {
      id: 7,
      storeId: 2,
      categoryId: 4,
      name: '페퍼로니 피자',
      description: '매콤한 페퍼로니와 치즈의 완벽한 조화를 느껴보세요.',
      price: 26000,
      imageUrl: null,
      isPopular: true,
      isRecommended: false,
      status: 'AVAILABLE',
      displayOrder: 2
    },
    {
      id: 8,
      storeId: 2,
      categoryId: 5,
      name: '까르보나라',
      description: '진짜 이탈리아식 까르보나라 파스타입니다.',
      price: 16000,
      imageUrl: null,
      isPopular: false,
      isRecommended: true,
      status: 'AVAILABLE',
      displayOrder: 3
    },

    // 한옥마을 한식당 (storeId: 3)
    {
      id: 9,
      storeId: 3,
      categoryId: 6,
      name: '불고기 정식',
      description: '부드러운 한우 불고기와 함께 나오는 한정식입니다.',
      price: 15000,
      imageUrl: null,
      isPopular: true,
      isRecommended: true,
      status: 'AVAILABLE',
      displayOrder: 1
    },
    {
      id: 10,
      storeId: 3,
      categoryId: 7,
      name: '김치찌개',
      description: '깊은 맛의 김치찌개로 집밥의 정취를 느껴보세요.',
      price: 8000,
      imageUrl: null,
      isPopular: false,
      isRecommended: false,
      status: 'AVAILABLE',
      displayOrder: 2
    }
  ],

  // 메뉴 옵션 (menu_options 테이블)
  menuOptions: [
    // 치킨 메뉴들의 맵기 옵션
    { id: 1, menuId: 1, name: '맵기 정도', type: 'SINGLE', isRequired: true, displayOrder: 1 },
    { id: 2, menuId: 2, name: '맵기 정도', type: 'SINGLE', isRequired: true, displayOrder: 1 },
    { id: 3, menuId: 3, name: '맵기 정도', type: 'SINGLE', isRequired: true, displayOrder: 1 },
    
    // 치킨 메뉴들의 사이즈 옵션
    { id: 4, menuId: 1, name: '사이즈', type: 'SINGLE', isRequired: true, displayOrder: 2 },
    { id: 5, menuId: 2, name: '사이즈', type: 'SINGLE', isRequired: true, displayOrder: 2 },
    { id: 6, menuId: 3, name: '사이즈', type: 'SINGLE', isRequired: true, displayOrder: 2 },

    // 피자 사이즈 옵션
    { id: 7, menuId: 6, name: '사이즈', type: 'SINGLE', isRequired: true, displayOrder: 1 },
    { id: 8, menuId: 7, name: '사이즈', type: 'SINGLE', isRequired: true, displayOrder: 1 }
  ],

  // 메뉴 옵션 아이템 (menu_option_items 테이블)
  menuOptionItems: [
    // 맵기 옵션들
    { id: 1, optionId: 1, name: '순한맛', additionalPrice: 0, displayOrder: 1, isActive: true },
    { id: 2, optionId: 1, name: '보통맛', additionalPrice: 0, displayOrder: 2, isActive: true },
    { id: 3, optionId: 1, name: '매운맛', additionalPrice: 1000, displayOrder: 3, isActive: true },
    
    { id: 4, optionId: 2, name: '순한맛', additionalPrice: 0, displayOrder: 1, isActive: true },
    { id: 5, optionId: 2, name: '보통맛', additionalPrice: 0, displayOrder: 2, isActive: true },
    { id: 6, optionId: 2, name: '매운맛', additionalPrice: 1000, displayOrder: 3, isActive: true },
    
    { id: 7, optionId: 3, name: '순한맛', additionalPrice: 0, displayOrder: 1, isActive: true },
    { id: 8, optionId: 3, name: '보통맛', additionalPrice: 0, displayOrder: 2, isActive: true },
    { id: 9, optionId: 3, name: '매운맛', additionalPrice: 1000, displayOrder: 3, isActive: true },

    // 치킨 사이즈 옵션들
    { id: 10, optionId: 4, name: '보통', additionalPrice: 0, displayOrder: 1, isActive: true },
    { id: 11, optionId: 4, name: '대형', additionalPrice: 3000, displayOrder: 2, isActive: true },
    
    { id: 12, optionId: 5, name: '보통', additionalPrice: 0, displayOrder: 1, isActive: true },
    { id: 13, optionId: 5, name: '대형', additionalPrice: 3000, displayOrder: 2, isActive: true },
    
    { id: 14, optionId: 6, name: '보통', additionalPrice: 0, displayOrder: 1, isActive: true },
    { id: 15, optionId: 6, name: '대형', additionalPrice: 3000, displayOrder: 2, isActive: true },

    // 피자 사이즈 옵션들
    { id: 16, optionId: 7, name: 'R(레귤러)', additionalPrice: 0, displayOrder: 1, isActive: true },
    { id: 17, optionId: 7, name: 'L(라지)', additionalPrice: 5000, displayOrder: 2, isActive: true },
    
    { id: 18, optionId: 8, name: 'R(레귤러)', additionalPrice: 0, displayOrder: 1, isActive: true },
    { id: 19, optionId: 8, name: 'L(라지)', additionalPrice: 5000, displayOrder: 2, isActive: true }
  ]
}
