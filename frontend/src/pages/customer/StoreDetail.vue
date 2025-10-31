<template>
  <!-- 헤더 배너 -->
  <HeaderBanner :title="selectedStore?.name || ''" />

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
import HeaderBanner from '@/components/common/HeaderBanner.vue'
import { customerApi } from '@/api/customer/customerApi'

export default {
  name: 'StoreDetail',
  components: {
    StoreInfo,
    StoreMenu: StoreMenuCard,
    CustomerContainer,
    HeaderBanner,
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

        // 메뉴 목록 조회
        menuItems.value = await customerApi.getMenusByStoreId(storeId.value)
        
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
