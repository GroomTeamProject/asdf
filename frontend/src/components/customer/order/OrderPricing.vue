<template>
  <div class="bg-white rounded-lg border border-gray-200 shadow-sm p-4 mb-4">
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
import { computed } from 'vue'
import { useCartStore } from '@/stores/customer/cart'

export default {
  name: 'OrderPricing',
  setup() {
    const cartStore = useCartStore()

    // 장바구니에서 총 금액 가져오기
    const totalAmount = computed(() => cartStore.totalPrice)

    // 배달비 계산
    const deliveryFee = computed(() => {
      if (cartStore.items.length === 0) return 0
      
      const storeInfo = cartStore.items[0]?.storeInfo
      const storeDeliveryFee = storeInfo?.deliveryFee || 0
      
      return storeDeliveryFee
    })

    // 할인 금액 계산
    const discountAmount = computed(() => {
      // TODO: 할인 정책 적용
      return 0
    })

    // 최종 금액 계산
    const finalAmount = computed(() => {
      return totalAmount.value + deliveryFee.value - discountAmount.value
    })

    return {
      totalAmount,
      deliveryFee,
      discountAmount,
      finalAmount,
    }
  },
}
</script>
