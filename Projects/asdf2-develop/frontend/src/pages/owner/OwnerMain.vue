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
          :restaurant="restaurant" 
          :store-hours="storeHours" 
        />
        <StoreStatusCard 
          :restaurant="restaurant" 
          :store-hours="storeHours" 
        />
        <RecentOrdersList 
          :recent-orders="recentOrders" 
        />
      </div>

 <!-- 주문 관리 탭 -->
  <div v-if="activeTab === 'orders'" class="tab-content">
    <OrdersList :store-id="currentStoreId" />
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
      @edit-hours="openEditHoursModal"
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

    <EditStoreHoursModal
    v-if="showEditHoursModal"
    :store-hours="storeHours"
    :loading="loading || isProcessing"
    @close="cancelEditHours"
    @save="saveStoreHours"
  />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Store } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { storeApi  } from '@/api/owner/storeApi'

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
import EditStoreHoursModal from '@/components/owner/restaurant/EditStoreHoursModal.vue'

const router = useRouter()

// 반응형 데이터
const activeTab = ref('dashboard')
const showAddMenuModal = ref(false)
const showEditRestaurantModal = ref(false)
const loading = ref(false)
const error = ref(null)
const storeHours = ref([])
const showEditHoursModal = ref(false)

// 순차 처리를 위한 상태 관리
const isProcessing = ref(false)
const currentOperation = ref('')

// 👇 currentStoreId 추가
const currentStoreId = ref(null)

// 👇 대시보드 통계 데이터 추가
const dashboardStats = ref({
  todayOrders: 0,
  todayRevenue: 0,
  todayOrderRate: 0,
  weeklyOrders: 0,
  weeklyRevenue: 0,
  monthlyOrders: 0,
  monthlyRevenue: 0,
  ordersByStatus: {
    pending: 0,
    confirmed: 0,
    preparing: 0,
    ready: 0,
    delivered: 0,
    cancelled: 0
  }
})

const recentOrdersData = ref([])

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

// 기존 목업 데이터들...
const orders = ref([])

const menuItems = ref([])

// 계산된 속성 - 대시보드 데이터 기반으로 수정
const recentOrders = computed(() => recentOrdersData.value.slice(0, 3))
const todayStats = computed(() => ({
  orders: dashboardStats.value.todayOrders,
  revenue: dashboardStats.value.todayRevenue,
  orderRate: dashboardStats.value.todayOrderRate
}))


// API 호출 함수들 - loadStoreInfo 수정
const loadStoreInfo = async () => {
  if (isProcessing.value) {
    console.log('⚠️ 다른 작업이 진행 중입니다.')
    return
  }

  try {
    isProcessing.value = true
    currentOperation.value = '가게 정보 로딩'
    loading.value = true
    error.value = null
    console.log('🔄 가게 정보 로딩 중...')
    
    const response = await storeApi.getMyStore()
    console.log('📦 API 응답:', response)
    
    // 👇 currentStoreId 설정
    currentStoreId.value = response.id

    // 백엔드 응답에 맞게 데이터 설정
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

    // 운영시간 정보 로드 (순차 처리)
    try {
      currentOperation.value = '운영시간 정보 로딩'
      const hoursResponse = await storeApi.getStoreHours()
      storeHours.value = hoursResponse || []
      console.log('✅ 운영시간 로드 완료:', storeHours.value)
    } catch (hoursError) {
      console.warn('❌ 운영시간 로드 실패:', hoursError)
      storeHours.value = []
    }
    
    console.log('✅ 가게 정보 로드 완료:', restaurant.value.name)
    console.log('🏪 현재 가게 ID:', currentStoreId.value)
    
    // 👇 가게 정보 로드 완료 후 대시보드 데이터 로드
    await loadDashboardData()
    
  } catch (err) {
    console.error('❌ 가게 정보 로딩 실패:', err);
    
    // 404 에러면 가게 등록 페이지로 리다이렉트
    if (err.response?.status === 404) {
      console.log('📝 등록된 가게가 없음 - 가게 등록 페이지로 이동');
      alert('등록된 가게가 없습니다. 가게를 등록해주세요.');
      router.push('/store-registration');
      return;
    }
    
    console.log('❌ API 연결 실패 - 목업 데이터 사용')
    // 👇 목업 데이터 사용 시에도 임시 ID 설정
    currentStoreId.value = 1
    
    // 목업 데이터로 fallback
    restaurant.value.id = 1
    restaurant.value.name = '테스트 치킨집'
    restaurant.value.category = 'CHICKEN'
    restaurant.value.rating = 4.5
    restaurant.value.totalOrders = 1250
    
    // 목업 데이터로도 대시보드 데이터 로드
    await loadDashboardData()
    
  } finally {
    loading.value = false
    isProcessing.value = false
    currentOperation.value = ''
  }
}

