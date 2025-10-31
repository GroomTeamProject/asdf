import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { notificationApi } from '@/api/customer/notificationApi'

export const useNotificationStore = defineStore('notification', () => {
  // 상태
  const notifications = ref([])
  const unreadCount = ref(0)
  const isLoading = ref(false)
  const error = ref(null)
  const sseConnection = ref(null)
  const paginationInfo = ref({
    totalPages: 0,
    totalElements: 0,
    currentPage: 0,
    hasNext: false
  })

  // 계산된 속성
  const hasUnreadNotifications = computed(() => unreadCount.value > 0)

  // 액션
  const fetchNotifications = async (userId, page = 0, size = 20) => {
    try {
      isLoading.value = true
      error.value = null
      
      const response = await notificationApi.getUserNotifications(userId, page, size)
      
      if (page === 0) {
        // 첫 페이지는 교체
        notifications.value = response.content || []
      } else {
        // 추가 페이지는 추가
        notifications.value = [...notifications.value, ...(response.content || [])]
      }
      
      // 페이징 정보 업데이트
      paginationInfo.value = {
        totalPages: response.totalPages || 0,
        totalElements: response.totalElements || 0,
        currentPage: response.number || page,
        hasNext: !response.last && (response.number || page) < (response.totalPages || 0) - 1
      }
      
      // 읽지 않은 알림 수 계산
      unreadCount.value = notifications.value.filter(notification => !notification.isRead).length
      
      return response
    } catch (err) {
      error.value = err.message
      console.error('알림 조회 실패:', err)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const markAsRead = async (notificationId) => {
    try {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        throw new Error('사용자 ID가 없습니다.')
      }

      // API 호출
      await notificationApi.markAsRead(notificationId, userId)
      
      // 로컬 상태 업데이트
      const notification = notifications.value.find(n => n.id === notificationId)
      if (notification && !notification.isRead) {
        notification.isRead = true
        unreadCount.value = Math.max(0, unreadCount.value - 1)
      }
    } catch (error) {
      console.error('알림 읽음 처리 실패:', error)
      throw error
    }
  }

  const markAllAsRead = async () => {
    try {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        throw new Error('사용자 ID가 없습니다.')
      }

      // API 호출
      await notificationApi.markAllAsRead(userId)
      
      // 로컬 상태 업데이트
      notifications.value.forEach(notification => {
        notification.isRead = true
      })
      unreadCount.value = 0
    } catch (error) {
      console.error('모든 알림 읽음 처리 실패:', error)
      throw error
    }
  }

  const addNotification = (notification) => {
    notifications.value.unshift(notification)
    if (!notification.isRead) {
      unreadCount.value += 1
    }
  }

  const connectSSE = (userId) => {
    if (sseConnection.value) {
      sseConnection.value.close()
    }
    
    sseConnection.value = notificationApi.connectSSE(userId)
    
    sseConnection.value.onmessage = (event) => {
      try {
        const notification = JSON.parse(event.data)
        addNotification(notification)
      } catch (err) {
        console.error('SSE 메시지 파싱 실패:', err)
      }
    }
    
    sseConnection.value.onerror = (err) => {
      console.error('SSE 연결 오류:', err)
    }
  }

  const disconnectSSE = () => {
    if (sseConnection.value) {
      sseConnection.value.close()
      sseConnection.value = null
    }
  }

  const clearNotifications = () => {
    notifications.value = []
    unreadCount.value = 0
    error.value = null
    paginationInfo.value = {
      totalPages: 0,
      totalElements: 0,
      currentPage: 0,
      hasNext: false
    }
  }

  return {
    // 상태
    notifications,
    unreadCount,
    isLoading,
    error,
    sseConnection,
    paginationInfo,
    
    // 계산된 속성
    hasUnreadNotifications,
    
    // 액션
    fetchNotifications,
    markAsRead,
    markAllAsRead,
    addNotification,
    connectSSE,
    disconnectSSE,
    clearNotifications
  }
})
