<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import Logo from '@/components/Logo.vue'
import { LucideShoppingCart, LucideHome, LucideSquareMenu, LucideUser, LucideBell } from 'lucide-vue-next'
import { useNotificationStore } from '@/stores/customer/notification'

const router = useRouter()
const notificationStore = useNotificationStore()

const showNotificationDropdown = ref(false)
const userId = localStorage.getItem('userId')

// 알림 드롭다운 토글
const toggleNotificationDropdown = () => {
  showNotificationDropdown.value = !showNotificationDropdown.value
  if (showNotificationDropdown.value && notificationStore.notifications.length === 0) {
    notificationStore.fetchNotifications(userId)
  }
}

// 알림함 페이지로 이동
const goToNotifications = () => {
  showNotificationDropdown.value = false
  router.push('/customer/notifications')
}

// 알림 읽음 처리
const markAsRead = async (notificationId) => {
  try {
    await notificationStore.markAsRead(notificationId)
  } catch (error) {
    console.error('알림 읽음 처리 실패:', error)
  }
}

// 알림 시간 포맷팅 (UTC + 9시간)
const formatNotificationTime = (dateString) => {
  const date = new Date(dateString)
  // UTC 시간에 9시간(32400000ms) 추가
  const kstDate = new Date(date.getTime() + 9 * 60 * 60 * 1000)
  return kstDate.toLocaleString('ko-KR')
}

// SSE 연결
onMounted(() => {
  if (userId) {
    notificationStore.connectSSE(userId)
    notificationStore.fetchNotifications(userId)
  }
})

onUnmounted(() => {
  notificationStore.disconnectSSE()
})
</script>

<template>
  <header class="flex h-16 w-full items-center justify-between border-b border-neutral-300 bg-white p-4">
    <div class="flex items-center gap-2">
      <Logo />
      <router-link to="/customer" class="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium">
        <lucide-home />
      </router-link>
    </div>

    <div class="flex items-center justify-between gap-2">
      <!-- 알림 버튼 -->
      <div class="relative">
        <button 
          @click="toggleNotificationDropdown"
          class="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium relative"
        >
          <lucide-bell />
          <!-- 읽지 않은 알림 개수 표시 -->
          <span 
            v-if="notificationStore.hasUnreadNotifications" 
            class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center"
          >
            {{ notificationStore.unreadCount }}
          </span>
        </button>
        
        <!-- 알림 드롭다운 -->
        <div 
          v-if="showNotificationDropdown" 
          class="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg border border-gray-200 z-50 max-h-96 overflow-y-auto"
        >
          <div class="p-4 border-b border-gray-200">
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-900">알림</h3>
              <button 
                @click="goToNotifications"
                class="text-sm text-blue-600 hover:text-blue-800"
              >
                전체보기
              </button>
            </div>
          </div>
          
          <div v-if="notificationStore.isLoading" class="p-4 text-center text-gray-500">
            로딩 중...
          </div>
          
          <div v-else-if="notificationStore.notifications.length === 0" class="p-4 text-center text-gray-500">
            새로운 알림이 없습니다
          </div>
          
          <div v-else class="max-h-64 overflow-y-auto">
            <div 
              v-for="notification in notificationStore.notifications.slice(0, 5)" 
              :key="notification.id"
              @click="markAsRead(notification.id)"
              class="p-4 border-b border-gray-100 hover:bg-gray-50 cursor-pointer"
              :class="{ 'bg-blue-50': !notification.isRead }"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <h4 class="text-sm font-medium text-gray-900">{{ notification.title }}</h4>
                  <p class="text-sm text-gray-600 mt-1">{{ notification.content }}</p>
                  <p class="text-xs text-gray-400 mt-1">
                    {{ formatNotificationTime(notification.createdAt) }}
                  </p>
                </div>
                <div v-if="!notification.isRead" class="w-2 h-2 bg-blue-500 rounded-full ml-2"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <router-link to="/customer/cart" class="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium">
        <lucide-shopping-cart />
      </router-link>
      <router-link to="/customer/order-history" class="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium">
        <lucide-square-menu />
      </router-link>
      <router-link to="/customer/mypage" class="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium">
        <lucide-user />
      </router-link>
    </div>
  </header>
</template>
