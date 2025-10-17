<template>
  <div class="owner-dashboard">
    <!-- Loading Overlay -->
    <div v-if="isProcessing" class="loading-overlay">
      <div class="loading-message">
        <div class="spinner"></div>
        <p>{{ currentOperation }}... 잠시만 기다려주세요.</p>
      </div>
    </div>

    <!-- Header -->
    <header class="dashboard-header">
      <div class="header-container">
        <div class="header-left">
          <Store class="header-icon" />
          <h1 class="header-title">점주 관리</h1>
        </div>
        <button 
          @click="goHome"
          class="role-change-btn"
          :disabled="isProcessing"
        >
          역할 변경
        </button>
      </div>
    </header>

    <!-- Navigation Tabs -->
    <div class="main-container">
      <TabNavigation 
        :active-tab="activeTab" 
        @update:active-tab="activeTab = $event" 
      />

      <!-- Dashboard Tab -->
      <div v-if="activeTab === 'dashboard'" class="tab-content">
      <StatsCards 
        :today-stats="todayStats" 
        :restaurant="dashboardData.restaurant" 
        :store-hours="dashboardData.storeHours" 
      />
      <StoreStatusCard 
        :restaurant="dashboardData.restaurant" 
        :store-hours="dashboardData.storeHours" 
      />
      <RecentOrdersList 
        :recent-orders="recentOrders" 
      />
    </div>

      <!-- Orders Tab -->
      <div v-if="activeTab === 'orders'" class="tab-content">
        <OrdersList 
          :orders="orders" 
          @update-status="updateOrderStatus"
        />
      </div>

      <!-- Menu Tab -->
      <div v-if="activeTab === 'menu'" class="tab-content">
        <MenuList 
          :menu-items="menuItems"
          @toggle-availability="toggleMenuAvailability"
          @delete-menu="deleteMenu"
          @add-menu="showAddMenuModal = true"
        />
      </div>

      <!-- Restaurant Tab -->
      <div v-if="activeTab === 'restaurant'" class="tab-content">
    <RestaurantInfo 
      :restaurant="restaurant"
      :store-hours="storeHours"
      @edit="openEditRestaurantModal"
      @refresh="handleRefresh"
    />
  </div>
    </div>

    <!-- Modals -->
    <AddMenuModal 
      v-if="showAddMenuModal"
      @close="hideAddMenuModal"
      @add="addNewMenu"
    />

    <EditRestaurantModal
      v-if="showEditRestaurantModal"
      :restaurant="restaurant"
      :loading="loading || isProcessing"
      @close="cancelEdit"
      @save="saveRestaurantInfo"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Store } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { storeApi } from '@/api/owner/storeApi'

// Components
import TabNavigation from '@/components/owner/common/TabNavigation.vue'
import StatsCards from '@/components/owner/dashboard/StatsCards.vue'
import StoreStatusCard from '@/components/owner/dashboard/StoreStatusCard.vue'
import RecentOrdersList from '@/components/owner/dashboard/RecentOrdersList.vue'
import OrdersList from '@/components/owner/orders/OrdersList.vue'
import MenuList from '@/components/owner/menu/MenuList.vue'
import RestaurantInfo from '@/components/owner/restaurant/RestaurantInfo.vue'
import AddMenuModal from '@/components/owner/menu/AddMenuModal.vue'
import EditRestaurantModal from '@/components/owner/restaurant/EditRestaurantModal.vue'

const router = useRouter()

// 반응형 데이터
const activeTab = ref('dashboard')
const showAddMenuModal = ref(false)
const showEditRestaurantModal = ref(false)
const loading = ref(false)
const error = ref(null)
const storeHours = ref([])

// 순차 처리를 위한 상태 관리
const isProcessing = ref(false)
const currentOperation = ref('')

// 대시보드 데이터 - 백엔드 응답 구조에 맞게 수정
const dashboardData = ref({
  todayStats: {
    orders: 0,
    revenue: 0
  },
  restaurant: {
    id: null,
    name: '',
    rating: 0,
    reviewCount: 0,
    totalOrders: 0
  },
  storeHours: [],
  recentOrders: []
})

// 가게 정보
const restaurant = ref({
  id: null,
  businessNumber: '',
  name: '',
  description: '',
  phone: '',
  address: '',
  detailAddress: '',
  category: '',
  minOrderAmount: 0,
  deliveryFee: 0,
  deliveryTimeMin: 0,
  deliveryTimeMax: 0,
  imageUrl: '',
  openTime:'',
  closeTime:'',
  status: 'OPEN',
  rating: 0,
  reviewCount: 0,
  totalOrders: 0,
  isActive: true
})

