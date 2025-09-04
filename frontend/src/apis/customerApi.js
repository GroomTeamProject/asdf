import { mocks } from './mock'
import { POST } from '@/libs/ajax'

export const customerApi = {
  // TODO: 실제 API 요청
  
  getCategories: async () => {
    return mocks.categories
  },
  getStores: async () => {
    return mocks.stores
  },
  getMenuItems: async () => {
    return mocks.menuItems
  },
  getStoreById: async (storeId) => {
    return mocks.stores.find(store => store.id == storeId)
  },
  getMenuById: async (menuId) => {
    return mocks.menuItems.find(menu => menu.id == menuId)
  },
  getMenusByStoreId: async (storeId) => {
    return mocks.menuItems.filter(menu => menu.storeId == storeId)
  },
  getMenuOptions: async (menuId) => {
    // 특정 메뉴의 옵션들을 가져옴
    const menuOptions = mocks.menuOptions.filter(option => option.menuId == menuId)
    
    // 각 옵션에 대한 아이템들을 추가
    return menuOptions.map(option => ({
      ...option,
      items: mocks.menuOptionItems.filter(item => item.optionId === option.id && item.isActive)
    }))
  },
  
  // 주문 생성
  createOrder: async (orderData) => {
    try {
      const result = await POST('/api/customers/orders', orderData)
      return result
    } catch (error) {
      console.error('주문 생성 실패:', error)
      throw error
    }
  },
}
