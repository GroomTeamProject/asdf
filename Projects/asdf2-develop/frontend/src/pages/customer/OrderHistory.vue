<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-4xl mx-auto p-4">
      <!-- 헤더 -->
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-900">주문 내역</h1>
        <p class="text-gray-600 mt-1">지금까지 주문한 내역을 확인하세요</p>
      </div>

      <!-- 로딩 상태 -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <span class="ml-2 text-gray-600">주문 내역을 불러오는 중...</span>
      </div>

      <!-- 주문 내역이 없는 경우 -->
      <div v-else-if="orders.length === 0" class="text-center py-12">
        <div class="text-gray-400 text-6xl mb-4">📦</div>
        <h3 class="text-lg font-medium text-gray-900 mb-2">주문 내역이 없습니다</h3>
        <p class="text-gray-600 mb-6">첫 주문을 시작해보세요!</p>
        <router-link 
          to="/customer/stores" 
          class="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          가게 둘러보기
        </router-link>
      </div>

      <!-- 주문 내역 목록 -->
      <div v-else class="space-y-4">
        <div 
          v-for="order in orders" 
          :key="order.id"
          class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow cursor-pointer"
          @click="goToOrderDetail(order.id)"
        >
          <div class="flex justify-between items-start mb-4">
            <div>
              <h3 class="text-lg font-semibold text-gray-900">{{ order.storeName }}</h3>
              <p class="text-sm text-gray-600">주문번호: {{ order.orderNumber }}</p>
            </div>
            <OrderStatusBadge :status="order.status" />
          </div>

          <div class="flex justify-between items-center mb-4">
            <div class="text-sm text-gray-600">
              <p>주문일시: {{ formatDate(order.orderedAt) }}</p>
              <p v-if="order.deliveredAt">배달완료: {{ formatDate(order.deliveredAt) }}</p>
            </div>
            <div class="text-right">
              <p class="text-lg font-semibold text-gray-900">{{ formatPrice(order.totalAmount) }}원</p>
              <p class="text-sm text-gray-600">{{ order.orderItems.length }}개 메뉴</p>
            </div>
          </div>

          <!-- 주문 메뉴 미리보기 -->
          <div class="border-t border-gray-100 pt-4">
            <div class="flex items-center justify-between">
              <div class="flex-1">
                <p class="text-sm text-gray-600 mb-1">주문 메뉴</p>
                <div class="flex flex-wrap gap-2">
                  <span 
                    v-for="item in order.orderItems.slice(0, 3)" 
                    :key="item.id"
                    class="inline-flex items-center px-2 py-1 rounded-full text-xs bg-gray-100 text-gray-700"
                  >
                    {{ item.menuName }} x{{ item.quantity }}
                  </span>
                  <span 
                    v-if="order.orderItems.length > 3"
                    class="inline-flex items-center px-2 py-1 rounded-full text-xs bg-gray-100 text-gray-700"
                  >
                    +{{ order.orderItems.length - 3 }}개 더
                  </span>
                </div>
              </div>
              <div class="text-gray-400">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                </svg>
              </div>
            </div>
            
            <!-- 리뷰 작성 버튼 (배달완료된 주문만) -->
            <div v-if="order.status === 'DELIVERED'" class="mt-4 pt-4 border-t border-gray-100">
              <button
                @click.stop="goToWriteReview(order.id)"
                class="w-full py-2 px-4 bg-yellow-500 text-white text-sm font-medium rounded-lg hover:bg-yellow-600 transition-colors"
              >
                리뷰 작성하기
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 더보기 버튼 (필요시) -->
      <div v-if="hasMore" class="text-center mt-8">
        <button 
          @click="loadMore"
          :disabled="loadingMore"
          class="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2 mx-auto"
        >
          <div v-if="loadingMore" class="animate-spin rounded-full h-4 w-4 border-b-2 border-gray-600"></div>
          {{ loadingMore ? '로딩 중...' : '더보기' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { customerApi } from '@/api/customer/customerApi.js'
import OrderStatusBadge from '@/components/customer/order/OrderStatusBadge.vue'

export default {
  name: 'OrderHistory',
  components: {
    OrderStatusBadge
  },
  setup() {
    const router = useRouter()
    const orders = ref([])
    const loading = ref(true)
    const hasMore = ref(false)
    const currentPage = ref(0)
    const loadingMore = ref(false)

    // 주문 내역 조회
    const fetchOrders = async (page = 0, append = false) => {
      try {
        if (append) {
          loadingMore.value = true
        } else {
          loading.value = true
        }
        
        // 사용자의 주문 내역 조회
        console.log(`📄 페이지 ${page} 요청 중...`)
        const response = await customerApi.getMyOrders(page, 20)
        console.log('📄 응답 데이터:', response)
        
        // 페이지네이션 응답 구조에 맞게 수정
        if (response && response.content) {
          if (append) {
            // 더보기: 기존 데이터에 추가
            orders.value = [...orders.value, ...response.content]
          } else {
            // 초기 로드: 새로 설정
            orders.value = response.content || []
          }
          hasMore.value = !response.last // 마지막 페이지가 아니면 더보기 버튼 표시
          currentPage.value = response.pageable?.pageNumber || 0
        } else {
          if (!append) {
            orders.value = []
          }
          hasMore.value = false
        }
      } catch (error) {
        console.error('주문 내역 조회 실패:', error)
        if (!append) {
          orders.value = []
        }
        hasMore.value = false
      } finally {
        loading.value = false
        loadingMore.value = false
      }
    }

    // 주문 상세 페이지로 이동
    const goToOrderDetail = (orderId) => {
      router.push(`/customer/order-history/${orderId}`)
    }

    // 리뷰 작성 페이지로 이동
    const goToWriteReview = (orderId) => {
      router.push(`/customer/write-review/${orderId}`)
    }

    // 날짜 포맷팅
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // 가격 포맷팅
    const formatPrice = (price) => {
      return new Intl.NumberFormat('ko-KR').format(price)
    }

    // 더보기 로드
    const loadMore = () => {
      if (hasMore.value && !loadingMore.value) {
        fetchOrders(currentPage.value + 1, true)
      }
    }

    onMounted(() => {
      fetchOrders()
    })

    return {
      orders,
      loading,
      hasMore,
      loadingMore,
      goToOrderDetail,
      goToWriteReview,
      formatDate,
      formatPrice,
      loadMore
    }
  }
}
</script>
