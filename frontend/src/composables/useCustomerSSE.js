/**
 * 사용자 SSE 훅
 * 사용자 페이지에서 사용하는 SSE 연결 및 알림 관리
 * 현재는 'customer-notification' 이벤트만 처리합니다.
 */
import { ref, onMounted, onUnmounted } from 'vue'
import sseManager from '@/utils/sse.js'

export function useCustomerSSE() {
  const isConnected = ref(false)
  const notifications = ref([])
  const currentUserId = ref(null)

  /**
   * SSE 연결 시작
   */
  const connectSSE = (userId) => {
    if (!userId) {
      console.warn('SSE 연결을 위해 userId가 필요합니다.')
      return
    }

    currentUserId.value = userId
    sseManager.connect(userId, 'CUSTOMER')
    
    // 연결 상태 업데이트
    isConnected.value = sseManager.getConnectionStatus()
    
    // customer-notification 이벤트 리스너 등록
    setupCustomerNotificationListeners()
  }

  /**
   * 알림 이벤트 리스너 설정
   */
  const setupCustomerNotificationListeners = () => {
    // 'customer-notification' 알림
    sseManager.addEventListener('customer-notification', (event) => {
      handleCustomerNotification(event)
    })

    console.log('🔔 고객 알림 이벤트 리스너 등록 완료')
  }

  /**
   * 고객 알림 처리
   * @param {Event} event - SSE 이벤트
   */
  const handleCustomerNotification = (event) => {
    console.log('🔔 [useCustomerSSE] customer-notification 이벤트 수신:', event.data)
    
    try {
      const data = JSON.parse(event.data)
      console.log('🔔 [useCustomerSSE] 파싱된 데이터:', data)
      
      const title = data.title || '알림'
      const message = data.message || data.content || event.data
      const type = data.type || 'info'
      
      console.log('🔔 [useCustomerSSE] 알림 표시:', { title, message, type })
      showNotification(title, message, type)
    } catch (error) {
      console.log('🔔 [useCustomerSSE] JSON 파싱 실패, 원본 데이터로 처리:', event.data)
      // JSON 파싱 실패 시 원본 데이터로 처리
      showNotification('알림', event.data, 'info')
    }
  }

  /**
   * 알림 표시
   * @param {string} title - 알림 제목
   * @param {string} message - 알림 메시지
   * @param {string} type - 알림 타입 (info, success, warning, error)
   */
  const showNotification = (title, message, type = 'info') => {
    const notification = {
      id: Date.now(),
      title,
      message,
      type,
      timestamp: new Date()
    }
    
    notifications.value.push(notification)
    
    // 5초 후 자동 제거
    setTimeout(() => {
      removeNotification(notification.id)
    }, 5000)
  }

  /**
   * 알림 제거
   * @param {number} id - 알림 ID
   */
  const removeNotification = (id) => {
    const index = notifications.value.findIndex(n => n.id === id)
    if (index > -1) {
      notifications.value.splice(index, 1)
    }
  }

  /**
   * 모든 알림 제거
   */
  const clearAllNotifications = () => {
    notifications.value = []
  }

  // 컴포넌트 마운트 시 SSE 연결 (이미 연결되어 있으면 스킵)
  onMounted(() => {
    // 사용자 ID가 있으면 자동 연결
    const userId = localStorage.getItem('userId')
    if (userId && !sseManager.getConnectionStatus()) {
      connectSSE(userId)
    } else if (userId && sseManager.getConnectionStatus()) {
      // 이미 연결되어 있으면 상태만 업데이트
      isConnected.value = true
    }
  })

  // 컴포넌트 언마운트 시에는 연결을 해제하지 않음 (전역 연결 유지)
  onUnmounted(() => {
    // SSE 연결은 유지하고 상태만 업데이트
    isConnected.value = sseManager.getConnectionStatus()
  })

  return {
    // 상태
    isConnected,
    notifications,
    currentUserId,
    
    // 메서드
    connectSSE,
    showNotification,
    removeNotification,
    clearAllNotifications,
    
    // SSE 관리자 직접 접근 (필요한 경우)
    sseManager
  }
}
