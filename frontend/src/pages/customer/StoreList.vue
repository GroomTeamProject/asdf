<template>
  <!-- 헤더 배너 -->
  <HeaderBanner title="가게 둘러보기" description="다양한 음식점을 확인하고 주문해보세요" />

  <!-- 카테고리 섹션 -->
  <section class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
    <h2 class="text-lg font-semibold text-gray-900 mb-4">카테고리</h2>
    <div class="grid grid-cols-4 gap-4">
      <button
        @click="filterByCategory('KOREAN')"
        class="p-4 bg-green-50 border border-green-200 rounded-lg hover:bg-green-100 transition-colors"
        :class="{ 'bg-green-100 border-green-300': selectedCategory === 'KOREAN' }"
      >
        <div class="text-2xl mb-2">🍜</div>
        <div class="text-sm font-medium text-green-700">한식</div>
      </button>
      <button
        @click="filterByCategory('CHINESE')"
        class="p-4 bg-red-50 border border-red-200 rounded-lg hover:bg-red-100 transition-colors"
        :class="{ 'bg-red-100 border-red-300': selectedCategory === 'CHINESE' }"
      >
        <div class="text-2xl mb-2">🥟</div>
        <div class="text-sm font-medium text-red-700">중식</div>
      </button>
      <button
        @click="filterByCategory('WESTERN')"
        class="p-4 bg-purple-50 border border-purple-200 rounded-lg hover:bg-purple-100 transition-colors"
        :class="{ 'bg-purple-100 border-purple-300': selectedCategory === 'WESTERN' }"
      >
        <div class="text-2xl mb-2">🍝</div>
        <div class="text-sm font-medium text-purple-700">양식</div>
      </button>
      <button
        @click="filterByCategory('JAPANESE')"
        class="p-4 bg-blue-50 border border-blue-200 rounded-lg hover:bg-blue-100 transition-colors"
        :class="{ 'bg-blue-100 border-blue-300': selectedCategory === 'JAPANESE' }"
      >
        <div class="text-2xl mb-2">🍱</div>
        <div class="text-sm font-medium text-blue-700">일식</div>
      </button>
      <button
        @click="filterByCategory('FAST_FOOD')"
        class="p-4 bg-orange-50 border border-orange-200 rounded-lg hover:bg-orange-100 transition-colors"
        :class="{ 'bg-orange-100 border-orange-300': selectedCategory === 'FAST_FOOD' }"
      >
        <div class="text-2xl mb-2">🍔</div>
        <div class="text-sm font-medium text-orange-700">패스트푸드</div>
      </button>
      <button
        @click="filterByCategory('CHICKEN')"
        class="p-4 bg-yellow-50 border border-yellow-200 rounded-lg hover:bg-yellow-100 transition-colors"
        :class="{ 'bg-yellow-100 border-yellow-300': selectedCategory === 'CHICKEN' }"
      >
        <div class="text-2xl mb-2">🍗</div>
        <div class="text-sm font-medium text-yellow-700">치킨</div>
      </button>
      <button
        @click="filterByCategory('PIZZA')"
        class="p-4 bg-red-50 border border-red-200 rounded-lg hover:bg-red-100 transition-colors"
        :class="{ 'bg-red-100 border-red-300': selectedCategory === 'PIZZA' }"
      >
        <div class="text-2xl mb-2">🍕</div>
        <div class="text-sm font-medium text-red-700">피자</div>
      </button>
      <button
        @click="filterByCategory('DESSERT')"
        class="p-4 bg-pink-50 border border-pink-200 rounded-lg hover:bg-pink-100 transition-colors"
        :class="{ 'bg-pink-100 border-pink-300': selectedCategory === 'DESSERT' }"
      >
        <div class="text-2xl mb-2">🍰</div>
        <div class="text-sm font-medium text-pink-700">디저트</div>
      </button>
    </div>
  </section>

  <!-- 페이지 컨테이너 -->
  <CustomerContainer max-width="6xl" padding="4">
    <!-- 검색바 섹션 -->
    <section class="mb-6">
      <StoreSearchBar v-model="searchQuery" />
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
import { useRoute } from 'vue-router'
import StoreSearchBar from '@/components/customer/storeList/StoreSearchBar.vue'
import StoreCard from '@/components/customer/storeList/StoreCard.vue'
import CustomerContainer from '@/components/customer/CustomerContainer.vue'
import HeaderBanner from '@/components/common/HeaderBanner.vue'
import { customerApi } from '@/api/customer/customerApi.js'

export default {
  name: 'Stores',
  components: {
    StoreSearchBar,
    StoreCard,
    CustomerContainer,
    HeaderBanner,
  },
  setup() {
    const route = useRoute()
    const searchQuery = ref('')
    const stores = ref([])
    const selectedCategory = ref(route.query.category || '')

    // 필터링된 가게 목록
    const filteredStores = computed(() => {
      let filtered = stores.value

      // 검색어 필터링
      if (searchQuery.value) {
        filtered = filtered.filter(
          (store) =>
            store.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
            store.description.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }

      // 카테고리 필터링
      if (selectedCategory.value) {
        filtered = filtered.filter((store) => store.category === selectedCategory.value)
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
