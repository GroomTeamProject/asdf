<template>
  <div class="min-h-screen bg-gray-100">
    <main class="max-w-6xl mx-auto p-4 pb-20">
      <!-- 음식점 정보   -->
      <StoreInfo :store="selectedStore" />

      <!-- 메뉴 -->
      <StoreMenu :menuItems="menuItems" :storeId="storeId" />
    </main>

    <!-- 장바구니/주문  버튼 -->
    <StoreCartButton />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import StoreInfo from '@/components/customer/storeDetail/StoreInfo.vue'
import StoreMenuCard from '@/components/customer/storeDetail/StoreMenuCard.vue'
import StoreCartButton from '@/components/customer/storeDetail/StoreCartButton.vue'
import { customerApi } from '@/apis/customerApi'

export default {
  name: 'StoreDetail',
  components: {
    StoreInfo,
    StoreMenu: StoreMenuCard,
    StoreCartButton,
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const selectedStore = ref(null)
    const menuItems = ref([])
    const storeId = ref(route.params.id)

    onMounted(async () => {
      selectedStore.value = await customerApi.getStoreById(storeId.value)
      menuItems.value = await customerApi.getMenusByStoreId(storeId.value)

      if (!selectedStore.value) {
        alert('음식점 정보를 찾을 수 없습니다.')
        router.push('/customer/stores')
        return
      }
    })

    return {
      selectedStore,
      menuItems,
      storeId: route.params.id,
    }
  },
}
</script>
