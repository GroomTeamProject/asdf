<template>
  <div class="sticky bottom-0 bg-white border-t border-gray-200 p-4">
    <button
      @click="handlePayment"
      :disabled="!canSubmitOrder"
      :class="[
        'w-full h-14 text-lg font-semibold rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2',
        canSubmitOrder
          ? 'bg-gradient-to-r from-blue-600 to-blue-700 text-white hover:shadow-lg cursor-pointer hover:from-blue-700 hover:to-blue-800'
          : 'bg-gray-300 text-gray-500 cursor-not-allowed',
      ]"
    >
      {{ submitButtonText }}
    </button>

    <!-- 결제 팝업 -->
    <div
      v-if="showPaymentPopup"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click.self="showPaymentPopup = false"
    >
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4 max-h-[80vh] overflow-y-auto">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-semibold">결제하기</h3>
          <button @click="showPaymentPopup = false" class="text-gray-500 hover:text-gray-700">✕</button>
        </div>

        <!-- PaymentWidget 컴포넌트 -->
        <PaymentWidget />
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/customer/order'
import { useCartStore } from '@/stores/customer/cart'
import { orderService } from '@/services/customer/orderService'
import { storeToRefs } from 'pinia'
import PaymentWidget from '@/pages/payment/PaymentWidget.vue'

export default {
  name: 'PaymentButton',
  components: {
    PaymentWidget,
  },
  setup() {
    const router = useRouter()
    const orderStore = useOrderStore()
    const cartStore = useCartStore()

    // 컴포넌트 마운트 시 주문 제출 상태 초기화
    orderStore.setSubmitting(false)

    // store에서 필요한 상태들만 추출
    const { deliveryAddress, deliveryDetailAddress, phoneNumber, selectedPaymentMethod, isSubmitting } = storeToRefs(orderStore)

    // 장바구니 상태
    const orderItems = computed(() => cartStore.items)
    const totalAmount = computed(() => cartStore.totalPrice)

    // 배달비, 할인, 최종 금액 계산
    const deliveryFee = computed(() => {
      const storeInfo = orderItems.value[0].storeInfo
      const storeDeliveryFee = storeInfo?.deliveryFee

      return storeDeliveryFee
    })

    const discountAmount = computed(() => 0)

    const finalAmount = computed(() => {
      return totalAmount.value + deliveryFee.value - discountAmount.value
    })

    // 주문 제출 가능 여부
    const canSubmitOrder = computed(() => {
      return deliveryAddress.value.trim() && phoneNumber.value.trim() && orderItems.value.length > 0 && !isSubmitting.value
    })

    // 제출 버튼 텍스트
    const submitButtonText = computed(() => {
      if (isSubmitting.value) return '주문 처리 중...'
      return `${finalAmount.value.toLocaleString()}원 결제하기`
    })

    // 결제 팝업 상태
    const showPaymentPopup = ref(false)

    // 결제 팝업 띄우기
    const handlePayment = async () => {
      if (!canSubmitOrder.value) return

      console.log('orderItems:', orderItems.value) // 디버깅용
      console.log('cartStore.items:', cartStore.items) // 디버깅용

      const confirmed = confirm(`총 ${finalAmount.value.toLocaleString()}원을 주문하시겠습니까?`)
      if (!confirmed) return

      // Pinia store의 주문 정보를 활용하여 orderInfo 구성
      const orderInfo = {
        orderId: Date.now(), // 임시 ID
        orderIdString: `order_${Date.now()}`,
        totalAmount: finalAmount.value,
        customerName: '고객', // 실제로는 사용자 정보에서 가져와야 함
        phoneNumber: phoneNumber.value,
        deliveryAddress: deliveryAddress.value,
        deliveryDetailAddress: deliveryDetailAddress.value,
        orderMemo: orderStore.finalOrderMemo,
        paymentMethod: selectedPaymentMethod.value,
        items: orderItems.value,
      }

      localStorage.setItem('orderInfo', JSON.stringify(orderInfo))

      await orderService.submitOrder({
        userId: localStorage.getItem('userId'),
        storeId: orderItems.value[0].storeId,
        deliveryAddress: deliveryAddress.value,
        deliveryDetailAddress: deliveryDetailAddress.value,
        phone: phoneNumber.value,
        orderMemo: orderStore.finalOrderMemo,
        orderItems: (cartStore.items || []).map((item) => ({
          menuId: item.id,
          quantity: item.quantity,
          options: Object.entries(item.selectedOptions || {}).map(([optionName, optionItemName]) => ({
            optionName,
            optionItemName,
            additionalPrice: 0,
          })),
        })),
      })

      // 로컬 환경에서는 결제 팝업 띄우지 않고 바로 완료 페이지로
      if (window.location.hostname === 'localhost') {
        router.push('/customer/order-complete')
      } else {
        showPaymentPopup.value = true
      }
    }

    return {
      canSubmitOrder,
      finalAmount,
      submitButtonText,
      handlePayment,
      showPaymentPopup,
    }
  },
}
</script>
