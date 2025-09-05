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
import { ref, onMounted } from 'vue'
import StoreSearchBar from '@/components/customer/storeList/StoreSearchBar.vue'
import StoreCategoryCard from '@/components/customer/storeList/StoreCategoryCard.vue'
import StoreCard from '@/components/customer/storeList/StoreCard.vue'
import { customerApi } from '@/apis/customerApi'

export default {
  name: 'Stores',
  components: {
    StoreSearchBar,
    StoreCategoryCard,
    StoreCard,
  },
  setup() {
    const searchQuery = ref('')
    const stores = ref([])
    const categories = ref([])

    onMounted(async () => {
      try {
        stores.value = await customerApi.getStores()
        categories.value = await customerApi.getCategories()
        
        console.log('가게 목록:', stores.value)
        console.log('카테고리 목록:', categories.value)
      } catch (error) {
        console.error('데이터 로딩 실패:', error)
        alert('데이터를 불러오는데 실패했습니다.')
      }
    })

    return {
      searchQuery,
      stores,
      categories,
    }
  },
}
</script>
