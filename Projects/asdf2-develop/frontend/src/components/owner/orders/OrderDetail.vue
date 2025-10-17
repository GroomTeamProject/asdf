<template>
  <!-- 모달 배경 -->
  <div v-if="show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
      <!-- 헤더 -->
      <div class="sticky top-0 bg-white border-b border-gray-200 p-4 flex justify-between items-center">
        <h2 class="text-xl font-bold">주문 상세 정보</h2>
        <button @click="close" class="text-gray-500 hover:text-gray-700 text-2xl">
          ×
        </button>
      </div>

      <!-- 로딩 상태 -->
      <div v-if="loading" class="p-8 text-center">
        <p class="text-gray-500">주문 정보를 불러오는 중...</p>
      </div>

      <!-- 에러 상태 -->
      <div v-else-if="error" class="p-8 text-center text-red-500">
        <p>주문 정보를 불러오는데 실패했습니다.</p>
        <p class="text-sm mt-2">{{ error }}</p>
        <button @click="loadOrderDetail" class="mt-4 bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600">
          다시 시도
        </button>
      </div>

      <!-- 주문 상세 내용 -->
      <div v-else-if="orderDetail" class="p-6 space-y-6">
        <!-- 기본 정보 -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="space-y-2">
            <h3 class="font-semibold text-lg border-b pb-2">주문 정보</h3>
            <div class="space-y-1 text-sm">
              <p><span class="font-medium">주문번호:</span> {{ orderDetail.orderNumber || orderDetail.id }}</p>
              <p><span class="font-medium">주문일시:</span> {{ formatFullDateTime(orderDetail.orderedAt) }}</p>
              <p><span class="font-medium">주문상태:</span> 
                <span :class="getStatusClass(orderDetail.status)" class="inline-flex px-2 py-1 text-xs rounded border ml-1">
                  {{ getStatusText(orderDetail.status) }}
                </span>
              </p>
              <p v-if="orderDetail.acceptedAt"><span class="font-medium">수락일시:</span> {{ formatFullDateTime(orderDetail.acceptedAt) }}</p>
              <p v-if="orderDetail.cookingStartedAt"><span class="font-medium">조리시작:</span> {{ formatFullDateTime(orderDetail.cookingStartedAt) }}</p>
              <p v-if="orderDetail.cookingCompletedAt"><span class="font-medium">조리완료:</span> {{ formatFullDateTime(orderDetail.cookingCompletedAt) }}</p>
              <p v-if="orderDetail.deliveredAt"><span class="font-medium">배달완료:</span> {{ formatFullDateTime(orderDetail.deliveredAt) }}</p>
              <p v-if="orderDetail.cancelledAt"><span class="font-medium">취소일시:</span> {{ formatFullDateTime(orderDetail.cancelledAt) }}</p>
            </div>
          </div>

          <div class="space-y-2">
            <h3 class="font-semibold text-lg border-b pb-2">고객 정보</h3>
            <div class="space-y-1 text-sm">
              <p><span class="font-medium">고객명:</span> {{ orderDetail.userName || '고객' }}</p>
              <p><span class="font-medium">연락처:</span> {{ orderDetail.phone }}</p>
              <p><span class="font-medium">배달주소:</span> {{ orderDetail.deliveryAddress }}</p>
              <p v-if="orderDetail.deliveryDetailAddress"><span class="font-medium">상세주소:</span> {{ orderDetail.deliveryDetailAddress }}</p>
              <p v-if="orderDetail.orderMemo" class="break-words">
                <span class="font-medium">주문메모:</span> {{ orderDetail.orderMemo }}
              </p>
            </div>
          </div>
        </div>

        <!-- 조리시간 정보 -->
        <div v-if="orderDetail.minCookingTime && orderDetail.maxCookingTime" class="bg-blue-50 p-4 rounded-lg">
          <h3 class="font-semibold text-lg mb-2">조리 시간</h3>
          <p class="text-sm">
            예상 조리시간: {{ orderDetail.minCookingTime }}-{{ orderDetail.maxCookingTime }}분
          </p>
        </div>

        <!-- 거절 사유 -->
        <div v-if="orderDetail.status === 'CANCELLED' && orderDetail.rejectReason" class="bg-red-50 border border-red-200 p-4 rounded-lg">
          <h3 class="font-semibold text-lg mb-2 text-red-800">거절 사유</h3>
          <p class="text-sm text-red-700">{{ orderDetail.rejectReason }}</p>
        </div>

        <!-- 주문 상품 목록 -->
        <div>
          <h3 class="font-semibold text-lg border-b pb-2 mb-4">주문 상품</h3>
          <div class="space-y-4">
            <div 
              v-for="item in orderDetail.orderItems" 
              :key="item.id"
              class="border border-gray-200 rounded-lg p-4"
            >
              <div class="flex justify-between items-start mb-2">
                <div>
                  <h4 class="font-medium">{{ item.menuName }}</h4>
                  <p class="text-sm text-gray-600">기본 가격: {{ formatPrice(item.menuPrice) }}</p>
                  <p class="text-sm text-gray-600">수량: {{ item.quantity }}개</p>
                </div>
                <div class="text-right">
                  <p class="font-bold">{{ formatPrice(item.totalPrice) }}</p>
                </div>
              </div>

              <!-- 옵션 정보 -->
              <div v-if="item.options && item.options.length > 0" class="mt-3 bg-gray-50 p-3 rounded">
                <h5 class="text-sm font-medium mb-2">선택 옵션</h5>
                <div class="space-y-1">
                  <div v-for="option in item.options" :key="option.id" class="flex justify-between text-sm">
                    <span>{{ option.optionName }}: {{ option.optionItemName }}</span>
                    <span v-if="option.additionalPrice > 0" class="text-green-600">
                      +{{ formatPrice(option.additionalPrice) }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 결제 정보 -->
        <div class="bg-gray-50 p-4 rounded-lg">
          <h3 class="font-semibold text-lg mb-3">결제 정보</h3>
          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span>메뉴 금액</span>
              <span>{{ formatPrice(orderDetail.menuTotalAmount) }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span>배달비</span>
              <span>{{ formatPrice(orderDetail.deliveryFee || 0) }}</span>
            </div>
            <div v-if="orderDetail.discountAmount > 0" class="flex justify-between text-sm text-red-600">
              <span>할인</span>
              <span>-{{ formatPrice(orderDetail.discountAmount) }}</span>
            </div>
            <div class="flex justify-between font-bold text-lg border-t pt-2">
              <span>총 결제금액</span>
              <span class="text-blue-600">{{ formatPrice(orderDetail.totalAmount) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 푸터 -->
      <div class="sticky bottom-0 bg-white border-t border-gray-200 p-4">
        <button @click="close" class="w-full bg-gray-500 text-white py-2 rounded hover:bg-gray-600">
          닫기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { orderApi } from '@/api/owner/orderApi'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  orderId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['close'])

// 상태 관리
const orderDetail = ref(null)
const loading = ref(false)
const error = ref(null)

// 주문 상세 정보 로드
const loadOrderDetail = async () => {
  if (!props.orderId) return

  loading.value = true
  error.value = null
  
  try {
    console.log('📋 주문 상세 정보 로드, 주문 ID:', props.orderId)
    orderDetail.value = await orderApi.getOrderDetail(props.orderId)
    console.log('✅ 주문 상세 정보 로드 완료')
  } catch (err) {
    console.error('❌ 주문 상세 정보 로드 실패:', err)
    error.value = err.response?.data?.message || err.message || '주문 정보를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 모달 닫기
const close = () => {
  emit('close')
  orderDetail.value = null
  error.value = null
}

// props 변경 감지
watch(() => [props.show, props.orderId], ([show, orderId]) => {
  if (show && orderId) {
    loadOrderDetail()
  }
}, { immediate: true })

// 유틸리티 함수들
const formatPrice = (price) => {
  return `${(price || 0).toLocaleString()}원`
}

const formatFullDateTime = (dateTime) => {
  if (!dateTime) return ''
  
  const date = new Date(dateTime)
  return `${date.toLocaleDateString('ko-KR')} ${date.toLocaleTimeString('ko-KR', { 
    hour: '2-digit', 
    minute: '2-digit',
    second: '2-digit'
  })}`
}

const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '주문 대기',
    'ACCEPTED': '주문 수락',
    'COOKING': '조리 중',
    'READY': '조리 완료',
    'DELIVERED': '배달 완료',
    'CANCELLED': '주문 취소'
  }
  return statusMap[status] || status
}

const getStatusClass = (status) => {
  const classMap = {
    'PENDING': 'border-yellow-400 bg-yellow-100 text-yellow-800',
    'ACCEPTED': 'border-blue-400 bg-blue-100 text-blue-800',
    'COOKING': 'border-orange-400 bg-orange-100 text-orange-800',
    'READY': 'border-green-400 bg-green-100 text-green-800',
    'DELIVERED': 'border-purple-400 bg-purple-100 text-purple-800',
    'CANCELLED': 'border-red-400 bg-red-100 text-red-800'
  }
  return classMap[status] || 'border-gray-400 bg-gray-100 text-gray-800'
}
</script>