<template>
  <div class="bg-white rounded-lg border border-gray-200 shadow-sm mt-6">
    <div class="p-4">
      <div class="flex justify-between items-center mb-4">
        <span class="text-gray-800">총 주문 금액</span>
        <span class="text-xl text-gray-800">{{ totalPrice.toLocaleString() }}원</span>
      </div>
      <button
        @click="placeOrder"
        class="w-full h-12 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
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
