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
      try {
        selectedStore.value = await customerApi.getStoreById(storeId.value)
        
        if (!selectedStore.value) {
          alert('음식점 정보를 찾을 수 없습니다.')
          router.push('/customer/stores')
          return
        }

        // 백엔드 응답에서 menuCategories를 menuItems로 변환
        if (selectedStore.value.menuCategories && selectedStore.value.menuCategories.length > 0) {
          // menuCategories에서 실제 메뉴 아이템들을 추출
          // 엔티티 구조: Store -> MenuCategory -> Menu
          menuItems.value = selectedStore.value.menuCategories.flatMap(category => 
            category.menus || []
          )
        } else {
          // menuCategories가 없으면 별도 API 호출
          menuItems.value = await customerApi.getMenusByStoreId(storeId.value)
        }
        
        console.log('가게 정보:', selectedStore.value)
        console.log('메뉴 아이템들:', menuItems.value)
      } catch (error) {
        console.error('데이터 로딩 실패:', error)
        alert('데이터를 불러오는데 실패했습니다.')
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
