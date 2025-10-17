<template>
  <!-- 헤더 배너 -->
  <div class="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-6">
    <div class="max-w-6xl mx-auto">
      <h1 class="text-2xl font-bold mb-2">{{ selectedStore?.name || '가게 정보' }}</h1>
      <p class="text-blue-100">맛있는 메뉴를 확인하고 주문해보세요</p>
    </div>
  </div>

  <!-- 페이지 컨테이너 -->
  <CustomerContainer max-width="6xl" padding="4" custom-class="pb-20">
    <!-- 음식점 정보 섹션 -->
    <section class="mb-6">
      <StoreInfo :store="selectedStore" />
    </section>

    <!-- 메뉴 섹션 -->
    <section>
      <StoreMenu :menuItems="menuItems" :storeId="storeId" />
    </section>
  </CustomerContainer>
  <StoreCartButton />
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import StoreInfo from '@/components/customer/storeDetail/StoreInfo.vue'
import StoreMenuCard from '@/components/customer/storeDetail/StoreMenuCard.vue'
import CustomerContainer from '@/components/customer/CustomerContainer.vue'
import { customerApi } from '@/api/customer/customerApi'

export default {
  name: 'StoreDetail',
  components: {
    StoreInfo,
    StoreMenu: StoreMenuCard,
    CustomerContainer,
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const selectedStore = ref(null)
    const menuItems = ref([])
    const storeId = ref(route.params.storeId)

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
      storeId: route.params.storeId,
    }
  },
}
</script>