// 목업 데이터 (나중에 실제 API로 교체 예정)
const orders = ref([
  {
    id: '1',
    customerName: '김고객',
    items: [
      { name: '후라이드 치킨', quantity: 1, price: 18000 },
      { name: '치킨무', quantity: 1, price: 2000 }
    ],
    total: 20000,
    status: 'pending',
    orderTime: '2024-01-15 14:30'
  },
  {
    id: '2',
    customerName: '이고객',
    items: [
      { name: '양념 치킨', quantity: 2, price: 20000 }
    ],
    total: 40000,
    status: 'preparing',
    orderTime: '2024-01-15 14:25'
  },
  {
    id: '3',
    customerName: '박고객',
    items: [
      { name: '후라이드 치킨', quantity: 1, price: 18000 }
    ],
    total: 18000,
    status: 'ready',
    orderTime: '2024-01-15 14:20'
  }
])

const menuItems = ref([
  {
    id: '1',
    name: '후라이드 치킨',
    description: '바삭바삭한 클래식 후라이드',
    price: 18000,
    category: '치킨',
    available: true
  },
  {
    id: '2',
    name: '양념 치킨',
    description: '달콤매콤한 양념치킨',
    price: 20000,
    category: '치킨',
    available: true
  },
  {
    id: '3',
    name: '치킨무',
    description: '아삭한 치킨무',
    price: 2000,
    category: '사이드',
    available: false
  }
])

// 계산된 속성 - 대시보드 데이터 사용
const recentOrders = computed(() => dashboardData.value.recentOrders || [])
const todayStats = computed(() => dashboardData.value.todayStats || { orders: 0, revenue: 0 })

// 대시보드 데이터 로드 함수 추가
// const loadDashboardData = async () => {
//   try {
//     console.log('🔄 대시보드 데이터 로딩 중...')
    
//     // API 호출 확인을 위한 로그 추가
//     console.log('📡 storeApi.getDashboard() 호출 시작')
//     const response = await storeApi.getDashboard()
//     console.log('📦 대시보드 API 응답:', response)
    
//     // 대시보드 데이터 설정
//     dashboardData.value = {
//       todayStats: response.todayStats || { orders: 0, revenue: 0 },
//       restaurant: response.restaurant || { 
//         id: null, 
//         name: '', 
//         rating: 0, 
//         reviewCount: 0, 
//         totalOrders: 0 
//       },
//       storeHours: response.storeHours || [],
//       recentOrders: response.recentOrders || []
//     }
    
//     console.log('✅ 대시보드 데이터 설정 완료:', dashboardData.value)
    
//   } catch (err) {
//     console.error('❌ 대시보드 데이터 로드 실패:', err)
//     console.error('❌ 에러 상세:', err.response?.data)
//     console.error('❌ 상태 코드:', err.response?.status)
    
//     // 실패 시 목업 데이터 사용
//     dashboardData.value = {
//       todayStats: { orders: 15, revenue: 450000 },
//       restaurant: { 
//         id: 1, 
//         name: '테스트 치킨집', 
//         rating: 4.5, 
//         reviewCount: 127, 
//         totalOrders: 1234 
//       },
//       storeHours: [],
//       recentOrders: []
//     }
//     console.log('📝 목업 데이터로 설정됨:', dashboardData.value)
//   }
// }

// // API 호출 함수들
// const loadStoreInfo = async () => {
//   if (isProcessing.value) {
//     console.log('⚠️ 다른 작업이 진행 중입니다.')
//     return
//   }

//   try {
//     isProcessing.value = true
//     loading.value = true
//     error.value = null
//     console.log('🔄 전체 데이터 로딩 시작...')
    
//     // 1. 대시보드 데이터 로드 (isProcessing 관리 없이)
//     currentOperation.value = '대시보드 데이터 로딩'
//     console.log('1️⃣ 대시보드 데이터 로드 시작')
//     await loadDashboardData()  // 이제 isProcessing 체크 없음
//     console.log('1️⃣ 대시보드 데이터 로드 완료')
    
//     // 2. 편집용 가게 정보 로드
//     currentOperation.value = '가게 정보 로딩'
//     console.log('2️⃣ 가게 정보 로드 시작')
//     const response = await storeApi.getMyStore()
//     console.log('2️⃣ 가게 정보 API 응답:', response)
    
