<template>
  <div class="h-full bg-white rounded-lg p-6 shadow space-y-6">
    <!-- 메뉴 이미지 -->
    <div class="image-placeholder w-full h-80 bg-gradient-to-br from-gray-200 to-gray-300 flex items-center justify-center rounded-lg">
      <span class="text-gray-500 text-lg">IMAGE</span>
    </div>

    <!-- 메뉴 태그 -->
    <div class="flex flex-wrap gap-2">
      <span v-for="tag in menuItem.tags" :key="tag" class="px-3 py-1 bg-blue-100 text-blue-800 text-sm rounded-full font-medium">
        {{ tag }}
      </span>
    </div>

    <!-- 메뉴 설명 -->
    <div class="space-y-2">
      <h1 class="text-2xl font-bold text-gray-900">
        {{ menuItem.name }}
      </h1>

      <p class="text-gray-600 leading-relaxed">
        {{ menuItem.description }}
      </p>
    </div>

    <!-- 메뉴 추가정보 -->
    <div class="flex items-center gap-4 text-sm">
      <div class="flex items-center gap-1">
        <span class="text-yellow-400 text-lg">⭐</span>
        <span class="font-medium">{{ menuItem.rating }}</span>
        <span class="text-gray-500">({{ menuItem.reviewCount }})</span>
      </div>
      <div class="text-gray-500">조리시간: {{ menuItem.preparationTime }}</div>
    </div>

    <!-- 가격 -->
    <div class="text-2xl font-bold text-blue-600">{{ menuItem.basePrice.toLocaleString() }}원</div>
  </div>
</template>

<script>
export default {
  name: 'MenuInfo',
  props: {
    menuItem: {
      type: Object,
      required: true,
    },
  },
}
</script>
