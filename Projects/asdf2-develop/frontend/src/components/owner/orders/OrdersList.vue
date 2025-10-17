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
    default: 1 // 👈 기본값 추가 (필수가 아니도록)
  }
})

// 상태 관리
const orders = ref([])
const loading = ref(false)
const error = ref(null)
const statusFilter = ref('')
const refreshInterval = ref(null)
const storeInfo = ref(null)

// 필터링된 주문 목록
const filteredOrders = computed(() => {
  if (!statusFilter.value) {
    return orders.value
  }
  return orders.value.filter(order => order.status === statusFilter.value)
})

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
    console.log('📋 주문 목록 로드 중..., storeId:', props.storeId)
    
    // props.storeId를 전달
    const ordersData = await orderApi.getOrders(props.storeId)
    
    console.log('🔍 Raw ordersData:', ordersData)
    
    // 기존 페이지네이션 처리 코드는 그대로 유지
    let ordersList = []
    
    if (ordersData && ordersData.content && Array.isArray(ordersData.content)) {
      ordersList = ordersData.content
      console.log('✅ 페이지네이션 응답:', ordersList.length, '개')
    } else if (Array.isArray(ordersData)) {
      ordersList = ordersData
      console.log('✅ 배열 응답:', ordersList.length, '개')
    } else {
      console.error('❌ 예상하지 못한 응답 구조:', ordersData)
      ordersList = []
    }
    
    orders.value = ordersList
    console.log('✅ 주문 목록 로드 완료:', ordersList.length, '개')
    
  } catch (err) {
    console.error('❌ 주문 목록 로드 실패:', err)
    error.value = err.response?.data?.message || err.message || '주문 목록을 불러오는데 실패했습니다.'
    orders.value = []
  } finally {
    loading.value = false
  }
}

// 필터 변경
const filterOrders = () => {
  console.log('🔍 주문 필터 변경:', statusFilter.value || '전체')
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