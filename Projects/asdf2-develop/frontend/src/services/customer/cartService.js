import { useCartStore } from '@/stores/customer/cart'

export class CartService {
  constructor() {
    this.cartStore = null
  }

  // store를 지연 초기화
  getStore() {
    if (!this.cartStore) {
      this.cartStore = useCartStore()
    }
    return this.cartStore
  }

  // 장바구니에 메뉴 추가
  addMenuToCart(menuData) {
    try {
      // 메뉴 데이터 검증
      if (!this.validateMenuData(menuData)) {
        throw new Error('유효하지 않은 메뉴 데이터입니다.')
      }

      // 장바구니에 추가
      this.getStore().addItem(menuData)
      
      return { success: true, message: '장바구니에 추가되었습니다.' }
    } catch (error) {
      console.error('장바구니 추가 실패:', error)
      return { success: false, message: error.message }
    }
  }

  // 장바구니에서 메뉴 제거
  removeMenuFromCart(itemKey) {
    try {
      this.getStore().removeItem(itemKey)
      
      return { success: true, message: '장바구니에서 제거되었습니다.' }
    } catch (error) {
      console.error('장바구니 제거 실패:', error)
      return { success: false, message: error.message }
    }
  }

  // 메뉴 수량 업데이트
  updateMenuQuantity(itemKey, quantity) {
    try {
      if (quantity < 0) {
        throw new Error('수량은 0 이상이어야 합니다.')
      }

      this.getStore().updateQuantity(itemKey, quantity)
      
      return { success: true, message: '수량이 업데이트되었습니다.' }
    } catch (error) {
      console.error('수량 업데이트 실패:', error)
      return { success: false, message: error.message }
    }
  }

  // 장바구니 비우기
  clearCart() {
    try {
      this.getStore().clearCart()
      
      return { success: true, message: '장바구니가 비워졌습니다.' }
    } catch (error) {
      console.error('장바구니 비우기 실패:', error)
      return { success: false, message: error.message }
    }
  }

  // 장바구니 상태 가져오기
  getCartState() {
    const store = this.getStore()
    return {
      items: store.items,
      totalItems: store.totalItems,
      totalPrice: store.totalPrice,
      hasItems: store.hasItems
    }
  }

  // 메뉴 데이터 검증
  validateMenuData(menuData) {
    return menuData && 
           menuData.id && 
           menuData.name && 
           typeof menuData.price === 'number' &&
           menuData.price >= 0
  }

  // 장바구니 총 가격 계산 (옵션 포함)
  calculateTotalPriceWithOptions(menuData, selectedOptions, quantity) {
    let total = menuData.price * quantity
    
    // 선택된 옵션들의 가격 추가
    if (selectedOptions && Object.keys(selectedOptions).length > 0) {
      // 여기서 옵션 가격을 계산할 수 있습니다
      // 현재는 기본 가격만 반환
    }
    
    return total
  }
}

// 싱글톤 인스턴스 생성
export const cartService = new CartService()
