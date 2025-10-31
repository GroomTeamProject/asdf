<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-4xl mx-auto p-4">
      <!-- 헤더 -->
      <div class="mb-6">
        <button 
          @click="goBack"
          class="flex items-center text-gray-600 hover:text-gray-900 mb-4"
        >
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
          </svg>
          뒤로가기
        </button>
        <h1 class="text-2xl font-bold text-gray-900">주문 상세</h1>
      </div>

      <!-- 로딩 상태 -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <span class="ml-2 text-gray-600">주문 정보를 불러오는 중...</span>
      </div>

      <!-- 주문 정보가 없는 경우 -->
      <div v-else-if="!order" class="text-center py-12">
        <div class="text-gray-400 text-6xl mb-4">❌</div>
        <h3 class="text-lg font-medium text-gray-900 mb-2">주문을 찾을 수 없습니다</h3>
        <p class="text-gray-600 mb-6">주문 번호를 확인해주세요.</p>
        <button 
          @click="goBack"
          class="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          주문 내역으로 돌아가기
        </button>
      </div>

      <!-- 주문 상세 정보 -->
      <div v-else class="space-y-6">
        <!-- 주문 상태 카드 -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex justify-between items-center mb-4">
            <div>
              <h2 class="text-lg font-semibold text-gray-900">{{ order.storeName }}</h2>
              <p class="text-sm text-gray-600">주문번호: {{ order.orderNumber }}</p>
            </div>
            <OrderStatusBadge :status="order.status" />
          </div>
          
          <div class="grid grid-cols-2 gap-4 text-sm">
            <div>
              <p class="text-gray-600">주문일시</p>
              <p class="font-medium">{{ formatDate(order.orderedAt) }}</p>
            </div>
            <div v-if="order.deliveredAt">
              <p class="text-gray-600">배달완료</p>
              <p class="font-medium">{{ formatDate(order.deliveredAt) }}</p>
            </div>
          </div>
        </div>

        <!-- 주문 메뉴 -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">주문 메뉴</h3>
          <div class="space-y-4">
            <div 
              v-for="item in order.orderItems" 
              :key="item.id"
              class="flex justify-between items-start py-3 border-b border-gray-100 last:border-b-0"
            >
              <div class="flex-1">
                <h4 class="font-medium text-gray-900">{{ item.menuName }}</h4>
                <p class="text-sm text-gray-600">수량: {{ item.quantity }}개</p>
                <div v-if="item.options && item.options.length > 0" class="mt-1">
                  <div 
                    v-for="option in item.options" 
                    :key="option.id"
                    class="text-xs text-gray-500"
                  >
                    {{ option.optionName }}: {{ option.optionItemName }}
                    <span v-if="option.additionalPrice > 0" class="text-blue-600">
                      (+{{ formatPrice(option.additionalPrice) }}원)
                    </span>
                  </div>
                </div>
              </div>
              <div class="text-right">
                <p class="font-medium text-gray-900">{{ formatPrice(item.totalPrice) }}원</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 배달 정보 -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">배달 정보</h3>
          <div class="space-y-3">
            <div>
              <p class="text-sm text-gray-600">배달 주소</p>
              <p class="font-medium">{{ order.deliveryAddress }}</p>
              <p v-if="order.deliveryDetailAddress" class="text-sm text-gray-600">
                {{ order.deliveryDetailAddress }}
              </p>
            </div>
            <div>
              <p class="text-sm text-gray-600">연락처</p>
              <p class="font-medium">{{ order.phone }}</p>
            </div>
            <div v-if="order.orderMemo">
              <p class="text-sm text-gray-600">요청사항</p>
              <p class="font-medium">{{ order.orderMemo }}</p>
            </div>
          </div>
        </div>

        <!-- 결제 정보 -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">결제 정보</h3>
          <div class="space-y-2">
            <div class="flex justify-between">
              <span class="text-gray-600">메뉴 금액</span>
              <span>{{ formatPrice(order.menuTotalAmount) }}원</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">배달비</span>
              <span>{{ formatPrice(order.deliveryFee) }}원</span>
            </div>
            <div v-if="order.discountAmount > 0" class="flex justify-between text-green-600">
              <span>할인</span>
              <span>-{{ formatPrice(order.discountAmount) }}원</span>
            </div>
            <div class="flex justify-between text-lg font-semibold border-t border-gray-200 pt-2">
              <span>총 결제금액</span>
              <span>{{ formatPrice(order.totalAmount) }}원</span>
            </div>
          </div>
        </div>

        <!-- 주문 취소/거절 사유 (해당하는 경우) -->
        <div v-if="order.cancelReason || order.rejectReason" class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">
            {{ order.cancelReason ? '취소 사유' : '거절 사유' }}
          </h3>
          <p class="text-gray-700">{{ order.cancelReason || order.rejectReason }}</p>
        </div>

        <!-- 조리 시간 정보 (해당하는 경우) -->
        <div v-if="order.minCookingTime && order.maxCookingTime" class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">예상 조리 시간</h3>
          <p class="text-gray-700">{{ order.minCookingTime }}분 ~ {{ order.maxCookingTime }}분</p>
        </div>

        <!-- 액션 버튼들 (맨 아래) -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="space-y-3">
            <!-- 리뷰 작성 버튼 (배달완료된 주문만) -->
            <div v-if="order.status === 'DELIVERED'">
              <button
                @click="goToWriteReview"
                class="w-full h-14 text-lg font-semibold rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 bg-yellow-100 text-yellow-700 hover:bg-yellow-200 focus:ring-yellow-500 border border-yellow-300 flex items-center justify-center gap-2"
              >
                <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                </svg>
                리뷰 작성하기
              </button>
            </div>
            
            <!-- 주문 취소 버튼 -->
            <div class="flex justify-center">
              <OrderCancelButton 
                :order-id="order.id" 
                :order-status="order.status"
                @order-cancelled="handleOrderCancelled"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { orderService } from '@/services/customer/orderService.js'
import OrderStatusBadge from '@/components/customer/order/OrderStatusBadge.vue'
import OrderCancelButton from '@/components/customer/order/OrderCancelButton.vue'

export default {
  name: 'OrderHistoryDetail',
  components: {
    OrderStatusBadge,
    OrderCancelButton
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const order = ref(null)
    const loading = ref(true)

    // 주문 상세 조회
    const fetchOrderDetail = async () => {
      try {
        loading.value = true
        const orderId = route.params.orderId
        const result = await orderService.getOrderDetail(orderId)
        order.value = result.data
      } catch (error) {
        console.error('주문 상세 조회 실패:', error)
        throw error
      } finally {
        loading.value = false
      }
    }

    // 뒤로가기
    const goBack = () => {
      router.push('/customer/order-history')
    }

    // 리뷰 작성 페이지로 이동
    const goToWriteReview = () => {
      router.push(`/customer/write-review/${order.value.id}`)
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

    // 주문 취소 완료 처리
    const handleOrderCancelled = () => {
      // 주문 정보 다시 불러오기
      fetchOrderDetail()
    }

    onMounted(() => {
      fetchOrderDetail()
    })

    return {
      order,
      loading,
      goBack,
      goToWriteReview,
      formatDate,
      formatPrice,
      handleOrderCancelled
    }
  }
}
</script>
