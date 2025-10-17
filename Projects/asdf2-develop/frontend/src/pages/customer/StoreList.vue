<template>
  <!-- 헤더 배너 -->
  <div class="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-6">
    <div class="max-w-6xl mx-auto">
      <h1 class="text-2xl font-bold mb-2">맛있는 가게 둘러보기</h1>
      <p class="text-blue-100">다양한 음식점을 확인하고 주문해보세요</p>
    </div>
  </div>

  <!-- 페이지 컨테이너 -->
  <CustomerContainer max-width="6xl" padding="4">
    <!-- 검색바 섹션 -->
    <section class="mb-6">
      <StoreSearchBar v-model="searchQuery" />
    </section>

    <!-- 카테고리 섹션 -->
    <section class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">카테고리</h2>
      <div class="grid grid-cols-4 gap-4">
        <button
          @click="filterByCategory('피자')"
          class="p-4 bg-red-50 border border-red-200 rounded-lg hover:bg-red-100 transition-colors"
        >
          <div class="text-2xl mb-2">🍕</div>
          <div class="text-sm font-medium text-red-700">피자</div>
        </button>
        <button
          @click="filterByCategory('치킨')"
          class="p-4 bg-yellow-50 border border-yellow-200 rounded-lg hover:bg-yellow-100 transition-colors"
        >
          <div class="text-2xl mb-2">🍗</div>
          <div class="text-sm font-medium text-yellow-700">치킨</div>
        </button>
        <button
          @click="filterByCategory('한식')"
          class="p-4 bg-green-50 border border-green-200 rounded-lg hover:bg-green-100 transition-colors"
        >
          <div class="text-2xl mb-2">🍜</div>
          <div class="text-sm font-medium text-green-700">한식</div>
        </button>
        <button
          @click="filterByCategory('일식')"
          class="p-4 bg-blue-50 border border-blue-200 rounded-lg hover:bg-blue-100 transition-colors"
        >
          <div class="text-2xl mb-2">🍱</div>
          <div class="text-sm font-medium text-blue-700">일식</div>
        </button>
      </div>
    </section>

    <!-- 음식점 리스트 섹션 -->
    <section>
      <h2 class="text-xl font-bold text-gray-900 mb-4">인기 음식점</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <StoreCard v-for="store in filteredStores" :key="store.id" :store="store" />
      </div>
      
      <!-- 가게가 없는 경우 -->
      <div v-if="filteredStores.length === 0" class="text-center py-12">
        <div class="text-gray-400 text-6xl mb-4">🍽️</div>
        <h3 class="text-lg font-medium text-gray-900 mb-2">가게가 없습니다</h3>
        <p class="text-gray-600">다른 검색어로 시도해보세요</p>
      </div>
    </section>
  </CustomerContainer>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import StoreSearchBar from '@/components/customer/storeList/StoreSearchBar.vue'
import StoreCard from '@/components/customer/storeList/StoreCard.vue'
import CustomerContainer from '@/components/customer/CustomerContainer.vue'
import { customerApi } from '@/api/customer/customerApi.js'

export default {
  name: 'Stores',
  components: {
    StoreSearchBar,
    StoreCard,
    CustomerContainer,
  },
  setup() {
    const searchQuery = ref('')
    const stores = ref([])
    const selectedCategory = ref('')

    // 필터링된 가게 목록
    const filteredStores = computed(() => {
      let filtered = stores.value

      // 검색어 필터링
      if (searchQuery.value) {
        filtered = filtered.filter(store => 
          store.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          store.description.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }

      // 카테고리 필터링
      if (selectedCategory.value) {
        filtered = filtered.filter(store => 
          store.description.toLowerCase().includes(selectedCategory.value.toLowerCase()) ||
          store.name.toLowerCase().includes(selectedCategory.value.toLowerCase())
        )
      }

      return filtered
    })

    // 카테고리 필터링
    const filterByCategory = (category) => {
      selectedCategory.value = selectedCategory.value === category ? '' : category
    }

    onMounted(async () => {
      try {
        stores.value = await customerApi.getStores()
      } catch (error) {
        console.error('데이터 로딩 실패:', error)
        alert('데이터를 불러오는데 실패했습니다.')
      }
    })

    return {
      searchQuery,
      stores,
      filteredStores,
      selectedCategory,
      filterByCategory,
    }
  },
}
</script>