//     // 편집용 restaurant 객체 설정
//     restaurant.value = {
//       id: response.id || null,
//       businessNumber: response.businessNumber || '',
//       name: response.name || '',
//       description: response.description || '',
//       phone: response.phone || '',
//       address: response.address || '',
//       detailAddress: response.detailAddress || '',
//       category: response.category || '',
//       minOrderAmount: response.minOrderAmount || 0,
//       deliveryFee: response.deliveryFee || 0,
//       deliveryTimeMin: response.deliveryTimeMin || 0,
//       deliveryTimeMax: response.deliveryTimeMax || 0,
//       imageUrl: response.imageUrl || '',
//       status: response.status || 'OPEN',
//       rating: response.rating || 0,
//       reviewCount: response.reviewCount || 0,
//       totalOrders: response.totalOrders || 0,
//       isActive: response.isActive !== undefined ? response.isActive : true
//     }
//     console.log('2️⃣ 편집용 가게 정보 설정 완료')

//     // 3. 운영시간 정보 로드
//     try {
//       currentOperation.value = '운영시간 정보 로딩'
//       console.log('3️⃣ 운영시간 정보 로드 시작')
//       const hoursResponse = await storeApi.getStoreHours()
//       storeHours.value = hoursResponse || []
//       console.log('3️⃣ 운영시간 로드 완료:', storeHours.value)
//     } catch (hoursError) {
//       console.warn('❌ 운영시간 로드 실패:', hoursError)
//       storeHours.value = []
//     }
    
//     console.log('🎉 전체 데이터 로드 완료!')
//     console.log('🏪 현재 가게 ID:', restaurant.value.id)
    
//   } catch (err) {
//     console.log('❌ API 연결 실패 - 목업 데이터 사용')
//     // 목업 데이터로 fallback
//     restaurant.value.name = '테스트 치킨집'
//     restaurant.value.category = 'CHICKEN'
//     restaurant.value.rating = 4.5
//     restaurant.value.totalOrders = 1250
    
//   } finally {
//     loading.value = false
//     isProcessing.value = false
//     currentOperation.value = ''
//   }
// }

const handleRefresh = async () => {
  console.log('🔄 데이터 새로고침 요청됨')
  await loadStoreInfo() // 전체 데이터 다시 로드
}

const openEditRestaurantModal = () => {
  if (isProcessing.value) {
    alert('다른 작업이 진행 중입니다. 잠시 후 다시 시도해주세요.')
    return
  }
  showEditRestaurantModal.value = true
}

const cancelEdit = () => {
  if (isProcessing.value) {
    const confirmed = confirm('작업이 진행 중입니다. 정말 취소하시겠습니까?')
    if (!confirmed) return
  }
  showEditRestaurantModal.value = false
}

const saveRestaurantInfo = async (formData) => {
  if (isProcessing.value) {
    alert('다른 작업이 진행 중입니다. 잠시 후 다시 시도해주세요.')
    return
  }

  try {
    isProcessing.value = true
    loading.value = true
    console.log('🔄 서버에 가게 정보 저장 시작...')
    console.log('📤 저장할 데이터:', formData)
    
    // 1. 기본 가게 정보 수정
    currentOperation.value = '기본 정보 저장'
    const storeResult = await storeApi.updateStore(formData.restaurant)
    console.log('✅ 기본 정보 저장 응답:', storeResult)
    
    // 2. 연락처 수정 (변경된 경우에만)
    if (formData.contact.phone !== restaurant.value.phone) {
      currentOperation.value = '연락처 정보 저장'
      const contactResult = await storeApi.updateContact(formData.contact)
      console.log('✅ 연락처 정보 저장 응답:', contactResult)
    }
    
    // 3. 배달 설정 수정
    currentOperation.value = '배달 설정 저장'
    const deliveryResult = await storeApi.updateDelivery(formData.delivery)
    console.log('✅ 배달 설정 저장 응답:', deliveryResult)
    
    // 4. 위치 정보 수정 (변경된 경우에만)
    if (formData.location.address !== restaurant.value.address || 
        formData.location.detailAddress !== restaurant.value.detailAddress) {
      currentOperation.value = '위치 정보 저장'
      const locationResult = await storeApi.updateLocation(formData.location)
      console.log('✅ 위치 정보 저장 응답:', locationResult)
    }
    
    // 5. 강제로 최신 데이터 다시 로드 (캐시 무시)
    currentOperation.value = '최신 정보 불러오기'
    console.log('🔄 최신 데이터 강제 로드 시작...')
    
    // 조금 대기 후 다시 로드 (서버 업데이트 반영 시간)
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const freshData = await storeApi.getMyStore()
    console.log('📦 최신 데이터:', freshData)
    
    // 명시적으로 모든 필드 업데이트
    restaurant.value = {
      id: freshData.id || null,
      businessNumber: freshData.businessNumber || '',
      name: freshData.name || '',
      description: freshData.description || '',
      phone: freshData.phone || '',
      address: freshData.address || '',
      detailAddress: freshData.detailAddress || '',
      category: freshData.category || '',
      minOrderAmount: freshData.minOrderAmount || 0,
      deliveryFee: freshData.deliveryFee || 0,
      deliveryTimeMin: freshData.deliveryTimeMin || 0,
      deliveryTimeMax: freshData.deliveryTimeMax || 0,
      imageUrl: freshData.imageUrl || '',
      status: freshData.status || 'OPEN',
      rating: freshData.rating || 0,
      reviewCount: freshData.reviewCount || 0,
      totalOrders: freshData.totalOrders || 0,
      isActive: freshData.isActive !== undefined ? freshData.isActive : true
    }
    
    console.log('✅ 업데이트된 restaurant 데이터:', restaurant.value)
    
    showEditRestaurantModal.value = false
    console.log('🎉 모든 정보 저장 및 업데이트 완료!')
    alert('가게 정보가 성공적으로 저장되었습니다!')
    
  } catch (err) {
    console.error('❌ 서버 저장 실패:', err)
    alert('서버 저장에 실패했습니다: ' + (err.message || '알 수 없는 오류'))
  } finally {
    loading.value = false
    isProcessing.value = false
    currentOperation.value = ''
  }
}

