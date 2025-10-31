<template>
  <!-- 메인 배너 -->
  <HeaderBanner 
    title="Quick Deliver" 
    description="원하는 음식을 빠르고 안전하게 배달받으세요" 
  />

  <!-- 페이지 컨테이너 -->
  <CustomerContainer max-width="2xl" padding="4" custom-class="space-y-6">
      <!-- 크루셀 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <div class="relative">
          <div class="flex transition-transform duration-300 ease-in-out" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
            <!-- 슬라이드 1: 배달 주문 -->
            <div class="w-full flex-shrink-0 bg-gradient-to-r from-orange-500 to-red-500 text-white p-8">
              <div class="text-center">
                <div class="text-4xl mb-4">🍕</div>
                <h3 class="text-xl font-bold mb-2">맛있는 피자 주문</h3>
                <p class="text-orange-100 mb-4">신선한 재료로 만든 피자를 배달받으세요</p>
                <button
                  @click="() => goToStores()"
                  class="bg-white text-orange-600 font-semibold px-6 py-2 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  주문하기
                </button>
              </div>
            </div>
            
            <!-- 슬라이드 2: 치킨 주문 -->
            <div class="w-full flex-shrink-0 bg-gradient-to-r from-yellow-500 to-orange-500 text-white p-8">
              <div class="text-center">
                <div class="text-4xl mb-4">🍗</div>
                <h3 class="text-xl font-bold mb-2">바삭한 치킨 주문</h3>
                <p class="text-yellow-100 mb-4">바삭하고 맛있는 치킨을 주문하세요</p>
                <button
                  @click="() => goToStores()"
                  class="bg-white text-yellow-600 font-semibold px-6 py-2 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  주문하기
                </button>
              </div>
            </div>
            
            <!-- 슬라이드 3: 한식 주문 -->
            <div class="w-full flex-shrink-0 bg-gradient-to-r from-green-500 to-blue-500 text-white p-8">
              <div class="text-center">
                <div class="text-4xl mb-4">🍜</div>
                <h3 class="text-xl font-bold mb-2">따뜻한 한식 주문</h3>
                <p class="text-green-100 mb-4">정성스럽게 만든 한식을 맛보세요</p>
                <button
                  @click="() => goToStores()"
                  class="bg-white text-green-600 font-semibold px-6 py-2 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  주문하기
                </button>
              </div>
            </div>
          </div>
          
          <!-- 네비게이션 도트 -->
          <div class="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex space-x-2">
            <button
              v-for="(slide, index) in slides"
              :key="index"
              @click="currentSlide = index"
              :class="[
                'w-2 h-2 rounded-full transition-colors',
                currentSlide === index ? 'bg-white' : 'bg-white bg-opacity-50'
              ]"
            ></button>
          </div>
        </div>
      </div>



      <!-- 인기 카테고리 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">인기 카테고리</h2>
        <div class="grid grid-cols-4 gap-3">
          <button
            @click="goToStores('KOREAN')"
            class="p-4 bg-green-50 border border-green-200 rounded-lg hover:bg-green-100 transition-colors"
          >
            <div class="text-2xl mb-2">🍜</div>
            <div class="text-sm font-medium text-green-700">한식</div>
          </button>
          <button
            @click="goToStores('CHINESE')"
            class="p-4 bg-red-50 border border-red-200 rounded-lg hover:bg-red-100 transition-colors"
          >
            <div class="text-2xl mb-2">🥟</div>
            <div class="text-sm font-medium text-red-700">중식</div>
          </button>
          <button
            @click="goToStores('WESTERN')"
            class="p-4 bg-purple-50 border border-purple-200 rounded-lg hover:bg-purple-100 transition-colors"
          >
            <div class="text-2xl mb-2">🍝</div>
            <div class="text-sm font-medium text-purple-700">양식</div>
          </button>
          <button
            @click="goToStores('JAPANESE')"
            class="p-4 bg-blue-50 border border-blue-200 rounded-lg hover:bg-blue-100 transition-colors"
          >
            <div class="text-2xl mb-2">🍱</div>
            <div class="text-sm font-medium text-blue-700">일식</div>
          </button>
          <button
            @click="goToStores('FAST_FOOD')"
            class="p-4 bg-orange-50 border border-orange-200 rounded-lg hover:bg-orange-100 transition-colors"
          >
            <div class="text-2xl mb-2">🍔</div>
            <div class="text-sm font-medium text-orange-700">패스트푸드</div>
          </button>
          <button
            @click="goToStores('CHICKEN')"
            class="p-4 bg-yellow-50 border border-yellow-200 rounded-lg hover:bg-yellow-100 transition-colors"
          >
            <div class="text-2xl mb-2">🍗</div>
            <div class="text-sm font-medium text-yellow-700">치킨</div>
          </button>
          <button
            @click="goToStores('PIZZA')"
            class="p-4 bg-red-50 border border-red-200 rounded-lg hover:bg-red-100 transition-colors"
          >
            <div class="text-2xl mb-2">🍕</div>
            <div class="text-sm font-medium text-red-700">피자</div>
          </button>
          <button
            @click="goToStores('DESSERT')"
            class="p-4 bg-pink-50 border border-pink-200 rounded-lg hover:bg-pink-100 transition-colors"
          >
            <div class="text-2xl mb-2">🍰</div>
            <div class="text-sm font-medium text-pink-700">디저트</div>
          </button>
        </div>
      </div>

      <!-- 빠른 액션 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">빠른 액션</h2>
        <div class="space-y-3">
          <button
            @click="() => goToStores()"
            class="w-full flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                </svg>
              </div>
              <div class="text-left">
                <div class="font-medium text-gray-900">가게 둘러보기</div>
                <div class="text-sm text-gray-600">다양한 가게를 확인해보세요</div>
              </div>
            </div>
            <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>

          <button
            @click="goToOrderHistory"
            class="w-full flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
                </svg>
              </div>
              <div class="text-left">
                <div class="font-medium text-gray-900">주문 내역</div>
                <div class="text-sm text-gray-600">이전 주문을 확인해보세요</div>
              </div>
            </div>
            <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>
        </div>
      </div>
  </CustomerContainer>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import CustomerContainer from '@/components/customer/CustomerContainer.vue'
import HeaderBanner from '@/components/common/HeaderBanner.vue'

export default {
  name: 'CustomerMain',
  components: {
    CustomerContainer,
    HeaderBanner,
  },
  setup() {
    const router = useRouter()
    const currentSlide = ref(0)
    const slides = ref([
      { id: 0, title: '피자', emoji: '🍕' },
      { id: 1, title: '치킨', emoji: '🍗' },
      { id: 2, title: '한식', emoji: '🍜' }
    ])
    let slideInterval = null

    const goToStores = (category = null) => {
      if (category) {
        router.push(`/customer/stores?category=${category}`)
      } else {
        router.push('/customer/stores')
      }
    }

    const goToOrderHistory = () => {
      router.push('/customer/order-history')
    }

    // 자동 슬라이드
    const startAutoSlide = () => {
      slideInterval = setInterval(() => {
        currentSlide.value = (currentSlide.value + 1) % slides.value.length
      }, 3000)
    }

    const stopAutoSlide = () => {
      if (slideInterval) {
        clearInterval(slideInterval)
        slideInterval = null
      }
    }

    onMounted(() => {
      startAutoSlide()
    })

    onUnmounted(() => {
      stopAutoSlide()
    })

    return {
      currentSlide,
      slides,
      goToStores,
      goToOrderHistory,
    }
  },
}
</script>
