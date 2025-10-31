import { customerApi } from '@/api/customer/customerApi'

export class MenuService {
  // 가게 목록 조회
  async getStores() {
    try {
      const stores = await customerApi.getStores()
      return {
        success: true,
        data: stores
      }
    } catch (error) {
      console.error('가게 목록 조회 실패:', error)
      throw error
    }
  }

  // 가게 상세 조회
  async getStoreById(storeId) {
    try {
      if (!storeId) {
        throw new Error('가게 ID가 필요합니다.')
      }

      const store = await customerApi.getStoreById(storeId)
      return {
        success: true,
        data: store
      }
    } catch (error) {
      console.error('가게 상세 조회 실패:', error)
      throw error
    }
  }

  // 가게별 메뉴 목록 조회
  async getMenusByStoreId(storeId) {
    try {
      if (!storeId) {
        throw new Error('가게 ID가 필요합니다.')
      }

      const menus = await customerApi.getMenusByStoreId(storeId)
      return {
        success: true,
        data: menus
      }
    } catch (error) {
      console.error('메뉴 목록 조회 실패:', error)
      throw error
    }
  }

  // 메뉴 상세 조회
  async getMenuById(storeId, menuId) {
    try {
      if (!storeId || !menuId) {
        throw new Error('가게 ID와 메뉴 ID가 필요합니다.')
      }

      const menu = await customerApi.getMenuById(storeId, menuId)
      return {
        success: true,
        data: menu
      }
    } catch (error) {
      console.error('메뉴 상세 조회 실패:', error)
      throw error
    }
  }

  // 메뉴와 가게 정보를 함께 조회 (MenuDetail.vue에서 사용)
  async getMenuWithStoreInfo(storeId, menuId) {
    try {
      if (!storeId || !menuId) {
        throw new Error('가게 ID와 메뉴 ID가 필요합니다.')
      }

      // 메뉴 정보 조회
      const menuResult = await this.getMenuById(storeId, menuId)
      
      let storeInfo = null
      
      // 메뉴에 가게 정보가 포함되어 있는지 확인
      if (menuResult.data?.store) {
        storeInfo = menuResult.data.store
      } else {
        // 메뉴에 가게 정보가 없으면 별도로 조회
        const storeResult = await this.getStoreById(storeId)
        storeInfo = storeResult.data
      }

      return {
        success: true,
        data: {
          menu: menuResult.data,
          store: storeInfo
        }
      }
    } catch (error) {
      console.error('메뉴와 가게 정보 조회 실패:', error)
      throw error
    }
  }
}

// 싱글톤 인스턴스 생성
export const menuService = new MenuService()