// 주문 상태 업데이트
const updateOrderStatus = (orderId, newStatus) => {
  if (isProcessing.value) {
    alert('다른 작업이 진행 중입니다.')
    return
  }
  
  const order = orders.value.find(o => o.id === orderId)
  if (order) {
    order.status = newStatus
  }
}

// 메뉴 관련 함수들
const toggleMenuAvailability = (itemId) => {
  if (isProcessing.value) {
    alert('다른 작업이 진행 중입니다.')
    return
  }
  
  const item = menuItems.value.find(m => m.id === itemId)
  if (item) {
    item.available = !item.available
  }
}

const deleteMenu = (itemId) => {
  if (isProcessing.value) {
    alert('다른 작업이 진행 중입니다.')
    return
  }
  
  const index = menuItems.value.findIndex(m => m.id === itemId)
  if (index > -1) {
    menuItems.value.splice(index, 1)
  }
}

const hideAddMenuModal = () => {
  showAddMenuModal.value = false
}

const addNewMenu = (menuData) => {
  if (isProcessing.value) {
    alert('다른 작업이 진행 중입니다.')
    return
  }
  
  const newItem = {
    id: Date.now().toString(),
    name: menuData.name,
    description: menuData.description,
    price: menuData.price,
    category: menuData.category,
    available: true
  }

  menuItems.value.push(newItem)
  hideAddMenuModal()
}

