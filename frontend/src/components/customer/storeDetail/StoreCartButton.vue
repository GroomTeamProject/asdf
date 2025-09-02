<template>
  <div v-show="hasItems" class="cart-button">
    <button
      @click="goToCart"
      class="w-full h-12 bg-gray-600 text-white border-2 border-gray-800 hover:bg-gray-700 rounded-md transition-colors"
    >
      <span>장바구니 보기 ({{ totalItems }}개) · {{ totalPrice.toLocaleString() }}원</span>
    </button>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { cartService } from '@/services/customer/cartService'

export default {
  name: 'StoreCartButton',
  setup() {
    const router = useRouter()

    const cartState = computed(() => cartService.getCartState())
    
    const hasItems = computed(() => cartState.value.hasItems)
    const totalItems = computed(() => cartState.value.totalItems)
    const totalPrice = computed(() => cartState.value.totalPrice)

    const goToCart = () => {
      router.push('/customer/cart')
    }

    return {
      hasItems,
      totalItems,
      totalPrice,
      goToCart,
    }
  },
}
</script>
