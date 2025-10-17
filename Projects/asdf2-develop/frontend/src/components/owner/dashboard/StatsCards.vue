<template>
  <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
    <!-- 오늘 주문 -->
    <div class="border-2 border-gray-400 bg-white rounded-lg p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-sm text-gray-600">오늘 주문</p>
          <p class="text-2xl text-gray-800">{{ todayStats.orders }}</p>
        </div>
        <Package class="w-8 h-8 text-gray-600" />
      </div>
    </div>

    <!-- 오늘 매출 -->
    <div class="border-2 border-gray-400 bg-white rounded-lg p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-sm text-gray-600">오늘 매출</p>
          <p class="text-2xl text-gray-800">{{ formatPrice(todayStats.revenue) }}</p>
        </div>
        <DollarSign class="w-8 h-8 text-gray-600" />
      </div>
    </div>

    <!-- 평점 -->
    <div class="border-2 border-gray-400 bg-white rounded-lg p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-sm text-gray-600">평점</p>
          <p class="text-2xl text-gray-800">{{ restaurant.rating || 0 }}</p>
        </div>
        <Star class="w-8 h-8 text-gray-600" />
      </div>
    </div>

    <!-- 오늘 운영시간 -->
    <div class="border-2 border-gray-400 bg-white rounded-lg p-6">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-sm text-gray-600">오늘 운영시간</p>
          <p class="text-lg text-gray-800">{{ getTodayOperatingHours() }}</p>
        </div>
        <svg class="w-8 h-8 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
        </svg>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Package, DollarSign, Star } from 'lucide-vue-next'

const props = defineProps({
  todayStats: {
    type: Object,
    required: true
  },
  restaurant: {
    type: Object,
    required: true
  },
  storeHours: {
    type: Array,
    required: true
  }
})

const formatPrice = (price) => {
  return `${(price || 0).toLocaleString()}원`
}

const getTodayOperatingHours = () => {
  if (!props.storeHours || props.storeHours.length === 0) {
    return '운영시간 정보 없음'
  }
  
  const today = new Date().getDay()
  const todayHour = props.storeHours.find(hour => hour.dayOfWeek === today)
  
  if (!todayHour) {
    return '오늘 운영시간 정보 없음'
  }
  
  if (todayHour.isClosed) {
    return '오늘 휴무'
  }
  
  if (todayHour.openTime && todayHour.closeTime) {
    const openTime = todayHour.openTime.substring(0, 5)
    const closeTime = todayHour.closeTime.substring(0, 5)
    return `${openTime} - ${closeTime}`
  }
  
  return '운영시간 미설정'
}
</script>