// 홈으로 이동
const goHome = () => {
  if (isProcessing.value) {
    const confirmed = confirm('작업이 진행 중입니다. 정말 이동하시겠습니까?')
    if (!confirmed) return
  }
  router.push('/')
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(async () => {
  console.log('🚀 점주 관리 시스템 시작')
  console.log('🔥 onMounted 실행됨 - 테스트 시작!')

  try {
    console.log('🧪 테스트: 대시보드 API 직접 호출 (큐 우회)')
    const dashboardResponse = await storeApi.getDashboardDirect()  // 🔥 이렇게 사용
    console.log('🎯 대시보드 테스트 응답:', dashboardResponse)
  } catch (error) {
    console.error('❌ 대시보드 테스트 실패:', error)
    console.error('❌ 에러 상태:', error.response?.status)
    console.error('❌ 에러 메시지:', error.response?.data)
  }
  
  try {
    isProcessing.value = true
    currentOperation.value = '데이터 로딩'
    loading.value = true
    
    console.log('📡 모든 API 병렬 호출 시작')
    
    // 모든 API를 병렬로 호출
    const [dashboardResult, storeResult, hoursResult] = await Promise.allSettled([
      storeApi.getDashboard(),
      storeApi.getMyStore(),
      storeApi.getStoreHours()
    ])
    
    // 1. 대시보드 데이터 처리
    if (dashboardResult.status === 'fulfilled') {
      console.log('📦 대시보드 API 응답:', dashboardResult.value)
      dashboardData.value = {
        todayStats: dashboardResult.value.todayStats || { orders: 0, revenue: 0 },
        restaurant: dashboardResult.value.restaurant || { 
          id: null, name: '', rating: 0, reviewCount: 0, totalOrders: 0 
        },
        storeHours: dashboardResult.value.storeHours || [],
        recentOrders: dashboardResult.value.recentOrders || []
      }
      console.log('✅ 대시보드 데이터 설정 완료:', dashboardData.value)
    } else {
      console.error('❌ 대시보드 로드 실패:', dashboardResult.reason)
      // 목업 데이터 설정
      dashboardData.value = {
        todayStats: { orders: 15, revenue: 450000 },
        restaurant: { 
          id: 1, name: '테스트 치킨집', rating: 4.5, reviewCount: 127, totalOrders: 1234 
        },
        storeHours: [],
        recentOrders: []
      }
    }
    
    // 2. 가게 정보 처리
    if (storeResult.status === 'fulfilled') {
      const response = storeResult.value
      console.log('📦 가게 정보 API 응답:', response)
      restaurant.value = {
        id: response.id || null,
        businessNumber: response.businessNumber || '',
        name: response.name || '',
        description: response.description || '',
        phone: response.phone || '',
        address: response.address || '',
        detailAddress: response.detailAddress || '',
        category: response.category || '',
        minOrderAmount: response.minOrderAmount || 0,
        deliveryFee: response.deliveryFee || 0,
        deliveryTimeMin: response.deliveryTimeMin || 0,
        deliveryTimeMax: response.deliveryTimeMax || 0,
        imageUrl: response.imageUrl || '',
        status: response.status || 'OPEN',
        rating: response.rating || 0,
        reviewCount: response.reviewCount || 0,
        totalOrders: response.totalOrders || 0,
        isActive: response.isActive !== undefined ? response.isActive : true
      }
      console.log('✅ 가게 정보 설정 완료')
    } else {
      console.error('❌ 가게 정보 로드 실패:', storeResult.reason)
    }
    
    // 3. 운영시간 처리
    if (hoursResult.status === 'fulfilled') {
      storeHours.value = hoursResult.value || []
      console.log('✅ 운영시간 설정 완료:', storeHours.value)
    } else {
      console.error('❌ 운영시간 로드 실패:', hoursResult.reason)
      storeHours.value = []
    }
    
    console.log('🎉 모든 데이터 로드 완료!')
    
  } catch (err) {
    console.error('❌ 전체 데이터 로드 실패:', err)
  } finally {
    loading.value = false
    isProcessing.value = false
    currentOperation.value = ''
  }
})
</script>

<style scoped>
/* 고정 크기 및 안정적인 레이아웃 */
.owner-dashboard {
  min-height: 100vh;
  background-color: #f5f5f5;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-size: 14px;
  overflow-x: auto;
}

/* Header 스타일 */
.dashboard-header {
  background: white;
  border-bottom: 2px solid #d1d5db;
  padding: 1rem;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-container {
  max-width: 1400px; /* 고정 최대 너비 */
  min-width: 1200px; /* 최소 너비 설정 */
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.header-icon {
  width: 24px;
  height: 24px;
  color: #6b7280;
}

.header-title {
  font-size: 20px;
  color: #1f2937;
  font-weight: 600;
  margin: 0;
}

.role-change-btn {
  border: 1px solid #9ca3af;
  color: #374151;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 14px;
}

.role-change-btn:hover:not(:disabled) {
  background-color: #f3f4f6;
}

.role-change-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Main Container */
.main-container {
  max-width: 1400px; /* 고정 최대 너비 */
  min-width: 1200px; /* 최소 너비 설정 */
  margin: 0 auto;
  padding: 1.5rem;
}

.tab-content {
  margin-top: 1.5rem;
}

/* Loading Overlay */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-message {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  min-width: 300px;
}

.loading-message p {
  margin: 0;
  color: #374151;
  font-weight: 500;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 반응형 - 화면이 너무 작을 때 */
@media (max-width: 1200px) {
  .header-container,
  .main-container {
    min-width: auto;
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .owner-dashboard {
    font-size: 13px;
  }
  
  .header-title {
    font-size: 18px;
  }
}

/* 큰 화면에서 너무 늘어나지 않도록 */
@media (min-width: 1600px) {
  .header-container,
  .main-container {
    max-width: 1500px;
  }
}

/* 컴포넌트별 고정 스타일 */
:deep(.stats-card) {
  min-height: 120px;
}

:deep(.store-status-card) {
  min-height: 200px;
}

:deep(.orders-list) {
  min-height: 400px;
}

:deep(.menu-list) {
  min-height: 500px;
}

:deep(.restaurant-info) {
  min-height: 600px;
}
</style>