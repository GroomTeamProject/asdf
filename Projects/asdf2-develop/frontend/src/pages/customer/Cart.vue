<template>
  <!-- 헤더 배너 -->
  <div class="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-6">
    <div class="max-w-6xl mx-auto">
      <h1 class="text-2xl font-bold mb-2">장바구니</h1>
      <p class="text-blue-100">주문할 메뉴를 확인해보세요</p>
    </div>
  </div>

  <!-- 페이지 컨테이너 -->
  <CustomerContainer max-width="6xl" padding="4" custom-class="space-y-6">
    <!-- 장바구니 아이템 목록 섹션 -->
    <section>
      <CartItems
        v-if="cart.length > 0"
        :cartItems="cart"
        @update-quantity="updateQuantity"
      />
      <CartEmpty v-if="cart.length === 0" />
    </section>

    <!-- 주문 요약 및 결제 섹션 -->
    <section v-if="cart.length > 0">
      <CartSummary
        :totalPrice="totalPrice"
        @place-order="placeOrder"
      />
    </section>
  </CustomerContainer>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import CartEmpty from '@/components/customer/cart/CartEmpty.vue'
import CartItems from '@/components/customer/cart/CartItems.vue'
import CartSummary from '@/components/customer/cart/CartSummary.vue'
import CustomerContainer from '@/components/customer/CustomerContainer.vue'
import { cartService } from '@/services/customer/cartService'

export default {
  name: 'Cart',
  components: {
    CartEmpty,
    CartItems,
    CartSummary,
    CustomerContainer,
  },
  setup() {
    const router = useRouter()

    // cartService를 통해 장바구니 상태 가져오기
    const cartState = computed(() => cartService.getCartState())
    const cart = computed(() => cartState.value.items)
    const totalPrice = computed(() => cartState.value.totalPrice)

    // 메서드들
    const goBack = () => {
      router.push('/customer/stores')
    }

    const updateQuantity = (itemKey, newQuantity) => {
      const result = cartService.updateMenuQuantity(itemKey, newQuantity)
      if (!result.success) {
        console.error('수량 업데이트 실패:', result.message)
      }
    }

    const placeOrder = async () => {
      if (cart.value.length === 0) {
        alert('장바구니가 비어있습니다.')
        return
      }

      // 주문 페이지로 이동
      router.push('/customer/order')
    }

    return {
      cart,
      totalPrice,
      goBack,
      updateQuantity,
      placeOrder,
    }
  },
}
</script>
