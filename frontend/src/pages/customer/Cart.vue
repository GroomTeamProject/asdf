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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import CartEmpty from '@/components/customer/cart/CartEmpty.vue'
import CartItems from '@/components/customer/cart/CartItems.vue'
import CartSummary from '@/components/customer/cart/CartSummary.vue'

export default {
  name: 'Cart',
  components: {
    CartEmpty,
    CartItems,
    CartSummary,
  },
  setup() {
    const router = useRouter()

    // 반응형 데이터
    const cart = ref(JSON.parse(localStorage.getItem('cart') || '[]'))

    // 계산된 속성
    const totalPrice = computed(() => {
      return cart.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
    })

    // 메서드들
    const goBack = () => {
      router.push('/customer/stores')
    }

    const updateQuantity = (itemId, newQuantity) => {
      if (newQuantity <= 0) {
        // 아이템 제거
        cart.value = cart.value.filter((item) => item.id !== itemId)
      } else {
        // 수량 업데이트
        const item = cart.value.find((item) => item.id === itemId)
        if (item) {
          item.quantity = newQuantity
        }
      }

      localStorage.setItem('cart', JSON.stringify(cart.value))
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

        localStorage.removeItem('cart')
        router.push('/customer/stores')
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
