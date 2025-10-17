<template>
  <div class="bg-white rounded-lg border border-gray-200 shadow-sm p-4 mb-4">
    <div class="flex items-center justify-between">
      <h3 class="font-medium text-gray-800">결제수단</h3>
              <button
          @click="changePaymentMethod"
          class="text-blue-600 text-sm hover:bg-blue-50 px-2 py-1 rounded transition-colors duration-200"
        >
        변경
      </button>
    </div>
    
    <div class="flex items-center gap-3 mt-2 p-3 bg-gray-50 rounded-lg">
      <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
        <span class="text-blue-600 text-sm">💳</span>
      </div>
      <span class="text-gray-800">{{ selectedPaymentMethod }}</span>
    </div>
  </div>
</template>

<script>
import { useOrderStore } from '@/stores/customer/order'
import { storeToRefs } from 'pinia'

export default {
  name: 'PaymentMethod',
  setup() {
    const orderStore = useOrderStore()
    
    // store에서 필요한 상태들만 추출
    const { selectedPaymentMethod } = storeToRefs(orderStore)
    
    // store의 actions 사용
    const { changePaymentMethod } = orderStore

    const handleChangePaymentMethod = () => {
      // TODO: 결제수단 선택 모달
      alert('결제수단 변경 기능은 추후 구현 예정입니다.')
    }

    return {
      selectedPaymentMethod,
      changePaymentMethod: handleChangePaymentMethod,
    }
  },
}
</script>
