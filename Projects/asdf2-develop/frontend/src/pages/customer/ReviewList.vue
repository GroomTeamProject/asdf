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
          <h1 class="text-lg font-semibold text-gray-900">
            {{ isStoreReviews ? '가게 리뷰' : '내 리뷰' }}
          </h1>
        </div>
      </div>
    </div>

    <!-- 가게 정보 (가게 리뷰인 경우) -->
    <div v-if="isStoreReviews && storeInfo" class="max-w-md mx-auto px-4 py-4">
      <div class="bg-white rounded-lg shadow-sm border p-4">
        <div class="flex items-center gap-3">
          <div class="w-12 h-12 bg-gray-200 rounded-lg flex items-center justify-center">
            <svg class="w-6 h-6 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
            </svg>
          </div>
          <div class="flex-1">
            <h2 class="font-semibold text-gray-900">{{ storeInfo.name }}</h2>
            <div class="flex items-center gap-2 mt-1">
              <div class="flex items-center gap-1">
                <svg class="w-4 h-4 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                </svg>
                <span class="text-sm font-medium text-gray-700">{{ storeInfo.averageRating?.toFixed(1) || '0.0' }}</span>
              </div>
              <span class="text-sm text-gray-500">({{ storeInfo.reviewCount || 0 }}개 리뷰)</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 리뷰 목록 -->
    <div class="max-w-md mx-auto px-4 pb-6">
      <!-- 로딩 상태 -->
      <div v-if="loading" class="space-y-4">
        <div v-for="i in 3" :key="i" class="bg-white rounded-lg shadow-sm border p-4 animate-pulse">
          <div class="flex items-center gap-3 mb-3">
            <div class="w-8 h-8 bg-gray-200 rounded-full"></div>
            <div class="flex-1">
              <div class="h-4 bg-gray-200 rounded w-1/3 mb-1"></div>
              <div class="h-3 bg-gray-200 rounded w-1/4"></div>
            </div>
          </div>
          <div class="space-y-2">
            <div class="h-3 bg-gray-200 rounded w-full"></div>
            <div class="h-3 bg-gray-200 rounded w-3/4"></div>
          </div>
        </div>
      </div>

      <!-- 리뷰가 없는 경우 -->
      <div v-else-if="reviews.length === 0" class="text-center py-12">
        <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
          </svg>
        </div>
        <h3 class="text-lg font-medium text-gray-900 mb-2">
          {{ isStoreReviews ? '아직 리뷰가 없어요' : '작성한 리뷰가 없어요' }}
        </h3>
        <p class="text-gray-500">
          {{ isStoreReviews ? '첫 번째 리뷰를 작성해보세요!' : '주문 후 리뷰를 작성해보세요!' }}
        </p>
      </div>

      <!-- 리뷰 목록 -->
      <div v-else class="space-y-4">
        <div
          v-for="review in reviews"
          :key="review.id"
          class="bg-white rounded-lg shadow-sm border p-4"
        >
          <!-- 리뷰 헤더 -->
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                <span class="text-sm font-medium text-blue-600">
                  {{ review.userName?.charAt(0) || 'U' }}
                </span>
              </div>
              <div>
                <div class="flex items-center gap-2">
                  <span class="font-medium text-gray-900">{{ review.userName || '익명' }}</span>
                  <div class="flex items-center gap-1">
                    <svg
                      v-for="star in 5"
                      :key="star"
                      class="w-4 h-4"
                      :class="star <= review.rating ? 'text-yellow-400' : 'text-gray-300'"
                      fill="currentColor"
                      viewBox="0 0 20 20"
                    >
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                  </div>
                </div>
                <p class="text-sm text-gray-500">{{ formatDate(review.createdAt) }}</p>
              </div>
            </div>
            
            <!-- 내 리뷰인 경우 수정/삭제 버튼 -->
            <div v-if="!isStoreReviews" class="flex gap-1">
              <button
                @click="editReview(review)"
                class="p-1 text-gray-400 hover:text-blue-600 transition-colors"
                title="수정"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                </svg>
              </button>
              <button
                @click="deleteReview(review.id)"
                class="p-1 text-gray-400 hover:text-red-600 transition-colors"
                title="삭제"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                </svg>
              </button>
            </div>
          </div>

          <!-- 리뷰 내용 -->
          <div class="mb-3">
            <p class="text-gray-800 leading-relaxed">{{ review.content }}</p>
          </div>

          <!-- 사장님 답글 -->
          <div v-if="review.ownerReply" class="bg-gray-50 rounded-lg p-3 mt-3">
            <div class="flex items-center gap-2 mb-2">
              <div class="w-6 h-6 bg-orange-100 rounded-full flex items-center justify-center">
                <svg class="w-3 h-3 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                </svg>
              </div>
              <span class="text-sm font-medium text-gray-700">사장님</span>
              <span class="text-xs text-gray-500">{{ formatDate(review.ownerRepliedAt) }}</span>
            </div>
            <p class="text-sm text-gray-700">{{ review.ownerReply }}</p>
          </div>
        </div>
      </div>
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
const reviews = ref([])
const storeInfo = ref(null)
const loading = ref(false)

