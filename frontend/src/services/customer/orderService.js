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

  // 주문 데이터 구성 (백엔드 스키마에 맞춤)
  buildOrderData(orderItems, totalAmount, deliveryFee, discountAmount, finalAmount, orderMemo, deliveryAddress, phoneNumber, paymentMethod) {
    // 주문 번호 생성 (임시)
    const orderNumber = 'ORD-' + Date.now().toString().slice(-8)
    
    // 주문 아이템들을 백엔드 스키마에 맞게 변환
    const orderItemsData = orderItems.map(item => {
      // 가게별로 그룹화된 아이템들을 평면화
      return item.items.map(cartItem => {
        const orderItem = {
          menu_id: cartItem.id,
          menu_name: cartItem.name,
          menu_price: cartItem.price,
          quantity: cartItem.quantity,
          total_price: cartItem.totalPrice || cartItem.price * cartItem.quantity,
          options: []
        }
        
        // 선택된 옵션들을 백엔드 스키마에 맞게 변환
        if (cartItem.selectedOptions && cartItem.menuOptions) {
          Object.entries(cartItem.selectedOptions).forEach(([optionGroupId, selectedItemId]) => {
            const optionGroup = cartItem.menuOptions.find(opt => opt.id == optionGroupId)
            if (optionGroup) {
              const selectedItem = optionGroup.items?.find(item => item.id == selectedItemId)
              if (selectedItem) {
                orderItem.options.push({
                  option_name: optionGroup.name,
                  option_item_name: selectedItem.name,
                  additional_price: selectedItem.additionalPrice || 0
                })
              }
            }
          })
        }
        
        return orderItem
      })
    }).flat() // 중첩된 배열을 평면화
    
    return {
      order_number: orderNumber,
      user_id: 1, // TODO: 실제 사용자 ID로 교체
      store_id: orderItems[0]?.storeId || 1, // TODO: 실제 가게 ID로 교체
      delivery_address: deliveryAddress,
      delivery_detail_address: '', // TODO: 상세 주소 필드 추가 필요
      phone: phoneNumber,
      order_memo: orderMemo,
      menu_total_amount: totalAmount,
      delivery_fee: deliveryFee,
      discount_amount: discountAmount,
      total_amount: finalAmount,
      order_items: orderItemsData
    }
  }

  // 주문 제출
  async submitOrder(orderData) {
    try {
      // TODO: 실제 주문 API 호출
      console.log('주문 데이터:', orderData)
      
      // 백엔드 API 호출 구조 (실제 구현 시 사용)
      /*
      const response = await fetch('/api/orders', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}` // TODO: 인증 토큰 추가
        },
        body: JSON.stringify(orderData)
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const result = await response.json()
      return {
        success: true,
        orderNumber: result.order_number,
        message: '주문이 성공적으로 접수되었습니다!'
      }
      */
      
      // 임시로 성공 처리 (개발용)
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            success: true,
            orderNumber: orderData.order_number,
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
