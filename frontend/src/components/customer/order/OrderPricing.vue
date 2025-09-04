<template>
  <div class="bg-white border-b border-gray-200 p-4">
    <h3 class="font-medium text-gray-800 mb-3">주문금액</h3>
    
    <div class="space-y-2">
      <div class="flex justify-between text-sm">
        <span class="text-gray-600">주문금액</span>
        <span class="text-gray-800">{{ totalAmount.toLocaleString() }}원</span>
      </div>
      
      <div class="flex justify-between text-sm">
        <span class="text-gray-600">배달비</span>
        <span class="text-gray-800">{{ deliveryFee.toLocaleString() }}원</span>
      </div>
      
      <div class="flex justify-between text-sm">
        <span class="text-gray-600">할인</span>
        <span class="text-red-600">-{{ discountAmount.toLocaleString() }}원</span>
      </div>
      
      <div class="border-t border-gray-200 pt-2 mt-2">
        <div class="flex justify-between font-medium">
          <span class="text-gray-800">결제금액</span>
          <span class="text-lg text-blue-600">{{ finalAmount.toLocaleString() }}원</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OrderPricing',
  props: {
    totalAmount: {
      type: Number,
      required: true,
    },
    deliveryFee: {
      type: Number,
      required: true,
    },
    discountAmount: {
      type: Number,
      required: true,
    },
    finalAmount: {
      type: Number,
      required: true,
    },
  },
}
</script>
