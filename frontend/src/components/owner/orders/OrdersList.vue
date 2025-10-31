<template>
  <div class="border-2 border-gray-400 bg-white rounded-lg">
    <div class="border-b border-gray-300 p-4">
      <div class="flex justify-between items-center">
        <h2 class="text-gray-800">주문 관리</h2>
        <div class="flex gap-2">
          <!-- 상태 필터 -->
          <select v-model="statusFilter" @change="filterOrders" class="border border-gray-400 rounded px-2 py-1">
            <option value="">전체 주문</option>
            <option value="PENDING">대기중</option>
            <option value="ACCEPTED">수락됨</option>
            <option value="COOKING">조리중</option>
            <option value="READY">조리완료</option>
            <option value="DELIVERED">배달완료</option>
            <option value="CANCELLED">취소됨</option>
          </select>
          
          <!-- 새로고침 버튼 -->
          <button @click="loadOrders" class="border border-gray-400 px-3 py-1 rounded hover:bg-gray-100">
            🔄 새로고침
          </button>
        </div>
      </div>
    </div>
    
    <div class="p-4">
      <!-- 로딩 상태 -->
      <div v-if="loading" class="text-center py-8">
        <p class="text-gray-500">주문 목록을 불러오는 중...</p>
      </div>
      
      <!-- 에러 상태 -->
      <div v-else-if="error" class="text-center py-8 text-red-500">
        <p>주문 목록을 불러오는데 실패했습니다.</p>
        <p class="text-sm">{{ error }}</p>
        <button @click="loadOrders" class="mt-2 bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600">
          다시 시도
        </button>
      </div>
      
      <!-- 주문 목록 -->
      <div v-else class="space-y-4">
        <OrderItem
          v-for="order in filteredOrders"
          :key="order.id"
          :order="order"
          @accept-order="handleAcceptOrder"
          @reject-order="handleRejectOrder"
          @start-cooking="handleStartCooking"
          @complete-cooking="handleCompleteCooking"
          @deliver-order="handleDeliverOrder"
          @refresh="loadOrders"
        />
        
        <!-- 주문이 없는 경우 -->
        <div v-if="filteredOrders.length === 0" class="text-center py-8 text-gray-500">
          <p>{{ statusFilter ? '해당 상태의 주문이 없습니다.' : '처리할 주문이 없습니다.' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue' // 👈 watch import 추가
import { orderApi } from '@/api/owner/orderApi'
import OrderItem from './Orderitem.vue'

const props = defineProps({
  storeId: {
    type: Number,
    default: 1 // 
  }
})

// 상태 관리
const orders = ref([])
const loading = ref(false)
const error = ref(null)
const statusFilter = ref('')
const refreshInterval = ref(null)
const storeInfo = ref(null)
const userId = ref(null)

// 필터링된 주문 목록
const filteredOrders = computed(() => {
  if (!statusFilter.value) {
    return orders.value
  }
  return orders.value.filter(order => order.status === statusFilter.value)
})

const getCurrentUser = () => {
  try {
    // 방법 1: localStorage에서 가져오기
    const storedUserId = localStorage.getItem('userId')
    
    if (storedUserId) {
      userId.value = parseInt(storedUserId)
      console.log('👤 localStorage에서 사용자 ID 조회:', userId.value)
      return
    }
    
    // 방법 2: JWT 토큰에서 추출 (필요시)
    const token = localStorage.getItem('token')
    if (token) {
      try {
        // JWT 디코딩 로직 추가 가능
        console.log('🔑 토큰 발견, 디코딩 필요')
      } catch (tokenError) {
        console.error('❌ 토큰 디코딩 실패:', tokenError)
      }
    }
    
    // 기본값 또는 에러 처리
    console.warn('⚠️ 사용자 ID를 찾을 수 없습니다.')
    userId.value = null
    
  } catch (error) {
    console.error('❌ 사용자 정보 조회 실패:', error)
    userId.value = null
  }
}

// 가게 정보 로드
const loadStoreInfo = async () => {
  try {
    console.log('🏪 가게 정보 로드 중...')
    storeInfo.value = await orderApi.getStoreInfo()
    console.log('✅ 가게 정보 로드 완료:', {
      name: storeInfo.value.name,
      deliveryTime: `${storeInfo.value.deliveryTimeMin}-${storeInfo.value.deliveryTimeMax}분`
    })
  } catch (error) {
    console.warn('⚠️ 가게 정보 로드 실패:', error)
    storeInfo.value = null
  }
}

const loadOrders = async () => {
  loading.value = true
  error.value = null
  
  try {
    // 사용자 ID 확인
    if (!userId.value) {
      getCurrentUser()
    }

    if (!userId.value) {
      throw new Error('사용자 인증이 필요합니다.')
    }

    console.log('📋 주문 목록 로드:', { 
      storeId: props.storeId,        // 내 가게 ID
      ownerUserId: userId.value,     // 내 ID (가게 주인)
      statusFilter: statusFilter.value,
      customerUserId: null           // 특정 고객 필터 없음 (모든 고객)
    })
    
    const ordersData = await orderApi.getOrders(
      props.storeId,           // 가게 ID (검색 조건)
      userId.value,            // 가게 주인 ID (인증용)
      statusFilter.value || '', // 상태 필터 (검색 조건)
      null                     // 고객 ID 필터 없음 (모든 고객의 주문)
    )
    
    console.log('🔍 Raw ordersData:', ordersData)
    
    // 응답 데이터 처리
    let ordersList = []
    
    if (ordersData && ordersData.content && Array.isArray(ordersData.content)) {
      ordersList = ordersData.content
      console.log('✅ 페이지네이션 응답:', {
        총개수: ordersData.totalElements || 0,
        현재페이지: (ordersData.number || 0) + 1,
        전체페이지: ordersData.totalPages || 0,
        페이지크기: ordersData.size || 20,
        주문개수: ordersList.length,
        비어있음: ordersData.empty || false
      })
    } else if (Array.isArray(ordersData)) {
      ordersList = ordersData
      console.log('✅ 배열 응답:', ordersList.length, '개')
    } else {
      console.error('❌ 예상하지 못한 응답 구조:', ordersData)
      console.error('❌ 응답 타입:', typeof ordersData)
      console.error('❌ 응답 키들:', ordersData ? Object.keys(ordersData) : 'null/undefined')
      ordersList = []
    }
    
    orders.value = ordersList
    console.log('✅ 주문 목록 로드 완료:', ordersList.length, '개')
    
    // 🔥 주문이 없는 경우에 대한 추가 로그
    if (ordersList.length === 0) {
      console.log('ℹ️ 주문이 없습니다:', {
        storeId: props.storeId,
        userId: userId.value,
        statusFilter: statusFilter.value || '전체'
      })
    }
    
  } catch (err) {
    console.error('❌ 주문 목록 로드 실패:', err)
    console.error('❌ 에러 상세 정보:', {
      status: err.response?.status,
      statusText: err.response?.statusText,
      data: err.response?.data,
      url: err.config?.url,
      method: err.config?.method,
      message: err.message
    })
    
    // 🔥 더 구체적인 에러 메시지
    if (err.response?.status === 500) {
      error.value = '서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
    } else if (err.response?.status === 404) {
      error.value = '주문 정보를 찾을 수 없습니다.'
    } else if (err.response?.status === 403) {
      error.value = '접근 권한이 없습니다.'
    } else if (err.response?.status === 401) {
      error.value = '로그인이 필요합니다.'
    } else {
      error.value = err.response?.data?.message || err.message || '주문 목록을 불러오는데 실패했습니다.'
    }
    
    orders.value = []
  } finally {
    loading.value = false
  }
}

// 🔥 필터 변경 시 API 재호출하도록 수정
const filterOrders = () => {
  console.log('🔍 주문 필터 변경:', statusFilter.value || '전체')
  loadOrders() // 서버에서 필터링하도록 API 재호출
}

// 주문 수락 처리 - 가게 정보 활용
const handleAcceptOrder = async (data) => {
  try {
    console.log('✅ 주문 수락 요청:', data)
    
    // 가게 정보를 함께 전달
    await orderApi.acceptOrder(data.orderId, {
      minCookingTime: data.minCookingTime,
      maxCookingTime: data.maxCookingTime
    }, storeInfo.value)
    
    alert('주문이 수락되었습니다!')
    await loadOrders() // 목록 새로고침
    
  } catch (error) {
    console.error('❌ 주문 수락 실패:', error)
    alert('주문 수락에 실패했습니다: ' + (error.response?.data?.message || error.message))
  }
}

// 주문 거절 처리
const handleRejectOrder = async (data) => {
  try {
    console.log('❌ 주문 거절 요청:', data)
    await orderApi.rejectOrder(data.orderId, {
      reason: data.reason
    })
    
    alert('주문이 거절되었습니다.')
    await loadOrders() // 목록 새로고침
    
  } catch (error) {
    console.error('❌ 주문 거절 실패:', error)
    alert('주문 거절에 실패했습니다: ' + (error.response?.data?.message || error.message))
  }
}

// 조리 시작 처리
const handleStartCooking = async (orderId) => {
  try {
    console.log('👨‍🍳 조리 시작:', orderId)
    await orderApi.startCooking(orderId)
    
    alert('조리를 시작했습니다!')
    await loadOrders()
    
  } catch (error) {
    console.error('❌ 조리 시작 실패:', error)
    alert('조리 시작에 실패했습니다: ' + (error.response?.data?.message || error.message))
  }
}

// 조리 완료 처리
const handleCompleteCooking = async (orderId) => {
  try {
    console.log('🍽️ 조리 완료:', orderId)
    await orderApi.completeCooking(orderId)
    
    alert('조리가 완료되었습니다!')
    await loadOrders()
    
  } catch (error) {
    console.error('❌ 조리 완료 실패:', error)
    alert('조리 완료 처리에 실패했습니다: ' + (error.response?.data?.message || error.message))
  }
}

// 배달 완료 처리
const handleDeliverOrder = async (orderId) => {
  try {
    console.log('🚚 배달 완료:', orderId)
    await orderApi.deliverOrder(orderId)
    
    alert('배달이 완료되었습니다!')
    await loadOrders()
    
  } catch (error) {
    console.error('❌ 배달 완료 실패:', error)
    alert('배달 완료 처리에 실패했습니다: ' + (error.response?.data?.message || error.message))
  }
}

// 자동 새로고침 설정 (30초마다)
const startAutoRefresh = () => {
  refreshInterval.value = setInterval(() => {
    console.log('🔄 자동 새로고침 실행')
    loadOrders()
  }, 30000) // 30초
}

const stopAutoRefresh = () => {
  if (refreshInterval.value) {
    clearInterval(refreshInterval.value)
    refreshInterval.value = null
  }
}

// 컴포넌트 라이프사이클
onMounted(() => {
  getCurrentUser() // 사용자 정보 먼저 가져오기
  loadStoreInfo()  // 가게 정보 로드 추가
  loadOrders() 
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})

// props 변경 감지
watch(() => props.storeId, (newStoreId) => {
  if (newStoreId) {
    loadOrders()
  }
}, { immediate: true })
</script>