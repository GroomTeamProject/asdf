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
import { useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/customer/order'
import { useCartStore } from '@/stores/customer/cart'
import { orderService } from '@/services/customer/orderService'
import { storeToRefs } from 'pinia'

export default {
  name: 'PaymentButton',
  setup() {
    const router = useRouter()
    const orderStore = useOrderStore()
    const cartStore = useCartStore()
    
    // store에서 필요한 상태들만 추출
    const {
      deliveryAddress,
      deliveryDetailAddress,
      phoneNumber,
      selectedPaymentMethod,
      isSubmitting,
    } = storeToRefs(orderStore)

    // 장바구니 상태
    const orderItems = computed(() => cartStore.items)
    const totalAmount = computed(() => cartStore.totalPrice)

    // 배달비, 할인, 최종 금액 계산
    const deliveryFee = computed(() => {
      return totalAmount.value >= 15000 ? 0 : 3000
    })

    const discountAmount = computed(() => 0)

    const finalAmount = computed(() => {
      return totalAmount.value + deliveryFee.value - discountAmount.value
    })

    // 주문 제출 가능 여부
    const canSubmitOrder = computed(() => {
      return (
        deliveryAddress.value.trim() &&
        phoneNumber.value.trim() &&
        orderItems.value.length > 0 &&
        !isSubmitting.value
      )
    })
    
    // 제출 버튼 텍스트
    const submitButtonText = computed(() => {
      if (isSubmitting.value) return '주문 처리 중...'
      return `${finalAmount.value.toLocaleString()}원 결제하기`
    })

    // 주문 제출
    const handlePayment = async () => {
      if (!canSubmitOrder.value) return

      const confirmed = confirm(
        `총 ${finalAmount.value.toLocaleString()}원을 주문하시겠습니까?`
      )
      if (!confirmed) return

      orderStore.setSubmitting(true)

      try {
        // 주문 데이터 구성
        const orderData = orderService.buildOrderData(
          orderItems.value,
          totalAmount.value,
          deliveryFee.value,
          discountAmount.value,
          finalAmount.value,
          orderStore.finalOrderMemo,
          deliveryAddress.value,
          deliveryDetailAddress.value,
          phoneNumber.value,
          selectedPaymentMethod.value
        )

        // 주문 제출
        const result = await orderService.submitOrder(orderData)

        if (result.success) {
          alert(result.message)

          // 장바구니 비우기
          orderService.clearCartAfterOrder()

          // 주문 완료 페이지로 이동
          router.push({
            path: '/customer/order-complete',
            query: {
              orderNumber: result.orderNumber,
              totalAmount: finalAmount.value,
              storeName: orderItems.value[0]?.storeName || '알 수 없는 가게',
              deliveryAddress: deliveryAddress.value,
              phoneNumber: phoneNumber.value,
            },
          })
        }
      } catch (error) {
        console.error('주문 제출 실패:', error)
        
        // 에러 메시지 표시
        const errorMessage = error.message || '주문 처리 중 오류가 발생했습니다. 다시 시도해주세요.'
        alert(errorMessage)
      } finally {
        orderStore.setSubmitting(false)
      }
    }

    return {
      canSubmitOrder,
      finalAmount,
      submitButtonText,
      handlePayment,
    }
  },
}
</script>
