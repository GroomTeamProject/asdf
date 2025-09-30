<template>
  <div class="border-2 border-gray-400 bg-white rounded-lg mb-6">
    <div class="border-b border-gray-300 p-4">
      <h2 class="text-gray-800">가게 현황</h2>
    </div>
    <div class="p-4">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <!-- 현재 영업 상태 -->
        <div class="p-4 rounded-lg border-2"
             :class="{
               'border-green-400 bg-green-50': getCurrentOperatingStatus().status === 'open',
               'border-red-400 bg-red-50': getCurrentOperatingStatus().status === 'closed',
               'border-gray-400 bg-gray-50': getCurrentOperatingStatus().status === 'unknown'
             }"ls>
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium"
                 :class="{
                   'text-green-600': getCurrentOperatingStatus().status === 'open',
                   'text-red-600': getCurrentOperatingStatus().status === 'closed',
                   'text-gray-600': getCurrentOperatingStatus().status === 'unknown'
                 }">현재 영업 상태</p>
              <p class="text-lg font-semibold"
                 :class="{
                   'text-green-800': getCurrentOperatingStatus().status === 'open',
                   'text-red-800': getCurrentOperatingStatus().status === 'closed',
                   'text-gray-800': getCurrentOperatingStatus().status === 'unknown'
                 }">{{ getCurrentOperatingStatus().message }}</p>
            </div>
            <div :class="{
                   'text-green-500': getCurrentOperatingStatus().status === 'open',
                   'text-red-500': getCurrentOperatingStatus().status === 'closed',
                   'text-gray-500': getCurrentOperatingStatus().status === 'unknown'
                 }">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
          </div>
        </div>
        
        <!-- 총 주문 수 -->
        <div class="border-2 border-gray-400 bg-gray-50 p-4 rounded-lg">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-600 font-medium">총 주문 수</p>
              <p class="text-lg font-semibold text-gray-800">{{ restaurant.totalOrders.toLocaleString() }}회</p>
            </div>
            <BarChart3 class="w-8 h-8 text-gray-600" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { BarChart3 } from 'lucide-vue-next'

const props = defineProps({
  restaurant: {
    type: Object,
    required: true
  },
  storeHours: {
    type: Array,
    required: true
  }
})

// 현재 영업 상태 확인
const getCurrentOperatingStatus = () => {
  if (!props.storeHours || props.storeHours.length === 0) {
    return { status: 'unknown', message: '운영시간 정보 없음' }
  }
  
  const now = new Date()
  const today = now.getDay()
  const currentTime = now.getHours() * 100 + now.getMinutes() // HHMM 형식
  
  const todayHour = props.storeHours.find(hour => hour.dayOfWeek === today)
  
  if (!todayHour || todayHour.isClosed) {
    return { status: 'closed', message: '오늘 휴무' }
  }
  
  if (todayHour.openTime && todayHour.closeTime) {
    const openTime = parseInt(todayHour.openTime.replace(':', '').substring(0, 4))
    const closeTime = parseInt(todayHour.closeTime.replace(':', '').substring(0, 4))
    
    if (currentTime >= openTime && currentTime <= closeTime) {
      return { status: 'open', message: '영업 중' }
    } else if (currentTime < openTime) {
      const formatTime = (timeString) => timeString.substring(0, 5)
      return { status: 'closed', message: `${formatTime(todayHour.openTime)} 오픈 예정` }
    } else {
      return { status: 'closed', message: '영업 종료' }
    }
  }
  
  return { status: 'unknown', message: '운영시간 미설정' }
}
</script>