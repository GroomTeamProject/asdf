<template>
  <div class="min-h-screen bg-gray-100">
    <main class="max-w-6xl mx-auto p-4 pb-20">
      <!-- 음식점 정보   -->
      <StoreInfo :store="selectedStore" />

      <!-- 메뉴 -->
      <StoreMenu :menuItems="menuItems" :storeId="storeId" @add-to-cart="addToCart" />
    </main>

    <!-- 장바구니/주문  버튼 -->
    <StoreCartButton :cart="cart" />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import StoreInfo from '@/components/customer/storeDetail/StoreInfo.vue'
import StoreMenu from '@/components/customer/storeDetail/StoreMenu.vue'
import StoreCartButton from '@/components/customer/storeDetail/StoreCartButton.vue'
import { mocks } from './mock'

export default {
  name: 'StoreDetail',
  components: {
    StoreInfo,
    StoreMenu,
    StoreCartButton,
  },
  setup() {
    const router = useRouter()
    const route = useRoute()

    // 반응형 데이터
    const selectedStore = ref(null)
    const menuItems = ref(mocks.menuItems)

    // 장바구니 데이터
    const cart = ref(JSON.parse(localStorage.getItem('cart') || '[]'))

    const addToCart = (itemId) => {
      const item = menuItems.value.find((m) => m.id === itemId)
      if (!item) return

      const existingItem = cart.value.find((cartItem) => cartItem.id === itemId)

      if (existingItem) {
        existingItem.quantity += 1
      } else {
        cart.value.push({ ...item, quantity: 1 })
      }

      // localStorage 업데이트
      localStorage.setItem('cart', JSON.stringify(cart.value))
    }

    // 페이지 초기화
    onMounted(() => {
      const storeId = route.params.id
      selectedStore.value = mocks.stores.find((store) => store.id === storeId)

      if (!selectedStore.value) {
        alert('음식점 정보를 찾을 수 없습니다.')
        router.push('/customer/stores')
        return
      }
    })

    return {
      selectedStore,
      menuItems,
      cart,
      addToCart,
      storeId: route.params.id,
    }
  },
}
</script>
