<template>
  <Transition
    enter-active-class="transition-all duration-300 ease-out"
    enter-from-class="transform translate-y-full opacity-0"
    enter-to-class="transform translate-y-0 opacity-100"
    leave-active-class="transition-all duration-300 ease-in"
    leave-from-class="transform translate-y-0 opacity-100"
    leave-to-class="transform translate-y-full opacity-0"
  >
    <div
      v-if="shouldShow()"
      class="fixed bottom-0 left-0 right-0 z-50 bg-white border-t border-gray-200 shadow-lg"
    >
      <div class="max-w-2xl mx-auto p-4">
        <button
          @click="goToOrder"
          class="w-full h-14 bg-gradient-to-r from-blue-600 to-blue-700 text-white font-semibold rounded-lg hover:shadow-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 flex items-center justify-center gap-3"
        >
          <!-- 장바구니 아이콘 -->
          <div class="relative">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4m0 0L7 13m0 0l-2.5 5M7 13l2.5 5m6-5v6a2 2 0 01-2 2H9a2 2 0 01-2-2v-6m8 0V9a2 2 0 00-2-2H9a2 2 0 00-2 2v4.01"></path>
            </svg>
            <!-- 아이템 개수 배지 -->
            <span
              v-if="cartStore.totalItems > 0"
              class="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center font-medium"
            >
              {{ cartStore.totalItems }}
            </span>
          </div>
          
          <!-- 텍스트 -->
          <span>{{ cartStore.totalItems }}개 메뉴 주문하기</span>
          
          <!-- 가격 -->
          <span class="font-bold text-lg">
            {{ cartStore.totalPrice.toLocaleString() }}원
          </span>
        </button>
      </div>
    </div>
  </Transition>
</template>

<script>
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@/stores/customer/cart'

export default {
  name: 'FloatingCartButton',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const cartStore = useCartStore()

    // 주문 페이지에서는 플로팅 버튼 숨기기
    const shouldShow = () => {
      return cartStore.hasItems && route.path !== '/customer/order'
    }

    const goToOrder = () => {
      router.push('/customer/order')
    }

    return {
      cartStore,
      goToOrder,
      shouldShow,
    }
  },
}
</script>

<style scoped>
/* 추가 스타일이 필요한 경우 여기에 작성 */
</style>
