<template>
  <div class="bg-white border-b border-gray-200 p-4">
    <div class="flex items-center justify-between mb-3">
      <h3 class="font-medium text-gray-800">선택한 메뉴</h3>
      <span class="text-sm text-gray-500">{{ totalItemCount }}개</span>
    </div>
    
    <!-- 가게별로 그룹화해서 표시 -->
    <div v-for="(storeGroup, storeId) in groupedByStore" :key="storeId" class="space-y-4 mb-6">
      <!-- 가게 헤더 -->
      <div class="bg-gray-50 px-4 py-3 rounded-lg">
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
            <span class="text-blue-600 text-sm">🏪</span>
          </div>
          <div>
            <h4 class="font-semibold text-gray-800">{{ storeGroup.storeName || '알 수 없는 가게' }}</h4>
            <p class="text-sm text-gray-500">{{ storeGroup.items.length }}개 메뉴</p>
          </div>
        </div>
      </div>
      
                <!-- 해당 가게의 메뉴들 -->
          <div class="space-y-3">
            <MenuItem
              v-for="item in storeGroup.items"
              :key="item.id"
              :item="item"
              @increase-quantity="increaseQuantity"
              @decrease-quantity="decreaseQuantity"
              @remove-item="removeItem"
              @edit-options="editOptions"
            />
          </div>
      
      <!-- 가게별 소계 -->
      <div class="bg-gray-100 px-4 py-2 rounded-lg">
        <div class="flex justify-between items-center">
          <span class="text-sm font-medium text-gray-700">{{ storeGroup.storeName }} 소계</span>
          <span class="font-semibold text-gray-900">{{ getStoreSubtotal(storeGroup.items).toLocaleString() }}원</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/customer/cart'
import { orderService } from '@/services/customer/orderService'
import MenuItem from './MenuItem.vue'

export default {
  name: 'OrderSummary',
  components: {
    MenuItem,
  },
  setup() {
    const router = useRouter()
    const cartStore = useCartStore()

    // 장바구니 상태
    const orderItems = computed(() => cartStore.items)
    
    // 가게별로 메뉴들을 그룹화
    const groupedByStore = computed(() => {
      return orderService.groupItemsByStore(orderItems.value)
    })

    // 총 아이템 개수
    const totalItemCount = computed(() => {
      return orderService.calculateTotalItemCount(orderItems.value)
    })

    // 가게별 소계 계산
    const getStoreSubtotal = (items) => {
      return items.reduce((total, item) => {
        const itemTotal = item.totalPrice || (item.price * item.quantity)
        return total + itemTotal
      }, 0)
    }

    // 수량 조작
    const increaseQuantity = (item) => {
      const result = orderService.updateItemQuantity(item, item.quantity + 1)
      if (!result.success) {
        alert('수량 업데이트에 실패했습니다: ' + result.message)
      }
    }

    const decreaseQuantity = (item) => {
      if (item.quantity > 1) {
        const result = orderService.updateItemQuantity(item, item.quantity - 1)
        if (!result.success) {
          alert('수량 업데이트에 실패했습니다: ' + result.message)
        }
      }
    }

    // 아이템 삭제
    const removeItem = (item) => {
      if (confirm('이 메뉴를 장바구니에서 제거하시겠습니까?')) {
        const result = orderService.removeItem(item)
        if (!result.success) {
          alert('메뉴 제거에 실패했습니다: ' + result.message)
        }
      }
    }

    // 옵션 변경
    const editOptions = (item) => {
      router.push(`/customer/stores/${item.storeId}/menu/${item.id}`)
    }

    return {
      groupedByStore,
      totalItemCount,
      getStoreSubtotal,
      increaseQuantity,
      decreaseQuantity,
      removeItem,
      editOptions,
    }
  },
}
</script>
