<template>
  <div
    @click="onClick"
    class="cursor-pointer bg-white rounded-lg border border-gray-200 shadow-sm hover:shadow-md transition-all duration-200 overflow-hidden"
  >
    <div class="aspect-video w-full overflow-hidden">
      <img 
        v-if="store.imageUrl" 
        :src="store.imageUrl" 
        :alt="store.name"
        class="w-full h-full object-cover"
        @error="handleImageError"
      />
      <div v-else class="w-full h-full bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center">
        <div class="text-center">
          <div class="text-4xl mb-2">🍽️</div>
          <span class="text-gray-500 text-sm">가게 이미지</span>
        </div>
      </div>
    </div>
    <div class="p-4">
      <div class="flex items-center justify-between mb-2">
        <h3 class="text-lg font-semibold text-gray-900">{{ store.name }}</h3>
        <div class="flex items-center gap-1">
          <svg class="w-4 h-4 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
          </svg>
          <span class="text-sm font-medium text-gray-700">{{ store.rating || '4.5' }}</span>
        </div>
      </div>
      
      <div class="flex items-center gap-4 text-sm text-gray-600 mb-3">
        <div class="flex items-center gap-1">
          <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <span>{{ store.deliveryTimeMin || 30 }}-{{ store.deliveryTimeMax || 40 }}분</span>
        </div>
        <div class="flex items-center gap-1">
          <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
          </svg>
          <span>{{ store.deliveryFee?.toLocaleString() || '3,000' }}원</span>
        </div>
      </div>
      
      <p class="text-sm text-gray-600 mb-3 line-clamp-2">{{ store.description || '맛있는 음식을 빠르게 배달해드립니다.' }}</p>
      
      <div class="flex items-center justify-between">
        <span class="text-xs text-gray-500">주문하기</span>
        <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
        </svg>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'StoreCard',
  props: {
    store: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    const router = useRouter()

    /*const onClick = () => {
      router.push(`/customer/stores/${props.store.id}`)
    }*/

    const onClick = () => {
      const userType = localStorage.getItem('userType')  // 로그인 상태 확인

      if (!userType) {
        alert('로그인이 필요한 기능입니다.')
        router.push('/login')
        return
      }

      if (userType != 'CUSTOMER') {
        alert('고객만 주문할 수 있습니다.')
        return
      }

  // CUSTOMER만 접근 가능
  router.push(`/customer/stores/${props.store.id}`)
}



    const handleImageError = (event) => {
      // 이미지 로드 실패 시 기본 이미지로 대체
      event.target.style.display = 'none'
    }

    return {
      onClick,
      handleImageError,
    }
  },
}
</script>
