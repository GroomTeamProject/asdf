<template>
  <div class="min-h-screen bg-gray-100">
    <main class="max-w-6xl mx-auto p-4">
      <!-- 검색바 -->
      <StoreSearchBar v-model="searchQuery" />

      <!-- 카테고리 -->
      <section>
        <h2 class="mb-4 text-gray-800 text-2xl font-bold">카테고리</h2>
        <div class="grid grid-cols-4 gap-4 mb-8">
          <StoreCategoryCard v-for="category in categories" :key="category.id" :storeCategory="category" />
        </div>
      </section>

      <!-- 음식점 리스트 -->
      <section>
        <h2 class="mb-4 text-gray-800 text-2xl font-bold">인기 음식점</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <StoreCard v-for="store in stores" :key="store.id" :store="store" />
        </div>
      </section>
    </main>
  </div>
</template>

<script>
import { ref } from 'vue'
import StoreSearchBar from '@/components/customer/StoreSearchBar.vue'
import StoreCategoryCard from '@/components/customer/StoreCategoryCard.vue'
import StoreCard from '@/components/customer/StoreCard.vue'

export default {
  name: 'Stores',
  components: {
    StoreSearchBar,
    StoreCategoryCard,
    StoreCard,
  },
  setup() {
    const searchQuery = ref('')
    const stores = ref([
      {
        id: '1',
        name: '맛있는 치킨집',
        category: '치킨',
        rating: 4.5,
        deliveryTime: '25-35분',
        deliveryFee: 2000,
        description: '바삭하고 맛있는 치킨 전문점',
      },
      {
        id: '2',
        name: '신선한 피자',
        category: '피자',
        rating: 4.3,
        deliveryTime: '20-30분',
        deliveryFee: 1500,
        description: '신선한 재료로 만든 수제 피자',
      },
      {
        id: '3',
        name: '한식당 고향',
        category: '한식',
        rating: 4.7,
        deliveryTime: '30-40분',
        deliveryFee: 3000,
        description: '정통 한식의 맛을 그대로',
      },
    ])

    const categories = ref([
      { id: 1, name: '치킨' },
      { id: 2, name: '피자' },
      { id: 3, name: '한식' },
      { id: 4, name: '중식' },
    ])

    return {
      searchQuery,
      stores,
      categories,
    }
  },
}
</script>
