<template>
  <div class="bg-white border-b border-gray-200 p-4">
    <h3 class="font-medium text-gray-800 mb-3">요청사항</h3>
    
    <div class="space-y-3">
      <!-- 빠른 선택 버튼들 -->
      <div class="flex flex-wrap gap-2">
        <button
          v-for="memo in quickMemos"
          :key="memo"
          @click="selectQuickMemo(memo)"
          :class="[
            'px-3 py-2 text-sm rounded-lg border transition-colors',
            selectedMemo === memo
              ? 'bg-blue-600 text-white border-blue-600'
              : 'bg-white text-gray-600 border-gray-300 hover:border-blue-300'
          ]"
        >
          {{ memo }}
        </button>
      </div>
      
      <!-- 직접 입력 -->
      <textarea
        v-model="customMemo"
        placeholder="직접 입력하거나 위 버튼을 선택해주세요"
        class="w-full h-20 p-3 border border-gray-300 rounded-lg resize-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
      ></textarea>
    </div>
  </div>
</template>

<script>
import { useOrderStore } from '@/stores/customer/order'
import { storeToRefs } from 'pinia'

export default {
  name: 'OrderMemo',
  setup() {
    const orderStore = useOrderStore()
    
    // store에서 필요한 상태들만 추출
    const { customMemo, selectedMemo, quickMemos } = storeToRefs(orderStore)
    
    // store의 actions 사용
    const { selectQuickMemo } = orderStore

    return {
      customMemo,
      selectedMemo,
      quickMemos,
      selectQuickMemo,
    }
  },
}
</script>
