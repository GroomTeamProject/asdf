<template>
  <div class="bg-white rounded-lg border border-gray-200 shadow-sm mb-6 overflow-hidden">
    <!-- 음식점 이미지 -->
    <div class="image-placeholder aspect-video md:aspect-[3/1] w-full">
      <span>IMAGE</span>
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
    </div>
  </div>
</template>

<script>
export default {
  name: 'StoreInfo',
  props: {
    store: {
      type: Object,
      required: true,
    },
  },
}
</script>
