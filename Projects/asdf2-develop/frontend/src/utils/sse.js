/**
 * SSE(Server-Sent Events) 연결을 관리하는 싱글톤 클래스
 * 연결 관리만 담당하며, 이벤트 처리(이벤트 리스너)는 각 서비스에서 담당합니다.
 */
import { EventSourcePolyfill } from 'event-source-polyfill'
//import EventSourcePolyfill from 'event-source-polyfill'

class SSEManager {
  constructor() {
    this.eventSource = null
    this.isConnected = false
    this.heartbeatTimeout = 300000 // ms
    this.userId = null
    this.userType = null
    this.listeners = new Map()
  }

  /**
   * SSE 연결 시작
   * @param {string} userId - 사용자 ID
   * @param {string} userType - 사용자 타입 (CUSTOMER, OWNER, RIDER)
   */
  connect(userId, userType = 'CUSTOMER') {
    if (this.isConnected && this.userId === userId) {
      return
    }

    this.userId = userId
    this.userType = userType
    this.disconnect() // 기존 연결이 있다면 끊기

    try {
      const baseURL = import.meta.env.VITE_API_URL
      const token = localStorage.getItem('jwt')
      const url = `${baseURL}/sse/connect`
      console.log('🔄 SSE 연결 시도:', url)

      this.eventSource = new EventSourcePolyfill(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        heartbeatTimeout: this.heartbeatTimeout,
      })
      this.setupBasicEventListeners()
    } catch (error) {
      console.error('❌ SSE 연결 오류 발생!')
      this.isConnected = false
      this.handleConnectionError()
    }
  }

  /**
   * 기본 이벤트 리스너 설정 (연결 관리만)
   */
  setupBasicEventListeners() {
    if (!this.eventSource) return

    this.eventSource.onopen = () => {
      console.log('✅ SSE 연결 성공!')
      this.isConnected = true
    }
  }

  /**
   * 이벤트 리스너 등록
   * @param {string} eventName - 이벤트 이름
   * @param {Function} callback - 콜백 함수
   */
  addEventListener(eventName, callback) {
    if (!this.eventSource) {
      console.warn('SSE가 연결되지 않았습니다.')
      return
    }

    const listener = (event) => {
      try {
        callback(event)
      } catch (error) {
        console.error(`SSE 이벤트 처리 오류 (${eventName}):`, error)
      }
    }

    this.eventSource.addEventListener(eventName, listener)

    // 리스너 관리
    if (!this.listeners.has(eventName)) {
      this.listeners.set(eventName, [])
    }
    this.listeners.get(eventName).push({ callback, listener })

    console.log(`📡 SSE 이벤트 리스너 등록: ${eventName}`)
  }

  /**
   * 모든 이벤트 리스너 제거
   */
  removeAllEventListeners() {
    if (!this.eventSource) return

    this.listeners.forEach((eventListeners, eventName) => {
      eventListeners.forEach(({ listener }) => {
        this.eventSource.removeEventListener(eventName, listener)
      })
    })

    this.listeners.clear()
    console.log('📡 모든 SSE 이벤트 리스너 제거 완료')
  }

  /**
   * 연결 오류 처리
   */
  handleConnectionError() {
    this.isConnected = false
  }

  /**
   * SSE 연결 해제
   */
  disconnect() {
    if (this.eventSource) {
      this.removeAllEventListeners()
      this.eventSource.close()
      this.eventSource = null

      console.log('✅ SSE 연결 해제 완료')
    } else {
    }
    this.isConnected = false
  }

  /**
   * 연결 상태 확인
   * @returns {boolean} 연결 상태
   */
  getConnectionStatus() {
    return this.isConnected
  }
}

// 싱글톤 인스턴스 생성 및 내보내기
export default new SSEManager()
