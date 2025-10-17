<template>
  <div class="flex items-start gap-3 p-3 bg-gray-50 rounded-lg">
    <div class="w-16 h-16 bg-gray-200 rounded-lg flex items-center justify-center">
      <span class="text-gray-500 text-sm">🍽️</span>
    </div>
    
    <div class="flex-1">
      <div class="flex items-start justify-between">
        <div>
          <h4 class="font-medium text-gray-800">{{ item.name }}</h4>
          <p class="text-sm text-gray-600">{{ (item.totalPrice || item.price * item.quantity).toLocaleString() }}원</p>
          
          <!-- 선택된 옵션들 (이름으로 표시) -->
          <div v-if="item.selectedOptions && hasSelectedOptions(item)" class="mt-1">
            <div v-for="(optionText, index) in getSelectedOptionsText(item)" :key="index" class="text-xs text-gray-500">
              {{ optionText }}
            </div>
          </div>
        </div>
        
        <div class="flex items-center gap-2">
          <button
            @click="decreaseQuantity"
            class="w-6 h-6 border border-gray-300 rounded-full flex items-center justify-center text-gray-600 hover:bg-gray-100"
          >
            -
          </button>
          <span class="w-8 text-center font-medium">{{ item.quantity }}</span>
          <button
            @click="increaseQuantity"
            class="w-6 h-6 border border-gray-300 rounded-full flex items-center justify-center text-gray-600 hover:bg-gray-100"
          >
            +
          </button>
        </div>
      </div>
      
      <div class="flex gap-2 mt-2">
        <button
          @click="editOptions"
          class="text-xs text-blue-600 hover:bg-blue-50 px-2 py-1 rounded"
        >
          옵션 변경
        </button>
        <button
          @click="removeItem"
          class="text-xs text-red-600 hover:bg-red-50 px-2 py-1 rounded"
        >
          삭제
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MenuItem',
  props: {
    item: {
      type: Object,
      required: true,
    },
  },
  emits: ['increase-quantity', 'decrease-quantity', 'remove-item', 'edit-options'],
  setup(props, { emit }) {
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

    // 수량 조작
    const increaseQuantity = () => {
      emit('increase-quantity', props.item)
    }

    const decreaseQuantity = () => {
      emit('decrease-quantity', props.item)
    }

    // 아이템 삭제
    const removeItem = () => {
      emit('remove-item', props.item)
    }

    // 옵션 변경
    const editOptions = () => {
      emit('edit-options', props.item)
    }

    return {
      hasSelectedOptions,
      getSelectedOptionsText,
      increaseQuantity,
      decreaseQuantity,
      removeItem,
      editOptions,
    }
  },
}
</script>
