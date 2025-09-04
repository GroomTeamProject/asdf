<template>
  <div class="sticky bottom-0 bg-white border-t border-gray-200 p-4">
    <button
      @click="handlePayment"
      :disabled="!canSubmitOrder"
      :class="[
        'w-full h-14 text-lg font-semibold rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2',
        canSubmitOrder
          ? 'bg-gradient-to-r from-blue-600 to-blue-700 text-white hover:shadow-lg cursor-pointer hover:from-blue-700 hover:to-blue-800'
          : 'bg-gray-300 text-gray-500 cursor-not-allowed'
      ]"
    >
      {{ submitButtonText }}
    </button>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useOrderStore } from '@/stores/customer/order'
import { storeToRefs } from 'pinia'

export default {
  name: 'PaymentButton',
  props: {
    canSubmitOrder: {
      type: Boolean,
      required: true,
    },
    finalAmount: {
      type: Number,
      required: true,
    },
  },
  emits: ['payment'],
  setup(props, { emit }) {
    const orderStore = useOrderStore()
    
    // store에서 필요한 상태들만 추출
    const { isSubmitting } = storeToRefs(orderStore)
    
    // 제출 버튼 텍스트
    const submitButtonText = computed(() => {
      if (isSubmitting.value) return '주문 처리 중...'
      return `${props.finalAmount.toLocaleString()}원 결제하기`
    })

    const handlePayment = () => {
      if (props.canSubmitOrder) {
        emit('payment')
      }
    }

    return {
      isSubmitting,
      submitButtonText,
      handlePayment,
    }
  },
}
</script>