const loadDashboardData = async () => {
  if (!currentStoreId.value) {
    console.warn('⚠️ 가게 ID가 없어서 대시보드를 로드할 수 없습니다.')
    return
  }

  try {
    currentOperation.value = '대시보드 데이터 로딩'
    console.log('📊 통합 대시보드 데이터 로딩 중...', currentStoreId.value)
    
    // 통합 대시보드 API 호출
    const dashboardResponse = await storeApi.getDashboard()
    console.log('📈 대시보드 응답:', dashboardResponse)
    
    // 받은 데이터를 각각의 상태에 할당
    // 1. 오늘 통계
    dashboardStats.value = {
      todayOrders: dashboardResponse.todayStats.orders || 0,
      todayRevenue: dashboardResponse.todayStats.revenue || 0,
      todayOrderRate: 0, // 백엔드에서 제공하지 않으면 0으로 설정
      weeklyOrders: 0,   // 백엔드에서 제공하지 않으면 0으로 설정
      weeklyRevenue: 0,  // 백엔드에서 제공하지 않으면 0으로 설정
      monthlyOrders: 0,  // 백엔드에서 제공하지 않으면 0으로 설정
      monthlyRevenue: 0, // 백엔드에서 제공하지 않으면 0으로 설정
      ordersByStatus: {
        pending: 0,
        confirmed: 0,
        preparing: 0,
        ready: 0,
        delivered: 0,
        cancelled: 0
      }
    }
    
    // 2. 가게 정보 업데이트 (기존 restaurant.value에 추가 정보 병합)
    if (dashboardResponse.restaurant) {
      restaurant.value = {
        ...restaurant.value, // 기존 정보 유지
        id: dashboardResponse.restaurant.id || restaurant.value.id,
        name: dashboardResponse.restaurant.name || restaurant.value.name,
        rating: dashboardResponse.restaurant.rating || restaurant.value.rating,
        reviewCount: dashboardResponse.restaurant.reviewCount || restaurant.value.reviewCount,
        totalOrders: dashboardResponse.restaurant.totalOrders || restaurant.value.totalOrders
      }
    }
    
    // 3. 운영시간 정보 (이미 별도 API로 로드되므로 선택적으로 업데이트)
    if (dashboardResponse.storeHours && dashboardResponse.storeHours.length > 0) {
      storeHours.value = dashboardResponse.storeHours.map(hour => ({
        dayOfWeek: hour.dayOfWeek,
        openTime: hour.openTime,
        closeTime: hour.closeTime,
        isClosed: hour.isClosed
      }))
    }
    
    // 4. 최근 주문 목록
    recentOrdersData.value = dashboardResponse.recentOrders?.map(order => ({
      id: order.id,
      orderNumber: order.orderNumber,
      customerName: order.customerName,
      total: order.total,
      status: order.status,
      orderTime: order.orderTime,
      items: order.items?.map(item => ({
        name: item.name,
        quantity: item.quantity,
        price: 0 // 백엔드에서 제공하지 않으면 0으로 설정
      })) || []
    })) || []
    
    console.log('✅ 통합 대시보드 데이터 로딩 완료')
    console.log('📊 통계:', dashboardStats.value)
    console.log('🏪 가게:', restaurant.value.name)
    console.log('🛒 최근 주문:', recentOrdersData.value.length, '건')
    
  } catch (error) {
    console.warn('❌ 대시보드 데이터 로드 실패:', error)
    
    // 목업 데이터로 fallback
    console.log('📊 목업 대시보드 데이터 사용')
    dashboardStats.value = {
      todayOrders: 15,
      todayRevenue: 245000,
      todayOrderRate: 12.5,
      weeklyOrders: 98,
      weeklyRevenue: 1580000,
      monthlyOrders: 432,
      monthlyRevenue: 6890000,
      ordersByStatus: {
        pending: 3,
        confirmed: 5,
        preparing: 2,
        ready: 1,
        delivered: 89,
        cancelled: 2
      }
    }
    
    recentOrdersData.value = [
      {
        id: 1,
        orderNumber: 'ORD-20241201-001',
        customerName: '김고객',
        items: [
          { name: '후라이드치킨', quantity: 1, price: 18000 },
          { name: '콜라', quantity: 2, price: 2000 }
        ],
        total: 22000,
        status: 'DELIVERED',
        orderTime: '2024-12-01 18:30:00'
      }
    ]
  }
}

// saveRestaurantInfo 함수에서도 currentStoreId 업데이트
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
    
    // 👇 currentStoreId도 업데이트
    currentStoreId.value = freshData.id
    
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
    console.log('🏪 업데이트된 가게 ID:', currentStoreId.value)
    
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

// 👇 handleRefresh 함수 수정 - 대시보드 데이터도 함께 새로고침
const handleRefresh = async () => {
  console.log('🔄 가게 정보 새로고침 요청됨')
  await loadStoreInfo()
  // loadStoreInfo에서 loadDashboardData를 호출하므로 별도 호출 불필요
}

// 나머지 함수들은 그대로...
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

const openEditHoursModal = () => {
  if (isProcessing.value) {
    alert('다른 작업이 진행 중입니다. 잠시 후 다시 시도해주세요.')
    return
  }
  showEditHoursModal.value = true
}

const cancelEditHours = () => {
  if (isProcessing.value) {
    const confirmed = confirm('작업이 진행 중입니다. 정말 취소하시겠습니까?')
    if (!confirmed) return
  }
  showEditHoursModal.value = false
}

const saveStoreHours = async (hoursData) => {
  if (isProcessing.value) {
    alert('다른 작업이 진행 중입니다. 잠시 후 다시 시도해주세요.')
    return
  }

  try {
    isProcessing.value = true
    loading.value = true
    currentOperation.value = '운영시간 저장'
    
    console.log('🔄 운영시간 저장 시작...', hoursData)
    
    await storeApi.updateStoreHours(hoursData)
    
    alert('운영시간이 성공적으로 저장되었습니다!')
    showEditHoursModal.value = false
    
    // 최신 데이터 다시 로드
    currentOperation.value = '최신 운영시간 불러오기'
    const freshHours = await storeApi.getStoreHours()
    storeHours.value = freshHours || []
    
    console.log('✅ 운영시간 저장 및 업데이트 완료!')
    
  } catch (error) {
    console.error('❌ 운영시간 저장 실패:', error)
    alert('운영시간 저장에 실패했습니다: ' + (error.message || '알 수 없는 오류'))
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
  await loadStoreInfo()
})
</script>

<style scoped>
/* 기존 스타일 그대로 유지 */
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
  max-width: 1400px;
  min-width: 1200px;
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
  max-width: 1400px;
  min-width: 1200px;
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