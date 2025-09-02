export const mocks = {
  stores: [
    {
      id: '1',
      name: '맛있는 치킨집',
      category: '치킨',
      rating: 4.5,
      deliveryTime: '25-35분',
      deliveryFee: 2000,
      description: '바삭하고 맛있는 치킨 전문점',
    },
    {
      id: '2',
      name: '신선한 피자',
      category: '피자',
      rating: 4.3,
      deliveryTime: '20-30분',
      deliveryFee: 1500,
      description: '신선한 재료로 만든 수제 피자',
    },
    {
      id: '3',
      name: '한식당 고향',
      category: '한식',
      rating: 4.7,
      deliveryTime: '30-40분',
      deliveryFee: 3000,
      description: '정통 한식의 맛을 그대로',
    },
  ],
  categories: [
    { id: 1, name: '치킨' },
    { id: 2, name: '피자' },
    { id: 3, name: '한식' },
    { id: 4, name: '중식' },
  ],
  menuItems: [
    {
      id: '1',
      name: '후라이드 치킨',
      description: '바삭바삭한 클래식 후라이드',
      price: 18000,
      category: '치킨',
    },
    {
      id: '2',
      name: '양념 치킨',
      description: '달콤매콤한 양념치킨',
      price: 20000,
      category: '치킨',
    },
    {
      id: '3',
      name: '치킨무',
      description: '아삭한 치킨무',
      price: 2000,
      category: '사이드',
    },
  ],
  // 메뉴 상세 정보
  menuDetails: {
    '1': {
      name: '치킨 강정',
      description: '바삭하고 촉촉한 치킨에 달콤매콤한 강정소스를 입힌 인기 메뉴입니다. 신선한 재료와 비법 소스로 만든 특별한 맛을 경험해보세요.',
      basePrice: 18000,
      rating: 4.8,
      reviewCount: 234,
      preparationTime: '15-25분',
      tags: ['인기', '매운맛', '바삭함'],
    },
    '2': {
      name: '양념 치킨',
      description: '달콤매콤한 양념소스에 버무린 치킨으로, 한 번 먹으면 멈출 수 없는 맛입니다.',
      basePrice: 20000,
      rating: 4.6,
      reviewCount: 189,
      preparationTime: '20-30분',
      tags: ['인기', '달콤함', '양념'],
    },
    '3': {
      name: '후라이드 치킨',
      description: '바삭바삭한 클래식 후라이드 치킨으로, 원조의 맛을 그대로 느낄 수 있습니다.',
      basePrice: 18000,
      rating: 4.5,
      reviewCount: 156,
      preparationTime: '15-25분',
      tags: ['클래식', '바삭함', '담백함'],
    }
  },
  // 메뉴 옵션
  menuOptions: [
    { id: 'spicy1', name: '순한맛', price: 0, group: 'spicy' },
    { id: 'spicy2', name: '중간맛', price: 0, group: 'spicy' },
    { id: 'spicy3', name: '매운맛', price: 1000, group: 'spicy' },
    { id: 'size1', name: '보통 사이즈', price: 0, group: 'size' },
    { id: 'size2', name: '큰 사이즈', price: 2000, group: 'size' },
  ],
}
