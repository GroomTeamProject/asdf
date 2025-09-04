import { cartService } from './cartService'

export class OrderService {
  // 가게별로 메뉴들을 그룹화
  groupItemsByStore(orderItems) {
    const groups = {}
    
    orderItems.forEach((item) => {
      const storeId = item.storeId || 'unknown'
      
      if (!groups[storeId]) {
        groups[storeId] = {
          storeId,
          storeName: item.storeName || '알 수 없는 가게',
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

  // 주문 데이터 구성
  buildOrderData(orderItems, totalAmount, deliveryFee, discountAmount, finalAmount, orderMemo, deliveryAddress, phoneNumber, paymentMethod) {
    return {
      items: orderItems,
      totalAmount,
      deliveryFee,
      discountAmount,
      finalAmount,
      orderMemo,
      deliveryAddress,
      phoneNumber,
      paymentMethod,
    }
  }

  // 주문 제출
  async submitOrder(orderData) {
    try {
      // TODO: 실제 주문 API 호출
      console.log('주문 데이터:', orderData)
      
      // 임시로 성공 처리
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            success: true,
            orderNumber: 'ORD-' + Date.now().toString().slice(-6),
            message: '주문이 성공적으로 접수되었습니다!'
          })
        }, 1000)
      })
    } catch (error) {
      console.error('주문 제출 실패:', error)
      throw error
    }
  }

  // 주문 완료 후 장바구니 정리
  clearCartAfterOrder() {
    return cartService.clearCart()
  }
}

// 싱글톤 인스턴스 생성
export const orderService = new OrderService()
