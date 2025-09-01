<template>
  <div v-show="cart.length > 0" class="cart-button">
    <button
      @click="goToCart"
      class="w-full h-12 bg-gray-600 text-white border-2 border-gray-800 hover:bg-gray-700 rounded-md transition-colors"
    >
      <span>장바구니 보기 ({{ totalQuantity }}개) · {{ totalPrice.toLocaleString() }}원</span>
    </button>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'StoreCartButton',
  props: {
    cart: {
      type: Array,
      required: true,
    },
  },
  setup(props) {
    const router = useRouter()

    // 계산된 속성
    const totalQuantity = computed(() => {
      return props.cart.reduce((sum, item) => sum + item.quantity, 0)
    })

    const totalPrice = computed(() => {
      return props.cart.reduce((sum, item) => sum + item.price * item.quantity, 0)
    })

    const goToCart = () => {
      router.push('/customer/cart')
    }

    return {
      totalQuantity,
      totalPrice,
      goToCart,
    }
  },
}
</script>
