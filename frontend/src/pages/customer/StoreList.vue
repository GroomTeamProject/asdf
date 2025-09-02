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
import StoreSearchBar from '@/components/customer/storeList/StoreSearchBar.vue'
import StoreCategoryCard from '@/components/customer/storeList/StoreCategoryCard.vue'
import StoreCard from '@/components/customer/storeList/StoreCard.vue'
import { mocks } from './mock'

export default {
  name: 'Stores',
  components: {
    StoreSearchBar,
    StoreCategoryCard,
    StoreCard,
  },
  setup() {
    const searchQuery = ref('')
    const stores = ref(mocks.stores)
    const categories = ref(mocks.categories)

    return {
      searchQuery,
      stores,
      categories,
    }
  },
}
</script>
