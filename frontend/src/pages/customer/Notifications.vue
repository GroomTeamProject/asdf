<template>
  <!-- 헤더 배너 -->
  <HeaderBanner title="알림함" />

  <!-- 페이지 컨테이너 -->
  <CustomerContainer max-width="4xl" padding="4" custom-class="space-y-6">
    <!-- 알림 필터 및 액션 -->
    <section class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-semibold text-gray-900">알림 관리</h2>
        <div class="flex gap-2">
          <button
            @click="markAllAsRead"
            :disabled="notificationStore.unreadCount === 0"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed text-sm"
          >
            모두 읽음
          </button>
          <button
            @click="refreshNotifications"
            :disabled="notificationStore.isLoading"
            class="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 disabled:bg-gray-300 disabled:cursor-not-allowed text-sm"
          >
            새로고침
          </button>
        </div>
      </div>

      <!-- 필터 탭 -->
      <div class="flex gap-2">
        <button
          @click="currentFilter = 'unread'"
          :class="currentFilter === 'unread' ? 'bg-blue-100 text-blue-700 border-blue-300' : 'bg-gray-50 text-gray-700 border-gray-200'"
          class="px-4 py-2 border rounded-lg text-sm font-medium transition-colors"
        >
          읽지 않음 ({{ notificationStore.unreadCount }})
        </button>
        <button
          @click="currentFilter = 'all'"
          :class="currentFilter === 'all' ? 'bg-blue-100 text-blue-700 border-blue-300' : 'bg-gray-50 text-gray-700 border-gray-200'"
          class="px-4 py-2 border rounded-lg text-sm font-medium transition-colors"
        >
          전체 ({{ notificationStore.notifications.length }})
        </button>
      </div>
    </section>

    <!-- 알림 목록 -->
    <section class="bg-white rounded-lg shadow-sm border border-gray-200">
      <!-- 로딩 상태 -->
      <div v-if="notificationStore.isLoading" class="p-8 text-center">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <p class="mt-2 text-gray-500">알림을 불러오는 중...</p>
      </div>

      <!-- 에러 상태 -->
      <div v-else-if="notificationStore.error" class="p-8 text-center">
        <p class="text-red-500 mb-4">{{ notificationStore.error }}</p>
        <button @click="refreshNotifications" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">다시 시도</button>
      </div>

      <!-- 빈 상태 -->
      <div v-else-if="filteredNotifications.length === 0" class="p-8 text-center">
        <div class="text-6xl mb-4">🔔</div>
        <h3 class="text-lg font-medium text-gray-900 mb-2">
          {{ currentFilter === 'unread' ? '읽지 않은 알림이 없습니다' : '알림이 없습니다' }}
        </h3>
        <p class="text-gray-500">
          {{ currentFilter === 'unread' ? '모든 알림을 확인했습니다.' : '새로운 알림이 오면 여기에 표시됩니다.' }}
        </p>
      </div>

      <!-- 알림 목록 -->
      <div v-else class="divide-y divide-gray-200">
        <div
          v-for="notification in filteredNotifications"
          :key="notification.id"
          @click="markAsRead(notification.id)"
          class="p-6 hover:bg-gray-50 cursor-pointer transition-colors"
          :class="{ 'bg-blue-50': !notification.isRead }"
        >
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <h4 class="text-lg font-medium text-gray-900">{{ notification.title }}</h4>
                <span v-if="!notification.isRead" class="inline-block w-2 h-2 bg-blue-500 rounded-full"></span>
              </div>

              <p class="text-gray-600 mb-3">{{ notification.content }}</p>

              <div class="flex items-center gap-4 text-sm text-gray-500">
                <span class="flex items-center gap-1">
                  <span class="w-2 h-2 rounded-full" :class="getTypeColor(notification.type)"></span>
                  {{ getTypeLabel(notification.type) }}
                </span>
                <span>{{ formatDate(notification.createdAt) }}</span>
              </div>
            </div>

            <div class="ml-4">
              <button
                @click.stop="markAsRead(notification.id)"
                v-if="!notification.isRead"
                class="text-blue-600 hover:text-blue-800 text-sm font-medium"
              >
                읽음
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 더보기 버튼 -->
      <div v-if="hasMoreNotifications" class="p-4 text-center border-t border-gray-200">
        <button 
          @click="loadMoreNotifications"
          :disabled="notificationStore.isLoading"
          class="px-6 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 disabled:bg-gray-50 disabled:cursor-not-allowed"
        >
          {{ notificationStore.isLoading ? '로딩 중...' : '더보기' }}
        </button>
        <!-- 디버깅 정보 (개발용) -->
        <div class="mt-2 text-xs text-gray-400">
          현재: {{ notificationStore.paginationInfo.currentPage + 1 }}페이지 / 
          전체: {{ notificationStore.paginationInfo.totalPages }}페이지 
          ({{ notificationStore.notifications.length }}/{{ notificationStore.paginationInfo.totalElements }}개)
        </div>
      </div>
    </section>
  </CustomerContainer>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useNotificationStore } from '@/stores/customer/notification'
