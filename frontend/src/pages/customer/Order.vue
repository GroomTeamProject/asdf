<template>
  <div class="max-w-2xl mx-auto bg-gray-100 min-h-screen">
    <!-- 배달 정보 -->
    <DeliveryInfo />

    <!-- 선택한 메뉴 -->
    <OrderSummary />

    <!-- 주문금액 -->
    <OrderPricing />

    <!-- 결제수단 -->
    <PaymentMethod />

    <!-- 요청사항 -->
    <OrderMemo />

    <!-- 결제 버튼 -->
    <PaymentButton />
  </div>
</template>

<script>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/customer/order'
import { useCartStore } from '@/stores/customer/cart'
import DeliveryInfo from '@/components/customer/order/DeliveryInfo.vue'
import OrderSummary from '@/components/customer/order/OrderSummary.vue'
import OrderPricing from '@/components/customer/order/OrderPricing.vue'
import OrderMemo from '@/components/customer/order/OrderMemo.vue'
import PaymentMethod from '@/components/customer/order/PaymentMethod.vue'
import PaymentButton from '@/components/customer/order/PaymentButton.vue'

export default {
  name: 'Order',
  components: {
    DeliveryInfo,
    OrderSummary,
    OrderPricing,
    OrderMemo,
    PaymentMethod,
    PaymentButton,
  },
  setup() {
    const router = useRouter()
    const orderStore = useOrderStore()
    const cartStore = useCartStore()

    // 페이지 진입 시 장바구니 확인 및 초기화
    onMounted(() => {
      if (cartStore.items.length === 0) {
        alert('장바구니가 비어있습니다.')
        router.push('/customer/stores')
        return
      }

      // 임시 배달 정보 설정
      // TODO: 실제로는 사용자 정보에서 가져와야 함
      orderStore.initializeDeliveryInfo(
        '서울시 강남구 테헤란로 123',
        '010-1234-5678'
      )
    })

    return {}
  },
}
</script>
