import { cartService } from './cartService'
import { customerApi } from '@/api/customer/customerApi'

export class OrderService {
  // 가게별로 메뉴들을 그룹화
  groupItemsByStore(orderItems) {
    const groups = {}
    
    orderItems.forEach((item) => {
      const storeId = item.storeId || 'unknown'
      
      if (!groups[storeId]) {
        groups[storeId] = {
          storeId,
          storeName: item.storeName,
          items: [],
        }
      }
      
      groups[storeId].items.push(item)
    })
    
    return groups
  }

  // 총 아이템 개수 계산
  calculateTotalItemCount(orderItems) {
    return orderItems.reduce((total, item) => total + item.quantity, 0)
  }

  // 배달비 계산
  calculateDeliveryFee(totalAmount, minOrderAmount = 15000) {
    return totalAmount >= minOrderAmount ? 0 : 3000
  }

  // 할인 금액 계산
  calculateDiscountAmount(totalAmount) {
    // TODO: 할인 정책 적용
    return 0
  }

  // 최종 결제 금액 계산
  calculateFinalAmount(totalAmount, deliveryFee, discountAmount) {
    return totalAmount + deliveryFee - discountAmount
  }

  // 선택된 옵션이 있는지 확인
  hasSelectedOptions(item) {
    return item.selectedOptions && Object.keys(item.selectedOptions).length > 0
  }

  // 선택된 옵션을 텍스트로 변환
  getSelectedOptionsText(item) {
    if (!item.selectedOptions || !item.menuOptions) return []
    
    const optionTexts = []
    
    // 각 옵션 그룹별로 선택된 아이템 찾기
    Object.entries(item.selectedOptions).forEach(([optionGroupId, selectedItemId]) => {
      const optionGroup = item.menuOptions.find((opt) => opt.id == optionGroupId)
      if (optionGroup) {
        const selectedItem = optionGroup.items?.find((item) => item.id == selectedItemId)
        if (selectedItem) {
          let optionText = `${optionGroup.name}: ${selectedItem.name}`
          if (selectedItem.additionalPrice > 0) {
            optionText += ` (+${selectedItem.additionalPrice.toLocaleString()}원)`
          }
          optionTexts.push(optionText)
        }
      }
    })
    
    return optionTexts
  }

  // 가게별 소계 계산
  calculateStoreSubtotal(items) {
    return items.reduce((total, item) => {
      const itemTotal = item.totalPrice || item.price * item.quantity
      return total + itemTotal
    }, 0)
  }

  // 수량 업데이트
  updateItemQuantity(item, newQuantity) {
    return cartService.updateMenuQuantity(item._key || item.id, newQuantity)
  }

  // 아이템 삭제
  removeItem(item) {
    return cartService.removeMenuFromCart(item._key || item.id)
  }

  // 주문 데이터 구성 (백엔드 스키마에 맞춤)
  buildOrderData(orderItems, totalAmount, deliveryFee, discountAmount, finalAmount, orderMemo, deliveryAddress, deliveryDetailAddress, phoneNumber, paymentMethod) {
    // 주문 아이템들을 백엔드 스키마에 맞게 변환
    const orderItemsData = orderItems.map(cartItem => {
      const orderItem = {
        menuId: cartItem.id,
        quantity: cartItem.quantity,
        options: []
      }
      
      // 옵션은 PaymentButton에서 직접 처리하므로 여기서는 빈 배열로 설정
      orderItem.options = []
      
      return orderItem
    })
    
    // 백엔드 OrderRequest 구조에 맞춤
    return {
      userId: localStorage.getItem('userId'),
      storeId: orderItems[0]?.storeId,
      deliveryAddress: deliveryAddress,
      deliveryDetailAddress: deliveryDetailAddress,
      phone: phoneNumber,
      orderMemo: orderMemo,
      orderItems: orderItemsData
    }
  }

  // 주문 제출
  async submitOrder(orderData) {
    try {
      console.log('주문 데이터:', orderData)
      
      const result = await customerApi.createOrder(orderData)
      console.log('주문 제출 결과:', result)
      
      return {
        success: true,
        orderNumber: result.orderNumber || result.order_number,
        message: result.message
      }
    } catch (error) {
      console.error('주문 제출 실패:', error)
      
      // 에러 상세 정보 로깅
      if (error.response) {
        console.error('응답 데이터:', error.response.data)
        console.error('응답 상태:', error.response.status)
      }
      
      throw new Error(error.response?.data?.message || error.message || '주문 처리 중 오류가 발생했습니다.')
    }
  }

  // 주문 완료 후 장바구니 정리
  clearCartAfterOrder() {
    return cartService.clearCart()
  }

  // 주문 내역 조회 (페이지네이션)
  async getMyOrders(page = 0, size = 20) {
    try {
      console.log(`📄 페이지 ${page} 요청 중...`)
      const response = await customerApi.getMyOrders(page, size)
      console.log('📄 응답 데이터:', response)
      
      return {
        success: true,
        data: response
      }
    } catch (error) {
      console.error('주문 내역 조회 실패:', error)
      throw error
    }
  }

  // 주문 상세 조회
  async getOrderDetail(orderId) {
    try {
      if (!orderId) {
        throw new Error('주문 ID가 필요합니다.')
      }

      const order = await customerApi.getOrderDetail(orderId)
      return {
        success: true,
        data: order
      }
    } catch (error) {
      console.error('주문 상세 조회 실패:', error)
      throw error
    }
  }

  // 주문 완료 정보 조회 (OrderComplete.vue에서 사용)
  async getOrderCompleteInfo(orderNumber, totalAmount, storeName, deliveryAddress, phoneNumber) {
    try {
      if (!orderNumber) {
        throw new Error('주문 번호가 필요합니다.')
      }

      // 실제로는 서버에서 주문 완료 정보를 조회해야 하지만,
      // 현재는 전달받은 데이터를 그대로 반환
      return {
        success: true,
        data: {
          orderNumber,
          totalAmount: parseInt(totalAmount),
          storeName,
          deliveryAddress,
          phoneNumber,
          orderTime: new Date().toLocaleString('ko-KR'),
          estimatedDeliveryTime: '약 30분 후'
        }
      }
    } catch (error) {
      console.error('주문 완료 정보 조회 실패:', error)
      throw error
    }
  }
}

// 싱글톤 인스턴스 생성
export const orderService = new OrderService()
