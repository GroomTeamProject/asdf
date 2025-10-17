<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 헤더 -->
    <div class="bg-white shadow-sm border-b">
      <div class="max-w-md mx-auto px-4 py-4">
        <div class="flex items-center gap-3">
          <button @click="goBack" class="p-2 hover:bg-gray-100 rounded-full">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
            </svg>
          </button>
          <h1 class="text-lg font-semibold text-gray-900">리뷰 작성</h1>
        </div>
      </div>
    </div>

    <!-- 주문 정보 -->
    <div class="max-w-md mx-auto px-4 py-6">
      <div class="bg-white rounded-lg shadow-sm border p-4 mb-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-3">주문 정보</h2>
        <div class="space-y-2">
          <div class="flex justify-between">
            <span class="text-gray-600">가게명</span>
            <span class="font-medium">{{ orderInfo?.storeName || '로딩 중...' }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">주문일시</span>
            <span class="font-medium">{{ formatDate(orderInfo?.createdAt) }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">주문번호</span>
            <span class="font-medium">#{{ orderInfo?.id }}</span>
          </div>
        </div>
      </div>

      <!-- 리뷰 작성 폼 -->
      <form @submit.prevent="submitReview" class="space-y-6">
        <!-- 평점 -->
        <div class="bg-white rounded-lg shadow-sm border p-4">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">평점</h3>
          <div class="flex gap-2">
            <button
              v-for="star in 5"
              :key="star"
              type="button"
              @click="reviewForm.rating = star"
              class="p-1 transition-colors"
            >
              <svg
                class="w-8 h-8"
                :class="star <= reviewForm.rating ? 'text-yellow-400' : 'text-gray-300'"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
            </button>
          </div>
          <p class="text-sm text-gray-500 mt-2">
            {{ getRatingText(reviewForm.rating) }}
          </p>
        </div>

        <!-- 리뷰 내용 -->
        <div class="bg-white rounded-lg shadow-sm border p-4">
          <h3 class="text-lg font-semibold text-gray-900 mb-3">리뷰 내용</h3>
          <textarea
            v-model="reviewForm.content"
            class="w-full h-32 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
            placeholder="음식의 맛, 배달 속도, 포장 상태 등에 대한 솔직한 후기를 남겨주세요."
            maxlength="1000"
          ></textarea>
          <div class="flex justify-between items-center mt-2">
            <p class="text-sm text-gray-500">
              최대 1000자까지 작성 가능합니다
            </p>
            <span class="text-sm text-gray-400">
              {{ reviewForm.content.length }}/1000
            </span>
          </div>
        </div>

        <!-- 제출 버튼 -->
        <div class="space-y-3">
          <button
            type="submit"
            :disabled="!isFormValid || isSubmitting"
            class="w-full py-3 px-4 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
          >
            {{ isSubmitting ? '리뷰 작성 중...' : '리뷰 작성하기' }}
          </button>
          
          <button
            type="button"
            @click="goBack"
            class="w-full py-3 px-4 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors"
          >
            취소
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { reviewApi } from '@/api/customer/reviewApi'
import { customerApi } from '@/api/customer/customerApi'

const router = useRouter()
const route = useRoute()

// 반응형 데이터
const orderInfo = ref(null)
const isSubmitting = ref(false)

// 리뷰 폼 데이터
const reviewForm = ref({
  rating: 0,
  content: ''
})

// 폼 유효성 검사
const isFormValid = computed(() => {
  return reviewForm.value.rating > 0 && reviewForm.value.rating <= 5
})

// 평점 텍스트
const getRatingText = (rating) => {
  const texts = {
    1: '매우 불만족',
    2: '불만족',
    3: '보통',
    4: '만족',
    5: '매우 만족'
  }
  return texts[rating] || '평점을 선택해주세요'
}

// 날짜 포맷팅
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 주문 정보 불러오기
const loadOrderInfo = async () => {
  try {
    const orderId = route.params.orderId
    if (!orderId) {
      alert('주문 정보를 찾을 수 없습니다.')
      router.push('/customer/order-history')
      return
    }

    const userId = localStorage.getItem('userId')
    if (!userId) {
      alert('로그인이 필요합니다.')
      router.push('/customer/login')
      return
    }

    const order = await customerApi.getOrderDetail(orderId)
    orderInfo.value = order
  } catch (error) {
    console.error('주문 정보 불러오기 실패:', error)
    alert('주문 정보를 불러올 수 없습니다.')
    router.push('/customer/order-history')
  }
}

// 리뷰 제출
const submitReview = async () => {
  if (!isFormValid.value) {
    alert('평점을 선택해주세요.')
    return
  }

  try {
    isSubmitting.value = true
    
    const userId = localStorage.getItem('userId')
    const reviewData = {
      userId: parseInt(userId),
      orderId: parseInt(route.params.orderId),
      rating: reviewForm.value.rating,
      content: reviewForm.value.content.trim()
    }

    await reviewApi.createReview(reviewData)
    
    alert('리뷰가 성공적으로 작성되었습니다!')
    router.push('/customer/order-history')
  } catch (error) {
    console.error('리뷰 작성 실패:', error)
    alert(error.response?.data?.message || '리뷰 작성에 실패했습니다.')
  } finally {
    isSubmitting.value = false
  }
}

// 뒤로가기
const goBack = () => {
  router.back()
}

// 컴포넌트 마운트 시 주문 정보 불러오기
onMounted(() => {
  loadOrderInfo()
})
</script>
