<template>
  <div class="border border-gray-400 rounded-lg p-4 bg-white">
    <div class="flex items-center justify-between mb-4">
      <div>
        <h3 class="text-lg font-semibold">주문 #{{ order.orderNumber || order.id }}</h3>
        <p class="text-sm text-gray-600">고객: {{ order.userName || '고객' }}</p>
        <p class="text-sm text-gray-600">주문 시간: {{ formatDateTime(order.orderedAt) }}</p>
        <p class="text-sm text-gray-600">연락처: {{ order.phone }}</p>
        <p class="text-sm text-gray-600">배달 주소: {{ order.deliveryAddress }} {{ order.deliveryDetailAddress }}</p>
        <p v-if="order.orderMemo" class="text-sm text-gray-600">메모: {{ order.orderMemo }}</p>
      </div>
      <div class="text-right">
        <span 
          class="inline-flex px-2 py-1 text-xs rounded border"
          :class="getStatusClass(order.status)"
        >
          {{ getStatusText(order.status) }}
        </span>
        <p class="mt-1 text-lg font-bold">{{ formatPrice(order.totalAmount) }}</p>
        
        <!-- 조리 시간 표시 -->
        <div v-if="order.minCookingTime && order.maxCookingTime" class="mt-1 text-sm text-gray-600">
          조리시간: {{ order.minCookingTime }}-{{ order.maxCookingTime }}분
        </div>
      </div>
    </div>
    
    <!-- 주문 내역 -->
    <div class="mb-4">
      <h4 class="mb-2 font-medium">주문 내역</h4>
      <div class="bg-gray-50 p-3 rounded">
        <div 
          v-for="item in order.orderItems" 
          :key="item.id"
          class="mb-2 last:mb-0"
        >
          <div class="flex justify-between text-sm">
            <span class="font-medium">{{ item.menuName }} x{{ item.quantity }}</span>
            <span class="font-medium">{{ formatPrice(item.totalPrice) }}</span>
          </div>
          <div class="text-xs text-gray-600 ml-2">
            기본 가격: {{ formatPrice(item.menuPrice) }}
          </div>
          
          <!-- 옵션 표시 -->
          <div v-if="item.options && item.options.length > 0" class="ml-4 mt-1">
            <div v-for="option in item.options" :key="option.id" class="text-xs text-gray-600">
              • {{ option.optionName }}: {{ option.optionItemName }}
              <span v-if="option.additionalPrice > 0">
                (+{{ formatPrice(option.additionalPrice) }})
              </span>
            </div>
          </div>
        </div>
        
        <!-- 총 금액 -->
        <div class="border-t pt-2 mt-2">
          <div class="flex justify-between text-sm">
            <span>메뉴 금액</span>
            <span>{{ formatPrice(order.menuTotalAmount) }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span>배달비</span>
            <span>{{ formatPrice(order.deliveryFee || 0) }}</span>
          </div>
          <div v-if="order.discountAmount > 0" class="flex justify-between text-sm text-red-600">
            <span>할인</span>
            <span>-{{ formatPrice(order.discountAmount) }}</span>
          </div>
          <div class="flex justify-between font-bold text-base border-t pt-1">
            <span>총 금액</span>
            <span>{{ formatPrice(order.totalAmount) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 거절 사유 표시 -->
    <div v-if="order.status === 'CANCELLED' && order.rejectReason" class="mb-4 p-3 bg-red-50 border border-red-200 rounded">
      <p class="text-sm text-red-700">
        <strong>거절 사유:</strong> {{ order.rejectReason }}
      </p>
    </div>

    <!-- 액션 버튼들 -->
    <div class="flex gap-2 flex-wrap">
      <!-- PENDING 상태: 수락/거절 -->
      <template v-if="order.status === 'PENDING'">
        <button 
          @click="showAcceptDialog = true"
          class="bg-green-600 text-white px-3 py-2 rounded text-sm hover:bg-green-700 transition-colors"
        >
          ✅ 주문 수락
        </button>
        <button 
          @click="showRejectDialog = true"
          class="bg-red-600 text-white px-3 py-2 rounded text-sm hover:bg-red-700 transition-colors"
        >
          ❌ 주문 거절
        </button>
      </template>
      
      <!-- ACCEPTED 상태: 조리 시작 -->
      <button 
        v-if="order.status === 'ACCEPTED'"
        @click="$emit('start-cooking', order.id)"
        class="bg-blue-600 text-white px-3 py-2 rounded text-sm hover:bg-blue-700 transition-colors"
      >
        👨‍🍳 조리 시작
      </button>
      
      <!-- COOKING 상태: 조리 완료 -->
      <button 
        v-if="order.status === 'COOKING'"
        @click="$emit('complete-cooking', order.id)"
        class="bg-orange-600 text-white px-3 py-2 rounded text-sm hover:bg-orange-700 transition-colors"
      >
        🍽️ 조리 완료
      </button>
      
      <!-- READY 상태: 배달 완료 -->
      <button 
        v-if="order.status === 'READY'"
        @click="$emit('deliver-order', order.id)"
        class="bg-purple-600 text-white px-3 py-2 rounded text-sm hover:bg-purple-700 transition-colors"
      >
        🚚 배달 완료
      </button>
      
      <!-- 주문 상세 보기 -->
      <button 
  @click="showOrderDetailModal = true"
  class="border border-gray-400 px-3 py-2 rounded text-sm hover:bg-gray-100 transition-colors"
>
  📋 상세보기
</button>

      <!-- 주문 상세 모달 추가 -->
      <OrderDetail 
        :show="showOrderDetailModal"
        :orderId="order.id"
        @close="showOrderDetailModal = false"
      />
    </div>
  </div>

  <!-- 수락 다이얼로그 -->
  <div v-if="showAcceptDialog" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white p-6 rounded-lg max-w-md w-full mx-4">
      <h3 class="text-lg font-semibold mb-4">주문 수락</h3>
      
      <!-- 가게 기본 배달시간 안내 추가 -->
      <div v-if="storeInfo && storeInfo.deliveryTimeMin && storeInfo.deliveryTimeMax" class="mb-4 p-3 bg-blue-50 border border-blue-200 rounded">
        <p class="text-sm text-blue-700">
          <strong>가게 기본 배달시간:</strong> {{ storeInfo.deliveryTimeMin }}-{{ storeInfo.deliveryTimeMax }}분
        </p>
        <p class="text-xs text-blue-600 mt-1">
          아래 입력란이 비어있으면 기본 배달시간이 자동으로 설정됩니다.
        </p>
      </div>

      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium mb-1">최소 조리 시간 (분)</label>
          <input 
            v-model.number="acceptForm.minCookingTime" 
            type="number" 
            min="1" 
            max="180"
            class="w-full border border-gray-300 rounded px-3 py-2"
            placeholder="20"
          />
        </div>
        
        <div>
          <label class="block text-sm font-medium mb-1">최대 조리 시간 (분)</label>
          <input 
            v-model.number="acceptForm.maxCookingTime" 
            type="number" 
            min="1" 
            max="180"
            class="w-full border border-gray-300 rounded px-3 py-2"
            placeholder="40"
          />
        </div>
      </div>
      
      <div class="flex gap-2 mt-6">
        <button 
          @click="showAcceptDialog = false"
          class="flex-1 border border-gray-300 px-4 py-2 rounded hover:bg-gray-100"
        >
          취소
        </button>
        <button 
          @click="acceptOrder"
          class="flex-1 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
        >
          수락
        </button>
      </div>
    </div>
  </div>

  <!-- 거절 다이얼로그 -->
  <div v-if="showRejectDialog" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white p-6 rounded-lg max-w-md w-full mx-4">
      <h3 class="text-lg font-semibold mb-4">주문 거절</h3>
      
      <div>
        <label class="block text-sm font-medium mb-1">거절 사유</label>
        <textarea 
          v-model="rejectForm.reason" 
          class="w-full border border-gray-300 rounded px-3 py-2 h-24 resize-none"
          placeholder="거절 사유를 입력해주세요 (예: 재료 부족, 영업 종료 등)"
          maxlength="500"
        ></textarea>
        <div class="text-xs text-gray-500 mt-1">{{ rejectForm.reason.length }}/500</div>
      </div>
      
      <div class="flex gap-2 mt-6">
        <button 
          @click="showRejectDialog = false"
          class="flex-1 border border-gray-300 px-4 py-2 rounded hover:bg-gray-100"
        >
          취소
        </button>
        <button 
          @click="rejectOrder"
          :disabled="!rejectForm.reason.trim()"
          class="flex-1 bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          거절
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import OrderDetail from './OrderDetail.vue'

const props = defineProps({
  order: {
    type: Object,
    required: true
  },
  storeInfo: { // 가게 정보 props 추가
    type: Object,
    default: null
  }
})

const emit = defineEmits([
  'accept-order', 
  'reject-order', 
  'start-cooking', 
  'complete-cooking', 
  'deliver-order',
  'refresh'
])

// 다이얼로그 상태
const showAcceptDialog = ref(false)
const showRejectDialog = ref(false)
const showOrderDetailModal = ref(false)

// 폼 데이터 - 가게 기본 배달시간으로 초기화
const acceptForm = ref({
  minCookingTime: null,
  maxCookingTime: null
})

// 가게 기본시간 사용 함수
const useStoreDefaultTime = () => {
  if (props.storeInfo) {
    acceptForm.value.minCookingTime = props.storeInfo.deliveryTimeMin
    acceptForm.value.maxCookingTime = props.storeInfo.deliveryTimeMax
  }
}

// 다이얼로그 열 때 기본값 설정
const openAcceptDialog = () => {
  // 가게 정보가 있으면 기본값으로 설정
  if (props.storeInfo && props.storeInfo.deliveryTimeMin && props.storeInfo.deliveryTimeMax) {
    acceptForm.value.minCookingTime = props.storeInfo.deliveryTimeMin
    acceptForm.value.maxCookingTime = props.storeInfo.deliveryTimeMax
  } else {
    // 기본값
    acceptForm.value.minCookingTime = 20
    acceptForm.value.maxCookingTime = 40
  }
  
  showAcceptDialog.value = true
}

const rejectForm = ref({
  reason: ''
})

// 주문 수락
const acceptOrder = () => {
  // 입력값이 없으면 가게 기본값 또는 시스템 기본값 사용
  let minTime = acceptForm.value.minCookingTime
  let maxTime = acceptForm.value.maxCookingTime
  
  // 빈값이면 가게 기본값 사용
  if (!minTime || !maxTime) {
    if (props.storeInfo && props.storeInfo.deliveryTimeMin && props.storeInfo.deliveryTimeMax) {
      minTime = minTime || props.storeInfo.deliveryTimeMin
      maxTime = maxTime || props.storeInfo.deliveryTimeMax
    } else {
      // 가게 정보가 없으면 시스템 기본값
      minTime = minTime || 20
      maxTime = maxTime || 40
    }
  }
  
  if (minTime > maxTime) {
    alert('최소 조리 시간이 최대 조리 시간보다 클 수 없습니다.')
    return
  }
  
  emit('accept-order', {
    orderId: props.order.id,
    minCookingTime: minTime,
    maxCookingTime: maxTime
  })
  
  showAcceptDialog.value = false
}

// 주문 거절
const rejectOrder = () => {
  emit('reject-order', {
    orderId: props.order.id,
    reason: rejectForm.value.reason.trim()
  })
  
  showRejectDialog.value = false
  rejectForm.value.reason = ''
}

// 유틸리티 함수들
const formatPrice = (price) => {
  return `${(price || 0).toLocaleString()}원`
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  
  const date = new Date(dateTime)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / (1000 * 60))
  
  if (diffMins < 60) {
    return `${diffMins}분 전`
  } else if (diffMins < 1440) {
    return `${Math.floor(diffMins / 60)}시간 전`
  } else {
    return date.toLocaleDateString('ko-KR') + ' ' + date.toLocaleTimeString('ko-KR', { 
      hour: '2-digit', 
      minute: '2-digit' 
    })
  }
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