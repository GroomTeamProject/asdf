import { apiFetch } from '@/libs/apiFetch'

const BASE_URL = 'http://localhost:8080'

// 알림 관련 API
export const notificationApi = {
  // 사용자 알림 조회 (페이징)
  getUserNotifications: async (userId, page = 0, size = 20) => {
    try {
      const response = await apiFetch(`${BASE_URL}/api/notifications/user/${userId}?page=${page}&size=${size}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      return await response.json()
    } catch (error) {
      console.error('알림 조회 실패:', error)
      throw error
    }
  },

  // SSE 연결 상태 확인
  getConnectionStatus: async (userId) => {
    try {
      const response = await apiFetch(`${BASE_URL}/api/sse/status?userId=${userId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      return await response.json()
    } catch (error) {
      console.error('SSE 연결 상태 확인 실패:', error)
      throw error
    }
  },

  // SSE 연결
  connectSSE: (userId) => {
    const eventSource = new EventSource(`${BASE_URL}/api/sse/connect?userId=${userId}`)
    return eventSource
  },

  // 개별 알림 읽음 처리
  markAsRead: async (notificationId, userId) => {
    try {
      const response = await apiFetch(`${BASE_URL}/api/notifications/${notificationId}/read?userId=${userId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        }
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      return await response.json()
    } catch (error) {
      console.error('알림 읽음 처리 실패:', error)
      throw error
    }
  },

  // 모든 알림 읽음 처리
  markAllAsRead: async (userId) => {
    try {
      const response = await apiFetch(`${BASE_URL}/api/notifications/user/${userId}/read-all`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        }
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      return await response.json()
    } catch (error) {
      console.error('모든 알림 읽음 처리 실패:', error)
      throw error
    }
  },

  // 읽지 않은 알림 조회
  getUnreadNotifications: async (userId, page = 0, size = 20) => {
    try {
      const response = await apiFetch(`${BASE_URL}/api/notifications/user/${userId}/unread/page?page=${page}&size=${size}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      return await response.json()
    } catch (error) {
      console.error('읽지 않은 알림 조회 실패:', error)
      throw error
    }
  },

  // 읽지 않은 알림 개수 조회
  getUnreadCount: async (userId) => {
    try {
      const response = await apiFetch(`${BASE_URL}/api/notifications/user/${userId}/unread-count`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      return await response.json()
    } catch (error) {
      console.error('읽지 않은 알림 개수 조회 실패:', error)
      throw error
    }
  },

  // 알림 통계 조회
  getNotificationStats: async (userId) => {
    try {
      const response = await apiFetch(`${BASE_URL}/api/notifications/user/${userId}/stats`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      return await response.json()
    } catch (error) {
      console.error('알림 통계 조회 실패:', error)
      throw error
    }
  }
}
