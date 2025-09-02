<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 장바구니 내용 -->
    <main class="max-w-6xl mx-auto p-4">
      <!-- 장바구니 아이템 목록 -->
      <CartItems v-if="cart.length > 0" :cartItems="cart" @update-quantity="updateQuantity" />
      <CartEmpty v-if="cart.length === 0" />

      <!-- 주문 요약 및 결제 -->
      <CartSummary v-if="cart.length > 0" :totalPrice="totalPrice" @place-order="placeOrder" />
    </main>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import CartEmpty from '@/components/customer/cart/CartEmpty.vue'
import CartItems from '@/components/customer/cart/CartItems.vue'
import CartSummary from '@/components/customer/cart/CartSummary.vue'
import { cartService } from '@/services/customer/cartService'

export default {
  name: 'Cart',
  components: {
    CartEmpty,
    CartItems,
    CartSummary,
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

    const placeOrder = () => {
      if (cart.value.length === 0) {
        alert('장바구니가 비어있습니다.')
        return
      }

      const confirmed = confirm(`총 ${totalPrice.value.toLocaleString()}원을 주문하시겠습니까?`)

      if (confirmed) {
        // 주문 API 호출 & 주문 완료 처리
        alert('주문이 완료되었습니다!')

        const result = cartService.clearCart()
        if (result.success) {
          router.push('/customer/stores')
        }
      }
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
