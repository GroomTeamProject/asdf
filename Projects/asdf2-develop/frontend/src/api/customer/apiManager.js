/**
 * API 요청 큐 관리자
 * 동시 요청을 순차적으로 처리하여 서버 부하를 줄이고 안정성을 높입니다.
 */
class ApiManager {
  constructor() {
    this.requestQueue = []
    this.isProcessing = false
  }

  /**
   * 큐에 있는 요청들을 순차적으로 처리
   */
  async processQueue() {
    if (this.isProcessing) return

    this.isProcessing = true

    while (this.requestQueue.length > 0) {
      const { apiFunction, resolve, reject } = this.requestQueue.shift()

      try {
        const result = await apiFunction()
        resolve(result)
        // 요청 간 간격을 두어 서버 부하 방지
        await new Promise((resolve) => setTimeout(resolve, 100))
      } catch (error) {
        reject(error)
      }
    }

    this.isProcessing = false
  }

  /**
   * API 요청을 큐에 추가하고 순차적으로 처리
   * @param {Function} apiFunction - 실행할 API 함수
   * @returns {Promise} API 응답 결과
   */
  async queueRequest(apiFunction) {
    return new Promise((resolve, reject) => {
      this.requestQueue.push({ apiFunction, resolve, reject })
      this.processQueue()
    })
  }
}

// 싱글톤 인스턴스 생성
const apiManager = new ApiManager()

export default apiManager
