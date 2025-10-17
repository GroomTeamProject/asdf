<template>
  <div class="bg-white rounded-lg border border-gray-200 shadow-sm mb-6 overflow-hidden">
    <!-- 음식점 이미지 -->
    <div class="aspect-video md:aspect-[3/1] w-full overflow-hidden">
      <img 
        v-if="store?.imageUrl" 
        :src="store.imageUrl" 
        :alt="store.name"
        class="w-full h-full object-cover"
        @error="handleImageError"
      />
      <div v-else class="w-full h-full bg-gradient-to-br from-gray-200 to-gray-300 flex items-center justify-center">
        <span class="text-gray-500 text-lg">IMAGE</span>
      </div>
    </div>

    <!-- 음식점 정보 -->
    <div class="p-6">
      <!-- 음식점 이름 -->
      <h1 class="text-2xl font-bold text-gray-800 mb-4">{{ store?.name }}</h1>

      <!-- 평점, 배달시간, 배달비 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
        <div class="flex items-center gap-2 p-3 bg-gray-50 rounded-lg border border-gray-200">
          <div class="w-8 h-8 bg-yellow-100 rounded-full flex items-center justify-center">
            <i data-lucide="star" class="w-4 h-4 text-yellow-600"></i>
          </div>
          <div>
            <p class="text-sm text-gray-500">평점</p>
            <p class="font-semibold text-gray-800">{{ store?.rating }}</p>
          </div>
        </div>

        <div class="flex items-center gap-2 p-3 bg-gray-50 rounded-lg border border-gray-200">
          <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
            <i data-lucide="clock" class="w-4 h-4 text-blue-600"></i>
          </div>
          <div>
            <p class="text-sm text-gray-500">배달시간</p>
            <p class="font-semibold text-gray-800">{{ store?.deliveryTime }}</p>
          </div>
        </div>

        <div class="flex items-center gap-2 p-3 bg-gray-50 rounded-lg border border-gray-200">
          <div class="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
            <i data-lucide="truck" class="w-4 h-4 text-green-600"></i>
          </div>
          <div>
            <p class="text-sm text-gray-500">배달비</p>
            <p class="font-semibold text-gray-800">{{ store?.deliveryFee?.toLocaleString() }}원</p>
          </div>
        </div>
      </div>

      <!-- 음식점 설명 -->
      <div class="border-t border-gray-200 pt-4">
        <h3 class="text-sm font-medium text-gray-700 mb-2">음식점 소개</h3>
        <p class="text-gray-600 leading-relaxed">{{ store?.description }}</p>
      </div>

      <!-- 리뷰 보기 버튼 -->
      <div class="border-t border-gray-200 pt-4 mt-4">
        <button
          @click="goToReviews"
          class="w-full py-3 px-4 bg-yellow-500 text-white font-medium rounded-lg hover:bg-yellow-600 transition-colors flex items-center justify-center gap-2"
        >
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
          </svg>
          리뷰 보기
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'StoreInfo',
  props: {
    store: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    const router = useRouter()

    const goToReviews = () => {
      router.push({
        name: 'StoreReviews',
        params: { storeId: props.store.id }
      })
    }

    const handleImageError = (event) => {
      // 이미지 로드 실패 시 기본 이미지로 대체
      event.target.style.display = 'none'
    }

    return {
      goToReviews,
      handleImageError
    }
  }
}
</script>