import CustomerContainer from '@/components/customer/CustomerContainer.vue'
import HeaderBanner from '@/components/common/HeaderBanner.vue'

export default {
  name: 'Notifications',
  components: {
    CustomerContainer,
    HeaderBanner,
  },
  setup() {
    const notificationStore = useNotificationStore()
    const currentFilter = ref('unread')
    const currentPage = ref(0)
    const pageSize = 20

    // 필터된 알림 목록
    const filteredNotifications = computed(() => {
      let notifications = notificationStore.notifications

      if (currentFilter.value === 'unread') {
        notifications = notifications.filter((notification) => !notification.isRead)
      }

      return notifications
    })

    // 더 많은 알림이 있는지 확인 (서버 페이징 정보 기반)
    const hasMoreNotifications = computed(() => {
      return notificationStore.paginationInfo.hasNext
    })

    // 알림 타입별 색상
    const getTypeColor = (type) => {
      const colors = {
        ORDER_STATUS: 'bg-green-500',
        PROMOTION: 'bg-purple-500',
        REVIEW_REQUEST: 'bg-yellow-500',
        DELIVERY_ARRIVAL: 'bg-blue-500',
      }
      return colors[type] || 'bg-gray-500'
    }

    // 알림 타입별 라벨
    const getTypeLabel = (type) => {
      const labels = {
        ORDER_STATUS: '주문 상태',
        PROMOTION: '프로모션',
        REVIEW_REQUEST: '리뷰 요청',
        DELIVERY_ARRIVAL: '배달 도착',
      }
      return labels[type] || '알림'
    }

    // 날짜 포맷팅 (UTC + 9시간)
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      // UTC 시간에 9시간(32400000ms) 추가
      const kstDate = new Date(date.getTime() + 9 * 60 * 60 * 1000)
      const now = new Date()
      const diff = now - kstDate

      if (diff < 60000) {
        // 1분 미만
        return '방금 전'
      } else if (diff < 3600000) {
        // 1시간 미만
        return `${Math.floor(diff / 60000)}분 전`
      } else if (diff < 86400000) {
        // 1일 미만
        return `${Math.floor(diff / 3600000)}시간 전`
      } else {
        return kstDate.toLocaleDateString('ko-KR')
      }
    }

    // 알림 읽음 처리
    const markAsRead = async (notificationId) => {
      try {
        await notificationStore.markAsRead(notificationId)
      } catch (error) {
        console.error('알림 읽음 처리 실패:', error)
        alert('알림 읽음 처리에 실패했습니다.')
      }
    }

    // 모든 알림 읽음 처리
    const markAllAsRead = async () => {
      try {
        await notificationStore.markAllAsRead()
        alert('모든 알림을 읽음 처리했습니다.')
      } catch (error) {
        console.error('모든 알림 읽음 처리 실패:', error)
        alert('모든 알림 읽음 처리에 실패했습니다.')
      }
    }

    // 알림 새로고침
    const refreshNotifications = async () => {
      currentPage.value = 0
      const userId = localStorage.getItem('userId')
      await notificationStore.fetchNotifications(userId, 0, pageSize)
    }

    // 더 많은 알림 로드
    const loadMoreNotifications = async () => {
      currentPage.value += 1
      const userId = localStorage.getItem('userId')
      await notificationStore.fetchNotifications(userId, currentPage.value, pageSize)
    }

    // 페이지 로드 시 알림 가져오기
    onMounted(async () => {
      const userId = localStorage.getItem('userId')
      if (userId) {
        await notificationStore.fetchNotifications(userId, 0, pageSize)
      }
    })

    return {
      notificationStore,
      currentFilter,
      filteredNotifications,
      hasMoreNotifications,
      getTypeColor,
      getTypeLabel,
      formatDate,
      markAsRead,
      markAllAsRead,
      refreshNotifications,
      loadMoreNotifications,
    }
  },
}
</script>
