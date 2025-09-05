import { GET, POST } from '@/libs/ajax'

export const customerApi = {
  // 카테고리 목록 조회
  getCategories: async () => {
    try {
      return await GET('/api/categories')
    } catch (error) {
      console.error('카테고리 조회 실패:', error)
      throw error
    }
  },

  // 가게 목록 조회
  getStores: async () => {
    try {
      return await GET('/api/stores')
    } catch (error) {
      console.error('가게 목록 조회 실패:', error)
      throw error
    }
  },

  // 가게 상세 조회
  getStoreById: async (storeId) => {
    try {
      return await GET(`/api/stores/${storeId}`)
    } catch (error) {
      console.error('가게 상세 조회 실패:', error)
      throw error
    }
  },

  // 메뉴 상세 조회
  getMenuById: async (menuId) => {
    try {
      return await GET(`/api/menus/${menuId}`)
    } catch (error) {
      console.error('메뉴 상세 조회 실패:', error)
      throw error
    }
  },

  // 특정 가게의 메뉴 목록 조회
  getMenusByStoreId: async (storeId) => {
    try {
      return await GET(`/api/stores/${storeId}/menus`)
    } catch (error) {
      console.error('가게 메뉴 목록 조회 실패:', error)
      throw error
    }
  },

  // 메뉴 옵션 조회 (백엔드에서 메뉴 상세 조회 시 함께 반환하므로 별도 호출 불필요)
  // getMenuOptions: async (menuId) => {
  //   // 이제 메뉴 상세 조회에서 옵션을 함께 반환하므로 사용하지 않음
  // },
  
  // 주문 생성
  createOrder: async (orderData) => {
    try {
      return await POST('/api/orders', orderData)
    } catch (error) {
      console.error('주문 생성 실패:', error)
      throw error
    }
  },
}
