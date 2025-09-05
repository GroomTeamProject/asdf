import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const isLoading = ref(false)

  // 아이템의 고유 키 생성 (메뉴 ID + 옵션 조합)
  const generateItemKey = (item) => {
    const baseKey = item.id.toString()
    const options = item.selectedOptions || {}
    
    if (Object.keys(options).length === 0) {
      // 옵션이 없는 경우
      return baseKey
    }
    
    // 옵션이 있는 경우: 옵션 키-값을 정렬해서 문자열로 만들기
    const optionKeys = Object.keys(options).sort()
    const optionString = optionKeys.map(key => `${key}:${options[key]}`).join('|')
    return `${baseKey}_${optionString}`
  }

  const totalItems = computed(() => {
    return items.value.reduce((total, item) => total + item.quantity, 0)
  })

  const totalPrice = computed(() => {
    return items.value.reduce((total, item) => {
      // totalPrice가 있으면 그것을 사용, 없으면 기본 가격 * 수량
      const itemTotal = item.totalPrice || (item.price * item.quantity)
      return total + itemTotal
    }, 0)
  })

  const hasItems = computed(() => items.value.length > 0)

  const addItem = (item) => {
    console.log('=== 장바구니 스토어 addItem 디버깅 ===')
    console.log('1. 받은 item 데이터:', item)
    console.log('2. item.storeId:', item.storeId)
    console.log('3. item.storeName:', item.storeName)
    console.log('4. item.storeInfo:', item.storeInfo)
    
    const itemKey = generateItemKey(item)
    console.log('5. 생성된 itemKey:', itemKey)
    
    // 같은 키를 가진 아이템 찾기
    const existingItem = items.value.find(cartItem => cartItem._key === itemKey)
    
    if (existingItem) {
      console.log('6. 기존 아이템 수량 증가')
      // 같은 메뉴 + 같은 옵션이면 수량만 증가
      const oldQuantity = existingItem.quantity
      const addQuantity = item.quantity || 1
      existingItem.quantity += addQuantity
      
      // totalPrice도 업데이트 (기존 단가 * 새로운 총 수량)
      if (existingItem.totalPrice && oldQuantity > 0) {
        const pricePerItem = existingItem.totalPrice / oldQuantity
        existingItem.totalPrice = pricePerItem * existingItem.quantity
      } else if (item.totalPrice) {
        // 기존에 totalPrice가 없었다면 새로운 아이템의 단가 사용
        const pricePerItem = item.totalPrice / addQuantity
        existingItem.totalPrice = pricePerItem * existingItem.quantity
      }
    } else {
      console.log('6. 새로운 아이템 추가')
      // 새로운 아이템으로 추가 (고유 키 포함, 깊은 복사로 참조 문제 방지)
      const newItem = {
        ...item,
        quantity: item.quantity || 1,
        _key: itemKey,
        selectedOptions: JSON.parse(JSON.stringify(item.selectedOptions || {})),
        menuOptions: JSON.parse(JSON.stringify(item.menuOptions || []))
      }
      console.log('7. 추가할 newItem:', newItem)
      items.value.push(newItem)
    }
    
    console.log('8. 현재 장바구니 items:', items.value)
  }

  const removeItem = (itemKey) => {
    const index = items.value.findIndex(item => item._key === itemKey)
    if (index > -1) {
      items.value.splice(index, 1)
    }
  }

  const updateQuantity = (itemKey, quantity) => {
    const item = items.value.find(cartItem => cartItem._key === itemKey)
    if (item) {
      if (quantity <= 0) {
        removeItem(itemKey)
      } else {
        const oldQuantity = item.quantity
        item.quantity = quantity
        
        // totalPrice가 있으면 수량 비율에 따라 업데이트
        if (item.totalPrice && oldQuantity > 0) {
          const pricePerItem = item.totalPrice / oldQuantity
          item.totalPrice = pricePerItem * quantity
        }
      }
    }
  }

  const clearCart = () => {
    items.value = []
  }

  const loadFromStorage = () => {
    // pinia-plugin-persistedstate가 자동으로 처리
  }

  const saveToStorage = () => {
    // pinia-plugin-persistedstate가 자동으로 처리
  }

  return {
    // 상태
    items,
    isLoading,
    
    // getters
    totalItems,
    totalPrice,
    hasItems,
    
    // actions
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
    loadFromStorage,
    saveToStorage
  }
}, {
  persist: {
    key: 'cart',
    storage: localStorage,
    paths: ['items']
  }
}) 