// 가게 리뷰인지 사용자 리뷰인지 확인
const isStoreReviews = computed(() => {
  return route.name === 'StoreReviews'
})

// 날짜 포맷팅
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = Math.abs(now - date)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 1) return '어제'
  if (diffDays < 7) return `${diffDays}일 전`
  if (diffDays < 30) return `${Math.ceil(diffDays / 7)}주 전`
  if (diffDays < 365) return `${Math.ceil(diffDays / 30)}개월 전`
  return `${Math.ceil(diffDays / 365)}년 전`
}

// 리뷰 목록 불러오기
const loadReviews = async () => {
  try {
    loading.value = true
    
    if (isStoreReviews.value) {
      // 가게 리뷰 목록
      const storeId = route.params.storeId
      if (!storeId) {
        alert('가게 정보를 찾을 수 없습니다.')
        router.push('/customer/stores')
        return
      }
      
      const [reviewsData, averageRating, reviewCount] = await Promise.all([
        reviewApi.getReviewsByStore(storeId),
        reviewApi.getStoreAverageRating(storeId),
        reviewApi.getStoreReviewCount(storeId)
      ])
      
      reviews.value = reviewsData || []
      
      // 가게 정보도 함께 불러오기
      try {
        const store = await customerApi.getStoreById(storeId)
        storeInfo.value = {
          ...store,
          averageRating,
          reviewCount
        }
      } catch (error) {
        console.error('가게 정보 불러오기 실패:', error)
        storeInfo.value = {
          name: '가게 정보',
          averageRating,
          reviewCount
        }
      }
    } else {
      // 사용자 리뷰 목록
      const userId = localStorage.getItem('userId')
      if (!userId) {
        alert('로그인이 필요합니다.')
        router.push('/customer/login')
        return
      }
      
      const reviewsData = await reviewApi.getReviewsByUser(userId)
      reviews.value = reviewsData || []
    }
  } catch (error) {
    console.error('리뷰 목록 불러오기 실패:', error)
    alert('리뷰 목록을 불러올 수 없습니다.')
    reviews.value = []
  } finally {
    loading.value = false
  }
}

// 리뷰 수정
const editReview = (review) => {
  router.push({
    name: 'WriteReview',
    params: { orderId: review.orderId },
    query: { edit: true, reviewId: review.id }
  })
}

// 리뷰 삭제
const deleteReview = async (reviewId) => {
  if (!confirm('정말로 이 리뷰를 삭제하시겠습니까?')) {
    return
  }
  
  try {
    await reviewApi.deleteReview(reviewId)
    alert('리뷰가 삭제되었습니다.')
    loadReviews() // 목록 새로고침
  } catch (error) {
    console.error('리뷰 삭제 실패:', error)
    alert(error.response?.data?.message || '리뷰 삭제에 실패했습니다.')
  }
}

// 뒤로가기
const goBack = () => {
  router.back()
}

// 컴포넌트 마운트 시 리뷰 목록 불러오기
onMounted(() => {
  loadReviews()
})
</script>
