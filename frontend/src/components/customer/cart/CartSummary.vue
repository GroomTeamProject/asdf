<template>
  <div class="border-2 border-gray-400 bg-white rounded-lg mt-6">
    <div class="p-4">
      <div class="flex justify-between items-center mb-4">
        <span class="text-gray-800">총 주문 금액</span>
        <span class="text-xl text-gray-800">{{ totalPrice.toLocaleString() }}원</span>
      </div>
      <button
        @click="placeOrder"
        class="w-full h-12 bg-gray-600 text-white border-2 border-gray-800 hover:bg-gray-700 rounded-md transition-colors"
      >
        결제하기
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CartSummary',
  props: {
    totalPrice: {
      type: Number,
      required: true,
    },
  },
  emits: ['place-order'],
  setup(props, { emit }) {
    const placeOrder = () => {
      emit('place-order')
    }

    return {
      placeOrder,
    }
  },
}
</script>
