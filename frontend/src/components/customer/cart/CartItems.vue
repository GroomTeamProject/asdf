<template>
  <div class="space-y-6">
    <!-- 가게별로 그룹화해서 표시 -->
    <div v-for="(storeGroup, storeId) in groupedByStore" :key="storeId" class="space-y-4">
      <!-- 가게 헤더 -->
      <div class="bg-white rounded-lg border border-gray-200 shadow-sm">
        <div class="bg-gray-50 px-4 py-3 border-b border-gray-200 rounded-t-lg">
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
              <i data-lucide="store" class="w-4 h-4 text-blue-600"></i>
            </div>
            <div>
              <h3 class="font-semibold text-gray-800">{{ storeGroup.storeName || '알 수 없는 가게' }}</h3>
              <p class="text-sm text-gray-500">{{ storeGroup.items.length }}개 메뉴</p>
            </div>
          </div>
        </div>
        
        <!-- 해당 가게의 메뉴들 -->
        <div class="p-4 space-y-4">
          <div v-for="item in storeGroup.items" :key="item._key || item.id" class="border border-gray-200 rounded-lg bg-gray-50">
            <div class="p-4">
              <div class="flex items-center gap-4">
                <div class="image-placeholder w-16 h-16">
                  <span>IMAGE</span>
                </div>
                <div class="flex-1">
                  <h3 class="mb-1 text-gray-800">{{ item.name }}</h3>
                  
                  <!-- 선택된 옵션 표시 -->
                  <div v-if="item.selectedOptions && hasSelectedOptions(item)" class="mb-2">
                    <div v-for="(optionText, index) in getSelectedOptionsText(item)" :key="index" class="text-xs text-gray-600">
                      {{ optionText }}
                    </div>
                  </div>
                  
                  <!-- 가격 정보 -->
                  <div class="text-gray-800">
                    <span v-if="item.totalPrice && item.totalPrice !== item.price * item.quantity" class="text-sm text-gray-500 line-through mr-2">
                      {{ (item.price * item.quantity).toLocaleString() }}원
                    </span>
                    <span class="font-medium">
                      {{ (item.totalPrice || item.price * item.quantity).toLocaleString() }}원
                    </span>
                  </div>
                </div>
                <div class="flex items-center gap-2">
                  <button
                    @click="updateQuantity(item._key || item.id, item.quantity - 1)"
                    class="border border-gray-300 text-gray-700 w-8 h-8 rounded flex items-center justify-center hover:bg-gray-200 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-1"
                  >
                    <i data-lucide="minus" class="w-4 h-4"></i>
                  </button>
                  <span class="w-8 text-center text-gray-800">{{ item.quantity }}</span>
                  <button
                    @click="updateQuantity(item._key || item.id, item.quantity + 1)"
                    class="border border-gray-300 text-gray-700 w-8 h-8 rounded flex items-center justify-center hover:bg-gray-200 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-1"
                  >
                    <i data-lucide="plus" class="w-4 h-4"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 가게별 소계 -->
        <div class="bg-gray-50 px-4 py-3 border-t border-gray-200 rounded-b-lg">
          <div class="flex justify-between items-center">
            <span class="text-sm font-medium text-gray-700">{{ storeGroup.storeName }} 소계</span>
            <span class="font-semibold text-gray-900">{{ getStoreSubtotal(storeGroup.items).toLocaleString() }}원</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { customerApi } from '@/apis/customerApi'

export default {
  name: 'CartItems',
  props: {
    cartItems: {
      type: Array,
      required: true,
    },
  },
  emits: ['update-quantity'],
  setup(props, { emit }) {
    const updateQuantity = (itemId, newQuantity) => {
      emit('update-quantity', itemId, newQuantity)
    }

    // 가게별로 메뉴들을 그룹화
    const groupedByStore = computed(() => {
      const groups = {}
      
      props.cartItems.forEach(item => {
        const storeId = item.storeId || 'unknown'
        
        if (!groups[storeId]) {
          groups[storeId] = {
            storeId,
            storeName: item.storeName || '알 수 없는 가게',
            items: []
          }
        }
        
        groups[storeId].items.push(item)
      })
      
      return groups
    })

    // 선택된 옵션이 있는지 확인
    const hasSelectedOptions = (item) => {
      return item.selectedOptions && Object.keys(item.selectedOptions).length > 0
    }

    // 선택된 옵션을 텍스트로 변환
    const getSelectedOptionsText = (item) => {
      if (!item.selectedOptions || !item.menuOptions) return []
      
      const optionTexts = []
      
      // 각 옵션 그룹별로 선택된 아이템 찾기
      Object.entries(item.selectedOptions).forEach(([optionGroupId, selectedItemId]) => {
        const optionGroup = item.menuOptions.find(opt => opt.id == optionGroupId)
        if (optionGroup) {
          const selectedItem = optionGroup.items?.find(item => item.id == selectedItemId)
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
    const getStoreSubtotal = (items) => {
      return items.reduce((total, item) => {
        const itemTotal = item.totalPrice || (item.price * item.quantity)
        return total + itemTotal
      }, 0)
    }

    return {
      updateQuantity,
      hasSelectedOptions,
      getSelectedOptionsText,
      groupedByStore,
      getStoreSubtotal,
    }
  },
}
</script>
