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
import { userApi } from '@/api/customer/userApi'
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

    // 기본 배송지 및 사용자 정보 불러오기
    const loadDefaultAddressAndUserInfo = async () => {
      try {
        const userId = localStorage.getItem('userId')
        if (userId) {
          // 사용자 정보와 배송지 정보를 병렬로 불러오기
          const [userProfile, addresses] = await Promise.all([
            userApi.getUserProfile(userId),
            userApi.getUserAddresses(userId)
          ])
          
          const defaultAddress = addresses?.find(addr => addr.isDefault)
          const userPhone = userProfile?.phone || ''
          
          if (defaultAddress) {
            // 기본 배송지가 있으면 자동으로 설정
            orderStore.initializeDeliveryInfo(
              defaultAddress.address,
              userPhone,
              defaultAddress.detailAddress
            )
          } else {
            // 기본 배송지가 없으면 사용자 전화번호만 설정
            orderStore.initializeDeliveryInfo('', userPhone, '')
          }
        }
      } catch (error) {
        console.error('사용자 정보 및 기본 배송지 불러오기 실패:', error)
        // 에러가 발생해도 빈 상태로 유지
        orderStore.initializeDeliveryInfo('', '', '')
      }
    }

    // 페이지 진입 시 장바구니 확인 및 초기화
    onMounted(async () => {
      if (cartStore.items.length === 0) {
        alert('장바구니가 비어있습니다.')
        router.push('/customer/stores')
        return
      }

      // 주문 제출 상태 초기화 (이전에 실패한 주문이 있을 수 있음)
      orderStore.setSubmitting(false)

      // 기본 배송지 및 사용자 정보 불러오기
      await loadDefaultAddressAndUserInfo()
    })

    return {}
  },
}
</script>
